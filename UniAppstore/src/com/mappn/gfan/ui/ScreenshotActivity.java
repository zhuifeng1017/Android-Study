package com.mappn.gfan.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.vo.ProductDetail;
import com.mappn.gfan.common.widget.IndicatorDrawable;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;

public class ScreenshotActivity extends Activity {

   private Animation inAnim;
   private LinearLayout mActionBar;
   private GestureDetector mGestureDetector;
   private Handler mHandler = new Handler() {
   };
   private Runnable mHideIndicator = new Runnable() {
      public void run() {
         ScreenshotActivity.this.mActionBar.startAnimation(ScreenshotActivity.this.outAnim);
         ScreenshotActivity.this.mActionBar.setVisibility(8);
      }
   };
   private int mIndex;
   private IndicatorDrawable mIndicator;
   private ProductDetail mProduct;
   private ImageView mScreenShot;
   private ArrayList<String> mScreenShots;
   private Animation outAnim;


   private void displayIndicator() {
      if(this.mActionBar.isShown()) {
         this.mHandler.removeCallbacks(this.mHideIndicator);
      } else {
         this.mActionBar.startAnimation(this.inAnim);
         this.mActionBar.setVisibility(0);
      }

      this.mHandler.postDelayed(this.mHideIndicator, 1500L);
   }

   private void displayScreenShot(int var1) {
      Drawable var2 = this.mScreenShot.getDrawable();
      Drawable var3 = this.mScreenShot.getBackground();
      if(var2 != null) {
         ((BitmapDrawable)var2).getBitmap().recycle();
         var2.setCallback((Callback)null);
      }

      if(var3 != null) {
         var3.setCallback((Callback)null);
      }

      this.mIndicator.setPage(this.mIndex);
      ImageUtils.downloadScreenShot(this.getApplicationContext(), (String)this.mScreenShots.get(var1), this.mScreenShot);
      this.displayIndicator();
   }

   private void initViews() {
      int var1 = 0;
      Intent var2 = this.getIntent();
      this.mProduct = (ProductDetail)var2.getSerializableExtra("extra.product.detail");
      this.mIndex = var2.getIntExtra("extra.screenshot.id", 0);
      String[] var3 = this.mProduct.getScreenshot();
      this.mScreenShots = new ArrayList();

      for(int var4 = var3.length; var1 < var4; ++var1) {
         String var5 = var3[var1];
         if(!TextUtils.isEmpty(var5)) {
            this.mScreenShots.add(var5);
         }
      }

      this.mActionBar = (LinearLayout)this.findViewById(2131492926);
      this.mIndicator = (IndicatorDrawable)this.findViewById(2131492897);
      this.mIndicator.setPageNumber(this.mScreenShots.size());
      this.mScreenShot = (ImageView)this.findViewById(2131492996);
      this.inAnim = AnimationUtils.loadAnimation(this, 2130968577);
      this.outAnim = AnimationUtils.loadAnimation(this, 2130968578);
      this.displayScreenShot(this.mIndex);
      this.mGestureDetector = new GestureDetector(this.getApplicationContext(), new SimpleOnGestureListener() {
         public boolean onFling(MotionEvent var1, MotionEvent var2, float var3, float var4) {
            float var5 = Math.abs(var3);
            boolean var6;
            if(var5 > Math.abs(var4) && var5 > 500.0F) {
               if(var3 > 0.0F) {
                  ScreenshotActivity.this.showPrevious();
               } else {
                  ScreenshotActivity.this.showNext();
               }

               ScreenshotActivity.this.displayIndicator();
               var6 = true;
            } else {
               var6 = false;
            }

            return var6;
         }
         public boolean onSingleTapUp(MotionEvent var1) {
            ScreenshotActivity.this.displayIndicator();
            return false;
         }
      });
   }

   private void showNext() {
      if(this.mIndex < this.mScreenShots.size() - 1) {
         int var1 = 1 + this.mIndex;
         this.mIndex = var1;
         this.displayScreenShot(var1);
         this.mIndicator.setPage(this.mIndex);
      }

   }

   private void showPrevious() {
      if(this.mIndex > 0) {
         int var1 = this.mIndex - 1;
         this.mIndex = var1;
         this.displayScreenShot(var1);
         this.mIndicator.setPage(this.mIndex);
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903107);
      this.initViews();
   }

   protected void onDestroy() {
      super.onDestroy();
      Drawable var1 = this.mScreenShot.getDrawable();
      Drawable var2 = this.mScreenShot.getBackground();
      if(var1 != null) {
         ((BitmapDrawable)var1).getBitmap().recycle();
         var1.setCallback((Callback)null);
      }

      if(var2 != null) {
         var2.setCallback((Callback)null);
      }

   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this);
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this);
   }

   public boolean onTouchEvent(MotionEvent var1) {
      return this.mGestureDetector.onTouchEvent(var1);
   }
}
