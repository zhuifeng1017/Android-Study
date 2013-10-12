package com.mappn.gfan.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.download.DownloadManager;
import com.mappn.gfan.common.util.DBUtils;
import com.mappn.gfan.common.util.DialogUtil;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.BuyLog;
import com.mappn.gfan.common.vo.DownloadInfo;
import com.mappn.gfan.common.vo.DownloadItem;
import com.mappn.gfan.common.vo.ProductDetail;
import com.mappn.gfan.common.widget.BaseTabActivity;
import com.mappn.gfan.common.widget.MarqueeTextView;
import com.mappn.gfan.ui.ChargeTypeListActivity;
import com.mappn.gfan.ui.LoginActivity;
import com.mappn.gfan.ui.PayMainActivity;
import com.mappn.gfan.ui.ProductCommentActivity;
import com.mappn.gfan.ui.ProductInfoActivity;
import com.mappn.gfan.ui.SafeActivity;
import java.io.Serializable;

public class ProductDetailActivity extends BaseTabActivity implements ApiAsyncTask.ApiRequestListener {

   public static final String ACTION_DOWNLOAD = "download";
   private static final int DIALOG_NO_BALANCE = 2;
   private static final int DIALOG_PURCHASE = 1;
   private static final String TAB_COMMENT = "comment";
   private static final String TAB_INFO = "info";
   private BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
      public void onReceive(Context var1, Intent var2) {
         Serializable var3 = var2.getSerializableExtra("extra.product.detail");
         if(var3 != null) {
            ProductDetail var4 = (ProductDetail)var3;
            if(var4.getPackageName().equals(ProductDetailActivity.this.mProduct.getPackageName())) {
               ProductDetailActivity.this.mProduct.isPendingDownload = var4.isPendingDownload;
               ProductDetailActivity.this.download();
            }
         }

      }
   };
   private boolean mIsRecommend;
   private ProductDetail mProduct;
   private TabHost mTabHost;


   private void excuteDownload() {
      DownloadInfo var1 = (DownloadInfo)this.mSession.getDownloadingList().get(this.mProduct.getPackageName());
      boolean var2;
      if(var1 != null && Utils.isFileExist(var1.mFilePath)) {
         var2 = true;
      } else {
         var2 = false;
      }

      if(var2) {
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296458), false);
      } else if(this.mSession.getInstalledApps().contains(this.mProduct.getPackageName()) && !Utils.isSameSign(this.getApplicationContext(), this.mProduct.getPackageName(), this.mProduct.getRsaMd5())) {
         if(!this.isFinishing()) {
            DialogUtil.createComfirmDownloadDialog(this, true, new DialogUtil.WarningDialogListener() {
               public void onWarningDialogCancel(int var1) {}
               public void onWarningDialogOK(int var1) {
                  MarketAPI.getDownloadUrl(ProductDetailActivity.this.getApplicationContext(), ProductDetailActivity.this, ProductDetailActivity.this.mProduct.getPid(), ProductDetailActivity.this.mProduct.getSourceType());
               }
            }).show();
         }
      } else {
         MarketAPI.getDownloadUrl(this.getApplicationContext(), this, this.mProduct.getPid(), this.mProduct.getSourceType());
      }

   }

   private void initTab(ProductDetail var1) {
      this.mTabHost = this.getTabHost();
      this.mTabHost.getTabWidget().setPadding(this.mSession.mTabMargin110, 0, this.mSession.mTabMargin9, 0);
      this.mTabHost.setup();
      ((FrameLayout)this.mTabHost.findViewById(2131492901)).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));
      TabSpec var2 = this.mTabHost.newTabSpec("info");
      var2.setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296453), -1, (TextView)null));
      Intent var4 = new Intent(this, ProductInfoActivity.class);
      var4.putExtra("extra.product.detail", var1);
      var2.setContent(var4);
      this.mTabHost.addTab(var2);
      TabSpec var7 = this.mTabHost.newTabSpec("comment");
      var7.setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296457), 1, (TextView)null));
      Intent var9 = new Intent(this, ProductCommentActivity.class);
      var9.putExtra("extra.product.detail", var1);
      var7.setContent(var9);
      this.mTabHost.addTab(var7);
      this.mTabHost.setCurrentTab(0);
   }

   private void initTopBar(ProductDetail var1) {
      ((TextView)this.findViewById(2131492913)).setBackgroundResource(ThemeManager.getResource(this.mSession, 30));
      ImageView var2 = (ImageView)this.findViewById(2131492864);
      ImageUtils.download(this.getApplicationContext(), var1.getIconUrl(), var2, 2130837640, true);
      MarqueeTextView var3 = (MarqueeTextView)this.findViewById(2131492868);
      var3.setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 13));
      if(this.mSession.getTheme() == 2131361833) {
         var3.setShadowLayer(0.1F, 0.0F, -2.0F, -16777216);
      }

      var3.setText(var1.getName());
      ImageButton var4 = (ImageButton)this.findViewById(2131492915);
      var4.setImageResource(ThemeManager.getResource(this.mSession, 35));
      var4.setBackgroundResource(ThemeManager.getResource(this.mSession, 42));
      var4.setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            Utils.trackEvent(ProductDetailActivity.this.getApplicationContext(), new String[]{"详情", "分享应用"});
            Utils.share(ProductDetailActivity.this.getCurrentActivity(), ProductDetailActivity.this.mProduct.getName(), ProductDetailActivity.this.mProduct.getPid());
         }
      });
   }

   private void startDownload(DownloadItem var1) {
      DownloadManager.Request var2 = new DownloadManager.Request(Uri.parse(var1.url));
      var2.setPackageName(this.mProduct.getPackageName());
      var2.setTitle(this.mProduct.getName());
      var2.setIconUrl(this.mProduct.getIconUrl());
      var2.setMD5(var1.fileMD5);
      var2.setSourceType(0);
      this.mSession.getDownloadManager().enqueue(this.getApplicationContext(), var2, (DownloadManager.EnqueueListener)null);
      if(this.mIsRecommend) {
         Context var11 = this.getApplicationContext();
         String[] var12 = new String[]{"详情", null};
         Object[] var13 = new Object[]{this.mProduct.getName()};
         var12[1] = String.format("首页推荐带来直接下载[%s]", var13);
         Utils.trackEvent(var11, var12);
      }

      Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296469), false);
      Utils.submitDownloadLog(this.getApplicationContext(), 0, 0, var1.url, var1.packageName);
      Intent var8 = this.getIntent();
      String var9 = var8.getStringExtra("extra.app.nid");
      String var10 = var8.getStringExtra("extra.app.rule");
      if(!TextUtils.isEmpty(var9)) {
         MarketAPI.reportIftttResult(this.getApplicationContext(), this.mProduct.getPid(), var9, var10, 2);
      }

   }

   private void startWaitingDownload(DownloadItem var1) {
      DownloadManager.Request var2 = new DownloadManager.Request(Uri.parse(var1.url));
      var2.setPackageName(this.mProduct.getPackageName());
      var2.setTitle(this.mProduct.getName());
      var2.setIconUrl(this.mProduct.getIconUrl());
      var2.setMD5(var1.fileMD5);
      var2.setSourceType(0);
      this.mSession.getDownloadManager().enqueueWaitRequest(var2, (DownloadManager.EnqueueListener)null);
      Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296470), true);
      Utils.submitDownloadLog(this.getApplicationContext(), 0, 0, var1.url, var1.packageName);
   }

   public void download() {
      if(2 == this.mProduct.getPayCategory()) {
         if(!this.mSession.isLogin()) {
            this.startActivity(new Intent(this.getApplicationContext(), LoginActivity.class));
         } else {
            DBUtils.isBought(this.getApplicationContext(), this.mProduct.getPid(), new DBUtils.DbOperationResultListener() {
               protected void onQueryResult(Boolean var1) {
                  if(!var1.booleanValue() && !ProductDetailActivity.this.isFinishing()) {
                     ProductDetailActivity.this.showDialog(1);
                  } else {
                     ProductDetailActivity.this.excuteDownload();
                  }

               }
            });
         }
      } else {
         this.excuteDownload();
      }

   }

   public void gotoDepositPage() {
      String var1 = this.mSession.getDefaultChargeType();
      if(var1 == null) {
         Intent var2 = new Intent(this.getApplicationContext(), ChargeTypeListActivity.class);
         var2.putExtra("payment", this.mProduct.getPrice());
         this.startActivity(var2);
      } else {
         Intent var4 = new Intent(this.getApplicationContext(), PayMainActivity.class);
         var4.putExtra("type", var1);
         var4.putExtra("payment", this.mProduct.getPrice());
         this.startActivity(var4);
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903071);
      Intent var2 = this.getIntent();
      this.mProduct = (ProductDetail)var2.getSerializableExtra("extra.product.detail");
      this.mIsRecommend = var2.getBooleanExtra("extra.key.recommend", false);
      this.initTopBar(this.mProduct);
      this.initTab(this.mProduct);
      if(var2.getBooleanExtra("is_buy", false)) {
         this.showDialog(1);
      }

      IntentFilter var3 = new IntentFilter("download");
      this.registerReceiver(this.mActionReceiver, var3);
   }

   protected Dialog onCreateDialog(int var1) {
      Dialog var2;
      switch(var1) {
      case 1:
         Object[] var3 = new Object[]{Integer.valueOf(this.mProduct.getPrice())};
         var2 = DialogUtil.newEnsurePurchaseDialog(this, var1, this.getString(2131296406, var3));
         break;
      case 2:
         var2 = DialogUtil.newInsufficientBalanceDialog(this, var1, this.getString(2131296410));
         break;
      default:
         var2 = null;
      }

      return var2;
   }

   public boolean onCreateOptionsMenu(Menu var1) {
      var1.add(0, 0, 0, this.getString(2131296663)).setIcon(2130837618);
      return true;
   }

   protected void onDestroy() {
      super.onDestroy();
      this.unregisterReceiver(this.mActionReceiver);
      this.getLocalActivityManager().removeAllActivities();
   }

   public void onError(int var1, int var2) {
      switch(var1) {
      case 5:
         if(219 == var2) {
            if(!this.isFinishing()) {
               this.showDialog(2);
            }
         } else if(212 == var2) {
            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296409), false);
         } else {
            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296408), false);
         }
         break;
      case 15:
         Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296471), false);
      }

   }

   public boolean onOptionsItemSelected(MenuItem var1) {
      boolean var2;
      switch(var1.getItemId()) {
      case 0:
         this.startActivity(new Intent(this.getApplicationContext(), SafeActivity.class));
         var2 = true;
         break;
      default:
         var2 = false;
      }

      return var2;
   }

   public void onSuccess(int var1, Object var2) {
      switch(var1) {
      case 5:
         BuyLog var3 = new BuyLog();
         var3.pId = this.mProduct.getPid();
         var3.packageName = this.mProduct.getPackageName();
         DBUtils.insertBuyLog(this.getApplicationContext(), var3);
         MarketAPI.getDownloadUrl(this, this, this.mProduct.getPid(), this.mProduct.getSourceType());
         break;
      case 15:
         DownloadItem var4 = (DownloadItem)var2;
         if(this.mProduct.isPendingDownload) {
            this.startWaitingDownload(var4);
         } else {
            this.startDownload(var4);
         }
      }

   }

   public void purchaseProduct(String var1) {
      MarketAPI.purchaseProduct(this, this, this.mProduct.getPid(), var1);
   }
}
