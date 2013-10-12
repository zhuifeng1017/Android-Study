package com.mappn.gfan.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mappn.gfan.R;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.download.DownloadManager;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.TopBar;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.DownloadItem;
import com.mappn.gfan.common.widget.AppListAdapter;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InstallNecessaryActivity extends BaseActivity implements OnClickListener, ApiAsyncTask.ApiRequestListener {

   private AppListAdapter mAdapter;
   private ListView mList;
   private FrameLayout mLoading;
   private TextView mNoData;
   private ProgressBar mProgress;


   private AppListAdapter doInitAdapter() {
      AppListAdapter var1 = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903058, new String[]{"logo", "app_title", "app_detail", "is_checked", "is_installed"}, new int[]{2131492867, 2131492868, 2131492905, 2131492907, 2131492906});
      var1.setActivity(this);
      var1.setContainsPlaceHolder(true);
      var1.setPlaceHolderResource(2130903059);
      return var1;
   }

   private void initData() {
      this.mAdapter = this.doInitAdapter();
      this.mList.setAdapter(this.mAdapter);
      MarketAPI.getRequired(this.getApplicationContext(), this);
   }

   private void initTopBar() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035)};
      TopBar.createTopBar(var1, this, var2, new int[]{0}, this.getString(R.string.sort_install_nessary_title));
   }

   private void initView() {
      LinearLayout var1 = (LinearLayout)this.findViewById(2131492889);
      var1.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
      var1.setOnClickListener(this);
      ((ImageView)var1.findViewById(2131492890)).setImageResource(ThemeManager.getResource(this.mSession, 21));
      ((TextView)var1.findViewById(2131492891)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 17));
      this.mList = (ListView)this.findViewById(16908298);
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.mList.setEmptyView(this.mLoading);
      this.mList.setChoiceMode(2);
      this.mList.setItemsCanFocus(false);
   }

   private void startDownload() {
      HashMap var1 = this.mAdapter.getCheckedList();
      if(var1 != null && var1.size() != 0) {
         Iterator var2 = var1.values().iterator();

         while(var2.hasNext()) {
            String var3 = (String)((HashMap)var2.next()).get("p_id");
            MarketAPI.getDownloadUrl(this.getApplicationContext(), this, var3, "0");
         }

         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296370), false);
      } else {
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296305), false);
      }

   }

   public void onClick(View var1) {
      switch(var1.getId()) {
      case 2131492889:
         this.startDownload();
         this.finish();
         break;
      case 2131492979:
         this.mNoData.setVisibility(8);
         this.mProgress.setVisibility(0);
         MarketAPI.getRequired(this.getApplicationContext(), this);
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903052);
      this.initTopBar();
      this.initView();
      this.initData();
   }

   public void onError(int var1, int var2) {
      this.mNoData.setVisibility(0);
      this.mProgress.setVisibility(8);
   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this);
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this);
   }

   public void onSuccess(int var1, Object var2) {
      switch(var1) {
      case 15:
         DownloadItem var3 = (DownloadItem)var2;
         DownloadManager.Request var4 = new DownloadManager.Request(Uri.parse(var3.url));
         HashMap var5 = (HashMap)this.mAdapter.getCheckedList().get(var3.pId);
         var4.setTitle((String)var5.get("app_title"));
         var4.setPackageName(var3.packageName);
         var4.setIconUrl((String)var5.get("logo"));
         var4.setSourceType(0);
         var4.setMD5(var3.fileMD5);
         this.mSession.getDownloadManager().enqueue(this.getApplicationContext(), var4, (DownloadManager.EnqueueListener)null);
         Utils.submitDownloadLog(this.getApplicationContext(), 0, 0, var3.url, var3.packageName);
         break;
      case 38:
         this.mAdapter.addData((ArrayList)var2);
      }

   }
}
