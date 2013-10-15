package com.xxx.appstore.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.widget.BaseActivity;
import com.xxx.appstore.common.widget.SplashView;
import com.mobclick.android.MobclickAgent;

public class MoreInfoActivity extends BaseActivity implements OnClickListener {

   private SplashView mSplashView;
   private RelativeLayout root;


   private void init() {
      LayoutInflater var1 = this.getLayoutInflater();
      LinearLayout var2 = new LinearLayout(this.getApplicationContext());
      var2.setLayoutParams(new LayoutParams(-1, -1));
      var2.setOrientation(1);
      TextView var3 = new TextView(this.getApplicationContext());
      var3.setPadding(10, 10, 10, 10);
      var3.setTextAppearance(this.getApplicationContext(), 2131361792);
      var3.setBackgroundResource(2130837761);
      var3.setGravity(16);
      var3.setText("联系我们");
      var2.addView(var3, -1, -2);
      RelativeLayout var4 = (RelativeLayout)var1.inflate(2130903099, var2, false);
      var4.setClickable(true);
      var4.setOnClickListener(this);
      ((ImageView)var4.findViewById(2131492867)).setImageResource(2130837588);
      ((TextView)var4.findViewById(2131492868)).setText(this.getString(2131296664));
      ((TextView)var4.findViewById(2131492905)).setText(this.getString(2131296665));
      var4.findViewById(2131492897).setVisibility(0);
      var2.addView(var4);
      RelativeLayout var5 = (RelativeLayout)var1.inflate(2130903099, var2, false);
      var5.setClickable(true);
      ((ImageView)var5.findViewById(2131492867)).setImageResource(2130837732);
      ((TextView)var5.findViewById(2131492868)).setText(this.getString(2131296666));
      ((TextView)var5.findViewById(2131492905)).setText(this.getString(2131296667));
      var2.addView(var5);
      RelativeLayout var6 = (RelativeLayout)var1.inflate(2130903099, var2, false);
      var6.setClickable(true);
      ((ImageView)var6.findViewById(2131492867)).setImageResource(2130837751);
      ((TextView)var6.findViewById(2131492868)).setText(this.getString(2131296671));
      ((TextView)var6.findViewById(2131492905)).setText(this.getString(2131296672));
      var2.addView(var6);
      this.mSplashView = new SplashView(this.getApplicationContext(), var2);
      LayoutParams var7 = new LayoutParams(-1, -1);
      var7.addRule(3, 2131492884);
      this.mSplashView.setLayoutParams(var7);
      this.root.addView(this.mSplashView);
   }

   private void initTopBar() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035), this.findViewById(2131493033)};
      TopBar.createTopBar(var1, this, var2, new int[]{0, 8}, this.getString(2131296553));
   }

   public void onClick(View var1) {
      Intent var2 = new Intent("android.intent.action.SEND");
      var2.setType("plain/text");
      var2.putExtra("android.intent.extra.EMAIL", new String[]{"gfan.support@mappn.com"});
      Utils.trackEvent(this.getApplicationContext(), new String[]{"菜单", "点击邮件反馈"});

      try {
         this.startActivity(var2);
      } catch (ActivityNotFoundException var6) {
         ;
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.root = (RelativeLayout)this.getLayoutInflater().inflate(2130903062, (ViewGroup)null, false);
      this.init();
      this.setContentView(this.root);
      this.initTopBar();
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
