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
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.ThemeManager;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.widget.BaseTabActivity;
import com.xxx.appstore.ui.FollowedRecommendActivity;
import com.xxx.appstore.ui.RecommendListActivity;

public class MasterTabActivity extends BaseTabActivity implements OnTabChangeListener {

   private static final String ALL = "all";
   private static final String FOLLOW = "follow";
   private TabHost mTabHost;
   private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         TopBar.initSkin(MasterTabActivity.this.mSession, MasterTabActivity.this);
         MasterTabActivity.this.initSkin();
      }
   };


   private void initSkin() {
      this.findViewById(2131492901).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));

      for(int var1 = 0; var1 < 2; ++var1) {
         TextView var2 = (TextView)this.mTabHost.getTabWidget().getChildTabViewAt(var1);
         if(var1 == 0) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296642), -1, var2);
         } else if(var1 == 1) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296643), 1, var2);
         }
      }

   }

   private void initTopBar() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035), this.findViewById(2131493033)};
      TopBar.createTopBar(var1, this, var2, new int[]{0, 0}, this.getString(2131296637));
   }

   private void initView() {
      this.mTabHost = (TabHost)this.findViewById(16908306);
      this.mTabHost.setup();
      this.mTabHost.getTabWidget().setPadding(this.mSession.mTabMargin72, 0, this.mSession.mTabMargin72, 0);
      ((FrameLayout)this.mTabHost.findViewById(2131492901)).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));
      Intent var1 = new Intent(this.getApplicationContext(), RecommendListActivity.class);
      TabSpec var2 = this.mTabHost.newTabSpec("all").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296642), -1, (TextView)null)).setContent(var1);
      this.mTabHost.addTab(var2);
      Intent var3 = new Intent(this.getApplicationContext(), FollowedRecommendActivity.class);
      var3.addFlags(67108864);
      TabSpec var5 = this.mTabHost.newTabSpec("follow").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296643), 1, (TextView)null)).setContent(var3);
      this.mTabHost.addTab(var5);
      this.mTabHost.setOnTabChangedListener(this);
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903051);
      this.initTopBar();
      this.initView();
      IntentFilter var2 = new IntentFilter("com.unistrong.appstore.theme");
      this.registerReceiver(this.mThemeReceiver, var2);
   }

   protected void onDestroy() {
      super.onDestroy();
      this.unregisterReceiver(this.mThemeReceiver);
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      return this.getParent().onKeyDown(var1, var2);
   }

   public void onTabChanged(String var1) {}
}
