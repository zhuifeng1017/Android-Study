package com.mappn.gfan.common.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.util.AlarmManageUtils;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.ui.HomeTabActivity;

public class AppPushReceiver extends BroadcastReceiver {

   public void onReceive(Context var1, Intent var2) {
      Session var3 = Session.get(var1);
      if("com.mappn.gfan.download.intent".equals(var2.getAction())) {
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
