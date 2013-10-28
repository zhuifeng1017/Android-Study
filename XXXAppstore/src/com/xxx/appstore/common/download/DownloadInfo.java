package com.xxx.appstore.common.download;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.net.Uri;

import com.xxx.appstore.common.util.Utils;

public class DownloadInfo {
	public static final String EXTRA_IS_WIFI_REQUIRED = "isWifiRequired";
	public static final int NETWORK_NO_CONNECTION = 2;
	public static final int NETWORK_OK = 1;
	public static final int NETWORK_TYPE_DISALLOWED_BY_REQUESTOR = 3;
	public int mAllowedNetworkTypes;
	public String mClass;
	private Context mContext;
	public int mControl;
	public long mCurrentBytes;
	public boolean mDeleted;
	public String mDescription;
	public int mDestination;
	public String mETag;
	public String mExtras;
	public String mFileName;
	public int mFuzz;
	public volatile boolean mHasActiveThread;
	public String mHint;
	public long mId;
	public long mLastMod;
	public String mMD5;
	public String mMimeType;
	public int mNumFailed;
	public String mPackage;
	public String mPackageName;
	public int mRedirectCount;
	public int mRetryAfter;
	public int mSource;
	public int mStatus;
	public String mTitle;
	public long mTotalBytes;
	public String mUri;
	public int mVisibility;

	private DownloadInfo(Context paramContext) {
		mContext = paramContext;
		mFuzz = Helper.rnd.nextInt(1001);
	}

	/**
     * Returns whether this download (which the download manager hasn't seen yet)
     * should be started.
     */
    private boolean isReadyToStart(long now) {
        if (mHasActiveThread) {
            // already running
            return false;
        }
        if ((mControl == DownloadManager.Impl.CONTROL_PAUSED) || (mControl == DownloadManager.Impl.CONTROL_PENDING))
        	return false;
         else if (mCurrentBytes == mTotalBytes) {
        	 return false;
         }
        switch (mStatus) {
            case 0: // status hasn't been initialized yet, this is a new download
            case DownloadManager.Impl.STATUS_PENDING: // download is explicit marked as ready to start
            case DownloadManager.Impl.STATUS_RUNNING: // download interrupted (process killed etc) while
                                                // running, without a chance to update the database
                return true;

            case DownloadManager.Impl.STATUS_WAITING_FOR_NETWORK:
            case DownloadManager.Impl.STATUS_QUEUED_FOR_WIFI:
                return checkCanUseNetwork() == NETWORK_OK;

            case DownloadManager.Impl.STATUS_WAITING_TO_RETRY:
                // download was waiting for a delayed restart
                return restartTime(now) <= now;
        }
        return false;
    }
    	
	/**
     * Returns whether this download is allowed to use the network.
     * @return one of the NETWORK_* constants
     */
	public int checkCanUseNetwork() {
		return (Helper.getActiveNetworkType(mContext) == null)?NETWORK_NO_CONNECTION:NETWORK_OK;
	}

	 /**
     * @return a non-localized string appropriate for logging corresponding to one of the
     * NETWORK_* constants.
     */
    public String getLogMessageForNetworkError(int networkError) {
        switch (networkError) {
            case NETWORK_NO_CONNECTION:
                return "no network connection available";
            case NETWORK_TYPE_DISALLOWED_BY_REQUESTOR:
                return "download was requested to not use the current network type";
            default:
                return "unknown error with network connectivity";
        }
    }

	public Uri getMyDownloadsUri() {
		return ContentUris.withAppendedId(DownloadManager.Impl.CONTENT_URI,
				mId);
	}

	public String getVerboseInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID      : " + mId + "\n");	
		sb.append("URI     : ");
		String str = ((mUri != null)? "yes" : "no");
		sb.append(str + "\n");
		sb.append("HINT    : " + mHint + "\n");
		sb.append("FILENAME: " + mFileName + "\n");
		sb.append("MIMETYPE: " + mMimeType + "\n");
		sb.append("DESTINAT: " + mDestination + "\n");
		sb.append("VISIBILI: " + mVisibility + "\n");
		sb.append("CONTROL : " + mControl + "\n");
		sb.append("STATUS  : " + mStatus + "\n");
		sb.append("FAILED_C: " + mNumFailed + "\n");
		sb.append("RETRY_AF: " + mRetryAfter + "\n");
		sb.append("REDIRECT: " + mRedirectCount	+ "\n");
		sb.append("LAST_MOD: " + mLastMod + "\n");
		sb.append("PACKAGE : " + mPackage + "\n");
		sb.append("CLASS   : " + mClass + "\n");
		sb.append("TOTAL   : " + mTotalBytes + "\n");
		sb.append("CURRENT : " + mCurrentBytes + "\n");
		sb.append("ETAG    : " + mETag + "\n");
		sb.append("DELETED : " + mDeleted + "\n");
		return sb.toString();
	}

	/**
     * Returns whether this download has a visible notification after
     * completion.
     */
    public boolean hasCompletionNotification() {
        if (!DownloadManager.Impl.isStatusCompleted(mStatus)) {
            return false;
        }
        if (mVisibility == DownloadManager.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
            return true;
        }
        return false;
    }
    
	public boolean isCompleted() {
		return DownloadManager.Impl.isStatusCompleted(mStatus);
	}

	public boolean isRunning() {
		return DownloadManager.Impl.isStatusRunning(mStatus);
	}

	public void logVerboseInfo() {
		Utils.D(getVerboseInfo());
	}

	/**
     * Returns the amount of time (as measured from the "now" parameter)
     * at which a download will be active.
     * 0 = immediately - service should stick around to handle this download.
     * -1 = never - service can go away without ever waking up.
     * positive value - service must wake up in the future, as specified in ms from "now"
     */
    long nextAction(long now) {
        if (DownloadManager.Impl.isStatusCompleted(mStatus)) {
            return -1;
        }
        if (mStatus != DownloadManager.Impl.STATUS_WAITING_TO_RETRY) {
            return 0;
        }
        long when = restartTime(now);
        if (when <= now) {
            return 0;
        }
        return when - now;
    }
    
    /**
     * Returns the time when a download should be restarted.
     */
    public long restartTime(long now) {
        if (mNumFailed == 0) {
            return now;
        }
        if (mRetryAfter > 0) {
            return mLastMod + mRetryAfter;
        }
        return mLastMod +
                Constants.RETRY_FIRST_DELAY *
                    (1000 + mFuzz) * (1 << (mNumFailed - 1));
    }
            
	boolean startIfReady(long paramLong) {
		if (!isReadyToStart(paramLong))
			return false;
		
		Utils.D("Service spawning thread to handle download " + mId);
		if (mHasActiveThread)
			throw new IllegalStateException("Multiple threads on same download");
		if (mStatus != DownloadManager.Impl.STATUS_RUNNING) {
			mStatus = DownloadManager.Impl.STATUS_RUNNING;
			ContentValues values = new ContentValues();
			values.put(DownloadManager.Impl.COLUMN_STATUS, mStatus);
			mContext.getContentResolver().update(getMyDownloadsUri(),
					values, null, null);
		}
		DownloadThread downloader = new DownloadThread(mContext,this);
		mHasActiveThread = true;
		downloader.start();
		return true;
	}

	public static class Reader {
		private Cursor mCursor;
		private CharArrayBuffer mNewChars;
		private CharArrayBuffer mOldChars;

		public Reader(Cursor paramCursor) {
			mCursor = paramCursor;
		}

		/**
         * Returns a String that holds the current value of the column, optimizing for the case
         * where the value hasn't changed.
         */
        private String getString(String old, String column) {
            int index = mCursor.getColumnIndexOrThrow(column);
            if (old == null) {
                return mCursor.getString(index);
            }
            if (mNewChars == null) {
                mNewChars = new CharArrayBuffer(128);
            }
            mCursor.copyStringToBuffer(index, mNewChars);
            int length = mNewChars.sizeCopied;
            if (length != old.length()) {
                return new String(mNewChars.data, 0, length);
            }
            if (mOldChars == null || mOldChars.sizeCopied < length) {
                mOldChars = new CharArrayBuffer(length);
            }
            char[] oldArray = mOldChars.data;
            char[] newArray = mNewChars.data;
            old.getChars(0, length, oldArray, 0);
            for (int i = length - 1; i >= 0; --i) {
                if (oldArray[i] != newArray[i]) {
                    return new String(newArray, 0, length);
                }
            }
            return old;
        }
        
        private Integer getInt(String column) {
            return mCursor.getInt(mCursor.getColumnIndexOrThrow(column));
        }

        private Long getLong(String column) {
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(column));
        }

		public DownloadInfo newDownloadInfo(Context context) {
			DownloadInfo info = new DownloadInfo(context);
			updateFromDatabase(info);
			return info;
		}

		public void updateFromDatabase(DownloadInfo info) {
			info.mId = getLong(DownloadManager.Impl.COLUMN_ID);
			info.mUri = getString(info.mUri, DownloadManager.Impl.COLUMN_URI);
			info.mHint = getString(info.mHint, DownloadManager.Impl.COLUMN_FILE_NAME_HINT);
			info.mFileName = getString(info.mFileName, DownloadManager.Impl.COLUMN_DATA);
			info.mMimeType = getString(info.mMimeType, DownloadManager.Impl.COLUMN_MIME_TYPE);
			info.mDestination = getInt(DownloadManager.Impl.COLUMN_DESTINATION);
            info.mVisibility = getInt(DownloadManager.Impl.COLUMN_VISIBILITY);
            info.mStatus = getInt(DownloadManager.Impl.COLUMN_STATUS);
            info.mNumFailed = getInt(DownloadManager.Impl.COLUMN_FAILED_CONNECTIONS);
			int i = getInt(DownloadManager.Impl.COLUMN_RETRY_AFTER_REDIRECT_COUNT).intValue();
			info.mRetryAfter = (0xFFFFFFF & i);
			info.mRedirectCount = (i >> 28);
			info.mLastMod = getLong("lastmod").longValue();
			info.mPackage = getString(info.mPackage, DownloadManager.Impl.COLUMN_NOTIFICATION_PACKAGE);
			info.mClass = getString(info.mClass, DownloadManager.Impl.COLUMN_NOTIFICATION_CLASS);
			info.mExtras = getString(info.mExtras, DownloadManager.Impl.COLUMN_NOTIFICATION_EXTRAS);
			info.mTotalBytes = getLong(DownloadManager.Impl.COLUMN_TOTAL_BYTES);
			info.mCurrentBytes = getLong(DownloadManager.Impl.COLUMN_CURRENT_BYTES);
			info.mETag = getString(info.mETag, DownloadManager.Impl.COLUMN_ETAG);
			info.mDeleted = getInt(DownloadManager.Impl.COLUMN_DELETED) == 1;
			info.mTitle = getString(info.mTitle, DownloadManager.Impl.COLUMN_TITLE);
			info.mDescription = getString(info.mDescription, DownloadManager.Impl.COLUMN_DESCRIPTION);
			info.mSource = getInt(DownloadManager.Impl.COLUMN_SOURCE).intValue();
			info.mPackageName = getString(info.mPackageName, DownloadManager.Impl.COLUMN_PACKAGE_NAME);
			info.mMD5 = getString(info.mPackageName,DownloadManager.Impl.COLUMN_MD5);
			info.mAllowedNetworkTypes = getInt(DownloadManager.Impl.COLUMN_ALLOW_NETWORK_TYPE).intValue();
			synchronized (this) {
                info.mControl = getInt(DownloadManager.Impl.COLUMN_CONTROL);
            }
		}
	}
}