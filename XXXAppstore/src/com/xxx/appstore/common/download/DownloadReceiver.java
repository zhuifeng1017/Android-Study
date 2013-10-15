package com.xxx.appstore.common.download;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.Uri;

import com.xxx.appstore.common.download.DownloadService;
import com.xxx.appstore.common.util.Utils;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {

   private void handleNotificationBroadcast(Context param1, Intent param2) {
      // $FF: Couldn't be decompiled
   }

   private void hideNotification(Context var1, Uri var2, Cursor var3) {
      ContentValues var4 = new ContentValues();
      var4.put("visibility", Integer.valueOf(2));
      var1.getContentResolver().update(var2, var4, (String)null, (String[])null);
   }

   private void openDownload(Context var1, Cursor var2) {
      String var3 = var2.getString(var2.getColumnIndexOrThrow("_data"));
      String var4 = var2.getString(var2.getColumnIndexOrThrow("mimetype"));
      if(var2.getInt(var2.getColumnIndexOrThrow("destination")) == 0 && !Utils.isSdcardWritable()) {
         Utils.makeEventToast(var1, var1.getString(2131296608), false);
      } else {
         Uri var5 = Uri.parse(var3);
         Uri var6;
         if(var5.getScheme() == null) {
            var6 = Uri.fromFile(new File(var3));
         } else {
            var6 = var5;
         }

         Intent var7 = new Intent("android.intent.action.VIEW");
         var7.setDataAndType(var6, var4);
         var7.setFlags(268435456);

         try {
            var1.startActivity(var7);
         } catch (ActivityNotFoundException var11) {
            Utils.D("no activity for " + var4, var11);
         }
      }

   }

   private void sendNotificationClickedIntent(Context var1, Intent var2, Cursor var3) {
      var1.sendBroadcast(new Intent("com.unistrong.appstore.download.intent"));
   }

   private void startService(Context var1) {
      var1.startService(new Intent(var1, DownloadService.class));
   }

   public void onReceive(Context var1, Intent var2) {
      String var3 = var2.getAction();
      if(var3.equals("android.intent.action.BOOT_COMPLETED")) {
         this.startService(var1);
      } else if(var3.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
         NetworkInfo var4 = (NetworkInfo)var2.getParcelableExtra("networkInfo");
         if(var4 != null && var4.isConnected()) {
            this.startService(var1);
         }
      } else if(var3.equals("gfan.intent.action.DOWNLOAD_WAKEUP")) {
         this.startService(var1);
      } else if(var3.equals("gfan.intent.action.DOWNLOAD_OPEN") || var3.equals("gfan.intent.action.DOWNLOAD_LIST") || var3.equals("gfan.intent.action.DOWNLOAD_HIDE")) {
         this.handleNotificationBroadcast(var1, var2);
      }

   }
}
