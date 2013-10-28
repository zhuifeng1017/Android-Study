package com.xxx.appstore.common.download;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.webkit.MimeTypeMap;
import com.xxx.appstore.common.util.Utils;
import java.io.File;
import java.util.Random;

public class Helper
{
  public static Random rnd = new Random(SystemClock.uptimeMillis());

  private static String chooseExtensionFromFilename(String mimeType,
          String filename, int dotIndex) {
      String extension = null;
      if (mimeType != null) {
          // Compare the last segment of the extension against the mime type.
          // If there's a mismatch, discard the entire extension.
          int lastDotIndex = filename.lastIndexOf('.');
          String typeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                  filename.substring(lastDotIndex + 1));
          if (typeFromExt == null || !typeFromExt.equalsIgnoreCase(mimeType)) {
              extension = chooseExtensionFromMimeType(mimeType, false);
              if (extension != null) {
            	  Utils.D("substituting extension from type");
              } else {
            	  Utils.D("couldn't find extension for " + mimeType);
              }
          }
      }
      if (extension == null) {
    	  Utils.D("keeping extension");
          extension = filename.substring(dotIndex);
      }
      return extension;
  }
  
  private static String chooseExtensionFromMimeType(String mimeType, boolean useDefaults) {
      String extension = null;
      if (mimeType != null) {
          extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
          if (extension != null) {
        	  Utils.D("adding extension from type");
              extension = "." + extension;
          } else {
        	  Utils.D("couldn't find extension for " + mimeType);
          }
      }
      if (extension == null) {
          if (mimeType != null && mimeType.toLowerCase().startsWith("text/")) {
              if (mimeType.equalsIgnoreCase("text/html")) {
            	  Utils.D("adding default html extension");
                  extension = Constants.DEFAULT_DL_HTML_EXTENSION;
              } else if (useDefaults) {
            	  Utils.D("adding default text extension");
                  extension = Constants.DEFAULT_DL_TEXT_EXTENSION;
              }
          } else if (useDefaults) {
        	  Utils.D("adding default binary extension");
              extension = Constants.DEFAULT_DL_BINARY_EXTENSION;
          }
      }
      return extension;
  }
  
  private static String chooseFilename(String url, String hint,
          String contentLocation, int destination) {
      String filename = null;

      // First, try to use the hint from the application, if there's one
      if (filename == null && hint != null && !hint.endsWith("/")) {
//          if (Constants.LOGVV) {
    	  Utils.D("getting filename from hint");
//          }
          int index = hint.lastIndexOf('/') + 1;
          if (index > 0) {
              filename = hint.substring(index);
          } else {
              filename = hint;
          }
      }
  
//   // If we couldn't do anything with the hint, move toward the content disposition
//      if (filename == null && contentDisposition != null) {
//          filename = parseContentDisposition(contentDisposition);
//          if (filename != null) {
////              if (Constants.LOGVV) {
//        	  Utils.D("getting filename from content-disposition");
////              }
//              int index = filename.lastIndexOf('/') + 1;
//              if (index > 0) {
//                  filename = filename.substring(index);
//              }
//          }
//      }

      // If we still have nothing at this point, try the content location
      if (filename == null && contentLocation != null) {
          String decodedContentLocation = Uri.decode(contentLocation);
          if (decodedContentLocation != null
                  && !decodedContentLocation.endsWith("/")
                  && decodedContentLocation.indexOf('?') < 0) {
//              if (Constants.LOGVV) {
        	  Utils.D("getting filename from content-location");
//              }
              int index = decodedContentLocation.lastIndexOf('/') + 1;
              if (index > 0) {
                  filename = decodedContentLocation.substring(index);
              } else {
                  filename = decodedContentLocation;
              }
          }
      }

      // If all the other http-related approaches failed, use the plain uri
      if (filename == null) {
          String decodedUrl = Uri.decode(url);
          if (decodedUrl != null
                  && !decodedUrl.endsWith("/") && decodedUrl.indexOf('?') < 0) {
              int index = decodedUrl.lastIndexOf('/') + 1;
              if (index > 0) {
//                  if (Constants.LOGVV) {
            	  Utils.D("getting filename from uri");
//                  }
                  filename = decodedUrl.substring(index);
              }
          }
      }

      // Finally, if couldn't get filename from URI, get a generic filename
      if (filename == null) {
//          if (Constants.LOGVV) {
    	  Utils.D("using default filename");
//          }
          filename = Constants.DEFAULT_DL_FILENAME;
      }

      // The VFAT file system is assumed as target for downloads.
      // Replace invalid characters according to the specifications of VFAT.
      filename = replaceInvalidVfatCharacters(filename);

      return filename;
  }
  
	private static String chooseFullPath(Context context, String url,
			String hint, String contentLocation,String mimeType,
			 int destination, long contentLength, int source)
			throws GenerateSaveFileError {
		File base = locateDestinationDirectory(context, mimeType, source, destination, contentLength);
		String filename = chooseFilename(url, hint, contentLocation, destination);

		// Split filename between base and extension
		// Add an extension if filename does not have one
		String extension = null;
		int dotIndex = filename.indexOf('.');
		if (dotIndex < 0) {
			extension = chooseExtensionFromMimeType(mimeType, true);
		} else {
			extension = chooseExtensionFromFilename(mimeType, filename, dotIndex);
			filename = filename.substring(0, dotIndex);
		}

		boolean recoveryDir = Constants.RECOVERY_DIRECTORY
				.equalsIgnoreCase(filename + extension);

		filename = base.getPath() + File.separator + filename;

//		if (Constants.LOGVV) {
			Utils.V("target file: " + filename + extension);
//		}

		return chooseUniqueFilename(destination, filename, extension, recoveryDir);
}

	private static String chooseUniqueFilename(int destination, String filename,
            String extension, boolean recoveryDir) throws GenerateSaveFileError {
        String fullFilename = filename + extension;
        if (!new File(fullFilename).exists()
                && (!recoveryDir ||
                (destination != DownloadManager.Impl.DESTINATION_CACHE_PARTITION))) {
            return fullFilename;
        }
        filename = filename + Constants.FILENAME_SEQUENCE_SEPARATOR;
        /*
        * This number is used to generate partially randomized filenames to avoid
        * collisions.
        * It starts at 1.
        * The next 9 iterations increment it by 1 at a time (up to 10).
        * The next 9 iterations increment it by 1 to 10 (random) at a time.
        * The next 9 iterations increment it by 1 to 100 (random) at a time.
        * ... Up to the point where it increases by 100000000 at a time.
        * (the maximum value that can be reached is 1000000000)
        * As soon as a number is reached that generates a filename that doesn't exist,
        *     that filename is used.
        * If the filename coming in is [base].[ext], the generated filenames are
        *     [base]-[sequence].[ext].
        */
        int sequence = 1;
        for (int magnitude = 1; magnitude < 1000000000; magnitude *= 10) {
            for (int iteration = 0; iteration < 9; ++iteration) {
                fullFilename = filename + sequence + extension;
                if (!new File(fullFilename).exists()) {
                    return fullFilename;
                }
//                if (Constants.LOGVV) {
                Utils.V("file with sequence number " + sequence + " exists");
//                }
                sequence += rnd.nextInt(magnitude) + 1;
            }
        }
        throw new GenerateSaveFileError(DownloadManager.Impl.STATUS_FILE_ERROR,
                "failed to generate an unused filename on internal download storage");
    }
  
  /**
   * Creates a filename (where the file should be saved) from info about a download.
   */
  public static String generateSaveFile(
          Context context,
          String url,
          String hint,
          String contentLocation,
          String mimeType,
          int destination,
          long contentLength,
          int source) throws GenerateSaveFileError {
	  return chooseFullPath(context, url, hint, contentLocation, mimeType, destination, contentLength, source);
  }

  public static Integer getActiveNetworkType(Context context)
  {
    ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
    Integer integer;
    if (cm == null)
    {
      Utils.D("couldn't get connectivity manager");
      integer = null;
    }
    else {
      NetworkInfo localNetworkInfo = cm.getActiveNetworkInfo();
      if (localNetworkInfo == null)
      {
        Utils.D("network is not available");
        integer = null;
      }
      else
      {
    	  integer = Integer.valueOf(localNetworkInfo.getType());
      }
    }
    return integer;
  }

  /**
   * @return the number of bytes available on the filesystem rooted at the given File
   */
  public static long getAvailableBytes(File root) {
      StatFs stat = new StatFs(root.getPath());
      // put a bit of margin (in case creating the file grows the system by a few blocks)
      long availableBlocks = (long) stat.getAvailableBlocks() - 4;
      return stat.getBlockSize() * availableBlocks;
  }

  private static File getCacheDestination(Context context, long contentLength)
    throws Helper.GenerateSaveFileError
  {
    File base = context.getFilesDir();
    if (getAvailableBytes(base) < contentLength)
    {
      Utils.D("download aborted - not enough internal free space");
      throw new GenerateSaveFileError(DownloadManager.Impl.STATUS_INSUFFICIENT_SPACE_ERROR, "not enough free space in internal download storage, unable to free any more");
    }
    return base;
  }

  private static File getExternalDestination(long contentLength,int paramInt, String paramString) throws GenerateSaveFileError {
      if (!isExternalMediaMounted()) {
          throw new GenerateSaveFileError(DownloadManager.Impl.STATUS_DEVICE_NOT_FOUND_ERROR,
                  "external media not mounted");
      }

      File root = Environment.getExternalStorageDirectory();
      if (getAvailableBytes(root) < contentLength) {
          // Insufficient space.
    	  Utils.D("download aborted - not enough free space");
          throw new GenerateSaveFileError(DownloadManager.Impl.STATUS_INSUFFICIENT_SPACE_ERROR,
                  "insufficient space on external media");
      }

      File base;
		if (paramInt == 0)
			base = new File(root.getPath(), "gfan/market");
		else if (1 == paramInt)
			base = new File(root.getPath(), "gfan/bbs");
		else if (2 == paramInt)
			base = new File(root.getPath(), "gfan/cloud");
		else
			base = null;
		
      if (!base.isDirectory() && !base.mkdirs()) {
          // Can't create download directory, e.g. because a file called "download"
          // already exists at the root level, or the SD card filesystem is read-only.
          throw new GenerateSaveFileError(DownloadManager.Impl.STATUS_FILE_ERROR,
                  "unable to create external downloads directory " + base.getPath());
      }
      return base;
  }
  
  public static boolean isExternalMediaMounted() {
      if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
          // No SD card found.
    	  Utils.D("no external storage");
          return false;
      }
      return true;
  }

	public static boolean isFilenameValid(String paramString, int paramInt) {
		File localFile = new File(paramString).getParentFile();
		boolean bool;
		if (paramInt == 0)
			bool = localFile.equals(new File(Environment
					.getExternalStorageDirectory(), "gfan/market"));
		else if (1 == paramInt)
			bool = localFile.equals(new File(Environment
					.getExternalStorageDirectory(), "gfan/bbs"));
		else if (2 == paramInt)
			bool = localFile.equals(new File(Environment
					.getExternalStorageDirectory(), "gfan/cloud"));
		else
			bool = localFile.equals(new File(Environment
					.getExternalStorageDirectory() + "gfan/others"));
		return bool;
	}

  public static boolean isNetworkAvailable(Context context)
  {
    ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
    if (cm == null) {
      Utils.D("couldn't get connectivity manager");
      return false;
    }
  
      NetworkInfo[] arrNetworkInfo = cm.getAllNetworkInfo();
      if (arrNetworkInfo != null) {
        for (int i = 0; i<arrNetworkInfo.length; i++) {
          if (arrNetworkInfo[i].getState() == NetworkInfo.State.CONNECTED)
          {
            Utils.D("network is available");
            return true;
          }
        }
      }
      Utils.D("network is not available");
      return false;
  }

  private static File locateDestinationDirectory(Context context, String mimeType,int source,
          int destination, long contentLength)
	throws GenerateSaveFileError {
	// DRM messages should be temporarily stored internally and then passed to
	// the DRM content provider
	if (destination == DownloadManager.Impl.DESTINATION_CACHE_PARTITION) {
		return getCacheDestination(context, contentLength);
	}
	
	return getExternalDestination(contentLength,source,mimeType);
}
  
//  private static File locateDestinationDirectory(Context paramContext, String paramString, int paramInt1, int paramInt2, long paramLong)
//    throws Helper.GenerateSaveFileError
//  {
//	  File localFile;
//    if (paramInt2 == 1)
//    	localFile = getCacheDestination(paramContext, paramLong); 
//    else
//    	localFile = getExternalDestination(paramLong, paramInt1, paramString);
//      return localFile;
//  }

  private static String replaceInvalidVfatCharacters(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int j = 0;
    for (int i = 0;i < paramString.length();i++)
    {
      char c = paramString.charAt(i);
      if (((c >= 0) && (c <= '\037')) || (c == '"') || (c == '*') || (c == '/') || (c == ':') || (c == '<') || (c == '>') || (c == '?') || (c == '\\') || (c == '|') || (c == ''))
          localStringBuffer.append('_');
      else
    	  localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }

  public static class GenerateSaveFileError extends Exception
  {
    private static final long serialVersionUID = 7750062109363258607L;
    String mMessage;
    int mStatus;

    public GenerateSaveFileError(int paramInt, String paramString)
    {
      this.mStatus = paramInt;
      this.mMessage = paramString;
    }
  }
}