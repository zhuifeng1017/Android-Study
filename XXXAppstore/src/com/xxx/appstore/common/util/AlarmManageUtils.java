package com.xxx.appstore.common.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.AppPushService;

import java.util.Calendar;
import java.util.Random;

public class AlarmManageUtils {

   private static long calculateAlarmTime(boolean var0) {
      Calendar var1 = Calendar.getInstance();
      int var2 = var1.get(11);
      long var3 = System.currentTimeMillis();
      long var5;
      if(var2 < 12) {
         var1.set(11, 12);
         var1.set(12, 0);
         var5 = var1.getTimeInMillis();
      } else if(!var0 && var2 < 14) {
         var5 = var3 + (long)(new Random()).nextInt(600000);
      } else if(var2 < 18) {
         var1.set(11, 18);
         var1.set(12, 0);
         var5 = var1.getTimeInMillis();
      } else if(!var0 && var2 < 21) {
         var5 = var3 + (long)(new Random()).nextInt(600000);
      } else {
         var1.add(6, 1);
         var1.set(11, 12);
         var1.set(12, 0);
         var5 = var1.getTimeInMillis();
      }

      return var5;
   }

   public static void cancelPushService(Context var0) {
      getAlarmManager(var0).cancel(PendingIntent.getService(var0, 0, new Intent(var0, AppPushService.class), 0));
   }

   private static AlarmManager getAlarmManager(Context var0) {
      return (AlarmManager)var0.getSystemService("alarm");
   }

   public static void notifyPushService(Context var0, boolean var1) {
      if(Session.get(var0).isNotificationRecommendApps()) {
         PendingIntent var2 = PendingIntent.getService(var0, 0, new Intent(var0, AppPushService.class), 0);
         AlarmManager var3 = getAlarmManager(var0);
         long var4 = calculateAlarmTime(var1);
         if(var4 >= 0L) {
            var3.set(0, var4, var2);
         }
      }

   }
}
