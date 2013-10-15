package com.xxx.appstore.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.AndroidHttpClient;
import com.xxx.appstore.common.codec.digest.DigestUtils;
import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.SecurityUtil;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.LogEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiRequestFactory {

   public static ArrayList<Integer> API_CACHE_MAP;
   private static final String[] REPLACE;
   private static ArrayList<Integer> S_ENCODE_FORM_REQUESTS = new ArrayList();
   private static ArrayList<Integer> S_ENCRYPT_REQUESTS = new ArrayList();
   private static ArrayList<Integer> S_JSON_REQUESTS = new ArrayList();
   private static ArrayList<Integer> S_XML_REQUESTS = new ArrayList();
   private static ArrayList<Integer> UCENTER_API;


   static {
      S_XML_REQUESTS.add(Integer.valueOf(16));
      S_XML_REQUESTS.add(Integer.valueOf(18));
      S_XML_REQUESTS.add(Integer.valueOf(33));
      S_XML_REQUESTS.add(Integer.valueOf(11));
      S_XML_REQUESTS.add(Integer.valueOf(34));
      S_XML_REQUESTS.add(Integer.valueOf(28));
      S_XML_REQUESTS.add(Integer.valueOf(35));
      S_XML_REQUESTS.add(Integer.valueOf(36));
      S_XML_REQUESTS.add(Integer.valueOf(30));
      S_XML_REQUESTS.add(Integer.valueOf(14));
      S_XML_REQUESTS.add(Integer.valueOf(2));
      S_XML_REQUESTS.add(Integer.valueOf(8));
      S_XML_REQUESTS.add(Integer.valueOf(3));
      S_XML_REQUESTS.add(Integer.valueOf(4));
      S_XML_REQUESTS.add(Integer.valueOf(37));
      S_XML_REQUESTS.add(Integer.valueOf(12));
      S_XML_REQUESTS.add(Integer.valueOf(10));
      S_XML_REQUESTS.add(Integer.valueOf(13));
      S_XML_REQUESTS.add(Integer.valueOf(38));
      S_XML_REQUESTS.add(Integer.valueOf(15));
      S_XML_REQUESTS.add(Integer.valueOf(19));
      S_XML_REQUESTS.add(Integer.valueOf(17));
      S_XML_REQUESTS.add(Integer.valueOf(5));
      S_XML_REQUESTS.add(Integer.valueOf(24));
      S_XML_REQUESTS.add(Integer.valueOf(25));
      S_XML_REQUESTS.add(Integer.valueOf(7));
      S_XML_REQUESTS.add(Integer.valueOf(27));
      S_XML_REQUESTS.add(Integer.valueOf(42));
      S_XML_REQUESTS.add(Integer.valueOf(43));
      S_XML_REQUESTS.add(Integer.valueOf(45));
      S_XML_REQUESTS.add(Integer.valueOf(46));
      S_XML_REQUESTS.add(Integer.valueOf(47));
      S_XML_REQUESTS.add(Integer.valueOf(48));
      S_XML_REQUESTS.add(Integer.valueOf(44));
      S_XML_REQUESTS.add(Integer.valueOf(49));
      S_XML_REQUESTS.add(Integer.valueOf(50));
      S_XML_REQUESTS.add(Integer.valueOf(51));
      S_XML_REQUESTS.add(Integer.valueOf(52));
      S_XML_REQUESTS.add(Integer.valueOf(53));
      S_XML_REQUESTS.add(Integer.valueOf(54));
      S_XML_REQUESTS.add(Integer.valueOf(55));
      S_XML_REQUESTS.add(Integer.valueOf(56));
      S_JSON_REQUESTS.add(Integer.valueOf(20));
      S_JSON_REQUESTS.add(Integer.valueOf(31));
      S_JSON_REQUESTS.add(Integer.valueOf(32));
      S_JSON_REQUESTS.add(Integer.valueOf(40));
      S_JSON_REQUESTS.add(Integer.valueOf(41));
      S_ENCRYPT_REQUESTS.add(Integer.valueOf(1));
      S_ENCRYPT_REQUESTS.add(Integer.valueOf(0));
      S_ENCRYPT_REQUESTS.add(Integer.valueOf(22));
      S_ENCRYPT_REQUESTS.add(Integer.valueOf(23));
      S_ENCRYPT_REQUESTS.add(Integer.valueOf(21));
      S_ENCODE_FORM_REQUESTS.add(Integer.valueOf(31));
      S_ENCODE_FORM_REQUESTS.add(Integer.valueOf(32));
      S_ENCODE_FORM_REQUESTS.add(Integer.valueOf(39));
      UCENTER_API = new ArrayList();
      UCENTER_API.add(Integer.valueOf(1));
      UCENTER_API.add(Integer.valueOf(0));
      UCENTER_API.add(Integer.valueOf(21));
      UCENTER_API.add(Integer.valueOf(26));
      UCENTER_API.add(Integer.valueOf(5));
      UCENTER_API.add(Integer.valueOf(6));
      UCENTER_API.add(Integer.valueOf(9));
      UCENTER_API.add(Integer.valueOf(22));
      UCENTER_API.add(Integer.valueOf(23));
      UCENTER_API.add(Integer.valueOf(24));
      UCENTER_API.add(Integer.valueOf(25));
      API_CACHE_MAP = new ArrayList();
      API_CACHE_MAP.add(Integer.valueOf(13));
      API_CACHE_MAP.add(Integer.valueOf(14));
      API_CACHE_MAP.add(Integer.valueOf(18));
      API_CACHE_MAP.add(Integer.valueOf(30));
      API_CACHE_MAP.add(Integer.valueOf(33));
      API_CACHE_MAP.add(Integer.valueOf(34));
      API_CACHE_MAP.add(Integer.valueOf(35));
      API_CACHE_MAP.add(Integer.valueOf(36));
      API_CACHE_MAP.add(Integer.valueOf(37));
      API_CACHE_MAP.add(Integer.valueOf(38));
      API_CACHE_MAP.add(Integer.valueOf(11));
      API_CACHE_MAP.add(Integer.valueOf(39));
      REPLACE = new String[]{"&", "&amp;", "\"", "&quot;", "\'", "&apos;", "<", "&lt;", ">", "&gt;"};
   }

   private static String generateJsonRequestBody(Object var0) {
      String var1;
      if(var0 == null) {
         var1 = "";
      } else if(var0 instanceof HashMap) {
         HashMap var2 = (HashMap)var0;
         Iterator var3 = var2.keySet().iterator();
         JSONObject var4 = new JSONObject();

         try {
            while(var3.hasNext()) {
               String var6 = (String)var3.next();
               var4.put(var6, var2.get(var6));
            }
         } catch (JSONException var7) {
            var7.printStackTrace();
            var1 = "";
            return var1;
         }

         var1 = var4.toString();
      } else {
         var1 = "";
      }

      return var1;
   }

   private static String generateLogBody(Context var0, Object var1) {
      HashMap var2 = (HashMap)var1;
      ArrayList var3 = DBUtils.submitLogs(var0, (String)var2.get("module"), ((Integer)var2.get("level")).intValue());
      String var4;
      if(var3 != null && var3.size() != 0) {
         JSONArray var5 = new JSONArray();

         try {
            Session var7 = Session.get(var0);
            Iterator var8 = var3.iterator();

            while(var8.hasNext()) {
               LogEntity var9 = (LogEntity)var8.next();
               JSONObject var10 = new JSONObject();
               var10.put("module", var9.module);
               var10.put("level", judgeLevel(var9.level));
               var10.put("client", var7.getAppName());
               var10.put("os", var7.getOsVersion());
               var10.put("model", var7.getModel());
               var10.put("rom", Build.FINGERPRINT);
               var10.put("network", var9.network);
               var10.put("client_time", var9.createTime);
               var10.put("body", var9.getLogContent());
               var10.put("fingerprint", Utils.getFingerPrint(var0));
               var5.put(var10);
            }
         } catch (JSONException var11) {
            var11.printStackTrace();
         }

         var4 = var5.toString();
      } else {
         var4 = "{}";
      }

      return var4;
   }

   private static String generateLogModules(String[] param0)
   {
     JSONArray localJSONArray = new JSONArray();
     try
     {
       int i = param0.length;
       for (int j = 0; j < i; j++)
       {
         String str = param0[j];
         JSONObject localJSONObject = new JSONObject();
         localJSONObject.put("module", str);
         localJSONArray.put(localJSONObject);
       }
     }
     catch (JSONException localJSONException)
     {
       localJSONException.printStackTrace();
     }
     return localJSONArray.toString();
   }
  
   private static String generateXmlRequestBody(Context var0, Object var1) {
      String var2;
      if(var1 == null) {
         var2 = "<request version=\"2\"></request>";
      } else if(var1 instanceof HashMap) {
         HashMap var3 = (HashMap)var1;
         StringBuilder var4 = new StringBuilder();
         var4.append("<request version=\"2\"");
         if(var3.containsKey("local_version")) {
            var4.append(" local_version=\"" + var3.get("local_version") + "\" ");
            var3.remove("local_version");
         }

         var4.append(">");
         Iterator var7 = var3.keySet().iterator();

         while(var7.hasNext()) {
            String var9 = (String)var7.next();
            if("upgradeList".equals(var9)) {
               var4.append("<products>");
               Iterator var25 = ((List)var3.get(var9)).iterator();

               while(var25.hasNext()) {
                  PackageInfo var27 = (PackageInfo)var25.next();
                  var4.append("<product package_name=\"").append(wrapText(var27.packageName));
                  var4.append("\" version_code=\"").append(var27.versionCode).append("\"/>");
               }

               var4.append("</products>");
            } else if(!"appList".equals(var9)) {
               var4.append("<").append(var9).append(">");

               try {
                  var4.append(wrapText(String.valueOf(var3.get(var9))));
               } catch (Exception var28) {
                  Utils.D("wrap text", var28);
               }

               var4.append("</").append(var9).append(">");
            } else {
               var4.append("<apps>");
               PackageManager var15 = var0.getPackageManager();
               Iterator var16 = var15.getInstalledPackages(0).iterator();

               while(var16.hasNext()) {
                  PackageInfo var18 = (PackageInfo)var16.next();
                  var4.append("<app package_name=\"").append(wrapText(var18.packageName));
                  var4.append("\" version_code=\"").append(var18.versionCode);
                  var4.append("\" version_name=\"").append(wrapText(var18.versionName));
                  var4.append("\" app_name=\"").append(wrapText(String.valueOf(var18.applicationInfo.loadLabel(var15))));
                  var4.append("\"/>");
               }

               var4.append("</apps>");
            }
         }

         var4.append("</request>");
         var2 = var4.toString();
      } else {
         var2 = "<request version=\"2\"></request>";
      }

      return var2;
   }

   private static ByteArrayEntity getEncryptRequest(Context var0, int var1, Object var2) {
      String var3 = generateXmlRequestBody(var0, var2);
      Utils.D("generate request body before encryption  is : " + var3);
      ByteArrayEntity var4;
      if(var1 == 23) {
         var4 = new ByteArrayEntity(SecurityUtil.encryptHttpChargeBody(var3));
      } else {
         var4 = new ByteArrayEntity(SecurityUtil.encryptHttpBody(var3));
      }

      return var4;
   }

   private static UrlEncodedFormEntity getFormRequest(int var0, Object var1) throws UnsupportedEncodingException {
      UrlEncodedFormEntity var10;
      if(var0 != 31 && var0 != 32) {
         if(var1 instanceof ArrayList) {
            var10 = new UrlEncodedFormEntity((ArrayList)var1, "UTF-8");
         } else {
            var10 = null;
         }
      } else {
         String var2 = generateJsonRequestBody(var1);
         Utils.D("generate JSON request body is : " + var2);
         String var3 = new String(SecurityUtil.encryptHttpChargePalipayBody(var2), "UTF-8");
         String var4;
         if(31 == var0) {
            var4 = "addAlipayOrder";
         } else {
            var4 = "queryAlipayOrderIsSuccess";
         }

         ArrayList var5 = new ArrayList(4);
         var5.add(new BasicNameValuePair("action", var4));
         var5.add(new BasicNameValuePair("data", var3));
         var5.add(new BasicNameValuePair("cno", "03"));
         var5.add(new BasicNameValuePair("sign", DigestUtils.md5Hex("action=" + var4 + "&data=" + var3 + "&cno=" + "03" + "h9sEVED84X81u9ev")));
         var10 = new UrlEncodedFormEntity(var5, "UTF-8");
      }

      return var10;
   }

   private static StringEntity getJsonRequest(Context var0, int var1, Object var2) throws UnsupportedEncodingException {
      String var3;
      if(40 == var1) {
         var3 = generateLogModules((String[])((String[])var2));
      } else if(41 == var1) {
         var3 = generateLogBody(var0, var2);
      } else {
         var3 = generateJsonRequestBody(var2);
      }

      Utils.D("generate JSON request body is : " + var3);
      return new StringEntity(var3, "UTF-8");
   }

   public static HttpUriRequest getRequest(String var0, int var1, HttpEntity var2, Session var3) throws IOException {
      Object var4;
      if(29 == var1) {
         var4 = new HttpGet(var0 + var3.getUid());
      } else if(UCENTER_API.contains(Integer.valueOf(var1))) {
         var4 = new HttpPost(var0);
         ((HttpPost)var4).setHeader("User-Agent", var3.getUCenterApiUserAgent());
         ((HttpPost)var4).setEntity(var2);
      } else if(S_XML_REQUESTS.contains(Integer.valueOf(var1))) {
         var4 = new HttpPost(var0);
         ((HttpPost)var4).setHeader("G-Header", var3.getJavaApiUserAgent());
         ((HttpPost)var4).addHeader("Accept-Encoding", "gzip");
         ((HttpPost)var4).setEntity(AndroidHttpClient.getCompressedEntity(var2.getContent()));
      } else if(40 != var1 && 41 != var1) {
         var4 = new HttpPost(var0);
         ((HttpPost)var4).setEntity(var2);
      } else {
         var4 = new HttpPost(var0);
         ((HttpPost)var4).addHeader("Accept-Encoding", "gzip");
         ((HttpPost)var4).setEntity(AndroidHttpClient.getCompressedEntity(var2.getContent()));
      }

      return (HttpUriRequest)var4;
   }

   public static HttpEntity getRequestEntity(Context var0, int var1, Object var2) throws UnsupportedEncodingException {
      Object var3;
      if(S_XML_REQUESTS.contains(Integer.valueOf(var1))) {
         var3 = getXmlRequest(var0, var2);
      } else if(S_ENCODE_FORM_REQUESTS.contains(Integer.valueOf(var1))) {
         var3 = getFormRequest(var1, var2);
      } else if(S_JSON_REQUESTS.contains(Integer.valueOf(var1))) {
         var3 = getJsonRequest(var0, var1, var2);
      } else if(S_ENCRYPT_REQUESTS.contains(Integer.valueOf(var1))) {
         var3 = getEncryptRequest(var0, var1, var2);
      } else {
         var3 = null;
      }

      return (HttpEntity)var3;
   }

   private static StringEntity getXmlRequest(Context var0, Object var1) throws UnsupportedEncodingException {
      String var2 = generateXmlRequestBody(var0, var1);
      Utils.D("generate XML request body is : " + var2);
      return new StringEntity(var2, "UTF-8");
   }

   private static String judgeLevel(int var0) {
      String var1;
      if(var0 == 1) {
         var1 = "V";
      } else if(var0 == 3) {
         var1 = "V";
      } else if(var0 == 2) {
         var1 = "D";
      } else if(var0 == 4) {
         var1 = "W";
      } else if(var0 == 5) {
         var1 = "E";
      } else {
         var1 = "";
      }

      return var1;
   }

   private static String wrapText(String var0) {
      String var1;
      if(!TextUtils.isEmpty(var0)) {
         int var2 = 0;
         int var3 = REPLACE.length;

         String var4;
         for(var4 = var0; var2 < var3; var2 += 2) {
            var4 = var4.replace(REPLACE[var2], REPLACE[var2 + 1]);
         }

         var1 = var4;
      } else {
         var1 = "";
      }

      return var1;
   }
}
