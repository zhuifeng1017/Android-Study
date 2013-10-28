package com.xxx.appstore.common;

import android.content.Context;
import android.text.TextUtils;
import com.xxx.appstore.common.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class ResponseCacheManager {

   private static ResponseCacheManager mInstance;
   private final HashMap<String, SoftReference<Object>> mResponseCache = new HashMap();


   public static ResponseCacheManager getInstance() {
      synchronized(ResponseCacheManager.class){}

      ResponseCacheManager var1;
      try {
         if(mInstance == null) {
            mInstance = new ResponseCacheManager();
         }

         var1 = mInstance;
      } finally {
         ;
      }

      return var1;
   }

   public static Object getResponseFromSdcard(Context var0, String var1) {
      File var2 = new File(var0.getCacheDir(), var1);
      Object var3;
      if(var2.exists()) {
         label25: {
            Object var7;
            try {
               ObjectInputStream var4 = new ObjectInputStream(new FileInputStream(var2));
               var7 = var4.readObject();
               var4.close();
               Utils.D("read response cache from sd card " + var1);
            } catch (IOException var8) {
               Utils.E("Error when get api cache", var8);
               break label25;
            } catch (ClassNotFoundException var9) {
               Utils.E("Error when get api cache", var9);
               break label25;
            }

            var3 = var7;
            return var3;
         }
      }

      var3 = null;
      return var3;
   }

   public static void saveResponseToSdcard(Context var0, String var1, Object var2) {
      File var3 = var0.getCacheDir();

      try {
         ObjectOutputStream var4 = new ObjectOutputStream(new FileOutputStream(new File(var3, var1)));
         var4.writeObject(var2);
         var4.close();
      } catch (IOException var6) {
         Utils.E("Error when save api cache", var6);
      }

      Utils.D("save response cache to sd card " + var1);
   }

   public void clear() {
      if(this.mResponseCache != null) {
         this.mResponseCache.clear();
      }

      mInstance = null;
   }

   public Object getResponse(Context var1, String var2) {
      Object var3;
      if(TextUtils.isEmpty(var2)) {
         var3 = null;
      } else if(this.mResponseCache == null) {
         var3 = null;
      } else if(!this.mResponseCache.containsKey(var2)) {
         var3 = null;
      } else {
         var3 = ((SoftReference)this.mResponseCache.get(var2)).get();
         if(var3 == null) {
            var3 = getResponseFromSdcard(var1, var2);
         }

         Utils.D("get response cache " + var2);
      }

      return var3;
   }

   public void putResponse(Context var1, int var2, String var3, Object var4) {
      synchronized(this){}

      try {
         if(this.mResponseCache != null) {
            if(!this.mResponseCache.containsKey(var3)) {
               saveResponseToSdcard(var1, var3, var4);
            }

            this.mResponseCache.put(var3, new SoftReference(var4));
         }
      } finally {
         ;
      }

   }
}
