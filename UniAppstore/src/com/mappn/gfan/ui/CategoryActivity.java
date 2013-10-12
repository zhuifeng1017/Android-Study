package com.mappn.gfan.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewAnimator;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.TopBar;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.widget.AppListAdapter;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.common.widget.NavigationTitle;
import com.mappn.gfan.ui.ProductListActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class CategoryActivity extends BaseActivity implements OnItemClickListener, OnClickListener, ApiAsyncTask.ApiRequestListener {

   private static final long ANIMATION_DURATION = 450L;
   private static final String TAB_NEW = "new";
   private static final String TAB_POP = "pop";
   private static final int VIEW_TYPE_APP_PRODUCT_LIST = 3;
   private static final int VIEW_TYPE_CATEGORY = 1;
   private static final int VIEW_TYPE_OTHERS_PRODUCT_LIST = 4;
   private static final int VIEW_TYPE_SUBCATEGORY = 2;
   private static TranslateAnimation sLeftInAnimation;
   private static TranslateAnimation sLeftOutAnimation;
   private static TranslateAnimation sRightOutAnimation;
   private static TranslateAnimation sRighttInAnimation;
   private AppListAdapter mAdapter;
   private int mCurrentLevel;
   private int mCurrentViewType = 1;
   private LayoutInflater mInflater;
   private AppListAdapter mListAdapterLevel1;
   private AppListAdapter mListAdapterLevel2;
   private ListView mListViewLevel1;
   private FrameLayout mLoading;
   private FrameLayout mLoadingLevel1;
   private TextView mLoadingNoData1;
   private TextView mLoadingNoData2;
   private ProgressBar mLoadingProgress1;
   private ProgressBar mLoadingProgress2;
   private TextView mNoData;
   private ProgressBar mProgress;
   private String mSecondLevelTitle;
   private ImageView mShadow;
   private TabHost mTabHost;
   private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         TopBar.initSkin(CategoryActivity.this.mSession, CategoryActivity.this);
         CategoryActivity.this.initSkin();
      }
   };
   private String mThirdLevelTitle;
   private String mTopLevelTitle;
   private ViewAnimator mViewAnimator;
   private ListView mlistView;
   private int width;


   private ListAdapter initAdapter() {
      this.mAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903049, new String[]{"icon_url", "category_name", "top_app", "app_count"}, new int[]{2131492864, 2131492886, 2131492888, 2131492887});
      this.mAdapter.setActivity(this.getParent());
      return this.mAdapter;
   }

   private void initAnimation() {
      sLeftOutAnimation = new TranslateAnimation(0.0F, (float)(-this.width), 0.0F, 0.0F);
      sRighttInAnimation = new TranslateAnimation((float)this.width, 0.0F, 0.0F, 0.0F);
      sLeftInAnimation = new TranslateAnimation((float)(-this.width), 0.0F, 0.0F, 0.0F);
      sRightOutAnimation = new TranslateAnimation(0.0F, (float)this.width, 0.0F, 0.0F);
      sLeftOutAnimation.setDuration(450L);
      sRighttInAnimation.setDuration(450L);
      sLeftInAnimation.setDuration(450L);
      sRightOutAnimation.setDuration(450L);
   }

   private void initAppListView(String var1) {
      this.mShadow = (ImageView)this.findViewById(2131492885);
      this.mShadow.setVisibility(8);
      this.mInflater.inflate(2130903094, this.mViewAnimator);
      this.mTabHost = (TabHost)this.findViewById(16908306);
      this.mTabHost.setup(this.getLocalActivityManager());
      this.mTabHost.getTabWidget().setPadding(this.mSession.mTabMargin72, 0, this.mSession.mTabMargin72, 0);
      ((FrameLayout)this.mTabHost.findViewById(2131492901)).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));
      Intent var3 = new Intent(this.getApplicationContext(), ProductListActivity.class);
      var3.putExtra("extra.order", 3);
      var3.putExtra("extra.category.id", var1);
      TabSpec var6 = this.mTabHost.newTabSpec("pop").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296322), -1, (TextView)null)).setContent(var3);
      this.mTabHost.addTab(var6);
      Intent var7 = new Intent(this.getApplicationContext(), ProductListActivity.class);
      var7.putExtra("extra.order", 2);
      var7.putExtra("extra.category.id", var1);
      TabSpec var10 = this.mTabHost.newTabSpec("new").setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296323), 1, (TextView)null)).setContent(var7);
      this.mTabHost.addTab(var10);
      this.mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
         public void onTabChanged(String var1) {
            if("new".equals(var1)) {
               Utils.trackEvent(CategoryActivity.this.getApplicationContext(), new String[]{"分类", "点击最新"});
            }

         }
      });
   }

   private void initListView(ArrayList<HashMap<String, Object>> var1) {
      FrameLayout var2 = (FrameLayout)LayoutInflater.from(this.getApplicationContext()).inflate(2130903092, (ViewGroup)null, false);
      this.mListViewLevel1 = (ListView)var2.findViewById(16908298);
      this.mLoadingLevel1 = (FrameLayout)var2.findViewById(2131492978);
      this.mLoadingProgress1 = (ProgressBar)var2.findViewById(2131492869);
      this.mLoadingProgress1.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mLoadingProgress1.setVisibility(0);
      this.mLoadingNoData1 = (TextView)var2.findViewById(2131492979);
      this.mLoadingNoData1.setOnClickListener(this);
      this.mListAdapterLevel1 = new AppListAdapter(this.getApplicationContext(), var1, 2130903049, new String[]{"icon_url", "category_name", "top_app", "app_count"}, new int[]{2131492864, 2131492886, 2131492888, 2131492887});
      this.mListViewLevel1.setAdapter(this.mListAdapterLevel1);
      this.mListViewLevel1.setEmptyView(this.mLoadingLevel1);
      this.mListViewLevel1.setOnItemClickListener(this);
      this.mViewAnimator.addView(var2);
   }

   private void initSkin() {
      if(this.mCurrentViewType == 4 || this.mCurrentViewType == 3) {
         this.findViewById(2131492901).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));

         for(int var1 = 0; var1 < 2; ++var1) {
            TextView var2 = (TextView)this.mTabHost.getTabWidget().getChildTabViewAt(var1);
            if(var1 == 0) {
               Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296322), -1, var2);
            } else if(var1 == 1) {
               Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296323), 1, var2);
            }
         }
      }

   }

   private void initTopBar() {
      this.mTopLevelTitle = this.getString(2131296321);
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035), this.findViewById(2131493033)};
      TopBar.createTopBar(var1, this, var2, new int[]{0, 0}, this.mTopLevelTitle);
   }

   private void initView() {
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.mlistView = (ListView)this.findViewById(16908298);
      this.mlistView.setEmptyView(this.mLoading);
      this.mlistView.setOnItemClickListener(this);
      this.mlistView.setAdapter(this.initAdapter());
      this.mViewAnimator = (ViewAnimator)this.findViewById(2131492883);
   }

   private void updateNavigationTitle(String var1, boolean var2) {
      NavigationTitle var3 = (NavigationTitle)this.findViewById(2131493035);
      if(var2) {
         var3.pushTitle(var1);
      } else {
         var3.popTitle();
      }

   }

   public void onClick(View var1) {
      switch(var1.getId()) {
      case 2131492979:
         if(this.mCurrentViewType == 1) {
            this.mNoData.setVisibility(8);
            this.mProgress.setVisibility(0);
            MarketAPI.getAllCategory(this.getApplicationContext(), this);
         } else {
            this.mLoadingNoData1.setVisibility(8);
            this.mLoadingProgress1.setVisibility(0);
            MarketAPI.getTopic(this.getApplicationContext(), this);
         }
      default:
      }
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903048);
      this.width = this.getWindowManager().getDefaultDisplay().getWidth();
      this.mInflater = LayoutInflater.from(this.getApplicationContext());
      IntentFilter var2 = new IntentFilter("com.mappn.gfan.theme");
      this.registerReceiver(this.mThemeReceiver, var2);
      this.initAnimation();
      this.initTopBar();
      this.initView();
      MarketAPI.getAllCategory(this.getApplicationContext(), this);
   }

   protected void onDestroy() {
      super.onDestroy();
      this.unregisterReceiver(this.mThemeReceiver);
   }

   public void onError(int var1, int var2) {
      if(var1 == 37) {
         this.mNoData.setVisibility(0);
         this.mProgress.setVisibility(8);
      } else if(var1 == 10) {
         this.mLoadingNoData1.setVisibility(0);
         this.mLoadingProgress1.setVisibility(8);
      } else if(var1 == 13) {
         this.mLoadingNoData2.setVisibility(0);
         this.mLoadingProgress2.setVisibility(8);
      }

   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      this.mViewAnimator.setOutAnimation(sLeftOutAnimation);
      this.mViewAnimator.setInAnimation(sRighttInAnimation);
      if(this.mCurrentViewType == 1) {
         HashMap var10 = (HashMap)this.mAdapter.getItem(var3);
         this.mSecondLevelTitle = (String)var10.get("category_name");
         this.updateNavigationTitle(this.mSecondLevelTitle, true);
         String var11 = (String)var10.get("category_id");
         if(!TextUtils.isEmpty(var11)) {
            this.initAppListView(var11);
            this.mCurrentViewType = 3;
         } else {
            this.initListView((ArrayList)var10.get("sub_category"));
            this.mCurrentViewType = 2;
         }

         Context var12 = this.getApplicationContext();
         String[] var13 = new String[]{"分类", null};
         Object[] var14 = new Object[]{this.mSecondLevelTitle};
         var13[1] = String.format("进入子分类->[%s]", var14);
         Utils.trackEvent(var12, var13);
      } else if(this.mCurrentViewType == 2) {
         HashMap var6 = (HashMap)this.mListAdapterLevel1.getItem(var3);
         this.mThirdLevelTitle = (String)var6.get("category_name");
         this.updateNavigationTitle(this.mThirdLevelTitle, true);
         this.initAppListView((String)var6.get("category_id"));
         this.mCurrentViewType = 4;
         Context var7 = this.getApplicationContext();
         String[] var8 = new String[]{"分类", null};
         Object[] var9 = new Object[]{this.mThirdLevelTitle};
         var8[1] = String.format("进入子分类->[%s]", var9);
         Utils.trackEvent(var7, var8);
      }

      this.mViewAnimator.showNext();
      ++this.mCurrentLevel;
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      boolean var3;
      if(4 == var1 && this.mCurrentLevel > 0) {
         this.mViewAnimator.setOutAnimation(sRightOutAnimation);
         this.mViewAnimator.setInAnimation(sLeftInAnimation);
         if(this.mCurrentViewType == 4) {
            this.updateNavigationTitle(this.mSecondLevelTitle, false);
            this.mCurrentViewType = 2;
         } else if(this.mCurrentViewType == 2 || this.mCurrentViewType == 3) {
            this.updateNavigationTitle(this.mTopLevelTitle, false);
            this.mCurrentViewType = 1;
         }

         if(this.mTabHost != null) {
            this.mShadow.setVisibility(0);
            this.getLocalActivityManager().removeAllActivities();
         }

         this.mViewAnimator.showPrevious();
         int var4 = this.mCurrentLevel;
         this.mCurrentLevel = var4 - 1;
         if(this.mViewAnimator.getChildAt(var4) != null) {
            this.mViewAnimator.removeViewAt(var4);
         }

         var3 = true;
      } else {
         var3 = this.getParent().onKeyDown(var1, var2);
      }

      return var3;
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
      if(var1 == 37) {
         this.mAdapter.addData((ArrayList)var2);
      } else if(var1 == 10) {
         this.mListAdapterLevel1.addData((ArrayList)var2);
      } else if(var1 == 13) {
         this.mListAdapterLevel2.addData((ArrayList)((HashMap)var2).get("product_list"));
      }

   }
}
