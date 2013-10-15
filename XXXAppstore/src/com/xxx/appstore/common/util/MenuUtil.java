package com.xxx.appstore.common.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.gfan.sdk.statistics.Collector;
import com.xxx.appstore.common.util.DialogUtil;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.ui.HomeActivity;

import org.apache.http.HttpResponse;

public class MenuUtil {

   public static final int DIALOG_RESPONSE = 200;


   public static Dialog createResponseDialog(final Context var0, int var1) {
      return DialogUtil.createBigInputDialog(var0, var1, 2131296538, new DialogUtil.InputDialogListener() {
         public void onInputDialogCancel(int var1) {}
         public void onInputDialogOK(int var1, String var2) {
            String var3 = var0.getClass().getName() + ":" + var2;
            if(!TextUtils.isEmpty(var2)) {
               Collector.comment(var0, var3, new Collector.IResponse() {
                  public void onFailed(Exception var1) {
                     Utils.makeEventToast(var0, var0.getString(2131296539), false);
                  }
                  public void onSuccess(HttpResponse var1) {
                     Utils.makeEventToast(var0, var0.getString(2131296539), false);
                  }
               });
            } else {
               Utils.makeEventToast(var0, var0.getString(2131296540), false);
            }

         }
      });
   }

   public static void onMenuSelectedHome(Context var0) {
      Activity var1 = (Activity)var0;
      Intent var2 = new Intent(var1, HomeActivity.class);
      var2.setFlags(67108864);
      var1.startActivity(var2);
   }

   public static void onMenuSelectedResponse(Context var0) {
      Activity var1 = (Activity)var0;
      if(!var1.isFinishing()) {
         if(Utils.isNetworkAvailable(var0)) {
            var1.showDialog(200);
         } else {
            Utils.makeEventToast(var0, var0.getString(2131296494), false);
         }
      }

   }
}
