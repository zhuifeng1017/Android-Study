package com.xxx.appstore.common.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.ContentValues;
import android.content.Context;
import android.os.PowerManager;
import android.os.Process;
import android.text.TextUtils;

import com.xxx.appstore.common.AndroidHttpClient;
import com.xxx.appstore.common.HttpClientFactory;
import com.xxx.appstore.common.util.Utils;

public class DownloadThread extends Thread
{
  private Context mContext;
  private DownloadInfo mInfo;

  public DownloadThread(Context context, DownloadInfo info)
  {
    mContext = context;
    mInfo = info;
  }

  /**
   * Add custom headers for this download to the HTTP request.
   */
  private void addRequestHeaders(InnerState innerState, HttpGet request) {
      if (innerState.mContinuingDownload) {
          if (innerState.mHeaderETag != null) {
              request.addHeader("If-Match", innerState.mHeaderETag);
          }
          request.addHeader("Range", "bytes=" + innerState.mBytesSoFar + "-");
      }
  }
  
  private boolean cannotResume(InnerState innerState) {
      return innerState.mBytesSoFar > 0 && innerState.mHeaderETag == null;
  }

  private void checkConnectivity(State state)
    throws DownloadThread.StopRequest
  {
	  int networkUsable = mInfo.checkCanUseNetwork();
      if (networkUsable != DownloadInfo.NETWORK_OK) {
          int status = DownloadManager.Impl.STATUS_WAITING_FOR_NETWORK;
          throw new StopRequest(status, mInfo.getLogMessageForNetworkError(networkUsable));
      }
  }

  private boolean checkFile(State state)
  {
      String s = mInfo.mMD5;
      boolean flag;
      if(TextUtils.isEmpty(s))
          flag = true;
      else if(s.equalsIgnoreCase(convertToHex(state.mDigester.digest())))
          flag = true;
      else
          flag = false;
      return flag;
  }

  /**
   * Check if the download has been paused or canceled, stopping the request appropriately if it
   * has been.
   */
  private void checkPausedOrCanceled(State state) throws StopRequest {
      synchronized (mInfo) {
          if (mInfo.mControl == DownloadManager.Impl.CONTROL_PAUSED) {
              throw new StopRequest(DownloadManager.Impl.STATUS_PAUSED_BY_APP,
                      "download paused by owner");
          }
          
          if(mInfo.mControl == DownloadManager.Impl.CONTROL_PENDING)
	          throw new StopRequest(DownloadManager.Impl.STATUS_QUEUED_FOR_WIFI,
	        		  "download is in pending status");
      }
      if (mInfo.mStatus == DownloadManager.Impl.STATUS_CANCELED) {
    	  Utils.submitDownloadLog(mContext, 2, mInfo.mSource, mInfo.mUri, "");
          throw new StopRequest(DownloadManager.Impl.STATUS_CANCELED, "download canceled");
      }
  }
 
  /**
   * Called just before the thread finishes, regardless of status, to take any necessary action on
   * the downloaded file.
   */
  private void cleanupDestination(State state, int finalStatus) {
      closeDestination(state);
      if (state.mFilename != null && DownloadManager.Impl.isStatusError(finalStatus)) {
          new File(state.mFilename).delete();
          state.mFilename = null;
      }
  }

  /**
   * Close the destination output stream.
   */
  private void closeDestination(State state) {
      try {
          // close the file
          if (state.mStream != null) {
              state.mStream.close();
              state.mStream = null;
          }
      } catch (IOException ex) {
          Utils.V("exception when closing the file after download : " + ex);
          // nothing can really be done if the file can't be closed
      }
  }
 
  private static String convertToHex(byte[] data) {
      StringBuilder buf = new StringBuilder();
      for (byte b : data) {
          int halfbyte = (b >>> 4) & 0x0F;
          int two_halfs = 0;
          do {
              buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
              halfbyte = b & 0x0F;
          } while (two_halfs++ < 1);
      }
      return buf.toString();
  }

  /**
   * Fully execute a single download request - setup and send the request, handle the response,
   * and transfer the data to the destination file.
   */
  private void executeDownload(State state, AndroidHttpClient client, HttpGet request)
          throws StopRequest, RetryDownload {
      InnerState innerState = new InnerState();
      byte data[] = new byte[Constants.BUFFER_SIZE];

      setupDestinationFile(state, innerState);
      addRequestHeaders(innerState, request);

      // check just before sending the request to avoid using an invalid connection at all
      checkConnectivity(state);

      HttpResponse response = sendRequest(state, client, request);
      handleExceptionalStatus(state, innerState, response);

//      if (Constants.LOGV) {
    	  Utils.D("received response for " + mInfo.mUri);
//      }

      processResponseHeaders(state, innerState, response);
      InputStream entityStream = openResponseEntity(state, response);
      transferData(state, innerState, data, entityStream);
  }
//  
//  private void executeDownload(State state, AndroidHttpClient paramAndroidHttpClient, HttpGet paramHttpGet)
//    throws DownloadThread.StopRequest, DownloadThread.RetryDownload
//  {
//    InnerState localInnerState = new InnerState();
//    byte[] arrayOfByte = new byte[4096];
//    setupDestinationFile(state, localInnerState);
//    addRequestHeaders(localInnerState, paramHttpGet);
//    checkConnectivity(state);
//    HttpResponse localHttpResponse = sendRequest(state, paramAndroidHttpClient, paramHttpGet);
//    handleExceptionalStatus(state, localInnerState, localHttpResponse);
//    Utils.D("received response for " + mInfo.mUri);
//    processResponseHeaders(state, localInnerState, localHttpResponse);
//    transferData(state, localInnerState, arrayOfByte, new DigestInputStream(openResponseEntity(state, localHttpResponse), state.mDigester));
//  }

  private void finalizeDestinationFile(State state)
    throws DownloadThread.StopRequest
  {
    syncDestination(state);
  }
  
  private int getFinalStatusForHttpError(State state) {
      if (!Helper.isNetworkAvailable(mContext)) {
          return DownloadManager.Impl.STATUS_WAITING_FOR_NETWORK;
      } else if (mInfo.mNumFailed < Constants.MAX_RETRIES) {
          state.mCountRetry = true;
          return DownloadManager.Impl.STATUS_WAITING_TO_RETRY;
      } else {
          Utils.W("reached max retries for " + mInfo.mId);
          return DownloadManager.Impl.STATUS_HTTP_DATA_ERROR;
      }
  }

	private static String getIpAddress(String s)
    {
        String s1 = URI.create(s).getHost();
        String s2;
        try
        {
            s2 = InetAddress.getByName(s1).getHostAddress();
            Utils.D((new StringBuilder()).append("ip address is > ").append(s2).toString());
        }
        catch(UnknownHostException unknownhostexception)
        {
            Utils.D("error when get ip address", unknownhostexception);
            s2 = "";
        }
        return s2;
    }
	
	/**
     * Called when we've reached the end of the HTTP response stream, to update the database and
     * check for consistency.
     */
    private void handleEndOfStream(State state, InnerState innerState) throws StopRequest {
        ContentValues values = new ContentValues();
        values.put(DownloadManager.Impl.COLUMN_CURRENT_BYTES, innerState.mBytesSoFar);
        if (innerState.mHeaderContentLength == null) {
            values.put(DownloadManager.Impl.COLUMN_TOTAL_BYTES, innerState.mBytesSoFar);
        }
        mContext.getContentResolver().update(mInfo.getMyDownloadsUri(), values, null, null);

        boolean lengthMismatched = (innerState.mHeaderContentLength != null)
                && (innerState.mBytesSoFar != Integer.parseInt(innerState.mHeaderContentLength));
        if (lengthMismatched) {
            if (cannotResume(innerState)) {
                throw new StopRequest(DownloadManager.Impl.STATUS_CANNOT_RESUME,
                        "mismatched content length");
            } else {
                throw new StopRequest(getFinalStatusForHttpError(state),
                        "closed socket before end of file");
            }
        }
    }
   
	/**
     * Check the HTTP response status and handle anything unusual (e.g. not 200/206).
     */
    private void handleExceptionalStatus(State state, InnerState innerState, HttpResponse response)
            throws StopRequest, RetryDownload {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 503 && mInfo.mNumFailed < Constants.MAX_RETRIES) {
            handleServiceUnavailable(state, response);
        }
        if (statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307) {
            handleRedirect(state, response, statusCode);
        }

        int expectedStatus = innerState.mContinuingDownload ? 206 : DownloadManager.Impl.STATUS_SUCCESS;
        if (statusCode != expectedStatus) {
            handleOtherStatus(state, innerState, statusCode);
        }
    }
    	
	/**
     * Handle a status that we don't know how to deal with properly.
     */
    private void handleOtherStatus(State state, InnerState innerState, int statusCode)
            throws StopRequest {
        int finalStatus;
        if (DownloadManager.Impl.isStatusError(statusCode)) {
            finalStatus = statusCode;
        } else if (statusCode >= 300 && statusCode < 400) {
            finalStatus = DownloadManager.Impl.STATUS_UNHANDLED_REDIRECT;
        } else if (innerState.mContinuingDownload && statusCode == DownloadManager.Impl.STATUS_SUCCESS) {
            finalStatus = DownloadManager.Impl.STATUS_CANNOT_RESUME;
        } else {
            finalStatus = DownloadManager.Impl.STATUS_UNHANDLED_HTTP_CODE;
        }
        Utils.D("throw new stop request ----> " + finalStatus + " statusCode " + statusCode
				+ " isContinuing " + innerState.mContinuingDownload
				+ " fileName " + state.mFilename);
        throw new StopRequest(finalStatus, "http error " + statusCode);
    }
	
    /**
     * Handle a 3xx redirect status.
     */
    private void handleRedirect(State state, HttpResponse response, int statusCode)
            throws StopRequest, RetryDownload {
        Utils.V("got HTTP redirect " + statusCode);
        if (state.mRedirectCount >= Constants.MAX_REDIRECTS) {
            throw new StopRequest(DownloadManager.Impl.STATUS_TOO_MANY_REDIRECTS, "too many redirects");
        }
        Header header = response.getFirstHeader("Location");
        if (header == null) {
            return;
        }
        Utils.V("Location :" + header.getValue());

        String newUri;
        try {
            newUri = new URI(mInfo.mUri).resolve(new URI(header.getValue())).toString();
        } catch(URISyntaxException ex) {
                Utils.D("Couldn't resolve redirect URI " + header.getValue()
                        + " for " + mInfo.mUri);
            throw new StopRequest(DownloadManager.Impl.STATUS_HTTP_DATA_ERROR,
                    "Couldn't resolve redirect URI");
        }
        ++state.mRedirectCount;
        state.mRequestUri = newUri;
        if (statusCode == 301 || statusCode == 303) {
            // use the new URI for all future requests (should a retry/resume be necessary)
            state.mNewUri = newUri;
        }
        throw new RetryDownload();
    }
    
    /**
     * Handle a 503 Service Unavailable status by processing the Retry-After header.
     */
    private void handleServiceUnavailable(State state, HttpResponse response) throws StopRequest {
        Utils.V("got HTTP response code 503");
        state.mCountRetry = true;
        Header header = response.getFirstHeader("Retry-After");
        if (header != null) {
           try {
        	   Utils.V("Retry-After :" + header.getValue());
               state.mRetryAfter = Integer.parseInt(header.getValue());
               if (state.mRetryAfter < 0) {
                   state.mRetryAfter = 0;
               } else {
                   if (state.mRetryAfter < Constants.MIN_RETRY_AFTER) {
                       state.mRetryAfter = Constants.MIN_RETRY_AFTER;
                   } else if (state.mRetryAfter > Constants.MAX_RETRY_AFTER) {
                       state.mRetryAfter = Constants.MAX_RETRY_AFTER;
                   }
                   state.mRetryAfter += Helper.rnd.nextInt(Constants.MIN_RETRY_AFTER + 1);
                   state.mRetryAfter *= 1000;
               }
           } catch (NumberFormatException ex) {
               // ignored - retryAfter stays 0 in this case.
           }
        }
        throw new StopRequest(DownloadManager.Impl.STATUS_WAITING_TO_RETRY,
                "got 503 Service Unavailable, will retry later");
    }
  
    private void logNetworkState() {
            Utils.I("Net " + (Helper.isNetworkAvailable(mContext) ? "Up" : "Down"));
    }
    
    /**
     * Stores information about the completed download, and notifies the initiating application.
     */
    private void notifyDownloadCompleted(
            int status, boolean countRetry, int retryAfter, int redirectCount, boolean gotData,
            String filename, String uri, String mimeType) {
        notifyThroughDatabase(
                status, countRetry, retryAfter, redirectCount, gotData, filename, uri, mimeType);
//        if (Downloads.Impl.isStatusCompleted(status)) {
//            mInfo.sendIntentIfRequested();
//        }
    }
 
  private void notifyThroughDatabase(
          int status, boolean countRetry, int retryAfter, int redirectCount, boolean gotData,
          String filename, String uri, String mimeType) {
      ContentValues values = new ContentValues();
      values.put(DownloadManager.Impl.COLUMN_STATUS, status);
      values.put(DownloadManager.Impl.COLUMN_DATA, filename);
      if (uri != null) {
          values.put(DownloadManager.Impl.COLUMN_URI, uri);
      }
      values.put(DownloadManager.Impl.COLUMN_MIME_TYPE, mimeType);
      values.put(DownloadManager.Impl.COLUMN_LAST_MODIFICATION, System.currentTimeMillis());
      values.put(DownloadManager.Impl.COLUMN_RETRY_AFTER_REDIRECT_COUNT, retryAfter + (redirectCount << 28));
      if (!countRetry) {
          values.put(DownloadManager.Impl.COLUMN_FAILED_CONNECTIONS, 0);
      } else if (gotData) {
          values.put(DownloadManager.Impl.COLUMN_FAILED_CONNECTIONS, 1);
      } else {
          values.put(DownloadManager.Impl.COLUMN_FAILED_CONNECTIONS, mInfo.mNumFailed + 1);
      }

      mContext.getContentResolver().update(mInfo.getMyDownloadsUri(), values, null, null);
  }

  /**
   * Open a stream for the HTTP response entity, handling I/O errors.
   * @return an InputStream to read the response entity
   */
  private InputStream openResponseEntity(State state, HttpResponse response)
          throws StopRequest {
      try {
          return response.getEntity().getContent();
      } catch (IOException ex) {
          logNetworkState();
          throw new StopRequest(getFinalStatusForHttpError(state),
                  "while getting entity: " + ex.toString(), ex);
      }
  }
  
  /**
   * Read HTTP response headers and take appropriate action, including setting up the destination
   * file and updating the database.
   */
  private void processResponseHeaders(State state, InnerState innerState, HttpResponse response)
          throws StopRequest {
      if (innerState.mContinuingDownload) {
          // ignore response headers on resume requests
          return;
      }

      readResponseHeaders(state, innerState, response);

      try {
          state.mFilename = Helper.generateSaveFile(
                  mContext,
                  mInfo.mUri,
                  mInfo.mHint,
                  innerState.mHeaderContentLocation,
                  state.mMimeType,
                  mInfo.mDestination,
                  mInfo.mTotalBytes,
                  mInfo.mSource);
      } catch (Helper.GenerateSaveFileError exc) {
          throw new StopRequest(exc.mStatus, exc.mMessage);
      }
      try {
          state.mStream = new FileOutputStream(state.mFilename);
      } catch (FileNotFoundException exc) {
          throw new StopRequest(DownloadManager.Impl.STATUS_FILE_ERROR,
                  "while opening destination file: " + exc.toString(), exc);
      }
 //     if (Constants.LOGV) {
    	  Utils.D("writing " + mInfo.mUri + " to " + state.mFilename);
//      }

      updateDatabaseFromHeaders(state, innerState);
      // check connectivity again now that we know the total size
      checkConnectivity(state);
  }

  /**
   * Read some data from the HTTP response stream, handling I/O errors.
   * @param data buffer to use to read data
   * @param entityStream stream for reading the HTTP response entity
   * @return the number of bytes actually read or -1 if the end of the stream has been reached
   */
  private int readFromResponse(State state, InnerState innerState, byte[] data,
                               InputStream entityStream) throws StopRequest {
      try {
          return entityStream.read(data);
      } catch (IOException ex) {
          logNetworkState();
          ContentValues values = new ContentValues();
          values.put(DownloadManager.Impl.COLUMN_CURRENT_BYTES, innerState.mBytesSoFar);
          mContext.getContentResolver().update(mInfo.getMyDownloadsUri(), values, null, null);
          if (cannotResume(innerState)) {
              String message = "while reading response: " + ex.toString()
              + ", can't resume interrupted download with no ETag";
              throw new StopRequest(DownloadManager.Impl.STATUS_CANNOT_RESUME,
                      message, ex);
          } else {
              throw new StopRequest(getFinalStatusForHttpError(state),
                      "while reading response: " + ex.toString(), ex);
          }
      }
  }
  
  /**
   * Read headers from the HTTP response and store them into local state.
   */
  private void readResponseHeaders(State state, InnerState innerState, HttpResponse response)
          throws StopRequest {
      Header header = null;
//      header = response.getFirstHeader("Content-Disposition");
//      if (header != null) {
//          innerState.mHeaderContentDisposition = header.getValue();
//      }
      header = response.getFirstHeader("Content-Location");
      if (header != null) {
          innerState.mHeaderContentLocation = header.getValue();
      }
      if (state.mMimeType == null) {
          header = response.getFirstHeader("Content-Type");
          if (header != null) {
              state.mMimeType = sanitizeMimeType(header.getValue());
          }
      }
      header = response.getFirstHeader("ETag");
 //     header = response.getFirstHeader("Last-Modified");
      if (header != null) {
          innerState.mHeaderETag = header.getValue();
      }
      String headerTransferEncoding = null;
      header = response.getFirstHeader("Transfer-Encoding");
      if (header != null) {
          headerTransferEncoding = header.getValue();
      }
      if (headerTransferEncoding == null) {
          header = response.getFirstHeader("Content-Length");
          if (header != null) {
              innerState.mHeaderContentLength = header.getValue();
              mInfo.mTotalBytes = Long.parseLong(innerState.mHeaderContentLength);
          }
      } else {
          // Ignore content-length with transfer-encoding - 2616 4.4 3
//          if (Constants.LOGVV) {
        	  Utils.D("ignoring content-length because of xfer-encoding");
 //         }
      }
 //     if (Constants.LOGVV) {
 //         Utils.D("Content-Disposition: " +
 //                 innerState.mHeaderContentDisposition);
          Utils.D("Content-Length: " + innerState.mHeaderContentLength);
          Utils.D("Content-Location: " + innerState.mHeaderContentLocation);
          Utils.D("Content-Type: " + state.mMimeType);
          Utils.D("ETag: " + innerState.mHeaderETag);
          Utils.D("Transfer-Encoding: " + headerTransferEncoding);
          Utils.D("total-bytes: " + mInfo.mTotalBytes);
//      }

      boolean noSizeInfo = innerState.mHeaderContentLength == null
              && (headerTransferEncoding == null
                  || !headerTransferEncoding.equalsIgnoreCase("chunked"));
      if (noSizeInfo) {
          throw new StopRequest(DownloadManager.Impl.STATUS_HTTP_DATA_ERROR,
                  "can't know size of download, giving up");
      }
  }
  
//	private void readResponseHeaders(State state, InnerState innerstate,
//			HttpResponse httpresponse) throws StopRequest {
//		Header header = httpresponse.getFirstHeader("Content-Location");
//		if (header != null)
//			innerstate.mHeaderContentLocation = header.getValue();
//		if (state.mMimeType == null) {
//			Header header4 = httpresponse.getFirstHeader("Content-Type");
//			if (header4 != null)
//				state.mMimeType = sanitizeMimeType(header4.getValue());
//		}
//		Header header1 = httpresponse.getFirstHeader("Last-Modified");
//		if (header1 != null)
//			innerstate.mHeaderETag = header1.getValue();
//		String s = null;
//		Header header2 = httpresponse.getFirstHeader("Transfer-Encoding");
//		if (header2 != null)
//			s = header2.getValue();
//		boolean flag;
//		if (s == null) {
//			Header header3 = httpresponse.getFirstHeader("Content-Length");
//			if (header3 != null) {
//				innerstate.mHeaderContentLength = header3.getValue();
//				mInfo.mTotalBytes = Long
//						.parseLong(innerstate.mHeaderContentLength);
//			}
//		} else {
//			Utils.D("ignoring content-length because of xfer-encoding");
//		}
//		Utils.D((new StringBuilder()).append("Content-Length: ")
//				.append(innerstate.mHeaderContentLength).toString());
//		Utils.D((new StringBuilder()).append("Content-Location: ")
//				.append(innerstate.mHeaderContentLocation).toString());
//		Utils.D((new StringBuilder()).append("Content-Type: ")
//				.append(state.mMimeType).toString());
//		Utils.D((new StringBuilder()).append("ETag: ")
//				.append(innerstate.mHeaderETag).toString());
//		Utils.D((new StringBuilder()).append("Transfer-Encoding: ").append(s)
//				.toString());
//		Utils.D((new StringBuilder()).append("total-bytes: ")
//				.append(mInfo.mTotalBytes).toString());
//		if (innerstate.mHeaderContentLength == null
//				&& (s == null || !s.equalsIgnoreCase("chunked")))
//			flag = true;
//		else
//			flag = false;
//		if (flag)
//			throw new StopRequest(495, "can't know size of download, giving up");
//	}

  /**
   * Report download progress through the database if necessary.
   */
  private void reportProgress(State state, InnerState innerState) {
      long now = System.currentTimeMillis();
      if (innerState.mBytesSoFar - innerState.mBytesNotified
                      > Constants.MIN_PROGRESS_STEP
              && now - innerState.mTimeLastNotification
                      > Constants.MIN_PROGRESS_TIME) {
          ContentValues values = new ContentValues();
          values.put(DownloadManager.Impl.COLUMN_CURRENT_BYTES, innerState.mBytesSoFar);
          mContext.getContentResolver().update(mInfo.getMyDownloadsUri(), values, null, null);
          innerState.mBytesNotified = innerState.mBytesSoFar;
          innerState.mTimeLastNotification = now;
      }
  }
  
  /**
   * Clean up a mimeType string so it can be used to dispatch an intent to
   * view a downloaded asset.
   * @param mimeType either null or one or more mime types (semi colon separated).
   * @return null if mimeType was null. Otherwise a string which represents a
   * single mimetype in lowercase and with surrounding whitespaces trimmed.
   */
  private static String sanitizeMimeType(String mimeType) {
      try {
          mimeType = mimeType.trim().toLowerCase(Locale.ENGLISH);

          final int semicolonIndex = mimeType.indexOf(';');
          if (semicolonIndex != -1) {
              mimeType = mimeType.substring(0, semicolonIndex);
          }
          return mimeType;
      } catch (NullPointerException npe) {
          return null;
      }
  }
  
  /**
   * Send the request to the server, handling any I/O exceptions.
   */
  private HttpResponse sendRequest(State state, AndroidHttpClient client, HttpGet request)
          throws StopRequest {
      try {
          return client.execute(request);
      } catch (IllegalArgumentException ex) {
          throw new StopRequest(DownloadManager.Impl.STATUS_HTTP_DATA_ERROR,
                  "while trying to execute request: " + ex.toString(), ex);
      } catch (IOException ex) {
          logNetworkState();
          throw new StopRequest(getFinalStatusForHttpError(state),
                  "while trying to execute request: " + ex.toString(), ex);
      }
  }
  
  /**
   * Prepare the destination file to receive data.  If the file already exists, we'll set up
   * appropriately for resumption.
   */
  private void setupDestinationFile(State state, InnerState innerState)
          throws StopRequest {
      if (!TextUtils.isEmpty(state.mFilename)) { // only true if we've already run a thread for this download
          if (!Helper.isFilenameValid(state.mFilename, state.mSourceType)) {
              // this should never happen
              throw new StopRequest(DownloadManager.Impl.STATUS_FILE_ERROR,
                      "found invalid internal destination filename");
          }
          // We're resuming a download that got interrupted
          File f = new File(state.mFilename);
          if (f.exists()) {
              long fileLength = f.length();
              if (fileLength == 0) {
                  // The download hadn't actually started, we can restart from scratch
                  f.delete();
                  state.mFilename = null;
              } else if (mInfo.mETag == null) {
                  // This should've been caught upon failure
                  f.delete();
                  throw new StopRequest(DownloadManager.Impl.STATUS_CANNOT_RESUME,
                          "Trying to resume a download that can't be resumed");
              } else {
                  // All right, we'll be able to resume this download
                  try {
                      state.mStream = new FileOutputStream(state.mFilename, true);
                      FileInputStream fileinputstream = new FileInputStream(
								state.mFilename);
						DigestInputStream digestinputstream = new DigestInputStream(
								fileinputstream, state.mDigester);
						byte abyte0[] = new byte[8192]; 
						try {
							while(digestinputstream.read(abyte0) != -1);
							digestinputstream.close();
							fileinputstream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							throw new StopRequest(DownloadManager.Impl.STATUS_FILE_ERROR,
									"while opening destination for resuming: " + e.toString(), e);
						}					
                  } catch (FileNotFoundException exc) {
                      throw new StopRequest(DownloadManager.Impl.STATUS_FILE_ERROR,
                              "while opening destination for resuming: " + exc.toString(), exc);
                  }
                  innerState.mBytesSoFar = (int) fileLength;
                  if (mInfo.mTotalBytes != -1) {
                      innerState.mHeaderContentLength = Long.toString(mInfo.mTotalBytes);
                  }
                  innerState.mHeaderETag = mInfo.mETag;
                  innerState.mContinuingDownload = true;
              }
          }
      }

      if (state.mStream != null && mInfo.mDestination == DownloadManager.Impl.DESTINATION_EXTERNAL) {
          closeDestination(state);
      }
  }
  
  /**
   * Sync the destination file to storage.
   */
  private void syncDestination(State state) {
      FileOutputStream downloadedFileStream = null;
      try {
          downloadedFileStream = new FileOutputStream(state.mFilename, true);
          downloadedFileStream.getFD().sync();
      } catch (FileNotFoundException ex) {
          Utils.W("file " + state.mFilename + " not found: " + ex);
      } catch (SyncFailedException ex) {
          Utils.W("file " + state.mFilename + " sync failed: " + ex);
      } catch (IOException ex) {
          Utils.W("IOException trying to sync " + state.mFilename + ": " + ex);
      } catch (RuntimeException ex) {
          Utils.W("exception while syncing file: ", ex);
      } finally {
          if(downloadedFileStream != null) {
              try {
                  downloadedFileStream.close();
              } catch (IOException ex) {
                  Utils.W("IOException while closing synced file: ", ex);
              } catch (RuntimeException ex) {
                  Utils.W("exception while closing file: ", ex);
              }
          }
      }
  }

  /**
   * Transfer as much data as possible from the HTTP response to the destination file.
   * @param data buffer to use to read data
   * @param entityStream stream for reading the HTTP response entity
   */
  private void transferData(State state, InnerState innerState, byte[] data,
                               InputStream entityStream) throws StopRequest {
      for (;;) {
          int bytesRead = readFromResponse(state, innerState, data, entityStream);
          if (bytesRead == -1) { // success, end of stream already reached
              handleEndOfStream(state, innerState);
              return;
          }

          state.mGotData = true;
          writeDataToDestination(state, data, bytesRead);
          innerState.mBytesSoFar += bytesRead;
          reportProgress(state, innerState);

          Utils.V("downloaded " + innerState.mBytesSoFar + " for "
                    + mInfo.mUri);

          checkPausedOrCanceled(state);
      }
  }
  
  /**
   * Update necessary database fields based on values of HTTP response headers that have been
   * read.
   */
  private void updateDatabaseFromHeaders(State state, InnerState innerState) {
      ContentValues values = new ContentValues();
      values.put(DownloadManager.Impl.COLUMN_DATA, state.mFilename);
      if (innerState.mHeaderETag != null) {
          values.put(DownloadManager.Impl.COLUMN_ETAG, innerState.mHeaderETag);
      }
      if (state.mMimeType != null) {
          values.put(DownloadManager.Impl.COLUMN_MIME_TYPE, state.mMimeType);
      }
      values.put(DownloadManager.Impl.COLUMN_TOTAL_BYTES, innerState.mHeaderContentLength);
      Utils.D("update the header : " + this.mInfo.mPackageName + " values " + values);
      mContext.getContentResolver().update(mInfo.getMyDownloadsUri(), values, null, null);
  }
 
  private void updatePcakageName()
  {
    if ((this.mInfo.mSource == 1) || (this.mInfo.mSource == 2))
    {
      String str = Utils.getPackageName(this.mContext, this.mInfo.mFileName);
      if (!TextUtils.isEmpty(str))
      {
        ContentValues values = new ContentValues();
        values.put("package_name", str);
        this.mContext.getContentResolver().update(this.mInfo.getMyDownloadsUri(), values, null, null);
      }
    }
  }

  private void writeDataToDestination(State state, byte[] paramArrayOfByte, int paramInt)
    throws DownloadThread.StopRequest
  {
    try
    {
      if (state.mStream == null)
        state.mStream = new FileOutputStream(state.mFilename, true);
      state.mStream.write(paramArrayOfByte, 0, paramInt);
      if (this.mInfo.mDestination == 0)
        closeDestination(state);
      return;
    }
    catch (IOException ex)
    {
      throw new StopRequest(DownloadManager.Impl.STATUS_FILE_ERROR,
    		  "while writing destination file: " + ex.toString(), ex);
    }
  }

  /**
   * Executes the download in a separate thread
   */
  public void run() {
      Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

      State state = new State(mInfo);
      AndroidHttpClient client = null;
      PowerManager.WakeLock wakeLock = null;
      int finalStatus = DownloadManager.Impl.STATUS_UNKNOWN_ERROR;

      try {
          PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
          wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Utils.sLogTag);
          wakeLock.acquire();

 //         if (Constants.LOGV) {
        	  Utils.D("initiating download for " + mInfo.mUri);
//          }

          client = HttpClientFactory.get().getHttpClient();

          boolean finished = false;
          while(!finished) {
              Utils.D("Initiating request for download " + mInfo.mId);
              // Set or unset proxy, which may have changed since last GET request.
              // setDefaultProxy() supports null as proxy parameter.
//              ConnRouteParams.setDefaultProxy(client.getParams(),
//                      Proxy.getPreferredHttpHost(mContext, state.mRequestUri));
              HttpGet request = new HttpGet(state.mRequestUri);
              try {
                  executeDownload(state, client, request);
                  finished = true;
              } catch (RetryDownload exc) {
                  // fall through
              } finally {
                  request.abort();
                  request = null;
              }
          }

//          if (Constants.LOGV) {
              Utils.D("download completed for " + mInfo.mUri);
 //         }
          finalizeDestinationFile(state);
          finalStatus = DownloadManager.Impl.STATUS_SUCCESS;
      } catch (StopRequest error) {
          // remove the cause before printing, in case it contains PII
    	  Utils.D("Aborting request for download " + mInfo.mId + ": " + error.getMessage());
          finalStatus = error.mFinalStatus;
          // fall through to finally block
      } catch (Throwable ex) { //sometimes the socket code throws unchecked exceptions
          Utils.D("Exception for id " + mInfo.mId + ": " + ex);
          finalStatus = DownloadManager.Impl.STATUS_UNKNOWN_ERROR;
          // falls through to the code that reports an error
      } finally {
          if (wakeLock != null) {
              wakeLock.release();
              wakeLock = null;
          }
//          if (client != null) { // by xiaohf 这里不需要关闭，因为UI业务部分需要用到
//              client.close();
//              client = null;
//          }
          cleanupDestination(state, finalStatus);
          notifyDownloadCompleted(finalStatus, state.mCountRetry, state.mRetryAfter,
        		  state.mRedirectCount,state.mGotData, 
        		  state.mFilename,state.mNewUri, state.mMimeType);
        if(finalStatus == DownloadManager.Impl.STATUS_SUCCESS) {
        updatePcakageName();
        if(Constants.MIMETYPE_APK.equals(mInfo.mMimeType))
      	  Utils.submitDownloadLog(mContext, 1, mInfo.mSource, mInfo.mUri, "");
    }
    else {
      }
          mInfo.mHasActiveThread = false;
      }
  }
//  
//  public void run()
//  {
//	  State state;
//      Process.setThreadPriority(10);
//      state = new State(mInfo);
//      android.os.PowerManager.WakeLock wakelock = ((PowerManager)mContext.getSystemService("power")).newWakeLock(1, Utils.sLogTag);
//      AndroidHttpClient androidhttpclient = HttpClientFactory.get().getHttpClient();
//      wakelock.acquire();
//      Utils.D((new StringBuilder()).append("initiating download for ").append(mInfo.mUri).toString());
//      boolean flag = false;
//      
//      HttpGet httpget;
//      Utils.D((new StringBuilder()).append("Initiating request for download ").append(mInfo.mId).append(" url ").append(mInfo.mUri).toString());
//      httpget = new HttpGet(state.mRequestUri);
//      int i = 0;
//      try {
//		executeDownload(state, androidhttpclient, httpget);
//	} catch (StopRequest e) {	
//	      Utils.W((new StringBuilder()).append("Aborting request for download ").append(mInfo.mId).append(" url: ").append(mInfo.mUri).append(" : ").append(e.getMessage()).toString());
//	      i = e.mFinalStatus;
//	} catch (RetryDownload e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//      
//      httpget.abort();
//      flag = true;
//      
//      Utils.D((new StringBuilder()).append("download completed for ").append(mInfo.mUri).toString());
////      if(!checkFile(state))
////          throw new Throwable("File MD5 code is not the same as server");
//      
//      if(wakelock != null) {
//          wakelock.release();
//          wakelock = null;
//      }
//      if(androidhttpclient != null)
//    	  androidhttpclient = null;
//      notifyDownloadCompleted(i, state.mCountRetry, state.mRetryAfter, state.mRedirectCount, state.mGotData, state.mFilename, state.mNewUri, state.mMimeType);
//
//      if(i == 200) {
//          updatePcakageName();
//          if(Constants.MIMETYPE_APK.equals(mInfo.mMimeType))
//        	  Utils.submitDownloadLog(mContext, 1, mInfo.mSource, mInfo.mUri, "");
//      }
//      else {
//        }
//      
//      mInfo.mHasActiveThread = false;
//      
//  }

  private static class InnerState
  {
    public int mBytesNotified = 0;
    public int mBytesSoFar = 0;
    public boolean mContinuingDownload = false;
    public String mHeaderContentLength;
    public String mHeaderContentLocation;
    public String mHeaderETag;
    public String mLastModified;
    public long mTimeLastNotification = 0L;
  }

  private class RetryDownload extends Throwable
  {
    private static final long serialVersionUID = 1L;

    private RetryDownload()
    {
    }
  }

  private static class State
  {
    public boolean mCountRetry = false;
    public MessageDigest mDigester;
    public String mFilename;
    public boolean mGotData = false;
    public String mMimeType;
    public String mNewUri;
    public int mRedirectCount = 0;
    public String mRequestUri;
    public int mRetryAfter = 0;
    public int mSourceType = -1;
    public FileOutputStream mStream;

    public State(DownloadInfo info)
    {
      this.mMimeType = DownloadThread.sanitizeMimeType(info.mMimeType);
      this.mRedirectCount = info.mRedirectCount;
      this.mRequestUri = info.mUri;
      this.mFilename = info.mFileName;
      this.mSourceType = info.mSource;
      try
      {
        this.mDigester = MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
          Utils.D("no algorithm for md5");
      }
    }
  }

  /**
   * Raised from methods called by run() to indicate that the current request should be stopped
   * immediately.
   *
   * Note the message passed to this exception will be logged and therefore must be guaranteed
   * not to contain any PII, meaning it generally can't include any information about the request
   * URI, headers, or destination filename.
   */
  private class StopRequest extends Throwable
  {
    private static final long serialVersionUID = 6338592678988347973L;
    public int mFinalStatus;

    public StopRequest(int finalStatus, String message) {
        super(message);
        mFinalStatus = finalStatus;
    }

    public StopRequest(int finalStatus, String message, Throwable throwable) {
        super(message, throwable);
        mFinalStatus = finalStatus;
    }
  }
}