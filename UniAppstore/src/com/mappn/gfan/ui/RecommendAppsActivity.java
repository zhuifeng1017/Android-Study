package com.mappn.gfan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.RecommendTopic;
import com.mappn.gfan.common.widget.AppListAdapter;
import com.mappn.gfan.common.widget.LazyloadListActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.ui.PreloadActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class RecommendAppsActivity extends LazyloadListActivity implements ApiAsyncTask.ApiRequestListener, OnItemClickListener {

   private static final int STATUS_DOWN = -1;
   private static final int STATUS_UNKNOWN = 0;
   private static final int STATUS_UP = 1;
   private AppListAdapter mAdapter;
   private boolean mAlreadyRanked;
   private int mCurrentRank;
   private Handler mHandler = new Handler();
   private RelativeLayout mLeftButton;
   private ImageView mLeftIcon;
   private TextView mLeftText;
   private FrameLayout mLoading;
   private TextView mNoData;
   private int mPreRank;
   private ProgressBar mProgress;
   private Runnable mRankTask = new Runnable() {
      public void run() {
         if(RecommendAppsActivity.this.mCurrentRank != RecommendAppsActivity.this.mPreRank) {
            MarketAPI.addMasterRecommendRating(RecommendAppsActivity.this.getApplicationContext(), RecommendAppsActivity.this, RecommendAppsActivity.this.mPreRank, RecommendAppsActivity.this.mTopic.id);
         }

      }
   };
   private RelativeLayout mRightButton;
   private ImageView mRightIcon;
   private TextView mRightText;
   private RecommendTopic mTopic;
   private int mTotalSize;


   private View createHeaderView(RecommendTopic var1) {
      RelativeLayout var2 = (RelativeLayout)this.getLayoutInflater().inflate(2130903112, this.mList, false);
      ((TextView)var2.findViewById(2131492868)).setText(var1.user);
      ((TextView)var2.findViewById(2131492929)).setText(String.valueOf(var1.fans));
      TextView var3 = (TextView)var2.findViewById(2131493000);
      if(TextUtils.isEmpty(var1.experience)) {
         var3.setVisibility(8);
      } else {
         var3.setText(this.mTopic.experience);
      }

      TextView var4 = (TextView)var2.findViewById(2131492905);
      if(TextUtils.isEmpty(var1.description)) {
         var4.setVisibility(8);
      } else {
         var4.setText(var1.description);
      }

      return var2;
   }

   private void initActionBar(RecommendTopic var1) {
      this.mLeftButton = (RelativeLayout)this.findViewById(2131492956);
      this.mLeftButton.setOnClickListener(this);
      this.mLeftButton.setEnabled(false);
      this.mRightButton = (RelativeLayout)this.findViewById(2131492958);
      this.mRightButton.setOnClickListener(this);
      this.mRightButton.setEnabled(false);
      this.mLeftIcon = (ImageView)this.findViewById(2131492890);
      this.mLeftIcon.setImageResource(2130837544);
      this.mLeftText = (TextView)this.findViewById(2131492891);
      this.mLeftText.setText(String.valueOf(var1.up));
      this.mRightIcon = (ImageView)this.findViewById(2131492959);
      this.mRightIcon.setImageResource(2130837524);
      this.mRightText = (TextView)this.findViewById(2131492960);
      this.mRightText.setText(String.valueOf(var1.down));
   }

   private void initViewsByTheme() {
      LinearLayout var1 = (LinearLayout)this.findViewById(2131492926);
      var1.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
      ((RelativeLayout)var1.findViewById(2131492956)).setBackgroundResource(ThemeManager.getResource(this.mSession, 16));
      ((RelativeLayout)var1.findViewById(2131492958)).setBackgroundResource(ThemeManager.getResource(this.mSession, 16));
      ((TextView)var1.findViewById(2131492891)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 17));
      ((TextView)var1.findViewById(2131492960)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 17));
      ((ImageView)this.findViewById(2131492957)).setBackgroundResource(ThemeManager.getResource(this.mSession, 19));
   }

   private void submitRanking() {
      this.mHandler.removeCallbacks(this.mRankTask);
      this.mHandler.postDelayed(this.mRankTask, 2000L);
   }

   private void toogleRankStatus(int var1) {
      switch(var1) {
      case -1:
         this.mLeftButton.setEnabled(true);
         this.mLeftIcon.setImageResource(ThemeManager.getResource(this.mSession, 26));
         this.mRightButton.setEnabled(false);
         this.mRightIcon.setImageResource(ThemeManager.getResource(this.mSession, 29));
         break;
      case 0:
         this.mLeftButton.setEnabled(true);
         this.mLeftIcon.setImageResource(ThemeManager.getResource(this.mSession, 26));
         this.mRightButton.setEnabled(true);
         this.mRightIcon.setImageResource(ThemeManager.getResource(this.mSession, 28));
         break;
      case 1:
         this.mLeftButton.setEnabled(false);
         this.mLeftIcon.setImageResource(ThemeManager.getResource(this.mSession, 27));
         this.mRightButton.setEnabled(true);
         this.mRightIcon.setImageResource(ThemeManager.getResource(this.mSession, 28));
      }

      this.mLeftText.setText(String.valueOf(this.mTopic.up));
      this.mRightText.setText(String.valueOf(this.mTopic.down));
   }

   public AppListAdapter doInitListAdapter() {
      this.mAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903113, new String[]{"icon_url", "name", "sub_category", "product_download", "app_size", "short_description"}, new int[]{2131492867, 2131492868, 2131493001, 2131492965, 2131492966, 2131492905});
      this.mAdapter.setProductList();
      return this.mAdapter;
   }

   public boolean doInitView(Bundle var1) {
      this.setContentView(2130903073);
      this.mTopic = (RecommendTopic)this.getIntent().getSerializableExtra("extra.recommend.detail");
      this.initViewsByTheme();
      this.initActionBar(this.mTopic);
      MarketAPI.getMasterRecommendRating(this.getApplicationContext(), this, this.mTopic.id);
      this.mList = (ListView)this.findViewById(16908298);
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.mList.addHeaderView(this.createHeaderView(this.mTopic), (Object)null, false);
      this.mList.setEmptyView(this.mLoading);
      this.mList.setItemsCanFocus(false);
      this.mList.setOnItemClickListener(this);
      this.lazyload();
      return true;
   }

   public void doLazyload() {
      MarketAPI.getMasterRecommendApps(this.getApplicationContext(), this, this.getItemsPerPage(), this.getStartIndex(), this.mTopic.id);
   }

   protected int getItemCount() {
      return this.mTotalSize;
   }

   public void onClick(View var1) {
      super.onClick(var1);
      switch(var1.getId()) {
      case 2131492956:
         this.mPreRank = 1;
         this.submitRanking();
         this.toogleRankStatus(this.mPreRank);
         break;
      case 2131492958:
         this.mPreRank = -1;
         this.submitRanking();
         this.toogleRankStatus(this.mPreRank);
         break;
      case 2131492979:
         this.mProgress.setVisibility(0);
         this.mNoData.setVisibility(8);
         this.lazyload();
      }

   }

   public void onError(int var1, int var2) {
      if(var1 == 47) {
         this.toogleRankStatus(this.mCurrentRank);
      } else if(var1 == 43) {
         if(var2 != 610) {
            this.mNoData.setVisibility(0);
            this.mProgress.setVisibility(8);
         }

         this.setLoadResult(false);
      }

   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      String var6 = (String)((HashMap)this.mAdapter.getItem(var3 - this.mList.getHeaderViewsCount())).get("packagename");
      Intent var7 = new Intent(this.getApplicationContext(), PreloadActivity.class);
      var7.putExtra("extra.key.package.name", var6);
      this.startActivity(var7);
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
      if(var1 == 48) {
         this.mCurrentRank = Utils.getInt((String)var2);
         this.toogleRankStatus(this.mCurrentRank);
         if(this.mCurrentRank != 0) {
            this.mAlreadyRanked = true;
         }
      } else if(var1 == 43) {
         HashMap var3 = (HashMap)var2;
         if(this.mTotalSize <= 0) {
            this.mTotalSize = ((Integer)var3.get("total_size")).intValue();
         }

         this.mAdapter.addData((ArrayList)var3.get("product_list"));
         this.setLoadResult(true);
      }

      if(var1 == 47) {
         this.mCurrentRank = this.mPreRank;
         if(this.mCurrentRank == 1) {
            if(this.mAlreadyRanked) {
               RecommendTopic var7 = this.mTopic;
               --var7.down;
            }

            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296650), false);
            RecommendTopic var6 = this.mTopic;
            ++var6.up;
         } else if(this.mCurrentRank == -1) {
            if(this.mAlreadyRanked) {
               RecommendTopic var5 = this.mTopic;
               --var5.up;
            }

            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296651), false);
            RecommendTopic var4 = this.mTopic;
            ++var4.down;
         }

         this.toogleRankStatus(this.mCurrentRank);
         this.mAlreadyRanked = true;
      }

   }
}
