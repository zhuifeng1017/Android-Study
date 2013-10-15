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

  private static String chooseExtensionFromFilename(String paramString1, String paramString2, int paramInt)
  {
    String str1 = null;
    if (paramString1 != null)
    {
      int i = paramString2.lastIndexOf('.');
      String str2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(paramString2.substring(i + 1));
      if ((str2 == null) || (!str2.equalsIgnoreCase(paramString1)))
      {
        str1 = chooseExtensionFromMimeType(paramString1, false);
        if (str1 == null) {
        	 Utils.D("couldn't find extension for " + paramString1);
        	 
        	Utils.D("keeping extension");
            str1 = paramString2.substring(paramInt);
        }
        else {
        	Utils.D("substituting extension from type");
        }
      }
    }
    return str1;

  }

  private static String chooseExtensionFromMimeType(String paramString, boolean paramBoolean)
  {
    String str = null;
    if (paramString != null)
    {
      str = MimeTypeMap.getSingleton().getExtensionFromMimeType(paramString);
      if (str != null)
      {
        Utils.D("adding extension from MIME type.");
        str = "." + str;
      }
      else
      {
    	  Utils.D("couldn't find extension for " + paramString);
          if (paramString.toLowerCase().startsWith("text/"))
            if (paramString.equalsIgnoreCase("text/html"))
            {
              Utils.D("adding default html extension");
              str = ".html";
            }
            else if (paramBoolean)
            {
              Utils.D("adding default text extension");
              str = ".txt";
            }
            else
            {
            	Utils.D("adding default binary extension");
                str = ".bin";
            }
      }
    }
    return str;
  }

  private static String chooseFilename(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    Object localObject = null;
    String str2;
    return "fuck";
//    int j;
//    if ((0 == 0) && (paramString2 != null) && (!paramString2.endsWith("/")))
//    {
//      Utils.V("getting filename from hint");
//      int k = 1 + paramString2.lastIndexOf('/');
//      if (k > 0)
//        localObject = paramString2.substring(k);
//    }
//    else if ((localObject == null) && (paramString3 != null))
//    {
//      str2 = Uri.decode(paramString3);
//      if ((str2 != null) && (!str2.endsWith("/")) && (str2.indexOf('?') < 0))
//      {
//        Utils.V("getting filename from content-location");
//        j = 1 + str2.lastIndexOf('/');
//        if (j <= 0)
//          break label210;
//      }
//    }
//    label210: for (localObject = str2.substring(j); ; localObject = str2)
//    {
//      if (localObject == null)
//      {
//        String str1 = Uri.decode(paramString1);
//        if ((str1 != null) && (!str1.endsWith("/")) && (str1.indexOf('?') < 0))
//        {
//          int i = 1 + str1.lastIndexOf('/');
//          if (i > 0)
//          {
//            Utils.V("getting filename from uri");
//            localObject = str1.substring(i);
//          }
//        }
//      }
//      if (localObject == null)
//      {
//        Utils.V("using default filename");
//        localObject = "downloadfile";
//      }
//      return replaceInvalidVfatCharacters((String)localObject);
//      localObject = paramString2;
//      break;
//    }
  }

  private static String chooseFullPath(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, long paramLong, int paramInt2)
    throws Helper.GenerateSaveFileError
  {
	  return "fuck";
//    File localFile = locateDestinationDirectory(paramContext, paramString4, paramInt2, paramInt1, paramLong);
//    String str1 = chooseFilename(paramString1, paramString2, paramString3, paramInt1);
//    int i = str1.indexOf('.');
//    String str5;
//    String str3;
//    if (i < 0)
//    {
//      str5 = chooseExtensionFromMimeType(paramString4, true);
//      str3 = str1;
//    }
//    String str2;
//    for (Object localObject = str5; ; localObject = str2)
//    {
//      boolean bool = "recovery".equalsIgnoreCase(str3 + (String)localObject);
//      String str4 = localFile.getPath() + File.separator + str3;
//      Utils.V("target file: " + str4 + (String)localObject);
//      return chooseUniqueFilename(paramInt1, str4, (String)localObject, bool);
//      str2 = chooseExtensionFromFilename(paramString4, str1, i);
//      str3 = str1.substring(0, i);
//    }
  }

  private static String chooseUniqueFilename(int paramInt, String paramString1, String paramString2, boolean paramBoolean)
  {
	  return "fuck";
//    int i = 1;
//    Object localObject = paramString1 + paramString2;
//    if ((!new File((String)localObject).exists()) && ((!paramBoolean) || (paramInt != i)));
//    while (true)
//    {
//      return localObject;
//      String str1 = paramString1 + "-";
//      int j = i;
//      while (true)
//      {
//        if (j >= 1000000000)
//          break label208;
//        int k = i;
//        for (int m = 0; ; m++)
//        {
//          if (m >= 9)
//            break label194;
//          String str2 = str1 + k + paramString2;
//          if (!new File(str2).exists())
//          {
//            localObject = str2;
//            break;
//          }
//          Utils.V("file with sequence number " + k + " exists");
//          k += 1 + rnd.nextInt(j);
//        }
//        label194: j *= 10;
//        i = k;
//      }
//      label208: localObject = null;
//    }
  }

  public static String generateSaveFile(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, long paramLong, int paramInt2)
    throws Helper.GenerateSaveFileError
  {
    return chooseFullPath(paramContext, paramString1, paramString2, paramString3, paramString4, paramInt1, paramLong, paramInt2);
  }

  public static Integer getActiveNetworkType(Context paramContext)
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    Integer localInteger;
    if (localConnectivityManager == null)
    {
      Utils.D("couldn't get connectivity manager");
      localInteger = null;
    }

      NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
      if (localNetworkInfo == null)
      {
        Utils.D("network is not available");
        localInteger = null;
      }
      else
      {
        localInteger = Integer.valueOf(localNetworkInfo.getType());
      }
    return localInteger;
  }

  public static long getAvailableBytes(File paramFile)
  {
    StatFs localStatFs = new StatFs(paramFile.getPath());
    return (localStatFs.getAvailableBlocks() - 4L) * localStatFs.getBlockSize();
  }

  private static File getCacheDestination(Context paramContext, long paramLong)
    throws Helper.GenerateSaveFileError
  {
    File localFile = paramContext.getFilesDir();
    if (getAvailableBytes(localFile) < paramLong)
    {
      Utils.D("download aborted - not enough internal free space");
      throw new GenerateSaveFileError(498, "not enough free space in internal download storage, unable to free any more");
    }
    return localFile;
  }

  private static File getExternalDestination(long paramLong, int paramInt, String paramString)
    throws Helper.GenerateSaveFileError
  {
		if (!isExternalMediaMounted())
			throw new GenerateSaveFileError(499, "external media not mounted");
		File localFile1 = Environment.getExternalStorageDirectory();
		if (getAvailableBytes(localFile1) < paramLong) {
			Utils.D("download aborted - not enough external free space");
			throw new GenerateSaveFileError(498,
					"insufficient space on external media");
		}
		File localFile2;
		if (paramInt == 0)
			localFile2 = new File(localFile1.getPath(), "gfan/market");
		else if (1 == paramInt)
			localFile2 = new File(localFile1.getPath(), "gfan/bbs");
		else if (2 == paramInt)
			localFile2 = new File(localFile1.getPath(), "gfan/cloud");
		else
			localFile2 = null;

		if ((null != localFile2) && (!localFile2.isDirectory()) && (!localFile2.mkdirs())) {
			throw new GenerateSaveFileError(492,
					"unable to create external downloads directory "
							+ localFile2.getPath());
		}
		return localFile2;
	}

	public static boolean isExternalMediaMounted() {
		boolean bool = false;
		if (!Environment.getExternalStorageState().equals("mounted"))
			Utils.D("no external storage");
		else
			bool = true;
		return bool;
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

  public static boolean isNetworkAvailable(Context paramContext)
  {
	  return true;
//    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
//    if (localConnectivityManager == null)
//      Utils.D("couldn't get connectivity manager");
//    label75: 
//    while (true)
//    {
//      Utils.D("network is not available");
//      boolean bool = false;
//      return bool;
//      NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager.getAllNetworkInfo();
//      if (arrayOfNetworkInfo != null)
//        for (int i = 0; ; i++)
//        {
//          if (i >= arrayOfNetworkInfo.length)
//            break label75;
//          if (arrayOfNetworkInfo[i].getState() == NetworkInfo.State.CONNECTED)
//          {
//            Utils.D("network is available");
//            bool = true;
//            break;
//          }
//        }
//    }
  }

  private static File locateDestinationDirectory(Context paramContext, String paramString, int paramInt1, int paramInt2, long paramLong)
    throws Helper.GenerateSaveFileError
  {
	  File localFile;
    if (paramInt2 == 1)
    	localFile = getCacheDestination(paramContext, paramLong); 
    else
    	localFile = getExternalDestination(paramLong, paramInt1, paramString);
      return localFile;
  }

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