package com.xxx.appstore.common.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.SplashInfo;

import java.io.File;

public class CheckSplashUpdateReceiver extends BroadcastReceiver implements ApiAsyncTask.ApiRequestListener {

   private Context mContext;


   public void onError(int var1, int var2) {
      Utils.D("market error when check upgrade info " + var2);
   }

   public void onReceive(Context var1, Intent var2) {
      this.mContext = var1;
      MarketAPI.checkNewSplash(var1, this);
   }

   public void onSuccess(int var1, Object var2) {
      final SplashInfo var3 = (SplashInfo)var2;
      final Session var4 = Session.get(this.mContext);
      if(var3 != null && !TextUtils.isEmpty(var3.url)) {
         File var5 = new File(this.mContext.getFilesDir(), "splash.png");
         if(var5.exists()) {
            var5.delete();
         }

         DownloadManager var6 = var4.getDownloadManager();
         DownloadManager.Request var7 = new DownloadManager.Request(Uri.parse(var3.url));
         var7.setMimeType("image/*");
         var7.setDestination(1);
         var7.setShowRunningNotification(false);
         var7.setTitle("splash.png");
         var6.enqueue(this.mContext, var7, new DownloadManager.EnqueueListener() {
            public void onFinish(long var1) {
               var4.setSplashId(var1);
               var4.setSplashTime(var3.timestamp);
            }
         });
      }

   }
}
