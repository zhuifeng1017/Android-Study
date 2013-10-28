package com.xxx.appstore.common.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import com.xxx.appstore.common.util.Utils;
//import com.xxx.appstore.ui.PayMainActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class BaseHelper {

   public static void chmod(String var0, String var1) {
      try {
         String var3 = "chmod " + var0 + " " + var1;
         Process var4 = Runtime.getRuntime().exec(var3);
         if(var4 != null) {
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.getInputStream()));

            while(true) {
               String var6 = var5.readLine();
               if(var6 == null) {
                  break;
               }

               Utils.D("aMarket line:" + var6);
            }
         }
      } catch (IOException var7) {
         var7.printStackTrace();
      }

   }

   public static String convertStreamToString(InputStream var0) {
      BufferedReader var1 = new BufferedReader(new InputStreamReader(var0));
      StringBuilder var2 = new StringBuilder();

      while(true) {
         boolean var13 = false;

         IOException var6;
         label92: {
            label91: {
               try {
                  var13 = true;
                  String var7 = var1.readLine();
                  if(var7 != null) {
                     var2.append(var7);
                     continue;
                  }

                  var13 = false;
                  break label91;
               } catch (IOException var17) {
                  var17.printStackTrace();
                  var13 = false;
               } finally {
                  if(var13) {
                     try {
                        var0.close();
                     } catch (IOException var14) {
                        var14.printStackTrace();
                     }

                  }
               }

               try {
                  var0.close();
                  break;
               } catch (IOException var15) {
                  var6 = var15;
                  break label92;
               }
            }

            try {
               var0.close();
               break;
            } catch (IOException var16) {
               var6 = var16;
            }
         }

         var6.printStackTrace();
         break;
      }

      return var2.toString();
   }

   public static void log(String var0, String var1) {
      Log.d(var0, var1);
   }

   public static void showDialog(Activity var0, String var1, String var2, int var3) {
      Builder var4 = new Builder(var0);
      var4.setIcon(var3);
      var4.setTitle(var1);
      var4.setMessage(var2);
      var4.setPositiveButton("确定", (OnClickListener)null);
      var4.show();
   }

   public static ProgressDialog showProgress(Context var0, CharSequence var1, CharSequence var2, boolean var3, boolean var4) {
      ProgressDialog var5 = new ProgressDialog(var0);
      var5.setTitle(var1);
      var5.setMessage(var2);
      var5.setIndeterminate(var3);
      var5.setCancelable(false);
 //     var5.setOnCancelListener(new PayMainActivity.AlixOnCancelListener((Activity)var0));
      var5.show();
      return var5;
   }

   public static JSONObject string2JSON(String paramString1, String paramString2)
   {
     int i = 0;
     JSONObject localJSONObject = new JSONObject();
     try
     {
       String[] arrayOfString1 = paramString1.split(paramString2);
       while (i < arrayOfString1.length)
       {
         String[] arrayOfString2 = arrayOfString1[i].split("=");
         localJSONObject.put(arrayOfString2[0], arrayOfString1[i].substring(1 + arrayOfString2[0].length()));
         i++;
       }
     }
     catch (Exception localException)
     {
       localException.printStackTrace();
     }
     return localJSONObject;
   }
}
