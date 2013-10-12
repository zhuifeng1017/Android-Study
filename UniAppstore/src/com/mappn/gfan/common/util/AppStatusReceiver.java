package com.mappn.gfan.common.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.util.AlarmManageUtils;
import com.mappn.gfan.common.util.DBUtils;
import com.mappn.gfan.common.util.Utils;
import java.io.File;

public class AppStatusReceiver extends BroadcastReceiver {

   public void onReceive(Context var1, Intent var2) {
      String var3 = var2.getAction();
      String var4 = var2.getData().getSchemeSpecificPart();
      Session var5 = Session.get(var1);
      if("android.intent.action.PACKAGE_REMOVED".equals(var3)) {
         var5.removeInstalledApp(var4);
         DBUtils.removeUpgradable(var1, var5, var4);
         if(var5.mNotSameApps.containsKey(var4)) {
            Utils.installApk(var1, new File((String)var5.mNotSameApps.get(var4)));
            var5.mNotSameApps.remove(var4);
         }
      } else if("android.intent.action.PACKAGE_REPLACED".equals(var3) && "com.mappn.gfan".equals(var4)) {
         AlarmManageUtils.notifyPushService(var1, false);
      }

   }
}
