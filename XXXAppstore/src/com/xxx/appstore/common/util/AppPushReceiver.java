package com.xxx.appstore.common.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.AlarmManageUtils;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.ui.HomeTabActivity;

public class AppPushReceiver extends BroadcastReceiver {

   public void onReceive(Context var1, Intent var2) {
      Session var3 = Session.get(var1);
      if("com.unistrong.appstore.download.intent".equals(var2.getAction())) {
         Intent var4 = new Intent(var1, HomeTabActivity.class);
         var4.setFlags(268435456);
         var4.putExtra("click.downloading", true);
         var1.startActivity(var4);
      } else if(var3.isNotificationRecommendApps()) {
         Utils.D("AppPushReceiver onReceive");
         AlarmManageUtils.notifyPushService(var1, false);
      }

   }
}
