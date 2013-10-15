package com.xxx.appstore.common.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xxx.appstore.common.widget.AppListAdapter;
import com.xxx.appstore.common.widget.BaseActivity;
import com.xxx.appstore.common.widget.LoadingDrawable;

public abstract class LazyloadListActivity extends BaseActivity implements AppListAdapter.LazyloadListener, OnClickListener {

   private static final int ITEMS_PER_PAGE = 20;
   private int mEndIndex = 19;
   private ProgressBar mFooterLoading;
   private TextView mFooterNoData;
   private FrameLayout mFooterView;
   private boolean mIsLoadOver = true;
   private int mItemsPerPage;
   protected ListView mList;
   private int mStartIndex = 0;


   private View createFooterView() {
      this.mFooterView = (FrameLayout)((LayoutInflater)this.getSystemService("layout_inflater")).inflate(2130903102, this.mList, false);
      this.mFooterView.setBackgroundResource(2130837639);
      this.mFooterLoading = (ProgressBar)this.mFooterView.findViewById(2131492869);
      this.mFooterLoading.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mFooterLoading.setVisibility(0);
      this.mFooterNoData = (TextView)this.mFooterView.findViewById(2131492979);
      this.mFooterNoData.setOnClickListener(this);
      this.mFooterNoData.setVisibility(8);
      return this.mFooterView;
   }

   private void initListView() {
      AppListAdapter var1 = this.doInitListAdapter();
      var1.setActivity(this.getParent());
      var1.setLazyloadListener(this);
      this.doInitHeaderViewOrFooterView();
      this.mList.addFooterView(this.createFooterView(), (Object)null, false);
      this.mList.setAdapter(var1);
   }

   protected void doInitHeaderViewOrFooterView() {}

   public abstract AppListAdapter doInitListAdapter();

   public abstract boolean doInitView(Bundle var1);

   public abstract void doLazyload();

   public int getEndIndex() {
      return this.mEndIndex;
   }

   protected int getItemCount() {
      return 0;
   }

   protected int getItemsPerPage() {
      return 20;
   }

   public int getStartIndex() {
      return this.mStartIndex;
   }

   public boolean isEnd() {
      boolean var1;
      if(this.mStartIndex >= this.getItemCount()) {
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   public boolean isLoadOver() {
      return this.mIsLoadOver;
   }

   public void lazyload() {
      if(this.mIsLoadOver) {
         this.mIsLoadOver = false;
         this.doLazyload();
      }

   }

   public void onClick(View var1) {
      if(var1.getId() == 2131492979) {
         this.mFooterLoading.setVisibility(0);
         this.mFooterNoData.setVisibility(8);
         this.doLazyload();
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      if(this.doInitView(var1)) {
         this.mItemsPerPage = this.getItemsPerPage();
         this.mEndIndex = this.mItemsPerPage - 1;
         this.initListView();
      }

   }

   public void reset() {
      this.mStartIndex = 0;
      this.mEndIndex = this.mItemsPerPage - 1;
      ((AppListAdapter)((HeaderViewListAdapter)this.mList.getAdapter()).getWrappedAdapter()).clearData();
   }

   public void setLoadResult(boolean var1) {
      this.mIsLoadOver = true;
      if(var1) {
         this.mStartIndex = 1 + this.mEndIndex;
         this.mEndIndex += this.mItemsPerPage;
         this.mFooterLoading.setVisibility(0);
         this.mFooterNoData.setVisibility(8);
         if(this.isEnd()) {
            this.mList.removeFooterView(this.mFooterView);
         }
      } else {
         this.mFooterLoading.setVisibility(8);
         this.mFooterNoData.setVisibility(0);
      }

   }
}
