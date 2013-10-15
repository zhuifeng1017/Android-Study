package com.xxx.appstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.xxx.appstore.common.util.Pair;
import com.xxx.appstore.common.util.SecurityUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class SessionManager implements Observer {

   public static final String P_AUTO_DELETE = "delete_after_installation";
   public static final String P_CARD_VERSION = "pref.card.version";
   public static final String P_CATEGORY_VERSION = "pref.category.version";
   public static final String P_CLEAR_CACHE = "auto_clear_cache";
   public static final String P_CURRENT_VERSION = "pref.current.version";
   public static final String P_DEFAULT_CHARGE_TYPE = "pref.charge.defaultChargeType";
   public static final String P_ISFIRST_LOGIN = "pref.is.first.login";
   public static final String P_ISLOGIN = "pref.isLogin";
   public static final String P_LPNS_BINDED_DEVID = "pref.lpns.binded.devid";
   public static final String P_LPNS_IS_BINDED = "pref.lpns.is.binded";
   public static final String P_MARKET_PASSWORD = "pref.market.password";
   public static final String P_MARKET_USERNAME = "pref.market.username";
   public static final String P_NOT_DOWNLOAD_IMAGE = "not_download_image";
   public static final String P_NO_APP_FILTER = "no_app_filter";
   public static final String P_OS_VERSION = "pref.os.version";
   public static final String P_PRODUCT_UPDATE_CHECK_TIMESTAMP = "pref.product.update.timestamp";
   public static final String P_RECOMMEND_APP_NOTIFICATION = "pref.recommend.app";
   public static final String P_RECOMMEND_APP_NOTIFICATION_CHECK_TIME = "pref.recommend.time";
   public static final String P_REMEMBER_PASSWORD = "pref.remember.password";
   public static final String P_SCREEN_SIZE = "pref.screen.size";
   public static final String P_SPLASH_ID = "pref.splash.id";
   public static final String P_SPLASH_TIME = "pref.splash.time";
   public static final String P_THEME = "pref.theme";
   public static final String P_UID = "pref.uid";
   public static final String P_UPDATE_APP_NOTIFICATION = "update_app_notification";
   public static final String P_UPDATE_ID = "pref.update.id";
   public static final String P_UPGRADE_NUM = "pref.upgrade.num";
   public static final String P_USER_COOKIES = "pref.cookies";
   private static SessionManager mInstance;
   private static final Method sApplyMethod = findApplyMethod();
   private Context mContext;
   private Thread mCurrentUpdateThread;
   private SharedPreferences mPreference;
   private LinkedList<Pair<String, Object>> mUpdateQueue = new LinkedList();


   private SessionManager(Context var1) {
      synchronized(this) {
         this.mContext = var1;
         if(this.mPreference == null) {
            this.mPreference = PreferenceManager.getDefaultSharedPreferences(this.mContext);
         }

      }
   }

   public static void apply(Editor var0) {
      if(sApplyMethod != null) {
         try {
            sApplyMethod.invoke(var0, new Object[0]);
            return;
         } catch (InvocationTargetException var4) {
            ;
         } catch (IllegalAccessException var5) {
            ;
         }
      }

      var0.commit();
   }

   private static Method findApplyMethod() {
      Method var1;
      Method var2;
      try {
         var2 = Editor.class.getMethod("apply", new Class[0]);
      } catch (NoSuchMethodException var3) {
         var1 = null;
         return var1;
      }

      var1 = var2;
      return var1;
   }

   public static SessionManager get(Context var0) {
      if(mInstance == null) {
         mInstance = new SessionManager(var0);
      }

      return mInstance;
   }

   private boolean isPreferenceNull() {
      boolean var1;
      if(this.mPreference == null) {
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   private void writePreference() {
      Editor var1 = this.mPreference.edit();
      LinkedList var2 = this.mUpdateQueue;
      synchronized(var2) {
         while(!this.mUpdateQueue.isEmpty()) {
            Pair var4 = (Pair)this.mUpdateQueue.remove();
            String var5 = (String)var4.first;
            if(!"pref.uid".equals(var5) && !"pref.market.username".equals(var5) && !"pref.market.password".equals(var5)) {
               if(!"pref.isLogin".equals(var5) && !"pref.lpns.is.binded".equals(var5) && !"pref.is.first.login".equals(var5)) {
                  if(!"pref.screen.size".equals(var5) && !"pref.os.version".equals(var5) && !"pref.lpns.binded.devid".equals(var5) && !"pref.charge.defaultChargeType".equals(var5)) {
                     if(!"pref.upgrade.num".equals(var5) && !"pref.card.version".equals(var5) && !"pref.current.version".equals(var5)) {
                        if("pref.product.update.timestamp".equals(var5) || "pref.splash.time".equals(var5) || "pref.splash.id".equals(var5) || "pref.update.id".equals(var5)) {
                           var1.putLong(var5, ((Long)var4.second).longValue());
                        }
                     } else {
                        var1.putInt(var5, ((Integer)var4.second).intValue());
                     }
                  } else {
                     var1.putString(var5, (String)var4.second);
                  }
               } else {
                  var1.putBoolean(var5, ((Boolean)var4.second).booleanValue());
               }
            } else {
               var1.putString(var5, SecurityUtil.encrypt(String.valueOf(var4.second)));
            }
         }
      }

      apply(var1);
   }

   private void writePreferenceSlowly() {
      if(this.mCurrentUpdateThread == null || !this.mCurrentUpdateThread.isAlive()) {
         this.mCurrentUpdateThread = new Thread() {
            public void run() {
               try {
                  sleep(10000L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
               }

               SessionManager.this.writePreference();
            }
         };
         this.mCurrentUpdateThread.setPriority(10);
         this.mCurrentUpdateThread.start();
      }

   }

   public void close() {
      this.mPreference = null;
      mInstance = null;
   }

   public String getLastNotificationTime() {
      return this.mPreference.getString("pref.recommend.time", (String)null);
   }

   public int getTheme() {
      return this.mPreference.getInt("pref.theme", 2131361833);
   }

   public boolean isAutoDelete() {
      return this.mPreference.getBoolean("delete_after_installation", true);
   }

   public boolean isNotificationRecommendApps() {
      return this.mPreference.getBoolean("pref.recommend.app", true);
   }

   public boolean isNotificationUpdateApps() {
      return this.mPreference.getBoolean("update_app_notification", true);
   }

   public boolean isStopDownloadImage() {
      return this.mPreference.getBoolean("not_download_image", false);
   }

   public HashMap<String, Object> readPreference() {
      HashMap var1;
      if(this.isPreferenceNull()) {
         var1 = null;
      } else {
         var1 = new HashMap();
         String var2 = this.mPreference.getString("pref.uid", (String)null);
         String var3;
         if(var2 == null) {
            var3 = "";
         } else {
            var3 = SecurityUtil.decrypt(var2);
         }

         var1.put("pref.uid", var3);
         var1.put("pref.screen.size", this.mPreference.getString("pref.screen.size", "320#480"));
         var1.put("pref.os.version", Integer.valueOf(this.mPreference.getInt("pref.os.version", 0)));
         var1.put("pref.isLogin", Boolean.valueOf(this.mPreference.getBoolean("pref.isLogin", false)));
         String var8 = this.mPreference.getString("pref.market.username", "");
         String var9;
         if(var8 == null) {
            var9 = "";
         } else {
            var9 = SecurityUtil.decrypt(var8);
         }

         var1.put("pref.market.username", var9);
         String var11 = this.mPreference.getString("pref.market.password", (String)null);
         String var12;
         if(var11 == null) {
            var12 = "";
         } else {
            var12 = SecurityUtil.decrypt(var11);
         }

         var1.put("pref.market.password", var12);
         var1.put("auto_clear_cache", Boolean.valueOf(this.mPreference.getBoolean("auto_clear_cache", false)));
         var1.put("pref.card.version", Integer.valueOf(this.mPreference.getInt("pref.card.version", -1)));
         var1.put("pref.lpns.is.binded", Boolean.valueOf(this.mPreference.getBoolean("pref.lpns.is.binded", false)));
         var1.put("pref.lpns.binded.devid", this.mPreference.getString("pref.lpns.binded.devid", ""));
         var1.put("no_app_filter", Boolean.valueOf(this.mPreference.getBoolean("no_app_filter", false)));
         var1.put("pref.upgrade.num", Integer.valueOf(this.mPreference.getInt("pref.upgrade.num", 0)));
         var1.put("pref.product.update.timestamp", Long.valueOf(this.mPreference.getLong("pref.product.update.timestamp", -1L)));
         var1.put("pref.update.id", Long.valueOf(this.mPreference.getLong("pref.update.id", -1L)));
         var1.put("pref.splash.id", Long.valueOf(this.mPreference.getLong("pref.splash.id", -1L)));
         var1.put("pref.splash.time", Long.valueOf(this.mPreference.getLong("pref.splash.time", 0L)));
         var1.put("pref.is.first.login", Boolean.valueOf(this.mPreference.getBoolean("pref.is.first.login", true)));
         var1.put("pref.current.version", Integer.valueOf(this.mPreference.getInt("pref.current.version", -1)));
         var1.put("pref.charge.defaultChargeType", this.mPreference.getString("pref.charge.defaultChargeType", (String)null));
      }

      return var1;
   }

   public void setNotificationTime(String var1) {
      this.mPreference.edit().putString("pref.recommend.time", var1).commit();
   }

   public void setTheme(int var1) {
      this.mPreference.edit().putInt("pref.theme", var1).commit();
   }

   public void update(Observable param1, Object param2) {
      // $FF: Couldn't be decompiled
   }

   public void writePreferenceQuickly() {
      this.mCurrentUpdateThread = new Thread() {
         public void run() {
            SessionManager.this.writePreference();
         }
      };
      this.mCurrentUpdateThread.setPriority(10);
      this.mCurrentUpdateThread.start();
   }
}
