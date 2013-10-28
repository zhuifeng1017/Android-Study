package com.xxx.appstore.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.ThemeManager;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.widget.BaseTabActivity;
import com.xxx.appstore.ui.AppsUpdateActivity;
import com.xxx.appstore.ui.DownloadManagerActivity;

public class AppManagerActivity extends BaseTabActivity {

   private BroadcastReceiver mClickReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         AppManagerActivity.this.mTabHost.setCurrentTabByTag("download");
      }
   };
   private TabHost mTabHost;
   private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         TopBar.initSkin(AppManagerActivity.this.mSession, AppManagerActivity.this);
         AppManagerActivity.this.initSkin();
      }
   };


   private void initSkin() {
      this.findViewById(2131492901).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));

      for(int var1 = 0; var1 < 2; ++var1) {
         TextView var2 = (TextView)this.mTabHost.getTabWidget().getChildTabViewAt(var1);
         if(var1 == 0) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296361), -1, var2);
         } else if(var1 == 1) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296362), 1, var2);
         }
      }

   }

   private void initTopBar() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035), this.findViewById(2131493033)};
      TopBar.createTopBar(var1, this, var2, new int[]{0, 0}, this.getString(2131296554));
   }

   private void initView() {
      this.mTabHost = (TabHost)this.findViewById(16908306);
      ((FrameLayout)this.mTabHost.findViewById(2131492901)).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));
      this.mTabHost.setup();
      this.mTabHost.getTabWidget().setPadding(this.mSession.mTabMargin72, 0, this.mSession.mTabMargin72, 0);
      Intent var1 = new Intent(this.getApplicationContext(), AppsUpdateActivity.class);
      TabSpec var2 = this.mTabHost.newTabSpec("installed").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296361), -1, (TextView)null)).setContent(var1);
      this.mTabHost.addTab(var2);
      Intent var3 = new Intent(this.getApplicationContext(), DownloadManagerActivity.class);
      TabSpec var4 = this.mTabHost.newTabSpec("download").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296362), 1, (TextView)null)).setContent(var3);
      this.mTabHost.addTab(var4);
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903041);
      IntentFilter var2 = new IntentFilter("com.xxx.appstore.download.intent");
      this.registerReceiver(this.mClickReceiver, var2);
      IntentFilter var4 = new IntentFilter("com.xxx.appstore.theme");
      this.registerReceiver(this.mThemeReceiver, var4);
      this.initTopBar();
      this.initView();
   }

   protected void onDestroy() {
      super.onDestroy();
      this.unregisterReceiver(this.mClickReceiver);
      this.unregisterReceiver(this.mThemeReceiver);
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      return this.getParent().onKeyDown(var1, var2);
   }
}
