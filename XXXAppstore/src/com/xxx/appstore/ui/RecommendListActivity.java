package com.xxx.appstore.ui;

import android.content.Context;
import android.content.Intent;
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

import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.RecommendTopic;
import com.xxx.appstore.common.widget.AppListAdapter;
import com.xxx.appstore.common.widget.LazyloadListActivity;
import com.xxx.appstore.common.widget.LoadingDrawable;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class RecommendListActivity extends LazyloadListActivity implements ApiAsyncTask.ApiRequestListener, OnItemClickListener, OnClickListener {

   private AppListAdapter mAdapter;
   private int mClickItemPos;
   private FrameLayout mLoading;
   private TextView mNoData;
   private ProgressBar mProgress;
   private int mTotalSize = 0;


   public AppListAdapter doInitListAdapter() {
      this.mAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903074, new String[]{"icon_url", "name", "update_time", "fans"}, new int[]{2131492878, 2131492927, 2131492928, 2131492929});
      return this.mAdapter;
   }

   public boolean doInitView(Bundle var1) {
      this.setContentView(2130903092);
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.mList = (ListView)this.findViewById(16908298);
      this.mList.setEmptyView(this.mLoading);
      this.mList.setOnItemClickListener(this);
      this.lazyload();
      return true;
   }

   public void doLazyload() {
      MarketAPI.getMasterRecommend(this.getApplicationContext(), this, this.getItemsPerPage(), this.getStartIndex());
   }

   protected int getItemCount() {
      return this.mTotalSize;
   }

   protected void onActivityResult(int var1, int var2, Intent var3) {
      if(var2 == -1 && var3 != null) {
         RecommendTopic var4 = (RecommendTopic)var3.getSerializableExtra("extra.recommend.detail");
         if(var4 != null) {
            HashMap var5 = (HashMap)this.mAdapter.getItem(this.mClickItemPos);
            if(var5 != null) {
               var5.put("like", "" + var4.up);
               var5.put("dislike", "" + var4.down);
               this.mAdapter.notifyDataSetChanged();
            }
         }
      }

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
      this.mNoData.setVisibility(0);
      this.mProgress.setVisibility(8);
      this.setLoadResult(false);
   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      this.mClickItemPos = var3;
      RecommendTopic var6 = Utils.mapToTopic((HashMap)this.mAdapter.getItem(this.mClickItemPos));
      Context var7 = this.getApplicationContext();
      String[] var8 = new String[]{"玩家推荐", null};
      Object[] var9 = new Object[]{var6.title};
      var8[1] = String.format("点击达人专题->专题名[%s]", var9);
      Utils.trackEvent(var7, var8);
      Utils.gotoMaster(this, var6);
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
      HashMap var3 = (HashMap)var2;
      this.mTotalSize = ((Integer)var3.get("total_size")).intValue();
      if(this.mTotalSize > 0) {
         this.mAdapter.addData((ArrayList)var3.get("recommend_list"));
      } else {
         HashMap var4 = new HashMap();
         var4.put("content", this.getString(2131296463));
         this.mAdapter.addData(var4);
      }

      this.setLoadResult(true);
   }
}
