package com.xxx.appstore.common.widget;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.ResponseCacheManager;
import com.xxx.appstore.ui.SearchActivity;
import com.mobclick.android.MobclickAgent;

public class BaseActivity extends ActivityGroup {

   protected Session mSession;


   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.mSession = Session.get(this.getApplicationContext());
      MobclickAgent.onError(this);
   }

   public void onLowMemory() {
      super.onLowMemory();
      ResponseCacheManager.getInstance().clear();
   }

   public boolean onSearchRequested() {
      this.startActivity(new Intent(this.getApplicationContext(), SearchActivity.class));
      return true;
   }
}
