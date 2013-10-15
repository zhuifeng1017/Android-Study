package com.xxx.appstore.common.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.xxx.appstore.common.util.Utils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class DownloadNotification {
	static final String LOGTAG = "DownloadNotification";
	public static final String PUBLIC_WHERE_OTA_COMPLETED = "status >= '200' AND source == '3' AND mimetype == 'application/vnd.android.package-archive'";
	static final String WHERE_BAD_COMPLETED = "status >= '400' AND (visibility == '0' OR visibility == '1') AND mimetype == 'application/vnd.android.package-archive'";
	static final String WHERE_COMPLETED = "status >= '200' AND visibility == '1'";
	static final String WHERE_OTA_COMPLETED = "status >= '200' AND source == '3' AND visibility == '0' AND mimetype == 'application/vnd.android.package-archive'";
	static final String WHERE_RUNNING = "(status >= '100') AND (status <= '199') AND (visibility IS NULL OR visibility == '0' OR visibility == '1')";
	Context mContext;
	NotificationManager mNotificationManager;
	HashMap<String, NotificationItem> mNotifications;

	DownloadNotification(Context paramContext) {
		this.mContext = paramContext;
		this.mNotifications = new HashMap();
		this.mNotificationManager = ((NotificationManager) this.mContext
				.getSystemService("notification"));
	}

	private String getDownloadingText(long paramLong1, long paramLong2) {
		String str = "";
		if (paramLong1 <= 0L)
			return str;

		StringBuilder localStringBuilder;
		long l = 100L * paramLong2 / paramLong1;
		localStringBuilder = new StringBuilder();
		localStringBuilder.append(l);
		localStringBuilder.append('%');
		str = localStringBuilder.toString();
		return str;
	}

	private String handleErrorMessage(int paramInt) {
		String str;
		if (400 == paramInt)
			str = this.mContext.getString(2131296605);
		else if (406 == paramInt)
			str = this.mContext.getString(2131296596);
		else if ((411 == paramInt) || (412 == paramInt) || (491 == paramInt))
			str = this.mContext.getString(2131296602);
		else if ((488 == paramInt) || (492 == paramInt))
			str = this.mContext.getString(2131296603);
		else if (490 == paramInt)
			str = this.mContext.getString(2131296606);
		else if ((493 == paramInt) || (494 == paramInt) || (496 == paramInt)
				|| (495 == paramInt) || (497 == paramInt))
			str = this.mContext.getString(2131296601);
		else if (499 == paramInt)
			str = this.mContext.getString(2131296599);
		else if (498 == paramInt)
			str = this.mContext.getString(2131296600);
		else
			str = this.mContext.getString(2131296557);
		return str;
	}

	private void updateActiveNotification() {
		ContentResolver contentresolver = mContext.getContentResolver();
		Uri uri = DownloadManager.Impl.CONTENT_URI;
		String as[] = new String[5];
		as[0] = "_id";
		as[1] = "title";
		as[2] = "notificationpackage";
		as[3] = "current_bytes";
		as[4] = "total_bytes";
		Cursor cursor = contentresolver
				.query(uri,
						as,
						"(status >= '100') AND (status <= '199') AND (visibility IS NULL OR visibility == '0' OR visibility == '1')",
						null, "_id");
		if (cursor != null) {
			Iterator iterator;
			NotificationItem notificationitem;
			Notification notification;
			RemoteViews remoteviews;
			StringBuilder stringbuilder;
			int i;
			int j;
			boolean flag;
			Intent intent;
			boolean flag1;
			Context context;
			Object aobj[];
			String s2;

			mNotifications.clear();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String s = cursor.getString(2);
				int k = cursor.getInt(4);
				int l = cursor.getInt(3);
				String s1 = cursor.getString(1);

				if (s1 == null || s1.length() == 0)
					s2 = mContext.getResources().getString(0x7f09003c);
				else
					s2 = s1;
				if (mNotifications.containsKey(s)) {
					((NotificationItem) mNotifications.get(s))
							.addItem(s2, l, k);
				} else {
					NotificationItem notificationitem1 = new NotificationItem();
					notificationitem1.mId = cursor.getInt(0);
					notificationitem1.mPackageName = s;
					notificationitem1.addItem(s2, l, k);
					mNotifications.put(s, notificationitem1);
				}
				cursor.moveToNext();
			}
			cursor.close();
			iterator = mNotifications.values().iterator();
			while (iterator.hasNext()) {
				notificationitem = (NotificationItem) iterator.next();
				notification = new Notification();
				notification.defaults = 0;
				notification.icon = 0x1080081;
				notification.flags = 2 | notification.flags;
				remoteviews = new RemoteViews("com.unistrong.appstore", 0x7f030055);
				stringbuilder = new StringBuilder(notificationitem.mTitles[0]);
				if (notificationitem.mTitleCount > 1) {
					stringbuilder.append(mContext.getString(0x7f09003d));
					stringbuilder.append(notificationitem.mTitles[1]);
					notification.number = notificationitem.mTitleCount;
					if (notificationitem.mTitleCount > 2) {
						context = mContext;
						aobj = new Object[1];
						aobj[0] = Integer
								.valueOf(notificationitem.mTitleCount - 2);
						stringbuilder.append(context
								.getString(0x7f09003e, aobj));
					}
				}
				remoteviews.setTextViewText(0x7f0c000b, stringbuilder);
				i = (int) notificationitem.mTotalBytes;
				j = (int) notificationitem.mCurrentBytes;
				if (notificationitem.mTotalBytes == -1L)
					flag = true;
				else
					flag = false;
				remoteviews.setProgressBar(0x7f0c00a6, i, j, flag);
				remoteviews.setTextViewText(
						0x7f0c00a3,
						getDownloadingText(notificationitem.mTotalBytes,
								notificationitem.mCurrentBytes));
				remoteviews.setImageViewResource(0x7f0c00a2, 0x1080081);
				notification.contentView = remoteviews;
				intent = new Intent("gfan.intent.action.DOWNLOAD_LIST");
				intent.setClassName("com.unistrong.appstore",
						DownloadReceiver.class.getName());
				intent.setData(Uri.parse((new StringBuilder())
						.append(DownloadManager.Impl.CONTENT_URI).append("/")
						.append(notificationitem.mId).toString()));
				if (notificationitem.mTitleCount > 1)
					flag1 = true;
				else
					flag1 = false;
				intent.putExtra("multiple", flag1);
				notification.contentIntent = PendingIntent.getBroadcast(
						mContext, 0, intent, 0);
				mNotificationManager.notify(notificationitem.mId, notification);
			}
		}
	}

	private void updateCompletedNotification() {
		ContentResolver contentresolver = mContext.getContentResolver();
		Uri uri = DownloadManager.Impl.CONTENT_URI;
		String as[] = new String[5];
		as[0] = "_id";
		as[1] = "title";
		as[2] = "status";
		as[3] = "lastmod";
		as[4] = "destination";
		Cursor cursor = contentresolver.query(uri, as,
				"status >= '200' AND visibility == '1'", null, "_id");
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Notification notification = new Notification();
				notification.defaults = 0;
				notification.icon = 0x1080082;
				String s = cursor.getString(1);
				if (s == null || s.length() == 0)
					s = mContext.getResources().getString(0x7f09003c);
				Uri uri1 = Uri.parse((new StringBuilder())
						.append(DownloadManager.Impl.CONTENT_URI).append("/")
						.append(cursor.getInt(0)).toString());
				String s2;
				Intent intent2;
				Intent intent3;
				if (DownloadManager.Impl.isStatusError(cursor.getInt(2))) {
					String s3 = handleErrorMessage(cursor.getInt(2));
					Intent intent4 = new Intent(
							"gfan.intent.action.DOWNLOAD_HIDE");
					intent4.putExtra("status", 491);
					s2 = s3;
					intent2 = intent4;
				} else {
					String s1 = mContext.getResources().getString(0x7f090040);
					Intent intent;
					Intent intent1;
					if (cursor.getInt(4) != 0)
						intent = new Intent("gfan.intent.action.DOWNLOAD_LIST");
					else
						intent = new Intent("gfan.intent.action.DOWNLOAD_OPEN");
					intent.putExtra("status", 200);
					intent1 = intent;
					s2 = s1;
					intent2 = intent1;
				}
				intent2.setClassName("com.unistrong.appstore",
						DownloadReceiver.class.getName());
				intent2.setData(uri1);
				notification.setLatestEventInfo(mContext, s, s2,
						PendingIntent.getBroadcast(mContext, 0, intent2, 0));
				intent3 = new Intent("gfan.intent.action.DOWNLOAD_HIDE");
				intent3.setClassName("com.unistrong.appstore",
						DownloadReceiver.class.getName());
				intent3.setData(uri1);
				notification.deleteIntent = PendingIntent.getBroadcast(
						mContext, 0, intent3, 0);
				notification.when = cursor.getLong(3);
				mNotificationManager.notify(cursor.getInt(0), notification);
				cursor.moveToNext();
			}
			cursor.close();
		}
	}

	private void updateOtaNotification() {
		ContentResolver localContentResolver = this.mContext
				.getContentResolver();
		Uri localUri = DownloadManager.Impl.CONTENT_URI;
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "_id";
		arrayOfString[1] = "_data";
		Cursor localCursor = localContentResolver
				.query(localUri,
						arrayOfString,
						"status >= '200' AND source == '3' AND visibility == '0' AND mimetype == 'application/vnd.android.package-archive'",
						null, "_id");
		if (localCursor == null)
			return;

		if (!localCursor.moveToFirst())
			return;

		String str = localCursor.getString(1);
		if (TextUtils.isEmpty(str))
			return;

		try {
			File localFile = new File(this.mContext.getFilesDir(),
					"aMarket.apk");
			if (Utils.copyMarketFile(this.mContext, str, "aMarket.apk"))
				Utils.installApk(this.mContext, localFile);
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("visibility", Integer.valueOf(2));
			this.mContext.getContentResolver().update(
					DownloadManager.Impl.CONTENT_URI, localContentValues,
					"_id = " + localCursor.getLong(0), null);
			localCursor.close();
		} catch (Exception localException) {
			Utils.E("ota exception", localException);
			localCursor.close();
		}
	}

	public void cancelNotification(long paramLong) {
		if (this.mNotificationManager != null)
			this.mNotificationManager.cancel((int) paramLong);
	}

	public void clearAllNotification() {
		if (this.mNotificationManager != null)
			this.mNotificationManager.cancelAll();
	}

	public void clearBadNotification() {
		Utils.D("start clear bad notifications");
		Cursor localCursor = this.mContext
				.getContentResolver()
				.query(DownloadManager.Impl.CONTENT_URI,
						null,
						"status >= '400' AND (visibility == '0' OR visibility == '1') AND mimetype == 'application/vnd.android.package-archive'",
						null, "_id");
		if (localCursor == null)
			return;

		int i = localCursor.getColumnIndexOrThrow("_id");
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("visibility", Integer.valueOf(2));
			this.mContext.getContentResolver().update(
					DownloadManager.Impl.CONTENT_URI, localContentValues, null,
					null);
			this.mNotificationManager.cancel(localCursor.getInt(i));
			localCursor.moveToNext();
		}
		localCursor.close();
	}

	public void updateNotification() {
		updateActiveNotification();
		updateCompletedNotification();
		updateOtaNotification();
	}

	static class NotificationItem {
		long mCurrentBytes = 0L;
		int mId;
		String mPackageName;
		String mPausedText = null;
		int mTitleCount = 0;
		String[] mTitles = new String[2];
		long mTotalBytes = 0L;

		void addItem(String paramString, long paramLong1, long paramLong2) {
			this.mCurrentBytes = (paramLong1 + this.mCurrentBytes);
			if ((paramLong2 <= 0L) || (this.mTotalBytes == -1L))
				;
			for (this.mTotalBytes = -1L;; this.mTotalBytes = (paramLong2 + this.mTotalBytes)) {
				if (this.mTitleCount < 2)
					this.mTitles[this.mTitleCount] = paramString;
				this.mTitleCount = (1 + this.mTitleCount);
				return;
			}
		}
	}
}