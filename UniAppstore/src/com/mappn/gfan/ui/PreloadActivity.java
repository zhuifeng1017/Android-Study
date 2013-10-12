package com.mappn.gfan.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.mappn.gfan.R;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.ProductDetail;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.ui.ProductDetailActivity;
import com.mobclick.android.MobclickAgent;
import java.util.HashMap;

public class PreloadActivity extends BaseActivity implements ApiAsyncTask.ApiRequestListener {

   private static final String ACTION_PACKAGENAME = "pkgName";
   private static final String ACTION_PID = "pid";


   private boolean checkBarcode(Intent var1) {
      Uri var2 = var1.getData();
      boolean var3;
      if(var2 != null) {
         HashMap var4 = Utils.parserUri(var2);
         if(var4 != null) {
            String var5 = (String)var4.get("p");
            if(!TextUtils.isEmpty(var5)) {
               String[] var6 = var5.split(":");
               if(var6 == null || var6.length < 2) {
                  var3 = true;
                  return var3;
               }

               String var7 = var6[0];
               String var8 = var6[1];
               if("pid".equalsIgnoreCase(var7)) {
                  MarketAPI.getProductDetailWithId(this, this, -1, var8, "0");
                  var3 = false;
                  return var3;
               }

               if("pkgName".equalsIgnoreCase(var7)) {
                  MarketAPI.getProductDetailWithPackageName(this, this, -1, var8);
                  var3 = false;
                  return var3;
               }
            }
         }
      }

      var3 = true;
      return var3;
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903117);
      Intent var2 = this.getIntent();
      ProgressBar var3 = (ProgressBar)this.findViewById(2131492869);
      var3.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext(), 0, R.color.hot3, R.color.color_e, 200));
      var3.setVisibility(0);
      if(this.checkBarcode(var2)) {
         String var4 = var2.getStringExtra("extra.key.package.name");
         if(TextUtils.isEmpty(var4)) {
            String var5 = var2.getStringExtra("extra.key.pid");
            String var6 = var2.getStringExtra("extra.key.source.type");
            if(var2.getBooleanExtra("extra.app.push", false)) {
               String var7 = var2.getStringExtra("extra.app.nid");
               String var8 = var2.getStringExtra("extra.app.rule");
               if(!TextUtils.isEmpty(var7)) {
                  MarketAPI.reportIftttResult(this.getApplicationContext(), var5, var7, var8, 1);
               }
            }

            if(TextUtils.isEmpty(var6)) {
               var6 = "0";
            }

            MarketAPI.getProductDetailWithId(this, this, -1, var5, var6);
         } else {
            MarketAPI.getProductDetailWithPackageName(this, this, -1, var4);
         }
      }

   }

   public void onError(int var1, int var2) {
      if(600 == var2) {
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296465), false);
      } else {
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296404), false);
      }

      this.finish();
   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this);
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this);
   }

   public void onSuccess(int var1, Object var2) {
      Intent var3 = new Intent(this.getApplicationContext(), ProductDetailActivity.class);
      var3.putExtra("extra.product.detail", (ProductDetail)var2);
      var3.putExtra("is_buy", this.getIntent().getBooleanExtra("is_buy", false));
      Intent var6 = this.getIntent();
      if(var6.getBooleanExtra("extra.app.push", false)) {
         String var7 = var6.getStringExtra("extra.app.nid");
         String var8 = var6.getStringExtra("extra.app.rule");
         var3.putExtra("extra.app.nid", var7);
         var3.putExtra("extra.app.rule", var8);
      }

      if(var6 != null && var6.getBooleanExtra("extra.app.push", false)) {
         ;
      }

      this.finish();
      this.startActivity(var3);
   }
}
