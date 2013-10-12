package com.mappn.gfan.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mappn.gfan.R;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.TopBar;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.widget.AppListAdapter;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.common.widget.TopGallery;
import com.mappn.gfan.ui.PreloadActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends BaseActivity implements OnClickListener, OnItemClickListener, ApiAsyncTask.ApiRequestListener {

   private static final int LOAD_NUM = 50;
   private static final int STATUS_ERROR = 0;
   private static final int STATUS_INIT = -1;
   private static final int STATUS_OK = 1;
   private ArrayList<HashMap<String, Object>> mBottomData;
   private int mIsBottomLoaded = -1;
   private int mIsToLoaded = -1;
   private FrameLayout mLoading;
   private TextView mNoData;
   private ProgressBar mProgress;
   private AppListAdapter mRecommendAdapter;
   private ListView mRecommendList;
   private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         TopBar.initSkin(HomeActivity.this.mSession, HomeActivity.this);
      }
   };
   private ArrayList<HashMap<String, Object>> mTopData;


   private void bindAdapter() {
      if(this.mIsToLoaded == 1 && this.mIsBottomLoaded == 1) {
         if(this.mTopData != null && this.mRecommendList.getHeaderViewsCount() == 0) {
            TopGallery var1 = new TopGallery(this, this.mRecommendList, this.mTopData);
            this.mRecommendList.addHeaderView(var1.getView(), (Object)null, false);
         }

         this.mRecommendAdapter = new AppListAdapter(this.getApplicationContext(), this.mBottomData, 2130903093, new String[]{"icon_url", "name", "sub_category", "is_star", "app_size", "product_download"}, new int[]{2131492867, 2131492868, 2131492905, 2131492967, 2131492966, 2131492965});
         this.mRecommendAdapter.setActivity(this.getParent());
         this.mRecommendAdapter.setProductList();
         this.mRecommendList.setAdapter(this.mRecommendAdapter);
         this.mRecommendList.setOnItemClickListener(this);
      }

   }

   private void handleBottomContent(ArrayList<HashMap<String, Object>> var1) {
      if(var1 != null && var1.size() != 0) {
         this.mBottomData = var1;
         this.mIsBottomLoaded = 1;
         this.bindAdapter();
      }

   }

   private void handleTopContent(ArrayList<HashMap<String, Object>> var1) {
      if(var1 != null && var1.size() != 0) {
         HashMap var2 = new HashMap();
         var2.put("id", (Object)null);
         var2.put("icon_url", Integer.valueOf(R.drawable.app_icon));
         var2.put("name", this.getString(R.string.sort_install_nessary_title));
         var2.put("pic_url", Integer.valueOf(R.drawable.install_topic));
         if(this.mSession.isFirstLogin()) {
            var1.add(0, var2);
         } else {
            var1.add(var2);
         }

         this.mTopData = var1;
         this.mIsToLoaded = 1;
         this.bindAdapter();
      }

   }

   private void initData() {
      MarketAPI.getHomeMasterRecommend(this.getApplicationContext(), this);
      MarketAPI.getHomeRecommend(this.getApplicationContext(), this, 0, 50);
   }

   private void initTopBar() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493034), this.findViewById(2131493033)};
      TopBar.createTopBar(var1, this, var2, new int[]{0, 0}, "");
   }

   private void initView(HashMap<String, Object> var1) {
      this.mRecommendList = (ListView)this.findViewById(16908298);
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.mRecommendList.setEmptyView(this.mLoading);
      if(var1 != null) {
         this.handleTopContent((ArrayList)var1.get("extra.home.data.top"));
         this.handleBottomContent((ArrayList)var1.get("extra.home.data.bottom"));
      } else {
         this.initData();
      }

   }

   public void onClick(View var1) {
      this.mNoData.setVisibility(8);
      this.mProgress.setVisibility(0);
      if(this.mIsToLoaded == 0) {
         this.mIsToLoaded = -1;
         MarketAPI.getHomeMasterRecommend(this.getApplicationContext(), this);
      }

      if(this.mIsBottomLoaded == 0) {
         this.mIsBottomLoaded = -1;
         MarketAPI.getHomeRecommend(this.getApplicationContext(), this, 0, 50);
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(R.layout.activity_home_layout);
      HashMap var2 = (HashMap)this.getIntent().getSerializableExtra("extra.home.data");
      this.initTopBar();
      this.initView(var2);
      IntentFilter var3 = new IntentFilter("com.mappn.gfan.theme");
      this.registerReceiver(this.mThemeReceiver, var3);
   }

   protected void onDestroy() {
      super.onDestroy();
      this.unregisterReceiver(this.mThemeReceiver);
   }

   public void onError(int var1, int var2) {
      switch(var1) {
      case 16:
         this.mIsBottomLoaded = 0;
         this.mNoData.setVisibility(0);
         this.mProgress.setVisibility(8);
         break;
      case 54:
         if(var2 == 610) {
            this.mTopData = null;
            this.mIsToLoaded = 1;
            this.bindAdapter();
         } else {
            this.mIsToLoaded = 0;
            this.mNoData.setVisibility(0);
            this.mProgress.setVisibility(8);
         }
      }

   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      switch(var1.getId()) {
      case 16908298:
         HashMap var6 = (HashMap)this.mRecommendAdapter.getItem(var3 - this.mRecommendList.getHeaderViewsCount());
         String var7 = (String)var6.get("p_id");
         Intent var8 = new Intent(this.getApplicationContext(), PreloadActivity.class);
         var8.putExtra("extra.key.pid", var7);
         if(var3 <= 20) {
            Context var10 = this.getApplicationContext();
            String[] var11 = new String[]{"首页", null};
            Object[] var12 = new Object[]{var6.get("name"), Integer.valueOf(var3)};
            var11[1] = String.format("点击推荐列表->应用名[%s]推荐位[%s]", var12);
            Utils.trackEvent(var10, var11);
            var8.putExtra("extra.key.recommend", true);
         }

         this.startActivity(var8);
      default:
      }
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      return this.getParent().onKeyDown(var1, var2);
   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this.getParent());
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this.getParent());
   }

   public void onSuccess(int var1, Object var2) {
      switch(var1) {
      case 16:
         this.handleBottomContent((ArrayList)((HashMap)var2).get("product_list"));
         break;
      case 54:
         this.handleTopContent((ArrayList)var2);
      }

   }
}
