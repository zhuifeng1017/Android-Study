package com.mappn.gfan.ui;

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
import com.mappn.gfan.Session;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.TopBar;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.widget.BaseTabActivity;
import com.mappn.gfan.ui.ProductListActivity;

public class RankTabActivity extends BaseTabActivity implements OnTabChangeListener {

   private static final int MAX_ITEMS = 100;
   private TabHost mTabHost;
   private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         TopBar.initSkin(RankTabActivity.this.mSession, RankTabActivity.this);
         RankTabActivity.this.initSkin();
      }
   };


   private void initSkin() {
      this.findViewById(2131492901).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));

      for(int var1 = 0; var1 < 4; ++var1) {
         TextView var2 = (TextView)this.mTabHost.getTabWidget().getChildTabViewAt(var1);
         if(var1 == 0) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296315), -1, var2);
         } else if(var1 == 1) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296313), 0, var2);
         } else if(var1 == 2) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296312), 0, var2);
         } else if(var1 == 3) {
            Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296314), 1, var2);
         }
      }

   }

   private void initTopBar() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035), this.findViewById(2131493033)};
      TopBar.createTopBar(var1, this, var2, new int[]{0, 0}, this.getString(2131296311));
   }

   private void initView() {
      this.mTabHost = (TabHost)this.findViewById(16908306);
      this.mTabHost.setup();
      this.mTabHost.getTabWidget().setPadding(this.mSession.mTabMargin9, 0, this.mSession.mTabMargin9, 0);
      ((FrameLayout)this.mTabHost.findViewById(2131492901)).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));
      Intent var1 = new Intent(this.getApplicationContext(), ProductListActivity.class);
      var1.putExtra("extra.category", "grow");
      var1.putExtra("extra.max.items", 100);
      TabSpec var4 = this.mTabHost.newTabSpec("grow").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296315), -1, (TextView)null)).setContent(var1);
      this.mTabHost.addTab(var4);
      Intent var5 = new Intent(this.getApplicationContext(), ProductListActivity.class);
      var5.putExtra("extra.category", "app");
      var5.putExtra("extra.max.items", 100);
      TabSpec var8 = this.mTabHost.newTabSpec("app").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296313), 0, (TextView)null)).setContent(var5);
      this.mTabHost.addTab(var8);
      Intent var9 = new Intent(this.getApplicationContext(), ProductListActivity.class);
      var9.putExtra("extra.category", "game");
      var9.putExtra("extra.max.items", 100);
      TabSpec var12 = this.mTabHost.newTabSpec("game").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296312), 0, (TextView)null)).setContent(var9);
      this.mTabHost.addTab(var12);
      Intent var13 = new Intent(this.getApplicationContext(), ProductListActivity.class);
      var13.putExtra("extra.category", "ebook");
      var13.putExtra("extra.max.items", 100);
      TabSpec var16 = this.mTabHost.newTabSpec("ebook").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296314), 1, (TextView)null)).setContent(var13);
      this.mTabHost.addTab(var16);
      this.mTabHost.setOnTabChangedListener(this);
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903051);
      this.initTopBar();
      this.initView();
      IntentFilter var2 = new IntentFilter("com.mappn.gfan.theme");
      this.registerReceiver(this.mThemeReceiver, var2);
   }

   protected void onDestroy() {
      super.onDestroy();
      this.unregisterReceiver(this.mThemeReceiver);
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      return this.getParent().onKeyDown(var1, var2);
   }

   public void onTabChanged(String var1) {
      if("app".equals(var1)) {
         Utils.trackEvent(this.getApplicationContext(), new String[]{"排行", "应用"});
      } else if("game".equals(var1)) {
         Utils.trackEvent(this.getApplicationContext(), new String[]{"排行", "游戏"});
      } else if("ebook".equals(var1)) {
         Utils.trackEvent(this.getApplicationContext(), new String[]{"排行", "电子书"});
      } else if("grow".equals(var1)) {
         Utils.trackEvent(this.getApplicationContext(), new String[]{"排行", "风向标"});
      }

   }
}
