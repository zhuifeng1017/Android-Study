package com.mappn.gfan.common;

import com.mappn.gfan.common.AndroidHttpClient;
import com.mappn.gfan.common.util.Utils;
import java.util.WeakHashMap;

public class HttpClientFactory {

   private static String MARKET_CLIENT = "market";
   private static HttpClientFactory mInstance;
   private WeakHashMap<String, AndroidHttpClient> mHttpClientMap = new WeakHashMap(1);


   public static HttpClientFactory get() {
      synchronized(HttpClientFactory.class){}

      HttpClientFactory var1;
      try {
         if(mInstance == null) {
            mInstance = new HttpClientFactory();
         }

         var1 = mInstance;
      } finally {
         ;
      }

      return var1;
   }

   public void close() {
      synchronized(this){}

      try {
         if(this.mHttpClientMap.containsKey(MARKET_CLIENT)) {
            AndroidHttpClient var2 = (AndroidHttpClient)this.mHttpClientMap.get(MARKET_CLIENT);
            if(var2 != null) {
               var2.getConnectionManager().shutdown();
               var2.close();
            }
         }

         this.mHttpClientMap.clear();
         mInstance = null;
      } finally {
         ;
      }

   }

   public AndroidHttpClient getHttpClient() {
      AndroidHttpClient var1 = (AndroidHttpClient)this.mHttpClientMap.get(MARKET_CLIENT);
      if(var1 == null) {
         var1 = AndroidHttpClient.newInstance("");
         this.mHttpClientMap.put(MARKET_CLIENT, var1);
      }

      return var1;
   }

   public void updateMarketHeader(String var1) {
      AndroidHttpClient var2 = (AndroidHttpClient)this.mHttpClientMap.get(MARKET_CLIENT);
      if(var2 != null) {
         var2.getParams().setParameter("G-Header", var1);
         Utils.D("update client " + var2.toString() + " g-header " + var1);
      }

   }
}
