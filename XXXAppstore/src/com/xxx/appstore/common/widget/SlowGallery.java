package com.xxx.appstore.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.xxx.appstore.common.util.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SlowGallery extends Gallery {

   public SlowGallery(Context var1) {
      super(var1);
   }

   public SlowGallery(Context var1, AttributeSet var2) {
      super(var1, var2);
   }

   public SlowGallery(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
   }

   public boolean onFling(MotionEvent var1, MotionEvent var2, float var3, float var4) {
      return super.onFling(var1, var2, var3 / 3.0F, var4);
   }

   static class TopRecommendAdapter extends BaseAdapter {

      private Context mContext;
      private ArrayList<HashMap<String, Object>> mDataSource;
      private LayoutInflater mLayoutInflater;


      public TopRecommendAdapter(Context var1, ArrayList<HashMap<String, Object>> var2) {
         this.mContext = var1;
         this.mDataSource = var2;
         this.mLayoutInflater = LayoutInflater.from(this.mContext);
      }

      public int getCount() {
         int var1;
         if(this.mDataSource != null && this.mDataSource.size() != 0) {
            var1 = this.mDataSource.size();
         } else {
            var1 = 0;
         }

         return var1;
      }

      public Object getItem(int var1) {
         return this.mDataSource.get(var1);
      }

      public long getItemId(int var1) {
         return (long)var1;
      }

      public View getView(int var1, View var2, ViewGroup var3) {
         ImageView var4;
         if(var2 == null) {
            var4 = (ImageView)this.mLayoutInflater.inflate(2130903097, var3, false);
         } else {
            var4 = (ImageView)var2;
         }

         Drawable var5 = var4.getDrawable();
         if(var5 != null) {
            var5.setCallback((Callback)null);
         }

         Object var6 = ((HashMap)this.mDataSource.get(var1)).get("pic_url");
         if(var6 instanceof String) {
            ImageUtils.downloadHomeTopDrawable(this.mContext, (String)var6, var4);
         } else if(var6 instanceof Integer) {
            Bitmap var7 = BitmapFactory.decodeResource(this.mContext.getResources(), ((Integer)var6).intValue());
            var4.setImageDrawable(ImageUtils.getMaskDrawable(this.mContext));
            var4.setBackgroundDrawable(new BitmapDrawable(var7));
         }

         return var4;
      }

      public void setData(ArrayList<HashMap<String, Object>> var1) {
         if(this.mDataSource == null) {
            this.mDataSource = new ArrayList();
         }

         this.mDataSource.addAll(var1);
         this.notifyDataSetChanged();
      }
   }
}
