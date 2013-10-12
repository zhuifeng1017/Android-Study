package com.mappn.gfan.common.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.RecommendTopic;
import com.mappn.gfan.common.widget.IndicatorDrawable;
import com.mappn.gfan.common.widget.SlowGallery;
import com.mappn.gfan.ui.InstallNecessaryActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class TopGallery implements AnimationListener, OnItemClickListener, OnItemSelectedListener {

   private static AnimationSet mAvatarInAnimation;
   private static AnimationSet mAvatarOutAnimation;
   private Activity mContext;
   private int mCurrentPage;
   private ArrayList<HashMap<String, Object>> mDataSource;
   private SlowGallery mGallery;
   private RelativeLayout mHomeGallery;
   private IndicatorDrawable mIndicator;
   private boolean mIsInAnimationEnd;
   private ImageView mMstaerAvatar;
   private int mNextPage;
   private TextView mTitle;


   public TopGallery(Activity var1, ViewGroup var2, ArrayList<HashMap<String, Object>> var3) {
      this.mContext = var1;
      this.mDataSource = var3;
      this.mHomeGallery = (RelativeLayout)LayoutInflater.from(var1).inflate(2130903098, var2, false);
      this.mHomeGallery.setFocusableInTouchMode(true);
      this.mGallery = (SlowGallery)this.mHomeGallery.findViewById(2131492969);
      this.mGallery.setOnItemSelectedListener(this);
      this.mGallery.setOnItemClickListener(this);
      this.mTitle = (TextView)this.mHomeGallery.findViewById(2131492972);
      this.mMstaerAvatar = (ImageView)this.mHomeGallery.findViewById(2131492971);
      this.mIndicator = (IndicatorDrawable)this.mHomeGallery.findViewById(2131492973);
      this.mIndicator.setPageNumber(var3.size());
      this.initAnimation();
      this.initImage();
      this.updateAvatar(this.mCurrentPage);
      this.updateTitle();
   }

   private void initAnimation() {
      AlphaAnimation var1 = new AlphaAnimation(0.0F, 1.0F);
      var1.setDuration(500L);
      var1.setFillAfter(true);
      var1.setAnimationListener(this);
      AlphaAnimation var2 = new AlphaAnimation(1.0F, 0.0F);
      var2.setDuration(500L);
      var2.setFillAfter(true);
      var2.setAnimationListener(this);
      TranslateAnimation var3 = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 1.0F, 1, 0.0F);
      var3.setDuration(500L);
      var3.setFillAfter(true);
      TranslateAnimation var4 = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 0.0F, 1, 1.0F);
      var4.setDuration(500L);
      var4.setFillAfter(true);
      mAvatarInAnimation = new AnimationSet(false);
      mAvatarInAnimation.addAnimation(var1);
      mAvatarInAnimation.addAnimation(var3);
      mAvatarInAnimation.setAnimationListener(this);
      mAvatarOutAnimation = new AnimationSet(false);
      mAvatarOutAnimation.addAnimation(var2);
      mAvatarOutAnimation.addAnimation(var4);
      mAvatarOutAnimation.setAnimationListener(this);
   }

   private void initImage() {
      SlowGallery.TopRecommendAdapter var1 = new SlowGallery.TopRecommendAdapter(this.mContext, this.mDataSource);
      this.mGallery.setAdapter(var1);
   }

   private void updateAvatar(int var1) {
      Object var2 = ((HashMap)this.mDataSource.get(var1)).get("icon_url");
      if(var2 instanceof String) {
         ImageUtils.download(this.mContext, (String)var2, this.mMstaerAvatar, 2130837570, true);
      } else if(var2 instanceof Integer) {
         Bitmap var3 = BitmapFactory.decodeResource(this.mContext.getResources(), ((Integer)var2).intValue());
         this.mMstaerAvatar.setImageBitmap(ImageUtils.createHomeUserIcon(this.mContext, var3));
      }

   }

   private void updateTitle() {
      String var1 = (String)((HashMap)this.mDataSource.get(this.mCurrentPage)).get("name");
      this.mTitle.setText(var1);
   }

   public View getView() {
      return this.mHomeGallery;
   }

   public void onAnimationEnd(Animation var1) {
      if(this.mIsInAnimationEnd) {
         this.mMstaerAvatar.startAnimation(mAvatarInAnimation);
         if(this.mIsInAnimationEnd) {
            this.updateAvatar(this.mNextPage);
            this.mIsInAnimationEnd = false;
         }
      }

   }

   public void onAnimationRepeat(Animation var1) {}

   public void onAnimationStart(Animation var1) {}

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      HashMap var6 = (HashMap)this.mDataSource.get(var3);
      if(var6.get("id") == null) {
         Intent var7 = new Intent(this.mContext, InstallNecessaryActivity.class);
         this.mContext.startActivity(var7);
         Utils.trackEvent(this.mContext, new String[]{"首页", "点击轮播图装机必备"});
      } else {
         RecommendTopic var8 = Utils.mapToTopic(var6);
         Utils.gotoMaster(this.mContext, var8);
         Activity var9 = this.mContext;
         String[] var10 = new String[]{"首页", null};
         Object[] var11 = new Object[]{var8.title};
         var10[1] = String.format("点击轮播图专题->专题名[%s]", var11);
         Utils.trackEvent(var9, var10);
      }

   }

   public void onItemSelected(AdapterView<?> var1, View var2, int var3, long var4) {
      this.mIndicator.setPage(var3);
      this.mCurrentPage = var3;
      this.updateTitle();
      this.mNextPage = var3;
      this.mIsInAnimationEnd = true;
      this.mMstaerAvatar.startAnimation(mAvatarOutAnimation);
   }

   public void onNothingSelected(AdapterView<?> var1) {}
}
