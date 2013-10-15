package com.xxx.appstore.common.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.xxx.appstore.common.util.ImageUtils;
import com.xxx.appstore.common.util.Utils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class CacheManager {

   private static final int MAX_IMAGE_IN_L1_MEMORY = 100;
   private static CacheManager mInstance;
   private final LinkedHashMap<Integer, Bitmap> mDrawableMap = new LinkedHashMap();
   private SoftReference<LinkedHashMap<Integer, Bitmap>> mL1Cache;
   private final LinkedHashMap<Integer, SoftReference<Bitmap>> mL2Cache;


   private CacheManager() {
      this.mL1Cache = new SoftReference(this.mDrawableMap);
      this.mL2Cache = new LinkedHashMap();
   }

   public static CacheManager getInstance() {
      synchronized(CacheManager.class){}

      CacheManager var1;
      try {
         if(mInstance == null) {
            mInstance = new CacheManager();
         }

         var1 = mInstance;
      } finally {
         ;
      }

      return var1;
   }

   public void cacheDrawable(Context var1, String var2, Bitmap var3) {
      synchronized(this){}
      if(var3 != null) {
         try {
            int var5 = var2.hashCode();
            LinkedHashMap var6 = (LinkedHashMap)this.mL1Cache.get();
            if(var6 == null) {
               this.mL1Cache = new SoftReference(this.mDrawableMap);
            }

            if(var6.size() < 100) {
               var6.put(Integer.valueOf(var5), var3);
            } else {
               Iterator var7 = var6.keySet().iterator();
               if(var7.hasNext()) {
                  var6.remove((Integer)var7.next());
                  var6.put(Integer.valueOf(var5), var3);
               }
            }

            if(!this.mL2Cache.containsKey(Integer.valueOf(var5))) {
               ImageUtils.saveBitmapToSdcard(var1, var5, var3);
            }

            this.mL2Cache.put(Integer.valueOf(var5), new SoftReference(var3));
         } finally {
            ;
         }
      }

   }

   public void cacheDrawableToL2(Context var1, String var2, Bitmap var3) {
      synchronized(this){}
      if(var3 != null) {
         try {
            int var5 = var2.hashCode();
            if(!this.mL2Cache.containsKey(Integer.valueOf(var5))) {
               ImageUtils.saveBitmapToSdcard(var1, var5, var3);
            }

            this.mL2Cache.put(Integer.valueOf(var5), null);
         } finally {
            ;
         }
      }

   }

   public void clearFromFile(final Context var1) {
      Thread var2 = new Thread() {
         public void run() {
            File var1x = var1.getCacheDir();
            if(var1x.exists()) {
               File[] var2 = var1x.listFiles();
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  var2[var4].delete();
               }
            }

         }
      };
      var2.setPriority(10);
      var2.start();
   }

   public void clearFromMemory() {
      if(this.mL1Cache.get() != null) {
         ((LinkedHashMap)this.mL1Cache.get()).clear();
      }

      if(this.mL2Cache != null) {
         this.mL2Cache.clear();
      }

      mInstance = null;
   }

   public boolean existsDrawable(String var1) {
      int var2 = var1.hashCode();
      boolean var3;
      if(this.mL2Cache.containsKey(Integer.valueOf(var2))) {
         var3 = true;
      } else {
         var3 = false;
      }

      return var3;
   }

   public Bitmap getDrawableFromCache(Context var1, String var2) {
      int var3 = var2.hashCode();
      LinkedHashMap var4 = (LinkedHashMap)this.mL1Cache.get();
      Bitmap var6;
      if(var4 != null) {
         var6 = (Bitmap)var4.get(Integer.valueOf(var3));
         if(var6 != null) {
            return var6;
         }
      } else {
         Utils.D("l1 cache is empty");
      }

      SoftReference var5 = (SoftReference)this.mL2Cache.get(Integer.valueOf(var3));
      if(var5 == null) {
         var6 = null;
      } else {
         Bitmap var7 = (Bitmap)var5.get();
         if(var7 != null) {
            Utils.D("get cache from l2");
         }

         if(var7 == null) {
            var6 = null;
         } else {
            var6 = var7;
         }
      }

      return var6;
   }
}
