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
   
   private final LinkedHashMap<Integer, Bitmap> mDrawableMap = new LinkedHashMap<Integer, Bitmap>();
   
   // 一级缓存，缓存在内存
   private SoftReference<LinkedHashMap<Integer, Bitmap>> mL1Cache;
   
   // 二级缓存，缓存在文件中
   private final LinkedHashMap<Integer, SoftReference<Bitmap>> mL2Cache;


   private CacheManager() {
      mL1Cache = new SoftReference<LinkedHashMap<Integer, Bitmap>>(mDrawableMap);
      mL2Cache = new LinkedHashMap<Integer, SoftReference<Bitmap>>();
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

   // 缓存图片
   public void cacheDrawable(Context context, String url, Bitmap bitmap) {
      synchronized(this){}
      if(bitmap != null) {
         try {
            int hCode = url.hashCode();
            LinkedHashMap<Integer, Bitmap> drawableCale = mL1Cache.get();
            if(drawableCale == null) {
               mL1Cache = new SoftReference<LinkedHashMap<Integer, Bitmap>>(mDrawableMap);
            }

            if(drawableCale.size() < MAX_IMAGE_IN_L1_MEMORY) {	// 缓存
               drawableCale.put(Integer.valueOf(hCode), bitmap);
            } else {	// 缓存数量大于100
               Iterator<Integer> item = drawableCale.keySet().iterator();
               if(item.hasNext()) {
                  drawableCale.remove((Integer)item.next());
                  drawableCale.put(Integer.valueOf(hCode), bitmap);
               }
            }

            // 缓存到文件
            if(!mL2Cache.containsKey(Integer.valueOf(hCode))) {
               ImageUtils.saveBitmapToSdcard(context, hCode, bitmap);
            }
            // 更新文件缓存
            mL2Cache.put(Integer.valueOf(hCode), new SoftReference<Bitmap>(bitmap));
         } finally {
            ;
         }
      }
   }

   public void cacheDrawableToL2(Context context, String url, Bitmap bitmap) {
      synchronized(this){}
      if(bitmap != null) {
         try {
            int hCode = url.hashCode();
            if(!mL2Cache.containsKey(Integer.valueOf(hCode))) {
               ImageUtils.saveBitmapToSdcard(context, hCode, bitmap);
            }
            // 更新文件缓存
            mL2Cache.put(Integer.valueOf(hCode), new SoftReference<Bitmap>(bitmap));
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
      if(mL1Cache.get() != null) {
         ((LinkedHashMap<Integer, Bitmap>)mL1Cache.get()).clear();
      }

      if(mL2Cache != null) {
         mL2Cache.clear();
      }

      mInstance = null;
   }

   public boolean existsDrawable(String url) {
      int hCode = url.hashCode();
      return mL2Cache.containsKey(Integer.valueOf(hCode));
   }

   // 从缓存中去图片
	public Bitmap getDrawableFromCache(Context context, String url) {
		int hCode = url.hashCode();
		LinkedHashMap<Integer, Bitmap> drawableMapL1 = mL1Cache.get();
		Bitmap bitmap = null;
		if (drawableMapL1 != null) {
			bitmap = (Bitmap) drawableMapL1.get(Integer.valueOf(hCode));
		} else {
			Utils.D("l1 cache is empty");
		}

		if (bitmap != null) {
			return bitmap;
		}

		SoftReference<Bitmap> drawableMapL2 = mL2Cache.get(Integer.valueOf(hCode));
		if (drawableMapL2 != null) {
			bitmap = (Bitmap) drawableMapL2.get();
		}
		
		return bitmap;
	}
}
