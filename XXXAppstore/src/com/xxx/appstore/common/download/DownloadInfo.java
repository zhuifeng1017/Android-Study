package com.xxx.appstore.common.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.net.Uri;

import com.xxx.appstore.common.util.Utils;

import java.util.Random;

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
		this.mContext = paramContext;
		this.mFuzz = Helper.rnd.nextInt(1001);
	}

	private boolean isReadyToStart(long paramLong) {
		boolean bool;
		if (this.mHasActiveThread)
			bool = false;
		else {
			if ((this.mControl == 1) || (this.mControl == 2))
				bool = false;
			else if (this.mCurrentBytes == this.mTotalBytes)
				bool = false;
			else
				switch (this.mStatus) {
				default:
					bool = false;
					break;
				case 200:
					bool = false;
					break;
				case 0:
				case 190:
				case 192:
					bool = true;
					break;
				case 195:
				case 196:
					if (checkCanUseNetwork() == 1)
						bool = true;
					else
						bool = false;
					break;
				case 194:
					if (restartTime(paramLong) <= paramLong)
						bool = true;
					else
						bool = false;
					break;
				}
		}
		return bool;
	}

	public int checkCanUseNetwork() {
		int i = 2;
		if (Helper.getActiveNetworkType(this.mContext) == null)
			i = 1;
		return i;
	}

	public String getLogMessageForNetworkError(int paramInt) {
		switch (paramInt) {
		default:
		case 2:
		}
		for (String str = "unknown error with network connectivity";; str = "no network connection available")
			return str;
	}

	public Uri getMyDownloadsUri() {
		return ContentUris.withAppendedId(DownloadManager.Impl.CONTENT_URI,
				this.mId);
	}

	public String getVerboseInfo() {
		StringBuilder localStringBuilder1 = new StringBuilder();
		localStringBuilder1.append("ID      : " + this.mId + "\n");
		StringBuilder localStringBuilder2 = new StringBuilder()
				.append("URI     : ");
		if (this.mUri != null)
			;
		for (String str = this.mUri;; str = "no") {
			localStringBuilder1.append(str + "\n");
			localStringBuilder1.append("HINT    : " + this.mHint + "\n");
			localStringBuilder1.append("FILENAME: " + this.mFileName + "\n");
			localStringBuilder1.append("MIMETYPE: " + this.mMimeType + "\n");
			localStringBuilder1.append("DESTINAT: " + this.mDestination + "\n");
			localStringBuilder1.append("VISIBILI: " + this.mVisibility + "\n");
			localStringBuilder1.append("CONTROL : " + this.mControl + "\n");
			localStringBuilder1.append("STATUS  : " + this.mStatus + "\n");
			localStringBuilder1.append("FAILED_C: " + this.mNumFailed + "\n");
			localStringBuilder1.append("RETRY_AF: " + this.mRetryAfter + "\n");
			localStringBuilder1.append("REDIRECT: " + this.mRedirectCount
					+ "\n");
			localStringBuilder1.append("LAST_MOD: " + this.mLastMod + "\n");
			localStringBuilder1.append("PACKAGE : " + this.mPackage + "\n");
			localStringBuilder1.append("CLASS   : " + this.mClass + "\n");
			localStringBuilder1.append("TOTAL   : " + this.mTotalBytes + "\n");
			localStringBuilder1
					.append("CURRENT : " + this.mCurrentBytes + "\n");
			localStringBuilder1.append("ETAG    : " + this.mETag + "\n");
			localStringBuilder1.append("DELETED : " + this.mDeleted + "\n");
			return localStringBuilder1.toString();
		}
	}

	public boolean hasCompletionNotification() {
		boolean bool;
		if (!DownloadManager.Impl.isStatusCompleted(this.mStatus))
			bool = false;
		else {
			if (this.mVisibility == 1)
				bool = true;
			else
				bool = false;
		}
		return bool;
	}

	public boolean isCompleted() {
		return DownloadManager.Impl.isStatusCompleted(this.mStatus);
	}

	public boolean isRunning() {
		return DownloadManager.Impl.isStatusRunning(this.mStatus);
	}

	public void logVerboseInfo() {
		Utils.D(getVerboseInfo());
	}

	long nextAction(long paramLong) {
		long l2;
		if (DownloadManager.Impl.isStatusCompleted(this.mStatus))
			l2 = -1L;
		else {
			if (this.mStatus != 194) {
				l2 = 0L;
			} else {
				long l1 = restartTime(paramLong);
				if (l1 <= paramLong)
					l2 = 0L;
				else
					l2 = l1 - paramLong;
			}
		}
		return l2;
	}

	public long restartTime(long paramLong) {
		long l;
		if (this.mNumFailed == 0)
			l = paramLong;
		else {

			if (this.mRetryAfter > 0)
				l = this.mLastMod + this.mRetryAfter;
			else
				l = this.mLastMod + 30 * (1000 + this.mFuzz)
						* (1 << this.mNumFailed - 1);
		}
		return l;
	}

	boolean startIfReady(long paramLong) {
		if (!isReadyToStart(paramLong))
			return false;
		Utils.D("Service spawning thread to handle download " + this.mId);
		if (this.mHasActiveThread)
			throw new IllegalStateException("Multiple threads on same download");
		if (this.mStatus != 192) {
			this.mStatus = 192;
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("status", Integer.valueOf(this.mStatus));
			this.mContext.getContentResolver().update(getMyDownloadsUri(),
					localContentValues, null, null);
		}
		DownloadThread localDownloadThread = new DownloadThread(this.mContext,
				this);
		this.mHasActiveThread = true;
		localDownloadThread.start();
		return true;
	}

	public static class Reader {
		private Cursor mCursor;
		private CharArrayBuffer mNewChars;
		private CharArrayBuffer mOldChars;

		public Reader(Cursor paramCursor) {
			this.mCursor = paramCursor;
		}

		private Integer getInt(String paramString) {
			return Integer.valueOf(this.mCursor.getInt(this.mCursor
					.getColumnIndexOrThrow(paramString)));
		}

		private Long getLong(String paramString) {
			return Long.valueOf(this.mCursor.getLong(this.mCursor
					.getColumnIndexOrThrow(paramString)));
		}

		private String getString(String paramString1, String paramString2) {
			int i = this.mCursor.getColumnIndexOrThrow(paramString2);
			String str;
			if (paramString1 == null)
				str = this.mCursor.getString(i);
			else {

				if (this.mNewChars == null)
					this.mNewChars = new CharArrayBuffer(128);
				this.mCursor.copyStringToBuffer(i, this.mNewChars);
				int j = this.mNewChars.sizeCopied;
				if (j != paramString1.length()) {
					str = new String(this.mNewChars.data, 0, j);
				} else {
					if ((this.mOldChars == null)
							|| (this.mOldChars.sizeCopied < j))
						this.mOldChars = new CharArrayBuffer(j);
					char[] arrayOfChar1 = this.mOldChars.data;
					char[] arrayOfChar2 = this.mNewChars.data;
					paramString1.getChars(0, j, arrayOfChar1, 0);
					for (int k = j - 1;; k--) {
						if (k < 0)
							str = paramString1;
						if (arrayOfChar1[k] != arrayOfChar2[k]) {
							str = new String(arrayOfChar2, 0, j);
							break;
						}
					}
				}
			}
			return str;
		}

		public DownloadInfo newDownloadInfo(Context paramContext) {
			DownloadInfo localDownloadInfo = new DownloadInfo(paramContext);
			updateFromDatabase(localDownloadInfo);
			return localDownloadInfo;
		}

		public void updateFromDatabase(DownloadInfo paramDownloadInfo) {
			paramDownloadInfo.mId = getLong("_id").longValue();
			paramDownloadInfo.mUri = getString(paramDownloadInfo.mUri, "uri");
			paramDownloadInfo.mHint = getString(paramDownloadInfo.mHint, "hint");
			paramDownloadInfo.mFileName = getString(
					paramDownloadInfo.mFileName, "_data");
			paramDownloadInfo.mMimeType = getString(
					paramDownloadInfo.mMimeType, "mimetype");
			paramDownloadInfo.mDestination = getInt("destination").intValue();
			paramDownloadInfo.mVisibility = getInt("visibility").intValue();
			paramDownloadInfo.mStatus = getInt("status").intValue();
			paramDownloadInfo.mNumFailed = getInt("numfailed").intValue();
			int i = getInt("redirectcount").intValue();
			paramDownloadInfo.mRetryAfter = (0xFFFFFFF & i);
			paramDownloadInfo.mRedirectCount = (i >> 28);
			paramDownloadInfo.mLastMod = getLong("lastmod").longValue();
			paramDownloadInfo.mPackage = getString(paramDownloadInfo.mPackage,
					"notificationpackage");
			paramDownloadInfo.mClass = getString(paramDownloadInfo.mClass,
					"notificationclass");
			paramDownloadInfo.mExtras = getString(paramDownloadInfo.mExtras,
					"notificationextras");
			paramDownloadInfo.mTotalBytes = getLong("total_bytes").longValue();
			paramDownloadInfo.mCurrentBytes = getLong("current_bytes")
					.longValue();
			paramDownloadInfo.mETag = getString(paramDownloadInfo.mETag, "etag");
			boolean bool = false;
			if (getInt("deleted").intValue() == 1)
				bool = true;

			paramDownloadInfo.mDeleted = bool;
			paramDownloadInfo.mTitle = getString(paramDownloadInfo.mTitle,
					"title");
			paramDownloadInfo.mDescription = getString(
					paramDownloadInfo.mDescription, "description");
			paramDownloadInfo.mSource = getInt("source").intValue();
			paramDownloadInfo.mPackageName = getString(
					paramDownloadInfo.mPackageName, "package_name");
			paramDownloadInfo.mMD5 = getString(paramDownloadInfo.mPackageName,
					"md5");
			paramDownloadInfo.mAllowedNetworkTypes = getInt("allow_network")
					.intValue();
			try {
				paramDownloadInfo.mControl = getInt("control").intValue();
				return;
			} finally {

			}
		}
	}
}