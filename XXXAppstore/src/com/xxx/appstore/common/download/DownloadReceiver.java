package com.xxx.appstore.common.download;

import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.xxx.appstore.R;
import com.xxx.appstore.common.util.Utils;

public class DownloadReceiver extends BroadcastReceiver {

	/**
     * Handle any broadcast related to a system notification.
     */
    private void handleNotificationBroadcast(Context context, Intent intent) {
        Uri uri = intent.getData();
        String action = intent.getAction();
            if (action.equals(Constants.ACTION_OPEN)) {
            	Utils.V("Receiver open for " + uri);
            } else if (action.equals(Constants.ACTION_LIST)) {
            	Utils.V("Receiver list for " + uri);
            } else { // ACTION_HIDE
            	Utils.V("Receiver hide for " + uri);
            }

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return;
        }
        try {
            if (!cursor.moveToFirst()) {
                return;
            }

            if (action.equals(Constants.ACTION_OPEN)) {
                openDownload(context, cursor);
                hideNotification(context, uri, cursor);
            } else if (action.equals(Constants.ACTION_LIST)) {
                sendNotificationClickedIntent(context, intent, cursor);
            } else { // ACTION_HIDE
                hideNotification(context, uri, cursor);
            }
        } finally {
            cursor.close();
        }
    }

   private void hideNotification(Context context, Uri uri, Cursor cursor) {
      ContentValues values  = new ContentValues();
      values .put(DownloadManager.Impl.COLUMN_VISIBILITY,
    		  DownloadManager.Impl.VISIBILITY_HIDDEN);
      context.getContentResolver().update(uri, values , null, null);
   }

   /**
    * Open the download that cursor is currently pointing to, since it's completed notification
    * has been clicked.
    */
   private void openDownload(Context context, Cursor cursor) {
       String filename = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.Impl.COLUMN_DATA));
       String mimetype =
           cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.Impl.COLUMN_MIME_TYPE));
       if(cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.Impl.COLUMN_DESTINATION)) == 0 && !Utils.isSdcardWritable()) {
           Utils.makeEventToast(context, context.getString(R.string.warning_sdcard_unmounted), false);
        } else {
	       Uri path = Uri.parse(filename);
	       // If there is no scheme, then it must be a file
	       if (path.getScheme() == null) {
	           path = Uri.fromFile(new File(filename));
	       }
	
	       Intent activityIntent = new Intent(Intent.ACTION_VIEW);
	       activityIntent.setDataAndType(path, mimetype);
	       activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	       try {
	           context.startActivity(activityIntent);
	       } catch (ActivityNotFoundException ex) {
	    	   Utils.D("no activity for " + mimetype, ex);
	       }
        }
   }
   
   private void sendNotificationClickedIntent(Context context, Intent intent, Cursor cursor) {
	   context.sendBroadcast(new Intent(com.xxx.appstore.Constants.BROADCAST_CLICK_INTENT));
   }

   private void startService(Context context) {
       context.startService(new Intent(context, DownloadService.class));
   }

   public void onReceive(Context context, Intent intent) {
       String action = intent.getAction();
       if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
           startService(context);
       } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
           NetworkInfo info = (NetworkInfo)
                   intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
           if (info != null && info.isConnected()) {
               startService(context);
           }
       } else if (action.equals(Constants.ACTION_RETRY)) {
           startService(context);
       } else if (action.equals(Constants.ACTION_OPEN)
               || action.equals(Constants.ACTION_LIST)
               || action.equals(Constants.ACTION_HIDE)) {
           handleNotificationBroadcast(context, intent);
       }
   }
}
