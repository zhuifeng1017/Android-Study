package com.xxx.appstore.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import com.gfan.sdk.statistics.Collector;
import com.xxx.appstore.Session;
import com.xxx.appstore.SessionManager;
import com.xxx.appstore.common.util.AlarmManageUtils;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.mobclick.android.MobclickAgent;

public class ClientPreferenceActivity extends PreferenceActivity {

   private OnPreferenceChangeListener changeListener = new OnPreferenceChangeListener() {
      public boolean onPreferenceChange(Preference var1, Object var2) {
         boolean var3;
         if("theme_dark".equals(var1.getKey())) {
            if(((Boolean)var2).booleanValue()) {
               ClientPreferenceActivity.this.mDarkTheme.setEnabled(false);
               ClientPreferenceActivity.this.mLightTheme.setEnabled(true);
               ClientPreferenceActivity.this.mLightTheme.setChecked(false);
               var1.getEditor().putInt("pref.theme", 2131361833).commit();
               ClientPreferenceActivity.this.sendBroadcast(new Intent("com.xxx.appstore.theme"));
               Utils.trackEvent(ClientPreferenceActivity.this.getApplicationContext(), new String[]{"设置", "换肤到深色系"});
            }

            var3 = true;
         } else if("theme_light".equals(var1.getKey())) {
            if(((Boolean)var2).booleanValue()) {
               ClientPreferenceActivity.this.mLightTheme.setEnabled(false);
               ClientPreferenceActivity.this.mDarkTheme.setEnabled(true);
               ClientPreferenceActivity.this.mDarkTheme.setChecked(false);
               var1.getEditor().putInt("pref.theme", 2131361832).commit();
               ClientPreferenceActivity.this.sendBroadcast(new Intent("com.xxx.appstore.theme"));
               Utils.trackEvent(ClientPreferenceActivity.this.getApplicationContext(), new String[]{"设置", "换肤到浅色系"});
            }

            var3 = true;
         } else if("update_app_notification".equals(var1.getKey())) {
            Context var16 = ClientPreferenceActivity.this.getApplicationContext();
            String[] var17 = new String[]{"设置", "设置应用更新通知" + String.valueOf(var2)};
            Utils.trackEvent(var16, var17);
            var3 = true;
         } else if("pref_market_app_not_download_image".equals(var1.getKey())) {
            Context var14 = ClientPreferenceActivity.this.getApplicationContext();
            String[] var15 = new String[]{"设置", "设置禁止加载图片" + String.valueOf(var2)};
            Utils.trackEvent(var14, var15);
            var3 = true;
         } else if("pref.recommend.app".equals(var1.getKey())) {
            if(((Boolean)var2).booleanValue()) {
               Context var12 = ClientPreferenceActivity.this.getApplicationContext();
               String[] var13 = new String[]{"设置", "设置机锋推荐" + String.valueOf(var2)};
               Utils.trackEvent(var12, var13);
               AlarmManageUtils.notifyPushService(ClientPreferenceActivity.this.getApplicationContext(), false);
            } else {
               AlarmManageUtils.cancelPushService(ClientPreferenceActivity.this.getApplicationContext());
               Context var10 = ClientPreferenceActivity.this.getApplicationContext();
               String[] var11 = new String[]{"设置", "设置机锋推荐" + String.valueOf(var2)};
               Utils.trackEvent(var10, var11);
            }

            var3 = true;
         } else {
            if("delete_after_installation".equals(var1.getKey())) {
               if(((Boolean)var2).booleanValue()) {
                  var1.getEditor().putInt("pref.theme", 2131361833).commit();
                  Context var8 = ClientPreferenceActivity.this.getApplicationContext();
                  String[] var9 = new String[]{"设置", "设置自动删除APK文件" + String.valueOf(var2)};
                  Utils.trackEvent(var8, var9);
                  SessionManager.get(ClientPreferenceActivity.this.getApplicationContext()).setTheme(2131361833);
               } else {
                  var1.getEditor().putInt("pref.theme", 2131361832).commit();
                  Context var5 = ClientPreferenceActivity.this.getApplicationContext();
                  String[] var6 = new String[]{"设置", "设置自动删除APK文件" + String.valueOf(var2)};
                  Utils.trackEvent(var5, var6);
                  SessionManager.get(ClientPreferenceActivity.this.getApplicationContext()).setTheme(2131361832);
               }

               ClientPreferenceActivity.this.sendBroadcast(new Intent("com.xxx.appstore.theme"));
            }

            var3 = false;
         }

         return var3;
      }
   };
   private CheckBoxPreference mDarkTheme;
   private CheckBoxPreference mLightTheme;
   private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         TopBar.initSkin(Session.get(var1), ClientPreferenceActivity.this);
      }
   };


   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      Collector.onError(this);
      this.setContentView(2130903070);
      Session var2 = Session.get(this.getApplicationContext());
      View[] var3 = new View[]{this.findViewById(2131493035)};
      TopBar.createTopBar(var2, this, var3, new int[]{0}, this.getString(2131296355));
      IntentFilter var4 = new IntentFilter("com.xxx.appstore.theme");
      this.registerReceiver(this.mThemeReceiver, var4);
      this.addPreferencesFromResource(2131034112);
      this.findPreference("update_app_notification").setOnPreferenceChangeListener(this.changeListener);
      this.findPreference("pref.recommend.app").setOnPreferenceChangeListener(this.changeListener);
      this.mDarkTheme = (CheckBoxPreference)this.findPreference("theme_dark");
      this.mLightTheme = (CheckBoxPreference)this.findPreference("theme_light");
      this.mDarkTheme.setOnPreferenceChangeListener(this.changeListener);
      this.mLightTheme.setOnPreferenceChangeListener(this.changeListener);
      if(this.mDarkTheme.isChecked()) {
         this.mDarkTheme.setEnabled(false);
         this.mLightTheme.setEnabled(true);
         this.mLightTheme.setChecked(false);
      } else {
         this.mLightTheme.setEnabled(false);
         this.mDarkTheme.setEnabled(true);
         this.mDarkTheme.setChecked(false);
      }

   }

   protected void onDestroy() {
      super.onDestroy();
      this.unregisterReceiver(this.mThemeReceiver);
   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this);
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this);
   }
}
