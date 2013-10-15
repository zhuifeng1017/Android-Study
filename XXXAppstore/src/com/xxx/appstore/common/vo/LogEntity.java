package com.xxx.appstore.common.vo;

import android.content.Context;
import android.text.TextUtils;

import com.xxx.appstore.common.util.Pair;
import com.xxx.appstore.common.util.Utils;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class LogEntity implements Serializable {

   public static final int LOG_LEVEL_D = 2;
   public static final String LOG_LEVEL_D_STR = "D";
   public static final int LOG_LEVEL_E = 5;
   public static final String LOG_LEVEL_E_STR = "E";
   public static final int LOG_LEVEL_I = 3;
   public static final String LOG_LEVEL_I_STR = "I";
   public static final int LOG_LEVEL_V = 1;
   public static final String LOG_LEVEL_V_STR = "V";
   public static final int LOG_LEVEL_W = 4;
   public static final String LOG_LEVEL_W_STR = "W";
   private static final long serialVersionUID = -1825000203436679450L;
   public long createTime;
   public int level;
   private String log;
   private JSONObject logObject;
   public String module;
   public String network;


   public LogEntity(Context var1, String var2, int var3) {
      this.module = var2;
      this.level = var3;
      this.createTime = System.currentTimeMillis() / 1000L;
      boolean var4 = Utils.isNetworkAvailable(var1);
      String var5;
      if(Utils.isMobileNetwork(var1)) {
         var5 = "Mobile Network ";
      } else {
         var5 = "Other Network ";
      }

      StringBuilder var6 = (new StringBuilder()).append(var5);
      String var7;
      if(var4) {
         var7 = "OK";
      } else {
         var7 = "ERROR";
      }

      this.network = var6.append(var7).toString();
      this.logObject = new JSONObject();
   }

   public void addLogContent(Pair<String, String> var1) {
      try {
         this.logObject.put((String)var1.first, var1.second);
      } catch (JSONException var3) {
         Utils.E("add log content meet json exception", var3);
      }

   }

   public String getLogContent() {
      String var1;
      if(!TextUtils.isEmpty(this.log)) {
         var1 = this.log;
      } else if(this.logObject != null) {
         var1 = this.logObject.toString();
      } else {
         var1 = "";
      }

      return var1;
   }

   public void setLogContent(String var1) {
      this.log = var1;
   }
}
