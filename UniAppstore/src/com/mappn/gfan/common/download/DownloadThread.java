package com.mappn.gfan.common.download;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import com.mappn.gfan.common.AndroidHttpClient;
import com.mappn.gfan.common.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

public class DownloadThread extends Thread
{
  private Context mContext;
  private DownloadInfo mInfo;

  public DownloadThread(Context paramContext, DownloadInfo paramDownloadInfo)
  {
    this.mContext = paramContext;
    this.mInfo = paramDownloadInfo;
  }

  private void addRequestHeaders(InnerState paramInnerState, HttpGet paramHttpGet)
  {
    if (paramInnerState.mContinuingDownload)
    {
      if (paramInnerState.mLastModified != null)
        paramHttpGet.addHeader("If-Range", paramInnerState.mLastModified);
      paramHttpGet.addHeader("Range", "bytes=" + paramInnerState.mBytesSoFar + "-");
    }
  }

  private boolean cannotResume(InnerState paramInnerState)
  {
    if ((paramInnerState.mBytesSoFar > 0) && (paramInnerState.mHeaderETag == null));
    for (boolean bool = true; ; bool = false)
      return bool;
  }

  private void checkConnectivity(State paramState)
    throws DownloadThread.StopRequest
  {
    int i = this.mInfo.checkCanUseNetwork();
    if (i != 1)
      throw new StopRequest(195, this.mInfo.getLogMessageForNetworkError(i));
  }

  private boolean checkFile(State state)
  {
      String s = mInfo.mMD5;
      boolean flag;
      if(TextUtils.isEmpty(s))
          flag = true;
      else
      if(s.equalsIgnoreCase(convertToHex(state.mDigester.digest())))
          flag = true;
      else
          flag = false;
      return flag;
  }

  private void checkPausedOrCanceled(State state)
	        throws StopRequest
	    {
	        DownloadInfo downloadinfo = mInfo;
	        if(mInfo.mControl == 1)
	            throw new StopRequest(193, "download paused by owner");

	        if(mInfo.mControl == 2)
	            throw new StopRequest(196, "download is in pending status");

	        if(mInfo.mStatus == 490)
	        {
	            if("application/vnd.android.package-archive".equals(mInfo.mMimeType))
	                Utils.submitDownloadLog(mContext, 2, mInfo.mSource, mInfo.mUri, "");
	            throw new StopRequest(490, "download canceled");
	        } else
	        {
	            return;
	        }
	    }

  private void cleanupDestination(State paramState, int paramInt)
  {
    closeDestination(paramState);
    if ((paramState.mFilename != null) && (DownloadManager.Impl.isStatusError(paramInt)))
    {
      new File(paramState.mFilename).delete();
      paramState.mFilename = null;
    }
  }

  private void closeDestination(State paramState)
  {
    try
    {
      if (paramState.mStream != null)
      {
        paramState.mStream.close();
        paramState.mStream = null;
      }
      return;
    }
    catch (IOException localIOException)
    {
      while (true)
        Utils.D("exception when closing the file after download : " + localIOException);
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

  private void executeDownload(State paramState, AndroidHttpClient paramAndroidHttpClient, HttpGet paramHttpGet)
    throws DownloadThread.StopRequest, DownloadThread.RetryDownload
  {
    InnerState localInnerState = new InnerState();
    byte[] arrayOfByte = new byte[4096];
    setupDestinationFile(paramState, localInnerState);
    addRequestHeaders(localInnerState, paramHttpGet);
    checkConnectivity(paramState);
    HttpResponse localHttpResponse = sendRequest(paramState, paramAndroidHttpClient, paramHttpGet);
    handleExceptionalStatus(paramState, localInnerState, localHttpResponse);
    Utils.D("received response for " + this.mInfo.mUri);
    processResponseHeaders(paramState, localInnerState, localHttpResponse);
    transferData(paramState, localInnerState, arrayOfByte, new DigestInputStream(openResponseEntity(paramState, localHttpResponse), paramState.mDigester));
  }

  private void finalizeDestinationFile(State paramState)
    throws DownloadThread.StopRequest
  {
    syncDestination(paramState);
  }
  
	private int getFinalStatusForHttpError(State paramState) {
		int i = 0;
		if (!Helper.isNetworkAvailable(this.mContext))
			i = 195;

		else if (this.mInfo.mNumFailed < 5) {
			paramState.mCountRetry = true;
			i = 194;
		} else {
			Utils.D("reached max retries for " + this.mInfo.mId);
			i = 495;
		}

		return i;
	}

	private void handleEndOfStream(State paramState, InnerState paramInnerState)
			throws DownloadThread.StopRequest {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("current_bytes",
				Integer.valueOf(paramInnerState.mBytesSoFar));
		if (paramInnerState.mHeaderContentLength == null)
			localContentValues.put("total_bytes",
					Integer.valueOf(paramInnerState.mBytesSoFar));
		this.mContext.getContentResolver().update(
				this.mInfo.getMyDownloadsUri(), localContentValues, null, null);
		if ((paramInnerState.mHeaderContentLength != null)
				&& (paramInnerState.mBytesSoFar != Integer
						.parseInt(paramInnerState.mHeaderContentLength))) {
			if (cannotResume(paramInnerState)) {
				throw new StopRequest(489, "mismatched content length");
			} else {
				throw new StopRequest(getFinalStatusForHttpError(paramState),
						"closed socket before end of file");
			}
		}
	}

	private void handleExceptionalStatus(State state, InnerState innerState, HttpResponse response)
            throws StopRequest, RetryDownload {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 503 && mInfo.mNumFailed < Constants.MAX_RETRIES) {
            handleServiceUnavailable(state, response);
        }
        if (statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307) {
            handleRedirect(state, response, statusCode);
        }
        
        int expectedStatus = innerState.mContinuingDownload ? 206 : 200;
        if (statusCode != expectedStatus) {
            handleOtherStatus(state, innerState, statusCode);
        } else {
        	// no longer redirected
        	state.mRedirectCount = 0;
        }
    }
	
	private void handleOtherStatus(State paramState,
			InnerState paramInnerState, int paramInt)
			throws DownloadThread.StopRequest {
		int i;
		if (DownloadManager.Impl.isStatusError(paramInt)) {
			i = paramInt;
		} else if ((paramInt >= 300) && (paramInt < 400)) {
			i = 493;
		} else if ((paramInnerState.mContinuingDownload) && (paramInt == 200)) {
			i = 489;
		} else {
			i = 494;
		}
		Utils.D("throw new stop request ----> " + i + " statusCode " + paramInt
				+ " isContinuing " + paramInnerState.mContinuingDownload
				+ " fileName " + paramState.mFilename);
		throw new StopRequest(i, "http error " + paramInt);
	}

	private void handleRedirect(State paramState,
			HttpResponse paramHttpResponse, int paramInt)
			throws DownloadThread.StopRequest, DownloadThread.RetryDownload {
		Utils.D("got HTTP redirect " + paramInt);
		if (paramState.mRedirectCount >= 5)
			throw new StopRequest(497, "too many redirects");
		Header localHeader = paramHttpResponse.getFirstHeader("Location");
		if (localHeader == null)
			return;
		Utils.D("Location :" + localHeader.getValue());
		try {
			String str = new URI(this.mInfo.mUri).resolve(
					new URI(localHeader.getValue())).toString();
			paramState.mRedirectCount++;
			paramState.mRequestUri = str;
			if ((paramInt == 301) || (paramInt == 303))
				paramState.mNewUri = str;

		} catch (URISyntaxException localURISyntaxException) {
			Utils.D("Couldn't resolve redirect URI " + localHeader.getValue()
					+ " for " + this.mInfo.mUri);
			throw new StopRequest(495, "Couldn't resolve redirect URI");
		}
		throw new RetryDownload();
	}

	private void handleServiceUnavailable(State paramState,
			HttpResponse paramHttpResponse) throws DownloadThread.StopRequest {
		Utils.D("got HTTP response code 503");
		paramState.mCountRetry = true;
		Header localHeader = paramHttpResponse.getFirstHeader("Retry-After");
		if (localHeader != null) {
			try {
				Utils.D("Retry-After :" + localHeader.getValue());
				paramState.mRetryAfter = Integer.parseInt(localHeader
						.getValue());
				if (paramState.mRetryAfter < 0) {
					paramState.mRetryAfter = 0;
				} else if (paramState.mRetryAfter < 30) {
					paramState.mRetryAfter = 30;
				} else if (paramState.mRetryAfter > 86400) {
					paramState.mRetryAfter = 86400;
				}

				paramState.mRetryAfter += Helper.rnd.nextInt(31);
				paramState.mRetryAfter = (1000 * paramState.mRetryAfter);
			} catch (NumberFormatException localNumberFormatException) {
			}
		}
		throw new StopRequest(194,
				"got 503 Service Unavailable, will retry later");
	}

  private void logNetworkState()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Net ");
    if (Helper.isNetworkAvailable(this.mContext));
    for (String str = "Up"; ; str = "Down")
    {
      Utils.I(str);
      return;
    }
  }

  private void notifyDownloadCompleted(int paramInt1, boolean paramBoolean1, int paramInt2, int paramInt3, boolean paramBoolean2, String paramString1, String paramString2, String paramString3)
  {
    notifyThroughDatabase(paramInt1, paramBoolean1, paramInt2, paramInt3, paramBoolean2, paramString1, paramString2, paramString3);
  }

	private void notifyThroughDatabase(int status, boolean countRetry,
			int retryAfter, int redirectCount, boolean gotData,
			String paramString1, String paramString2, String paramString3) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("status", Integer.valueOf(status));
		localContentValues.put("_data", paramString1);
		if (paramString2 != null)
			localContentValues.put("uri", paramString2);
		localContentValues.put("mimetype", paramString3);
		localContentValues.put("lastmod",
				Long.valueOf(System.currentTimeMillis()));
		localContentValues.put("redirectcount",
				Integer.valueOf(retryAfter + (redirectCount << 28)));
		if (!countRetry) {
			localContentValues.put("numfailed", Integer.valueOf(0));
		} else if (gotData) {
			localContentValues.put("numfailed", Integer.valueOf(1));
		} else {
			localContentValues.put("numfailed",
					Integer.valueOf(1 + this.mInfo.mNumFailed));
		}
		this.mContext.getContentResolver().update(
				this.mInfo.getMyDownloadsUri(), localContentValues, null, null);
	}

  private InputStream openResponseEntity(State paramState, HttpResponse paramHttpResponse)
    throws DownloadThread.StopRequest
  {
    try
    {
      InputStream localInputStream = paramHttpResponse.getEntity().getContent();
      return localInputStream;
    }
    catch (IOException localIOException)
    {
      logNetworkState();
      throw new StopRequest(getFinalStatusForHttpError(paramState), "while getting entity: " + localIOException.toString(), localIOException);
    }
  }

  private void processResponseHeaders(State state, InnerState innerstate, HttpResponse httpresponse)
	        throws StopRequest
	    {
	        if(!innerstate.mContinuingDownload)
	        {
	            readResponseHeaders(state, innerstate, httpresponse);
	            try
	            {
	                state.mFilename = Helper.generateSaveFile(mContext, mInfo.mUri, mInfo.mHint, innerstate.mHeaderContentLocation, state.mMimeType, mInfo.mDestination, mInfo.mTotalBytes, mInfo.mSource);
	            }
	            catch(Helper.GenerateSaveFileError generatesavefileerror)
	            {
	                throw new StopRequest(generatesavefileerror.mStatus, generatesavefileerror.mMessage);
	            }
	            try
	            {
	                state.mStream = new FileOutputStream(state.mFilename);
	            }
	            catch(FileNotFoundException filenotfoundexception)
	            {
	                throw new StopRequest(492, (new StringBuilder()).append("while opening destination file: ").append(filenotfoundexception.toString()).toString(), filenotfoundexception);
	            }
	            Utils.D((new StringBuilder()).append("writing ").append(mInfo.mUri).append(" to ").append(state.mFilename).toString());
	            Utils.D((new StringBuilder()).append("totalbytes ").append(mInfo.mTotalBytes).toString());
	            updateDatabaseFromHeaders(state, innerstate);
	            checkConnectivity(state);
	        }
	    }

  private int readFromResponse(State paramState, InnerState paramInnerState, byte[] paramArrayOfByte, InputStream paramInputStream)
    throws DownloadThread.StopRequest
  {
    try
    {
      int i = paramInputStream.read(paramArrayOfByte);
      return i;
    }
    catch (IOException localIOException)
    {
      logNetworkState();
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("current_bytes", Integer.valueOf(paramInnerState.mBytesSoFar));
      this.mContext.getContentResolver().update(this.mInfo.getMyDownloadsUri(), localContentValues, null, null);
      if (cannotResume(paramInnerState))
        throw new StopRequest(489, "while reading response: " + localIOException.toString() + ", can't resume interrupted download with no ETag", localIOException);
      throw new StopRequest(getFinalStatusForHttpError(paramState), "while reading response: " + localIOException.toString(), localIOException);
    }
  }

	private void readResponseHeaders(State state, InnerState innerstate,
			HttpResponse httpresponse) throws StopRequest {
		Header header = httpresponse.getFirstHeader("Content-Location");
		if (header != null)
			innerstate.mHeaderContentLocation = header.getValue();
		if (state.mMimeType == null) {
			Header header4 = httpresponse.getFirstHeader("Content-Type");
			if (header4 != null)
				state.mMimeType = sanitizeMimeType(header4.getValue());
		}
		Header header1 = httpresponse.getFirstHeader("Last-Modified");
		if (header1 != null)
			innerstate.mHeaderETag = header1.getValue();
		String s = null;
		Header header2 = httpresponse.getFirstHeader("Transfer-Encoding");
		if (header2 != null)
			s = header2.getValue();
		boolean flag;
		if (s == null) {
			Header header3 = httpresponse.getFirstHeader("Content-Length");
			if (header3 != null) {
				innerstate.mHeaderContentLength = header3.getValue();
				mInfo.mTotalBytes = Long
						.parseLong(innerstate.mHeaderContentLength);
			}
		} else {
			Utils.D("ignoring content-length because of xfer-encoding");
		}
		Utils.D((new StringBuilder()).append("Content-Length: ")
				.append(innerstate.mHeaderContentLength).toString());
		Utils.D((new StringBuilder()).append("Content-Location: ")
				.append(innerstate.mHeaderContentLocation).toString());
		Utils.D((new StringBuilder()).append("Content-Type: ")
				.append(state.mMimeType).toString());
		Utils.D((new StringBuilder()).append("ETag: ")
				.append(innerstate.mHeaderETag).toString());
		Utils.D((new StringBuilder()).append("Transfer-Encoding: ").append(s)
				.toString());
		Utils.D((new StringBuilder()).append("total-bytes: ")
				.append(mInfo.mTotalBytes).toString());
		if (innerstate.mHeaderContentLength == null
				&& (s == null || !s.equalsIgnoreCase("chunked")))
			flag = true;
		else
			flag = false;
		if (flag)
			throw new StopRequest(495, "can't know size of download, giving up");
	}

  private void reportProgress(State paramState, InnerState paramInnerState)
  {
    long l = System.currentTimeMillis();
    if ((paramInnerState.mBytesSoFar - paramInnerState.mBytesNotified > 4096) && (l - paramInnerState.mTimeLastNotification > 1500L))
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("current_bytes", Integer.valueOf(paramInnerState.mBytesSoFar));
      this.mContext.getContentResolver().update(this.mInfo.getMyDownloadsUri(), localContentValues, null, null);
      paramInnerState.mBytesNotified = paramInnerState.mBytesSoFar;
      paramInnerState.mTimeLastNotification = l;
    }
  }
  
	private static String sanitizeMimeType(String paramString) {
		String localObject;
		try {
			localObject = paramString.trim().toLowerCase(Locale.ENGLISH);
			int i = ((String) localObject).indexOf(';');
			if (i != -1) {
				String str = ((String) localObject).substring(0, i);
				localObject = str;
			}

		} catch (NullPointerException localNullPointerException) {
			localObject = null;
		}
		return localObject;
	}

  private HttpResponse sendRequest(State paramState, AndroidHttpClient paramAndroidHttpClient, HttpGet paramHttpGet)
    throws DownloadThread.StopRequest
  {
    try
    {
      HttpResponse localHttpResponse = paramAndroidHttpClient.execute(paramHttpGet);
      return localHttpResponse;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new StopRequest(495, "while trying to execute request: " + localIllegalArgumentException.toString(), localIllegalArgumentException);
    }
    catch (IOException localIOException)
    {
      logNetworkState();
      throw new StopRequest(getFinalStatusForHttpError(paramState), "while trying to execute request: " + localIOException.toString(), localIOException);
    }
  }

	private void setupDestinationFile(State state, InnerState innerstate)
			throws StopRequest {
		if (!TextUtils.isEmpty(state.mFilename)) {
			if (!Helper.isFilenameValid(state.mFilename, state.mSourceType))
				throw new StopRequest(492,
						"found invalid internal destination filename");
			File file = new File(state.mFilename);
			if (file.exists()) {
				long l = file.length();
				if (l == 0L) {
					file.delete();
					state.mFilename = null;
				} else {
					if (mInfo.mETag == null) {
						file.delete();
						throw new StopRequest(489,
								"Trying to resume a download that can't be resumed");
					}
					try {
						state.mStream = new FileOutputStream(state.mFilename,
								true);
						FileInputStream fileinputstream = new FileInputStream(
								state.mFilename);
						DigestInputStream digestinputstream = new DigestInputStream(
								fileinputstream, state.mDigester);
						for (byte abyte0[] = new byte[8192]; digestinputstream
								.read(abyte0) != -1;)
							;
						digestinputstream.close();
						fileinputstream.close();
					} catch (FileNotFoundException filenotfoundexception) {
						throw new StopRequest(
								492,
								(new StringBuilder())
										.append("while opening destination for resuming: ")
										.append(filenotfoundexception
												.toString()).toString(),
								filenotfoundexception);
					} catch (IOException ioexception) {
						throw new StopRequest(
								492,
								(new StringBuilder())
										.append("while opening destination for resuming: ")
										.append(ioexception.toString())
										.toString(), ioexception);
					}
					innerstate.mBytesSoFar = (int) l;
					if (mInfo.mTotalBytes != -1L)
						innerstate.mHeaderContentLength = Long
								.toString(mInfo.mTotalBytes);
					innerstate.mHeaderETag = mInfo.mETag;
					innerstate.mContinuingDownload = true;
				}
			}
		}
		if (state.mStream != null && mInfo.mDestination == 0)
			closeDestination(state);
	}

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

  private void transferData(State paramState, InnerState paramInnerState, byte[] paramArrayOfByte, InputStream paramInputStream)
    throws DownloadThread.StopRequest
  {
    while (true)
    {
      int i = readFromResponse(paramState, paramInnerState, paramArrayOfByte, paramInputStream);
      if (i == -1)
      {
        handleEndOfStream(paramState, paramInnerState);
        return;
      }
      paramState.mGotData = true;
      writeDataToDestination(paramState, paramArrayOfByte, i);
      paramInnerState.mBytesSoFar = (i + paramInnerState.mBytesSoFar);
      reportProgress(paramState, paramInnerState);
      checkPausedOrCanceled(paramState);
    }
  }

  private void updateDatabaseFromHeaders(State paramState, InnerState paramInnerState)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("_data", paramState.mFilename);
    if (paramInnerState.mHeaderETag != null)
      localContentValues.put("etag", paramInnerState.mHeaderETag);
    if (paramState.mMimeType != null)
      localContentValues.put("mimetype", paramState.mMimeType);
    localContentValues.put("total_bytes", Long.valueOf(Long.parseLong(paramInnerState.mHeaderContentLength)));
    Utils.D("update the header : " + this.mInfo.mPackageName + " values " + localContentValues);
    this.mContext.getContentResolver().update(this.mInfo.getMyDownloadsUri(), localContentValues, null, null);
  }

  private void updatePcakageName()
  {
    if ((this.mInfo.mSource == 1) || (this.mInfo.mSource == 2))
    {
      String str = Utils.getPackageName(this.mContext, this.mInfo.mFileName);
      if (!TextUtils.isEmpty(str))
      {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("package_name", str);
        this.mContext.getContentResolver().update(this.mInfo.getMyDownloadsUri(), localContentValues, null, null);
      }
    }
  }

  private void writeDataToDestination(State paramState, byte[] paramArrayOfByte, int paramInt)
    throws DownloadThread.StopRequest
  {
    try
    {
      if (paramState.mStream == null)
        paramState.mStream = new FileOutputStream(paramState.mFilename, true);
      paramState.mStream.write(paramArrayOfByte, 0, paramInt);
      if (this.mInfo.mDestination == 0)
        closeDestination(paramState);
      return;
    }
    catch (IOException localIOException)
    {
      throw new StopRequest(492, "while writing destination file: " + localIOException.toString(), localIOException);
    }
  }

  // ERROR //
  public void run()
  {
    // Byte code:
    //   0: bipush 10
    //   2: invokestatic 739	android/os/Process:setThreadPriority	(I)V
    //   5: new 14	com/mappn/gfan/common/download/DownloadThread$State
    //   8: dup
    //   9: aload_0
    //   10: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   13: invokespecial 742	com/mappn/gfan/common/download/DownloadThread$State:<init>	(Lcom/mappn/gfan/common/download/DownloadInfo;)V
    //   16: astore_1
    //   17: aload_0
    //   18: getfield 29	com/mappn/gfan/common/download/DownloadThread:mContext	Landroid/content/Context;
    //   21: ldc_w 744
    //   24: invokevirtual 748	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   27: checkcast 750	android/os/PowerManager
    //   30: iconst_1
    //   31: getstatic 753	com/mappn/gfan/common/util/Utils:sLogTag	Ljava/lang/String;
    //   34: invokevirtual 757	android/os/PowerManager:newWakeLock	(ILjava/lang/String;)Landroid/os/PowerManager$WakeLock;
    //   37: astore 13
    //   39: aload 13
    //   41: astore 7
    //   43: aload 7
    //   45: invokevirtual 762	android/os/PowerManager$WakeLock:acquire	()V
    //   48: new 58	java/lang/StringBuilder
    //   51: dup
    //   52: invokespecial 59	java/lang/StringBuilder:<init>	()V
    //   55: ldc_w 764
    //   58: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: aload_0
    //   62: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   65: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   68: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   74: invokestatic 207	com/mappn/gfan/common/util/Utils:D	(Ljava/lang/String;)V
    //   77: invokestatic 770	com/mappn/gfan/common/HttpClientFactory:get	()Lcom/mappn/gfan/common/HttpClientFactory;
    //   80: invokevirtual 774	com/mappn/gfan/common/HttpClientFactory:getHttpClient	()Lcom/mappn/gfan/common/AndroidHttpClient;
    //   83: astore 19
    //   85: aload 19
    //   87: astore 8
    //   89: iconst_0
    //   90: istore 20
    //   92: iload 20
    //   94: ifne +293 -> 387
    //   97: new 58	java/lang/StringBuilder
    //   100: dup
    //   101: invokespecial 59	java/lang/StringBuilder:<init>	()V
    //   104: ldc_w 776
    //   107: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: aload_0
    //   111: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   114: getfield 273	com/mappn/gfan/common/download/DownloadInfo:mId	J
    //   117: invokevirtual 276	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   120: ldc_w 778
    //   123: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: aload_0
    //   127: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   130: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   133: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   139: invokestatic 207	com/mappn/gfan/common/util/Utils:D	(Ljava/lang/String;)V
    //   142: new 50	org/apache/http/client/methods/HttpGet
    //   145: dup
    //   146: aload_1
    //   147: getfield 394	com/mappn/gfan/common/download/DownloadThread$State:mRequestUri	Ljava/lang/String;
    //   150: invokespecial 779	org/apache/http/client/methods/HttpGet:<init>	(Ljava/lang/String;)V
    //   153: astore 24
    //   155: aload_0
    //   156: aload_1
    //   157: aload 8
    //   159: aload 24
    //   161: invokespecial 781	com/mappn/gfan/common/download/DownloadThread:executeDownload	(Lcom/mappn/gfan/common/download/DownloadThread$State;Lcom/mappn/gfan/common/AndroidHttpClient;Lorg/apache/http/client/methods/HttpGet;)V
    //   164: aload 24
    //   166: invokevirtual 784	org/apache/http/client/methods/HttpGet:abort	()V
    //   169: iconst_1
    //   170: istore 20
    //   172: goto -80 -> 92
    //   175: astore 26
    //   177: aload 24
    //   179: invokevirtual 784	org/apache/http/client/methods/HttpGet:abort	()V
    //   182: aload 26
    //   184: athrow
    //   185: astore 23
    //   187: aload 8
    //   189: astore 4
    //   191: aload 7
    //   193: astore_3
    //   194: aload 23
    //   196: astore_2
    //   197: new 58	java/lang/StringBuilder
    //   200: dup
    //   201: invokespecial 59	java/lang/StringBuilder:<init>	()V
    //   204: ldc_w 786
    //   207: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: aload_0
    //   211: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   214: getfield 273	com/mappn/gfan/common/download/DownloadInfo:mId	J
    //   217: invokevirtual 276	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   220: ldc_w 788
    //   223: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   226: aload_0
    //   227: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   230: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   233: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   236: ldc_w 790
    //   239: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   242: aload_2
    //   243: invokevirtual 793	com/mappn/gfan/common/download/DownloadThread$StopRequest:getMessage	()Ljava/lang/String;
    //   246: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   249: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   252: invokestatic 675	com/mappn/gfan/common/util/Utils:W	(Ljava/lang/String;)V
    //   255: aload_2
    //   256: getfield 796	com/mappn/gfan/common/download/DownloadThread$StopRequest:mFinalStatus	I
    //   259: istore 9
    //   261: aload_3
    //   262: ifnull +7 -> 269
    //   265: aload_3
    //   266: invokevirtual 799	android/os/PowerManager$WakeLock:release	()V
    //   269: aload 4
    //   271: ifnull +3 -> 274
    //   274: aload_0
    //   275: aload_1
    //   276: iload 9
    //   278: invokespecial 801	com/mappn/gfan/common/download/DownloadThread:cleanupDestination	(Lcom/mappn/gfan/common/download/DownloadThread$State;I)V
    //   281: aload_0
    //   282: iload 9
    //   284: aload_1
    //   285: getfield 267	com/mappn/gfan/common/download/DownloadThread$State:mCountRetry	Z
    //   288: aload_1
    //   289: getfield 417	com/mappn/gfan/common/download/DownloadThread$State:mRetryAfter	I
    //   292: aload_1
    //   293: getfield 368	com/mappn/gfan/common/download/DownloadThread$State:mRedirectCount	I
    //   296: aload_1
    //   297: getfield 697	com/mappn/gfan/common/download/DownloadThread$State:mGotData	Z
    //   300: aload_1
    //   301: getfield 173	com/mappn/gfan/common/download/DownloadThread$State:mFilename	Ljava/lang/String;
    //   304: aload_1
    //   305: getfield 397	com/mappn/gfan/common/download/DownloadThread$State:mNewUri	Ljava/lang/String;
    //   308: aload_1
    //   309: getfield 506	com/mappn/gfan/common/download/DownloadThread$State:mMimeType	Ljava/lang/String;
    //   312: invokespecial 803	com/mappn/gfan/common/download/DownloadThread:notifyDownloadCompleted	(IZIIZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   315: iload 9
    //   317: sipush 200
    //   320: if_icmpne +7 -> 327
    //   323: aload_0
    //   324: invokespecial 805	com/mappn/gfan/common/download/DownloadThread:updatePcakageName	()V
    //   327: ldc 142
    //   329: aload_0
    //   330: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   333: getfield 145	com/mappn/gfan/common/download/DownloadInfo:mMimeType	Ljava/lang/String;
    //   336: invokevirtual 149	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   339: ifeq +35 -> 374
    //   342: iload 9
    //   344: sipush 200
    //   347: if_icmpne +27 -> 374
    //   350: aload_0
    //   351: getfield 29	com/mappn/gfan/common/download/DownloadThread:mContext	Landroid/content/Context;
    //   354: iconst_1
    //   355: aload_0
    //   356: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   359: getfield 152	com/mappn/gfan/common/download/DownloadInfo:mSource	I
    //   362: aload_0
    //   363: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   366: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   369: ldc 157
    //   371: invokestatic 163	com/mappn/gfan/common/util/Utils:submitDownloadLog	(Landroid/content/Context;IILjava/lang/String;Ljava/lang/String;)V
    //   374: aload_0
    //   375: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   378: astore 10
    //   380: aload 10
    //   382: iconst_0
    //   383: putfield 808	com/mappn/gfan/common/download/DownloadInfo:mHasActiveThread	Z
    //   386: return
    //   387: new 58	java/lang/StringBuilder
    //   390: dup
    //   391: invokespecial 59	java/lang/StringBuilder:<init>	()V
    //   394: ldc_w 810
    //   397: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   400: aload_0
    //   401: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   404: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   407: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   410: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   413: invokestatic 207	com/mappn/gfan/common/util/Utils:D	(Ljava/lang/String;)V
    //   416: aload_0
    //   417: aload_1
    //   418: invokespecial 812	com/mappn/gfan/common/download/DownloadThread:checkFile	(Lcom/mappn/gfan/common/download/DownloadThread$State;)Z
    //   421: ifne +209 -> 630
    //   424: new 733	java/lang/Throwable
    //   427: dup
    //   428: ldc_w 814
    //   431: invokespecial 815	java/lang/Throwable:<init>	(Ljava/lang/String;)V
    //   434: athrow
    //   435: astore 22
    //   437: aload 8
    //   439: astore 4
    //   441: aload 7
    //   443: astore_3
    //   444: aload 22
    //   446: astore 11
    //   448: new 58	java/lang/StringBuilder
    //   451: dup
    //   452: invokespecial 59	java/lang/StringBuilder:<init>	()V
    //   455: ldc_w 817
    //   458: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   461: aload_0
    //   462: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   465: getfield 273	com/mappn/gfan/common/download/DownloadInfo:mId	J
    //   468: invokevirtual 276	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   471: ldc_w 788
    //   474: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   477: aload_0
    //   478: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   481: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   484: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   487: ldc_w 686
    //   490: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   493: aload 11
    //   495: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   498: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   501: invokestatic 675	com/mappn/gfan/common/util/Utils:W	(Ljava/lang/String;)V
    //   504: aload_3
    //   505: ifnull +7 -> 512
    //   508: aload_3
    //   509: invokevirtual 799	android/os/PowerManager$WakeLock:release	()V
    //   512: aload 4
    //   514: ifnull +3 -> 517
    //   517: aload_0
    //   518: aload_1
    //   519: sipush 491
    //   522: invokespecial 801	com/mappn/gfan/common/download/DownloadThread:cleanupDestination	(Lcom/mappn/gfan/common/download/DownloadThread$State;I)V
    //   525: aload_0
    //   526: sipush 491
    //   529: aload_1
    //   530: getfield 267	com/mappn/gfan/common/download/DownloadThread$State:mCountRetry	Z
    //   533: aload_1
    //   534: getfield 417	com/mappn/gfan/common/download/DownloadThread$State:mRetryAfter	I
    //   537: aload_1
    //   538: getfield 368	com/mappn/gfan/common/download/DownloadThread$State:mRedirectCount	I
    //   541: aload_1
    //   542: getfield 697	com/mappn/gfan/common/download/DownloadThread$State:mGotData	Z
    //   545: aload_1
    //   546: getfield 173	com/mappn/gfan/common/download/DownloadThread$State:mFilename	Ljava/lang/String;
    //   549: aload_1
    //   550: getfield 397	com/mappn/gfan/common/download/DownloadThread$State:mNewUri	Ljava/lang/String;
    //   553: aload_1
    //   554: getfield 506	com/mappn/gfan/common/download/DownloadThread$State:mMimeType	Ljava/lang/String;
    //   557: invokespecial 803	com/mappn/gfan/common/download/DownloadThread:notifyDownloadCompleted	(IZIIZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   560: sipush 491
    //   563: sipush 200
    //   566: if_icmpne +7 -> 573
    //   569: aload_0
    //   570: invokespecial 805	com/mappn/gfan/common/download/DownloadThread:updatePcakageName	()V
    //   573: ldc 142
    //   575: aload_0
    //   576: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   579: getfield 145	com/mappn/gfan/common/download/DownloadInfo:mMimeType	Ljava/lang/String;
    //   582: invokevirtual 149	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   585: ifeq +36 -> 621
    //   588: sipush 491
    //   591: sipush 200
    //   594: if_icmpne +27 -> 621
    //   597: aload_0
    //   598: getfield 29	com/mappn/gfan/common/download/DownloadThread:mContext	Landroid/content/Context;
    //   601: iconst_1
    //   602: aload_0
    //   603: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   606: getfield 152	com/mappn/gfan/common/download/DownloadInfo:mSource	I
    //   609: aload_0
    //   610: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   613: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   616: ldc 157
    //   618: invokestatic 163	com/mappn/gfan/common/util/Utils:submitDownloadLog	(Landroid/content/Context;IILjava/lang/String;Ljava/lang/String;)V
    //   621: aload_0
    //   622: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   625: astore 10
    //   627: goto -247 -> 380
    //   630: aload_0
    //   631: aload_1
    //   632: invokespecial 819	com/mappn/gfan/common/download/DownloadThread:finalizeDestinationFile	(Lcom/mappn/gfan/common/download/DownloadThread$State;)V
    //   635: aload 7
    //   637: ifnull +8 -> 645
    //   640: aload 7
    //   642: invokevirtual 799	android/os/PowerManager$WakeLock:release	()V
    //   645: aload 8
    //   647: ifnull +3 -> 650
    //   650: aload_0
    //   651: aload_1
    //   652: sipush 200
    //   655: invokespecial 801	com/mappn/gfan/common/download/DownloadThread:cleanupDestination	(Lcom/mappn/gfan/common/download/DownloadThread$State;I)V
    //   658: aload_0
    //   659: sipush 200
    //   662: aload_1
    //   663: getfield 267	com/mappn/gfan/common/download/DownloadThread$State:mCountRetry	Z
    //   666: aload_1
    //   667: getfield 417	com/mappn/gfan/common/download/DownloadThread$State:mRetryAfter	I
    //   670: aload_1
    //   671: getfield 368	com/mappn/gfan/common/download/DownloadThread$State:mRedirectCount	I
    //   674: aload_1
    //   675: getfield 697	com/mappn/gfan/common/download/DownloadThread$State:mGotData	Z
    //   678: aload_1
    //   679: getfield 173	com/mappn/gfan/common/download/DownloadThread$State:mFilename	Ljava/lang/String;
    //   682: aload_1
    //   683: getfield 397	com/mappn/gfan/common/download/DownloadThread$State:mNewUri	Ljava/lang/String;
    //   686: aload_1
    //   687: getfield 506	com/mappn/gfan/common/download/DownloadThread$State:mMimeType	Ljava/lang/String;
    //   690: invokespecial 803	com/mappn/gfan/common/download/DownloadThread:notifyDownloadCompleted	(IZIIZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   693: sipush 200
    //   696: sipush 200
    //   699: if_icmpne +7 -> 706
    //   702: aload_0
    //   703: invokespecial 805	com/mappn/gfan/common/download/DownloadThread:updatePcakageName	()V
    //   706: ldc 142
    //   708: aload_0
    //   709: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   712: getfield 145	com/mappn/gfan/common/download/DownloadInfo:mMimeType	Ljava/lang/String;
    //   715: invokevirtual 149	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   718: ifeq +36 -> 754
    //   721: sipush 200
    //   724: sipush 200
    //   727: if_icmpne +27 -> 754
    //   730: aload_0
    //   731: getfield 29	com/mappn/gfan/common/download/DownloadThread:mContext	Landroid/content/Context;
    //   734: iconst_1
    //   735: aload_0
    //   736: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   739: getfield 152	com/mappn/gfan/common/download/DownloadInfo:mSource	I
    //   742: aload_0
    //   743: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   746: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   749: ldc 157
    //   751: invokestatic 163	com/mappn/gfan/common/util/Utils:submitDownloadLog	(Landroid/content/Context;IILjava/lang/String;Ljava/lang/String;)V
    //   754: aload_0
    //   755: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   758: astore 10
    //   760: goto -380 -> 380
    //   763: astore 12
    //   765: aload 12
    //   767: astore 6
    //   769: aconst_null
    //   770: astore 8
    //   772: aconst_null
    //   773: astore 7
    //   775: aload 7
    //   777: ifnull +8 -> 785
    //   780: aload 7
    //   782: invokevirtual 799	android/os/PowerManager$WakeLock:release	()V
    //   785: aload 8
    //   787: ifnull +3 -> 790
    //   790: aload_0
    //   791: aload_1
    //   792: sipush 491
    //   795: invokespecial 801	com/mappn/gfan/common/download/DownloadThread:cleanupDestination	(Lcom/mappn/gfan/common/download/DownloadThread$State;I)V
    //   798: aload_0
    //   799: sipush 491
    //   802: aload_1
    //   803: getfield 267	com/mappn/gfan/common/download/DownloadThread$State:mCountRetry	Z
    //   806: aload_1
    //   807: getfield 417	com/mappn/gfan/common/download/DownloadThread$State:mRetryAfter	I
    //   810: aload_1
    //   811: getfield 368	com/mappn/gfan/common/download/DownloadThread$State:mRedirectCount	I
    //   814: aload_1
    //   815: getfield 697	com/mappn/gfan/common/download/DownloadThread$State:mGotData	Z
    //   818: aload_1
    //   819: getfield 173	com/mappn/gfan/common/download/DownloadThread$State:mFilename	Ljava/lang/String;
    //   822: aload_1
    //   823: getfield 397	com/mappn/gfan/common/download/DownloadThread$State:mNewUri	Ljava/lang/String;
    //   826: aload_1
    //   827: getfield 506	com/mappn/gfan/common/download/DownloadThread$State:mMimeType	Ljava/lang/String;
    //   830: invokespecial 803	com/mappn/gfan/common/download/DownloadThread:notifyDownloadCompleted	(IZIIZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   833: sipush 491
    //   836: sipush 200
    //   839: if_icmpne +7 -> 846
    //   842: aload_0
    //   843: invokespecial 805	com/mappn/gfan/common/download/DownloadThread:updatePcakageName	()V
    //   846: ldc 142
    //   848: aload_0
    //   849: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   852: getfield 145	com/mappn/gfan/common/download/DownloadInfo:mMimeType	Ljava/lang/String;
    //   855: invokevirtual 149	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   858: ifeq +36 -> 894
    //   861: sipush 491
    //   864: sipush 200
    //   867: if_icmpne +27 -> 894
    //   870: aload_0
    //   871: getfield 29	com/mappn/gfan/common/download/DownloadThread:mContext	Landroid/content/Context;
    //   874: iconst_1
    //   875: aload_0
    //   876: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   879: getfield 152	com/mappn/gfan/common/download/DownloadInfo:mSource	I
    //   882: aload_0
    //   883: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   886: getfield 155	com/mappn/gfan/common/download/DownloadInfo:mUri	Ljava/lang/String;
    //   889: ldc 157
    //   891: invokestatic 163	com/mappn/gfan/common/util/Utils:submitDownloadLog	(Landroid/content/Context;IILjava/lang/String;Ljava/lang/String;)V
    //   894: aload_0
    //   895: getfield 31	com/mappn/gfan/common/download/DownloadThread:mInfo	Lcom/mappn/gfan/common/download/DownloadInfo;
    //   898: iconst_0
    //   899: putfield 808	com/mappn/gfan/common/download/DownloadInfo:mHasActiveThread	Z
    //   902: aload 6
    //   904: athrow
    //   905: astore 25
    //   907: aload 24
    //   909: invokevirtual 784	org/apache/http/client/methods/HttpGet:abort	()V
    //   912: goto -820 -> 92
    //   915: astore 21
    //   917: aload 21
    //   919: astore 6
    //   921: goto -146 -> 775
    //   924: astore 18
    //   926: aload 18
    //   928: astore 6
    //   930: aconst_null
    //   931: astore 8
    //   933: goto -158 -> 775
    //   936: astore 5
    //   938: aload 5
    //   940: astore 6
    //   942: aload_3
    //   943: astore 7
    //   945: aload 4
    //   947: astore 8
    //   949: goto -174 -> 775
    //   952: astore 11
    //   954: aconst_null
    //   955: astore_3
    //   956: aconst_null
    //   957: astore 4
    //   959: goto -511 -> 448
    //   962: astore 16
    //   964: aconst_null
    //   965: astore 4
    //   967: aload 7
    //   969: astore 17
    //   971: aload 16
    //   973: astore 11
    //   975: aload 17
    //   977: astore_3
    //   978: goto -530 -> 448
    //   981: astore_2
    //   982: aconst_null
    //   983: astore_3
    //   984: aconst_null
    //   985: astore 4
    //   987: goto -790 -> 197
    //   990: astore 14
    //   992: aconst_null
    //   993: astore 4
    //   995: aload 7
    //   997: astore 15
    //   999: aload 14
    //   1001: astore_2
    //   1002: aload 15
    //   1004: astore_3
    //   1005: goto -808 -> 197
    //
    // Exception table:
    //   from	to	target	type
    //   155	164	175	finally
    //   97	155	185	com/mappn/gfan/common/download/DownloadThread$StopRequest
    //   164	185	185	com/mappn/gfan/common/download/DownloadThread$StopRequest
    //   387	435	185	com/mappn/gfan/common/download/DownloadThread$StopRequest
    //   630	635	185	com/mappn/gfan/common/download/DownloadThread$StopRequest
    //   907	912	185	com/mappn/gfan/common/download/DownloadThread$StopRequest
    //   97	155	435	java/lang/Throwable
    //   164	185	435	java/lang/Throwable
    //   387	435	435	java/lang/Throwable
    //   630	635	435	java/lang/Throwable
    //   907	912	435	java/lang/Throwable
    //   17	39	763	finally
    //   155	164	905	com/mappn/gfan/common/download/DownloadThread$RetryDownload
    //   97	155	915	finally
    //   164	185	915	finally
    //   387	435	915	finally
    //   630	635	915	finally
    //   907	912	915	finally
    //   43	85	924	finally
    //   197	261	936	finally
    //   448	504	936	finally
    //   17	39	952	java/lang/Throwable
    //   43	85	962	java/lang/Throwable
    //   17	39	981	com/mappn/gfan/common/download/DownloadThread$StopRequest
    //   43	85	990	com/mappn/gfan/common/download/DownloadThread$StopRequest
  }

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

    public State(DownloadInfo paramDownloadInfo)
    {
      this.mMimeType = DownloadThread.sanitizeMimeType(paramDownloadInfo.mMimeType);
      this.mRedirectCount = paramDownloadInfo.mRedirectCount;
      this.mRequestUri = paramDownloadInfo.mUri;
      this.mFilename = paramDownloadInfo.mFileName;
      this.mSourceType = paramDownloadInfo.mSource;
      try
      {
        this.mDigester = MessageDigest.getInstance("MD5");
        return;
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        while (true)
          Utils.D("no algorithm for md5");
      }
    }
  }

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