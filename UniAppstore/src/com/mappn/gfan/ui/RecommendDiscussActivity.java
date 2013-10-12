package com.mappn.gfan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.RecommendTopic;
import com.mappn.gfan.common.widget.AppListAdapter;
import com.mappn.gfan.common.widget.LazyloadListActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.ui.LoginActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class RecommendDiscussActivity extends LazyloadListActivity implements ApiAsyncTask.ApiRequestListener {

   private EditText mComment;
   private AppListAdapter mCommentAdapter;
   private FrameLayout mLoading;
   private TextView mNoData;
   private ProgressBar mProgress;
   private Button mSend;
   private RecommendTopic mTopic;
   private int mTotalSize;


   private void addMyComment() {
      String var1 = this.mComment.getText().toString();
      HashMap var2 = new HashMap();
      var2.put("content", var1);
      var2.put("author", this.mSession.getUserName());
      var2.put("date", Utils.formatTime(System.currentTimeMillis()));
      if(this.mTotalSize <= 0) {
         this.mCommentAdapter.clearData();
      }

      this.mCommentAdapter.insertData(var2);
   }

   private void handlePostError(int var1) {
      switch(var1) {
      case 225:
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296467), false);
         break;
      case 232:
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296466), false);
         break;
      case 233:
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296468), false);
         break;
      default:
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296472), false);
      }

   }

   private void initViews() {
      RelativeLayout var1 = (RelativeLayout)this.findViewById(2131492981);
      var1.setOnClickListener(this);
      var1.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
      ((TextView)var1.findViewById(2131492866)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 18));
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.mList = (ListView)this.findViewById(16908298);
      this.mList.setEmptyView(this.mLoading);
      this.toogleCommentViewStatus();
   }

   private void toogleCommentViewStatus() {
      if(this.mSession.isLogin()) {
         this.findViewById(2131492981).setVisibility(4);
         LinearLayout var1 = (LinearLayout)((LinearLayout)((ViewStub)this.findViewById(2131492982)).inflate());
         var1.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
         ((Button)var1.findViewById(2131493019)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 18));
         this.mComment = (EditText)this.findViewById(2131493018);
         this.mSend = (Button)this.findViewById(2131493019);
         this.mSend.setOnClickListener(this);
      }

   }

   public AppListAdapter doInitListAdapter() {
      this.mCommentAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903109, new String[]{"author", "date", "content"}, new int[]{2131492997, 2131492908, 2131492998});
      this.mCommentAdapter.setActivity(this);
      return this.mCommentAdapter;
   }

   public boolean doInitView(Bundle var1) {
      this.setContentView(2130903104);
      this.mTopic = (RecommendTopic)this.getIntent().getSerializableExtra("extra.recommend.detail");
      this.initViews();
      this.lazyload();
      return true;
   }

   public void doLazyload() {
      MarketAPI.getMasterRecommendDiscuss(this.getApplicationContext(), this, this.getItemsPerPage(), this.getStartIndex(), this.mTopic.id);
   }

   protected int getItemCount() {
      return this.mTotalSize;
   }

   protected void onActivityResult(int var1, int var2, Intent var3) {
      this.toogleCommentViewStatus();
      super.onActivityResult(var1, var2, var3);
   }

   public void onClick(View var1) {
      super.onClick(var1);
      switch(var1.getId()) {
      case 2131492979:
         this.mProgress.setVisibility(0);
         this.mNoData.setVisibility(8);
         this.lazyload();
         break;
      case 2131492981:
         this.startActivityForResult(new Intent(this.getApplicationContext(), LoginActivity.class), 0);
         break;
      case 2131493019:
         String var2 = this.mComment.getText().toString();
         if(TextUtils.isEmpty(var2)) {
            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296473), false);
         } else {
            this.mSend.setEnabled(false);
            MarketAPI.addMasterRecommendDiscuss(this.getApplicationContext(), this, this.mTopic.id, var2.trim());
         }
      }

   }

   public void onError(int var1, int var2) {
      if(var1 == 46) {
         this.mSend.setEnabled(true);
         this.handlePostError(var2);
      } else if(var1 == 45) {
         this.mNoData.setVisibility(0);
         this.mProgress.setVisibility(8);
         this.setLoadResult(false);
      }

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
      if(var1 == 45) {
         HashMap var3 = (HashMap)var2;
         this.mTotalSize = ((Integer)var3.get("total_size")).intValue();
         if(this.mTotalSize > 0) {
            this.mCommentAdapter.addData((ArrayList)var3.get("comment_list"));
         } else {
            HashMap var4 = new HashMap();
            var4.put("content", this.getString(2131296463));
            this.mCommentAdapter.addData(var4);
         }

         this.setLoadResult(true);
      } else if(var1 == 46) {
         this.mSend.setEnabled(true);
         this.addMyComment();
         this.mComment.setText("");
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296435), false);
      }

   }
}
