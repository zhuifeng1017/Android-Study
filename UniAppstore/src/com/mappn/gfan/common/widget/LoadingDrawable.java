package com.mappn.gfan.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

public class LoadingDrawable extends AnimationDrawable {

   private static final int BLOCK_NUMBER = 4;
   private static final int DURATION = 200;
   private static final int HEIGHT_LARGE = 40;
   private static final int HEIGHT_MEDIUM = 20;
   private static final int HEIGHT_SMALL = 12;
   public static final int SIZE_LARGE = 2;
   public static final int SIZE_MEDIUM = 1;
   public static final int SIZE_SMALL = 0;
   private static final int WIDTH_LARGE = 180;
   private static final int WIDTH_MEDIUM = 90;
   private static final int WIDTH_SMALL = 54;
   private int color1;
   private int color2;
   private int mDuration;
   private int mLoadingStyle;


   public LoadingDrawable(Context var1) {
      this(var1, 0, 0, 0, 200);
   }

   public LoadingDrawable(Context var1, int var2, int var3, int var4, int var5) {
      this.mLoadingStyle = var2;
      if(var3 == 0) {
         this.color1 = Color.parseColor("#80ff9600");
         this.color2 = Color.parseColor("#30000000");
      } else {
         this.color1 = var1.getResources().getColor(var3);
         this.color2 = var1.getResources().getColor(var4);
      }

      this.mDuration = var5;
      Resources var6 = var1.getResources();
      this.setOneShot(false);
      int var7 = 0;
      int var12;
      float var13;
      float var14;
      float var15;
      float var16;
      switch(this.mLoadingStyle) {
      case 0:
         float var41 = var6.getDimension(2131230720);
         float var42 = var41 / 2.0F;
         float var43 = var6.getDimension(2131230721) / 2.0F;
         int var44 = (int)var6.getDimension(2131230726);
         var7 = (int)var6.getDimension(2131230727);
         var12 = var44;
         var13 = var43;
         var14 = var41;
         var15 = var42;
         var16 = var41;
         break;
      case 1:
         float var37 = var6.getDimension(2131230722);
         float var38 = var37 / 2.0F;
         float var39 = var6.getDimension(2131230723) / 2.0F;
         int var40 = (int)var6.getDimension(2131230728);
         var7 = (int)var6.getDimension(2131230729);
         var12 = var40;
         var13 = var39;
         var14 = var37;
         var15 = var38;
         var16 = var37;
         break;
      case 2:
         float var8 = var6.getDimension(2131230724);
         float var9 = var8 / 2.0F;
         float var10 = var6.getDimension(2131230725) / 2.0F;
         int var11 = (int)var6.getDimension(2131230730);
         var7 = (int)var6.getDimension(2131230731);
         var12 = var11;
         var13 = var10;
         var14 = var8;
         var15 = var9;
         var16 = var8;
         break;
      default:
         var12 = 0;
         var13 = 0.0F;
         var14 = 0.0F;
         var15 = 0.0F;
         var16 = 0.0F;
      }

      Paint var17 = new Paint();
      var17.setColor(this.color1);
      var17.setStyle(Style.FILL);
      var17.setAntiAlias(true);
      Paint var18 = new Paint();
      var18.setColor(this.color2);
      var18.setStyle(Style.FILL);
      var18.setAntiAlias(true);
      Canvas var19 = new Canvas();
      int var20 = 0;

      while(var20 < 6) {
         Bitmap var22;
         label56: {
            Bitmap var36;
            try {
               var36 = Bitmap.createBitmap(var12, var7, Config.ARGB_4444);
            } catch (OutOfMemoryError var45) {
               var22 = null;
               break label56;
            }

            var22 = var36;
         }

         if(var22 != null) {
            var19.setBitmap(var22);
            var19.drawColor(0);
            int var23 = 0;
            int var24 = var7 / 2;
            int var25;
            if(var20 < 4) {
               var25 = var20;
            } else {
               var25 = 3 - var20 % 3;
            }

            int var30;
            for(int var26 = 0; var26 < 4; var23 = var30) {
               if(var26 == 0) {
                  var30 = (int)((float)var23 + var16 + var15);
               } else {
                  var30 = (int)((float)var23 + var16 + var14);
               }

               float var31;
               float var34;
               Paint var35;
               float var32;
               float var33;
               if(var26 == var25) {
                  var31 = (float)var30 - var13;
                  var32 = (float)var24 - var13;
                  var33 = var13 + (float)var30;
                  var34 = var13 + (float)var24;
                  var35 = var17;
               } else {
                  var31 = (float)var30 - var15;
                  var32 = (float)var24 - var15;
                  var33 = var15 + (float)var30;
                  var34 = var15 + (float)var24;
                  var35 = var18;
               }

               var19.drawRect(var31, var32, var33, var34, var35);
               ++var26;
            }

            int var27 = var20 + 1;
            var19.save();
            BitmapDrawable var29 = new BitmapDrawable(var22);
            this.addFrame(var29, this.mDuration);
            var20 = var27;
         }
      }

   }

   public int getMinimumHeight() {
      int var1;
      switch(this.mLoadingStyle) {
      case 0:
         var1 = 12;
         break;
      case 1:
         var1 = 20;
         break;
      case 2:
         var1 = 40;
         break;
      default:
         var1 = super.getMinimumHeight();
      }

      return var1;
   }

   public int getMinimumWidth() {
      int var1;
      switch(this.mLoadingStyle) {
      case 0:
         var1 = 54;
         break;
      case 1:
         var1 = 90;
         break;
      case 2:
         var1 = 180;
         break;
      default:
         var1 = super.getMinimumWidth();
      }

      return var1;
   }
}
