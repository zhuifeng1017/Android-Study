package com.mappn.gfan.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.widget.AppListAdapter;
import com.mappn.gfan.common.widget.LazyloadListActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.ui.CategoryActivity;
import com.mappn.gfan.ui.PreloadActivity;
import com.mappn.gfan.ui.RankTabActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductListActivity extends LazyloadListActivity implements ApiAsyncTask.ApiRequestListener, OnItemClickListener, OnClickListener {

   private AppListAdapter mAdapter;
   private String mCategory;
   private String mCategoryId;
   private FrameLayout mLoading;
   private TextView mNoData;
   private ProgressBar mProgress;
   private int mSortType;
   private int mTotalSize;


   public AppListAdapter doInitListAdapter() {
      this.mAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903093, new String[]{"icon_url", "name", "author_name", "is_star", "rating", "product_download"}, new int[]{2131492867, 2131492868, 2131492905, 2131492967, 2131492964, 2131492965});
      this.mAdapter.setProductList();
      if(!TextUtils.isEmpty(this.mCategory)) {
         this.mAdapter.setRankList();
      }

      return this.mAdapter;
   }

   public boolean doInitView(Bundle var1) {
      Intent var2 = this.getIntent();
      boolean var3;
      if(var2 != null) {
         this.mCategory = var2.getStringExtra("extra.category");
         if(TextUtils.isEmpty(this.mCategory)) {
            this.mSortType = var2.getIntExtra("extra.order", 1);
            this.mCategoryId = var2.getStringExtra("extra.category.id");
         }

         this.setContentView(2130903092);
         int var4 = var2.getIntExtra("extra.max.items", 0);
         if(var4 > 0) {
            this.mTotalSize = var4;
         }

         this.mList = (ListView)this.findViewById(16908298);
         this.mLoading = (FrameLayout)this.findViewById(2131492978);
         this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
         this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
         this.mProgress.setVisibility(0);
         this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
         this.mNoData.setOnClickListener(this);
         this.mList.setEmptyView(this.mLoading);
         this.mList.setOnItemClickListener(this);
         this.lazyload();
         var3 = true;
      } else {
         var3 = false;
      }

      return var3;
   }

   public void doLazyload() {
      if(3 == this.mSortType) {
         MarketAPI.getProducts(this.getApplicationContext(), this, this.getItemsPerPage(), this.getStartIndex(), this.mSortType, this.mCategoryId);
      } else if(2 == this.mSortType) {
         MarketAPI.getProducts(this.getApplicationContext(), this, this.getItemsPerPage(), this.getStartIndex(), this.mSortType, this.mCategoryId);
      } else if("grow".equals(this.mCategory)) {
         MarketAPI.getGrowFast(this.getApplicationContext(), this, this.getStartIndex(), this.getItemsPerPage());
      } else {
         MarketAPI.getRankByCategory(this.getApplicationContext(), this, this.getStartIndex(), this.getItemsPerPage(), this.mCategory);
      }

   }

   protected int getItemCount() {
      return this.mTotalSize;
   }

   public void onClick(View var1) {
      super.onClick(var1);
      if(var1.getId() == 2131492979) {
         this.mProgress.setVisibility(0);
         this.mNoData.setVisibility(8);
         this.lazyload();
      }

   }

   public void onError(int var1, int var2) {
      if(var2 != 610) {
         this.mNoData.setVisibility(0);
         this.mProgress.setVisibility(8);
      }

      this.setLoadResult(false);
   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      HashMap var6 = (HashMap)this.mAdapter.getItem(var3);
      if(var6 != null) {
         String var7 = (String)var6.get("name");
         if(((Boolean)var6.get("is_star")).booleanValue()) {
            Context var11 = this.getApplicationContext();
            String[] var12 = new String[]{"分类", "点击分类广告位->[" + var7 + "]"};
            Utils.trackEvent(var11, var12);
         }

         String var8 = (String)var6.get("p_id");
         Intent var9 = new Intent(this.getApplicationContext(), PreloadActivity.class);
         var9.putExtra("extra.key.pid", var8);
         this.startActivity(var9);
      }

   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      Activity var3 = this.getParent();
      boolean var4;
      if(var3 != null) {
         var4 = var3.onKeyDown(var1, var2);
      } else {
         var4 = super.onKeyDown(var1, var2);
      }

      return var4;
   }

   protected void onPause() {
      super.onPause();
      Activity var1 = this.getParent();
      if(var1 instanceof RankTabActivity) {
         MobclickAgent.onPause(var1.getParent());
      } else if(!(var1 instanceof CategoryActivity)) {
         MobclickAgent.onPause(var1);
      }

   }

   protected void onPrepareDialog(int var1, Dialog var2) {
      super.onPrepareDialog(var1, var2);
      if(var2.isShowing()) {
         var2.dismiss();
      }

   }

   protected void onResume() {
      super.onResume();
      Activity var1 = this.getParent();
      if(var1 instanceof RankTabActivity) {
         MobclickAgent.onResume(var1.getParent());
      } else if(!(var1 instanceof CategoryActivity)) {
         MobclickAgent.onResume(var1);
      }

   }

   public void onSuccess(int var1, Object var2) {
      HashMap var3 = (HashMap)var2;
      if(this.mTotalSize <= 0) {
         this.mTotalSize = ((Integer)var3.get("total_size")).intValue();
      }

      this.mAdapter.addData((ArrayList)var3.get("product_list"));
      this.setLoadResult(true);
   }
}
