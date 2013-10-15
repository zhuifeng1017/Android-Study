package com.xxx.appstore.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.widget.AppListAdapter;
import com.xxx.appstore.common.widget.LazyloadListActivity;
import com.xxx.appstore.common.widget.LoadingDrawable;
import com.xxx.appstore.ui.PreloadActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultActivity extends LazyloadListActivity implements ApiAsyncTask.ApiRequestListener, OnItemClickListener {

   private static final int STATUS_INIT = 0;
   private static final int STATUS_LOADING = 1;
   private static final int STATUS_NODATA = 2;
   private static final int STATUS_RETRY = 3;
   private AppListAdapter mAdapter;
   private int mEndPosition;
   private String mKeywords;
   private FrameLayout mLoading;
   private TextView mNoData;
   private ProgressBar mProgress;
   private int mSearchType;
   private int mTotalSize;
   private TextView title;


   private View createTitleView() {
      View var1 = this.getLayoutInflater().inflate(2130903045, (ViewGroup)null, false);
      this.title = (TextView)var1.findViewById(2131492875);
      return var1;
   }

   private void switchHintStatus(int var1) {
      switch(var1) {
      case 0:
         this.mNoData.setClickable(false);
         this.mNoData.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
         this.mNoData.setText(2131296270);
         this.mProgress.setVisibility(8);
         this.mNoData.setVisibility(0);
         break;
      case 1:
         this.mNoData.setClickable(false);
         this.mProgress.setVisibility(0);
         this.mNoData.setVisibility(8);
         break;
      case 2:
         this.mNoData.setClickable(false);
         this.mNoData.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
         this.mNoData.setText(2131296269);
         this.mProgress.setVisibility(8);
         this.mNoData.setVisibility(0);
         break;
      case 3:
         this.mNoData.setClickable(true);
         this.mNoData.setCompoundDrawablesWithIntrinsicBounds(0, 2130837556, 0, 0);
         this.mNoData.setText(2131296416);
         this.mProgress.setVisibility(8);
         this.mNoData.setVisibility(0);
      }

   }

   public AppListAdapter doInitListAdapter() {
      if(this.mSearchType == 0) {
         this.mAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903124, new String[]{"icon_url", "name", "author_name", "price", "rating", "product_download"}, new int[]{2131492867, 2131492868, 2131492905, 2131492866, 2131492964, 2131492965});
         this.mAdapter.setProductList();
      } else {
         this.mAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903077, new String[]{"search_result_title"}, new int[]{2131492868});
         this.mAdapter.setContainsPlaceHolder(true);
         this.mAdapter.setPlaceHolderResource(2130903059);
      }

      return this.mAdapter;
   }

   public boolean doInitView(Bundle var1) {
      this.setContentView(2130903092);
      this.mSearchType = this.getIntent().getIntExtra("extra.search.type", 0);
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.switchHintStatus(0);
      this.mList = (ListView)this.findViewById(16908298);
      this.mList.setVisibility(8);
      this.mList.addHeaderView(this.createTitleView(), (Object)null, false);
      this.mList.setEmptyView(this.mLoading);
      this.mList.setOnItemClickListener(this);
      return true;
   }

   public void doLazyload() {
      if(this.mSearchType == 0) {
         MarketAPI.search(this.getApplicationContext(), this, this.getItemsPerPage(), this.getStartIndex(), 0, this.mKeywords);
      } else {
         MarketAPI.getSearchFromBBS(this.getApplicationContext(), this, this.mKeywords, this.getStartIndex(), this.getItemsPerPage());
      }

      this.switchHintStatus(1);
   }

   public int getEndIndex() {
      return this.mEndPosition;
   }

   protected int getItemCount() {
      return this.mTotalSize;
   }

   public void onError(int var1, int var2) {
      if(var2 == 610) {
         this.switchHintStatus(2);
      } else if(var2 == 600) {
         this.switchHintStatus(3);
      }

      this.setLoadResult(false);
   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      HashMap var6 = (HashMap)this.mAdapter.getItem(var3 - this.mList.getHeaderViewsCount());
      if(this.mSearchType == 0) {
         String var12 = (String)var6.get("p_id");
         Intent var13 = new Intent(this.getApplicationContext(), PreloadActivity.class);
         var13.putExtra("extra.key.pid", var12);
         var13.putExtra("extra.key.source.type", (String)var6.get("source_type"));
         this.startActivity(var13);
      } else {
         String var7 = (String)var6.get("downloadUrl");
         String var8 = (String)var6.get("search_result_title");
         DownloadManager.Request var9 = new DownloadManager.Request(Uri.parse(var7));
         var9.setTitle(var8);
         var9.setSourceType(1);
         this.mSession.getDownloadManager().enqueue(this.getApplicationContext(), var9, (DownloadManager.EnqueueListener)null);
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296271), false);
         Utils.trackEvent(this.getApplicationContext(), new String[]{"搜索", "附件下载"});
         Utils.submitDownloadLog(this.getApplicationContext(), 0, 1, var7, "");
      }

   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      if(var1 == 4) {
         this.resetSearchResult();
      }

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
      case 11:
         this.mList.setVisibility(0);
         HashMap var7 = (HashMap)var2;
         this.mTotalSize = ((Integer)var7.get("total_size")).intValue();
         this.mEndPosition = ((Integer)var7.get("end_position")).intValue();
         if(this.mTotalSize > 0) {
            ArrayList var8 = (ArrayList)var7.get("product_list");
            this.mAdapter.addData(var8);
            TextView var9 = this.title;
            Object[] var10 = new Object[]{Integer.valueOf(this.mTotalSize)};
            var9.setText(this.getString(2131296272, var10));
         } else {
            this.switchHintStatus(2);
         }

         this.setLoadResult(true);
         break;
      case 39:
         this.mList.setVisibility(0);
         HashMap var3 = (HashMap)var2;
         this.mTotalSize = ((Integer)var3.get("total_size")).intValue();
         this.mEndPosition = ((Integer)var3.get("end_position")).intValue();
         if(this.mTotalSize > 0) {
            ArrayList var4 = (ArrayList)((ArrayList)var3.get("bbsAttJkVOList"));
            this.mAdapter.addData(var4);
            TextView var5 = this.title;
            Object[] var6 = new Object[]{Integer.valueOf(this.mTotalSize)};
            var5.setText(this.getString(2131296272, var6));
         } else {
            this.switchHintStatus(2);
         }

         this.setLoadResult(true);
      }

   }

   public void resetSearchResult() {
      this.switchHintStatus(0);
      this.reset();
   }

   public void setSearchKeyword(String var1) {
      this.mKeywords = var1;
      this.resetSearchResult();
   }
}
