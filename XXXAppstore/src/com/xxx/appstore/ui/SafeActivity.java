package com.xxx.appstore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.widget.AppListAdapter;
import com.xxx.appstore.common.widget.BaseActivity;
import com.xxx.appstore.common.widget.LoadingDrawable;
import com.xxx.appstore.ui.PreloadActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class SafeActivity extends BaseActivity implements OnItemClickListener {

   private AppListAdapter mAdapter;
   private ListView mList;


   private View createHeaderView() {
      TextView var1 = (TextView)LayoutInflater.from(this.getApplicationContext()).inflate(2130903101, this.mList, false);
      var1.setText("机锋市场的应用经过以下厂商检测，保证安全、无毒，请放心下载。");
      return var1;
   }

   private void doInitList() {
      ArrayList var1 = new ArrayList();
      HashMap var2 = new HashMap();
      var2.put("icon_url", this.getResources().getDrawable(2130837619));
      var2.put("name", "360手机安全卫士");
      var2.put("info", "全方位的手机安全和隐私保护。");
      var1.add(var2);
      HashMap var7 = new HashMap();
      var7.put("icon_url", this.getResources().getDrawable(2130837620));
      var7.put("name", "金山手机卫士");
      var7.put("info", "防骚扰、防病毒、隐私保护、查健康。");
      var1.add(var7);
      HashMap var12 = new HashMap();
      var12.put("icon_url", this.getResources().getDrawable(2130837621));
      var12.put("name", "QQ手机管家");
      var12.put("info", "独具卡巴双核查杀引擎，专业保护手机安全。");
      var1.add(var12);
      this.mAdapter = new AppListAdapter(this.getApplicationContext(), var1, 2130903100, new String[]{"icon_url", "name", "info"}, new int[]{2131492974, 2131492975, 2131492976});
      this.mAdapter.setActivity(this);
      this.mList.setAdapter(this.mAdapter);
   }

   public void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903050);
      Session var2 = this.mSession;
      View[] var3 = new View[]{this.findViewById(2131493035)};
      TopBar.createTopBar(var2, this, var3, new int[]{0}, this.getString(2131296663));
      FrameLayout var4 = (FrameLayout)this.findViewById(2131492978);
      ProgressBar var5 = (ProgressBar)var4.findViewById(2131492869);
      var5.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      var5.setVisibility(0);
      this.mList = (ListView)this.findViewById(16908298);
      this.mList.setEmptyView(var4);
      this.mList.setOnItemClickListener(this);
      this.mList.addHeaderView(this.createHeaderView(), (Object)null, false);
      this.doInitList();
   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      Intent var6 = new Intent(this.getApplicationContext(), PreloadActivity.class);
      var6.setFlags(536870912);
      if(var3 == 1) {
         var6.putExtra("extra.key.pid", String.valueOf('\uaff0'));
      } else if(var3 == 2) {
         var6.putExtra("extra.key.pid", String.valueOf(99207));
      } else if(var3 == 3) {
         var6.putExtra("extra.key.pid", String.valueOf(21363));
      }

      var6.putExtra("extra.order", "0");
      this.startActivity(var6);
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
