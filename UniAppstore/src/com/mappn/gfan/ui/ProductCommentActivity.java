package com.mappn.gfan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.mappn.gfan.common.vo.ProductDetail;
import com.mappn.gfan.common.widget.AppListAdapter;
import com.mappn.gfan.common.widget.LazyloadListActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.ui.LoginActivity;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductCommentActivity extends LazyloadListActivity implements ApiAsyncTask.ApiRequestListener {

   private EditText mComment;
   private AppListAdapter mCommentAdapter;
   private boolean mIsPaused;
   private ProductDetail mProduct;
   private Button mSend;
   private int mTotalSize;


   private void addMyComment() {
      String var1 = this.mComment.getText().toString();
      HashMap var2 = new HashMap();
      var2.put("comment", var1);
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
      var1.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
      var1.setOnClickListener(this);
      ((TextView)var1.findViewById(2131492866)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 18));
      FrameLayout var2 = (FrameLayout)this.findViewById(2131492978);
      ProgressBar var3 = (ProgressBar)var2.findViewById(2131492869);
      var3.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      var3.setVisibility(0);
      this.mList = (ListView)this.findViewById(16908298);
      this.mList.setEmptyView(var2);
      this.toogleCommentViewStatus();
   }

   private void toogleCommentViewStatus() {
      if(this.mSession.isLogin()) {
         this.findViewById(2131492981).setVisibility(4);
         LinearLayout var1 = (LinearLayout)((LinearLayout)((ViewStub)this.findViewById(2131492982)).inflate());
         var1.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
         ((Button)var1.findViewById(2131493019)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 17));
         this.mComment = (EditText)this.findViewById(2131493018);
         this.mSend = (Button)this.findViewById(2131493019);
         this.mSend.setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 18));
         this.mSend.setOnClickListener(this);
      }

   }

   public AppListAdapter doInitListAdapter() {
      this.mCommentAdapter = new AppListAdapter(this.getApplicationContext(), (ArrayList)null, 2130903109, new String[]{"author", "date", "comment"}, new int[]{2131492997, 2131492908, 2131492998});
      this.mCommentAdapter.setActivity(this);
      return this.mCommentAdapter;
   }

   public boolean doInitView(Bundle var1) {
      this.setContentView(2130903104);
      Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "查看评论"});
      this.mProduct = (ProductDetail)this.getIntent().getSerializableExtra("extra.product.detail");
      this.initViews();
      this.doLazyload();
      return true;
   }

   public void doLazyload() {
      MarketAPI.getComments(this, this, this.mProduct.getPid(), this.getItemsPerPage(), this.getStartIndex());
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
      if(2131492981 == var1.getId()) {
         this.startActivityForResult(new Intent(this.getApplicationContext(), LoginActivity.class), 0);
      } else if(2131493019 == var1.getId()) {
         String var2 = this.mComment.getText().toString();
         if(TextUtils.isEmpty(var2)) {
            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296473), false);
         } else {
            this.mSend.setEnabled(false);
            MarketAPI.addComment(this.getApplicationContext(), this, this.mProduct.getPid(), var2.trim());
            Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "发表评论"});
         }
      }

   }

   protected void onDestroy() {
      super.onDestroy();
      this.mCommentAdapter = null;
      this.mProduct = null;
      this.mComment = null;
      this.mSend = null;
   }

   public void onError(int var1, int var2) {
      switch(var1) {
      case 2:
         this.setLoadResult(false);
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296465), false);
         break;
      case 3:
         this.mSend.setEnabled(true);
         this.handlePostError(var2);
      }

      Log.d("error", "status code " + var2);
   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this.getParent());
      this.mIsPaused = true;
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this.getParent());
      if(this.mIsPaused) {
         ((RelativeLayout)this.findViewById(2131492981)).setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
         this.mIsPaused = false;
      }

   }

   public void onSuccess(int var1, Object var2) {
      if(!this.isFinishing()) {
         switch(var1) {
         case 2:
            if(var2 instanceof HashMap) {
               HashMap var3 = (HashMap)var2;
               this.mTotalSize = ((Integer)var3.get("total_size")).intValue();
               if(this.mTotalSize > 0) {
                  this.mCommentAdapter.addData((ArrayList)var3.get("comment_list"));
               } else {
                  HashMap var4 = new HashMap();
                  var4.put("comment", this.getString(2131296463));
                  this.mCommentAdapter.addData(var4);
               }

               this.setLoadResult(true);
            }
            break;
         case 3:
            this.mSend.setEnabled(true);
            this.addMyComment();
            this.mComment.setText("");
            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296435), false);
         }
      }

   }
}
