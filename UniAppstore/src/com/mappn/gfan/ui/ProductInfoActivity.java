package com.mappn.gfan.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.download.DownloadManager;
import com.mappn.gfan.common.util.AppSecurityPermissions;
import com.mappn.gfan.common.util.DialogUtil;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.util.StringUtils;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.DownloadInfo;
import com.mappn.gfan.common.vo.ProductDetail;
import com.mappn.gfan.common.vo.RecommendTopic;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.ui.ScreenshotActivity;
import com.mobclick.android.MobclickAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

public class ProductInfoActivity extends BaseActivity implements ApiAsyncTask.ApiRequestListener, OnClickListener, Observer {

   private static final int MASTER_ID = 200;
   private static final int MORE_PRODUCT_ID = 100;
   private static final int STATUS_DOWNLOADED = 3;
   private static final int STATUS_DOWNLOADING = 5;
   private static final int STATUS_INSTALLED = 1;
   private static final int STATUS_PENDING = 6;
   private static final int STATUS_UNINSTALLED = 0;
   private static final int STATUS_UPDATABLE = 2;
   private static final int STATUS_UPDATE_DOWNLOADED = 4;
   private boolean isInit = false;
   private boolean isMasterLoaded = false;
   private boolean isMoreAppLoaded = false;
   private boolean isPermissionShow;
   private LinearLayout mActionBar;
   private TextView mBtnCancelDownload;
   private int mCurrentStatus;
   private DownloadInfo mDownloadInfo;
   private ProgressBar mDownloadProgress;
   private TextView mDownloadRemainSize;
   private RelativeLayout mDownloadStatusBar;
   private TextView mDownloadStatusTitle;
   private Handler mHandler = new Handler();
   private int mInitAppStatus;
   private boolean mIsShortDescription = true;
   private long mLastRatingTime = 0L;
   private TextView mLongDescription;
   private ArrayList<HashMap<String, Object>> mMasters;
   private ArrayList<HashMap<String, Object>> mMoreApps;
   private Button mMoreButton;
   private RatingBar mMyRating;
   private ImageView mPermissionIndicator;
   private ProductDetail mProduct;
   private TextView mRanking;
   private OnClickListener mScreenShotClickListener = new OnClickListener() {
      public void onClick(View var1) {
         Utils.trackEvent(ProductInfoActivity.this.getApplicationContext(), new String[]{"详情", "点击截图"});
         Integer var2 = (Integer)var1.getTag();
         Intent var3 = new Intent(ProductInfoActivity.this.getApplicationContext(), ScreenshotActivity.class);
         var3.putExtra("extra.product.detail", ProductInfoActivity.this.mProduct);
         var3.putExtra("extra.screenshot.id", var2);
         ProductInfoActivity.this.startActivity(var3);
      }
   };
   private ScrollView mScrollView;
   private LinearLayout mSecurityList;
   private RelativeLayout mSecurityView;
   private TextView mShortDescription;


   private void excuteInstallation() {
      if(Utils.compareFileWithPathAndPkg(this.getApplicationContext(), this.mProduct.getFilePath(), this.mProduct.getPackageName())) {
         Utils.installApk(this.getApplicationContext(), new File(this.mProduct.getFilePath()));
      } else {
         DialogUtil.createComfirmDownloadDialog(this, false, new DialogUtil.WarningDialogListener() {
            public void onWarningDialogCancel(int var1) {}
            public void onWarningDialogOK(int var1) {
               ProductInfoActivity.this.mSession.mNotSameApps.put(ProductInfoActivity.this.mProduct.getPackageName(), ProductInfoActivity.this.mProduct.getFilePath());
               Utils.uninstallApk(ProductInfoActivity.this.getApplicationContext(), ProductInfoActivity.this.mProduct.getPackageName());
            }
         }).show();
      }

   }

   private void handleLeftAction(int var1) {
      switch(var1) {
      case 0:
      case 2:
         Intent var6 = new Intent("download");
         var6.putExtra("extra.product.detail", this.mProduct);
         this.sendBroadcast(var6);
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "点击下载或更新"});
         break;
      case 1:
         Utils.openApk(this.getApplicationContext(), this.mProduct.getPackageName());
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "打开应用"});
         break;
      case 3:
      case 4:
         this.excuteInstallation();
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "安装应用"});
      case 5:
      default:
         break;
      case 6:
         DownloadInfo var2 = (DownloadInfo)this.mSession.getDownloadingList().get(this.mProduct.getPackageName());
         if(var2 != null) {
            DownloadManager var3 = this.mSession.getDownloadManager();
            long[] var4 = new long[]{var2.id};
            var3.resumeDownload(var4);
         }

         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "马上开始下载"});
      }

   }

   private void handleRightAction(int var1) {
      switch(var1) {
      case 0:
         Intent var7 = new Intent("download");
         this.mProduct.isPendingDownload = true;
         var7.putExtra("extra.product.detail", this.mProduct);
         this.sendBroadcast(var7);
         this.mCurrentStatus = 6;
         this.switchActionBarStatus(this.mCurrentStatus);
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "加入下载队列"});
         break;
      case 1:
      case 2:
      case 4:
         Utils.uninstallApk(this.getApplicationContext(), this.mProduct.getPackageName());
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "卸载应用"});
         break;
      case 3:
         DownloadManager var5 = this.mSession.getDownloadManager();
         long[] var6 = new long[]{this.mDownloadInfo.id};
         var5.deleteDownloadedFile(var6);
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "删除已下载文件"});
      case 5:
      default:
         break;
      case 6:
         DownloadInfo var2 = (DownloadInfo)this.mSession.getDownloadingList().get(this.mProduct.getPackageName());
         if(var2 != null) {
            var2.mStatus = 490;
            this.mSession.notifyDataChanged();
            DownloadManager var3 = this.mSession.getDownloadManager();
            long[] var4 = new long[]{var2.id};
            var3.cancelDownload(var4);
         }

         this.mCurrentStatus = this.mInitAppStatus;
         this.switchActionBarStatus(this.mCurrentStatus);
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "移除下载队列"});
      }

   }

   private void initActionBar(ProductDetail var1) {
      if(this.mSession.getUpdateList().containsKey(this.mProduct.getPackageName())) {
         this.mInitAppStatus = 2;
      } else if(this.mSession.getInstalledApps().contains(this.mProduct.getPackageName())) {
         this.mInitAppStatus = 1;
      } else {
         this.mInitAppStatus = 0;
      }

      ConcurrentHashMap var2 = this.mSession.getDownloadingList();
      if(var2 != null) {
         DownloadInfo var3 = (DownloadInfo)var2.get(var1.getPackageName());
         if(var3 == null) {
            this.mCurrentStatus = this.mInitAppStatus;
            return;
         }

         this.mDownloadInfo = var3;
         if(DownloadManager.Impl.isStatusWaiting(var3.mControl)) {
            this.mCurrentStatus = 6;
            this.mActionBar.setVisibility(0);
            this.switchActionBarStatus(this.mCurrentStatus);
            return;
         }

         if(DownloadManager.Impl.isStatusPending(var3.mStatus)) {
            this.mActionBar.setVisibility(4);
            this.initDownloadStatusBar();
            this.mDownloadProgress.setIndeterminate(true);
            this.mDownloadStatusTitle.setText(2131296559);
            this.mCurrentStatus = 5;
         } else {
            if(DownloadManager.Impl.isStatusRunning(var3.mStatus)) {
               this.mActionBar.setVisibility(4);
               this.initDownloadStatusBar();
               this.mCurrentStatus = 5;
               return;
            }

            if(DownloadManager.Impl.isStatusSuccess(var3.mStatus) && Utils.isFileExist(var3.mFilePath)) {
               this.mProduct.setFilePath(var3.mFilePath);
               if(this.mInitAppStatus == 2) {
                  this.mCurrentStatus = 4;
               } else {
                  this.mCurrentStatus = 3;
               }

               return;
            }
         }
      }

      this.mCurrentStatus = this.mInitAppStatus;
   }

   private void initAppInfo(ProductDetail var1) {
      ((RatingBar)this.findViewById(2131492964)).setRating(var1.getRating());
      TextView var2 = (TextView)this.findViewById(2131493011);
      Object[] var3 = new Object[]{Integer.valueOf(var1.getRatingCount())};
      var2.setText(this.getString(2131296434, var3));
      TextView var4 = (TextView)this.findViewById(2131493012);
      Object[] var5 = new Object[]{StringUtils.getDownloadInterval(var1.getDownloadCount())};
      var4.setText(this.getString(2131296431, var5));
      TextView var6 = (TextView)this.findViewById(2131493013);
      Object[] var7 = new Object[]{var1.getAuthorName()};
      var6.setText(this.getString(2131296432, var7));
      TextView var8 = (TextView)this.findViewById(2131493014);
      Object[] var9 = new Object[]{StringUtils.formatSize((long)var1.getAppSize())};
      var8.setText(this.getString(2131296429, var9));
      TextView var10 = (TextView)this.findViewById(2131493015);
      Object[] var11 = new Object[]{var1.getVersionName()};
      var10.setText(this.getString(2131296430, var11));
   }

   private void initDescriptionView(ProductDetail var1) {
      this.mScrollView = (ScrollView)this.findViewById(2131492919);
      ViewTreeObserver var2 = ((FrameLayout)this.findViewById(2131492976)).getViewTreeObserver();
      this.mShortDescription = (TextView)this.findViewById(2131493007);
      this.mShortDescription.setText(var1.getLongDescription());
      this.mLongDescription = (TextView)this.findViewById(2131493008);
      this.mLongDescription.setText(var1.getLongDescription());
      this.mMoreButton = (Button)this.findViewById(2131493009);
      this.mMoreButton.setOnClickListener(this);
      var2.addOnPreDrawListener(new OnPreDrawListener() {
         public boolean onPreDraw() {
            boolean var2;
            if(ProductInfoActivity.this.isInit) {
               var2 = true;
            } else {
               if(ProductInfoActivity.this.mesureDescription(ProductInfoActivity.this.mShortDescription, ProductInfoActivity.this.mLongDescription)) {
                  ProductInfoActivity.this.mMoreButton.setVisibility(0);
               }

               ProductInfoActivity.this.isInit = true;
               var2 = true;
            }

            return var2;
         }
      });
   }

   private void initDownloadStatusBar() {
      if(this.mDownloadStatusBar != null) {
         if(!this.mDownloadStatusBar.isShown()) {
            this.mDownloadStatusBar.setVisibility(0);
         }
      } else {
         this.mActionBar.setVisibility(4);
         this.mDownloadStatusBar = (RelativeLayout)this.findViewById(2131492920);
         this.mDownloadStatusBar.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
         this.mDownloadStatusBar.setVisibility(0);
         this.mBtnCancelDownload = (TextView)this.mDownloadStatusBar.findViewById(2131492962);
         this.mBtnCancelDownload.setBackgroundResource(ThemeManager.getResource(this.mSession, 37));
         this.mBtnCancelDownload.setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 18));
         this.mBtnCancelDownload.setOnClickListener(this);
         this.mDownloadStatusTitle = (TextView)this.mDownloadStatusBar.findViewById(2131492961);
         this.mDownloadStatusTitle.setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 36));
         this.mDownloadRemainSize = (TextView)this.mDownloadStatusBar.findViewById(2131492963);
         this.mDownloadRemainSize.setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 36));
         this.mDownloadProgress = (ProgressBar)this.mDownloadStatusBar.findViewById(2131492869);
         this.mDownloadProgress.setProgressDrawable(this.getResources().getDrawable(ThemeManager.getResource(this.mSession, 40)));
      }

   }

   private void initGallery(ProductDetail var1) {
      ArrayList var2 = new ArrayList();
      String[] var3 = var1.getScreenshotLdpi();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var12 = var3[var5];
         if(!TextUtils.isEmpty(var12)) {
            var2.add(var12);
         }
      }

      if(var2.size() > 0) {
         LayoutInflater var6 = this.getLayoutInflater();
         LinearLayout var7 = (LinearLayout)this.findViewById(2131492955);
         int var8 = var2.size();

         for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = (String)var2.get(var9);
            ImageView var11 = (ImageView)var6.inflate(2130903096, var7, false);
            var11.setTag(Integer.valueOf(var9));
            var11.setOnClickListener(this.mScreenShotClickListener);
            ImageUtils.downloadDeatilScreenshot(this.getApplicationContext(), var10, var11);
            var7.addView(var11);
         }
      }

   }

   private void initMasterView() {
      if(this.mMasters != null && this.mMasters.size() != 0) {
         LinearLayout var1 = (LinearLayout)((ViewStub)this.findViewById(2131492923)).inflate().findViewById(2131492946);
         Iterator var2 = this.mMasters.iterator();

         while(var2.hasNext()) {
            RecommendTopic var3 = Utils.mapToTopic((HashMap)var2.next());
            RelativeLayout var4 = (RelativeLayout)this.getLayoutInflater().inflate(2130903119, var1, false);
            ImageView var5 = (ImageView)var4.findViewById(2131492878);
            ImageUtils.download(this.getApplicationContext(), var3.icon, var5, 2130837569, true);
            ((TextView)var4.findViewById(2131492927)).setText(var3.user);
            var4.setId(200);
            var4.setTag(var3);
            var4.setOnClickListener(this);
            var1.addView(var4);
         }
      }

   }

   private void initMoreAppView() {
      if(this.mMoreApps != null && this.mMoreApps.size() != 0) {
         LinearLayout var1 = (LinearLayout)((ViewStub)this.findViewById(2131492924)).inflate().findViewById(2131492948);
         Iterator var2 = this.mMoreApps.iterator();

         while(var2.hasNext()) {
            HashMap var3 = (HashMap)var2.next();
            RelativeLayout var4 = (RelativeLayout)this.getLayoutInflater().inflate(2130903121, var1, false);
            ImageView var5 = (ImageView)var4.findViewById(2131493016);
            TextView var6 = (TextView)var4.findViewById(2131493017);
            String var7 = (String)var3.get("icon_url");
            ImageUtils.download(this.getApplicationContext(), var7, var5, 2130837640, true);
            var6.setText((String)var3.get("name"));
            var4.setId(100);
            var4.setTag((String)var3.get("packagename"));
            var4.setOnClickListener(this);
            var1.addView(var4);
         }
      }

   }

   private void initMoreView() {
      if(this.isMoreAppLoaded && this.isMasterLoaded) {
         this.initMoreAppView();
         this.initMasterView();
      }

   }

   private void initPermissionView(ProductDetail var1) {
      String var2 = var1.getPermission();
      String[] var3;
      if(!TextUtils.isEmpty(var2)) {
         var3 = var2.split(",");
      } else {
         var3 = null;
      }

      AppSecurityPermissions var4 = new AppSecurityPermissions(this, var3);
      this.mSecurityList = (LinearLayout)this.findViewById(2131492925);
      this.mSecurityList.addView(var4.getPermissionsView());
      this.mSecurityView = (RelativeLayout)this.findViewById(2131492940);
      this.mSecurityView.setOnClickListener(this);
      this.mPermissionIndicator = (ImageView)this.mSecurityView.findViewById(2131492943);
   }

   private void initRatingLayout(ProductDetail var1) {
      if(!this.mSession.getInstalledApps().contains(var1.getPackageName())) {
         View var7 = this.findViewById(2131493020);
         if(var7 != null) {
            var7.setVisibility(8);
         }
      } else {
         View var2 = this.findViewById(2131493021);
         View var3 = this.findViewById(2131493022);
         View var4;
         View var5;
         if(var2 == null) {
            View var6 = ((ViewStub)this.findViewById(2131492922)).inflate();
            var4 = var6.findViewById(2131493021);
            var4.setOnClickListener(this);
            var5 = var6.findViewById(2131493022);
            this.initRatingView(var5);
         } else {
            var4 = var2;
            var5 = var3;
         }

         if(this.mSession.isLogin()) {
            var4.setVisibility(8);
            var5.setVisibility(0);
            MarketAPI.getMyRating(this.getApplicationContext(), this, this.mProduct.getPid());
         } else {
            var4.setVisibility(0);
            var5.setVisibility(8);
         }
      }

   }

   private void initRatingView(View var1) {
      this.mMyRating = (RatingBar)var1.findViewById(2131493024);
      this.mRanking = (TextView)var1.findViewById(2131493025);
      this.mMyRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
         public void onRatingChanged(RatingBar var1, final float var2, boolean var3) {
            switch((int)var2) {
            case 1:
               ProductInfoActivity.this.mRanking.setText(2131296476);
               break;
            case 2:
               ProductInfoActivity.this.mRanking.setText(2131296477);
               break;
            case 3:
               ProductInfoActivity.this.mRanking.setText(2131296478);
               break;
            case 4:
               ProductInfoActivity.this.mRanking.setText(2131296479);
               break;
            case 5:
               ProductInfoActivity.this.mRanking.setText(2131296480);
            }

            long var4 = System.currentTimeMillis();
            if(ProductInfoActivity.this.mLastRatingTime != 0L && var4 - ProductInfoActivity.this.mLastRatingTime > 2000L) {
               ProductInfoActivity.this.mHandler.postDelayed(new Runnable() {
                  public void run() {
                     if(ProductInfoActivity.this.mProduct != null) {
                        MarketAPI.addRating(ProductInfoActivity.this.getApplicationContext(), ProductInfoActivity.this, ProductInfoActivity.this.mProduct.getPid(), (int)var2);
                     }

                  }
               }, 2000L);
               ProductInfoActivity.this.mLastRatingTime = var4;
            }

         }
      });
   }

   private void initViewsByTheme() {
      this.mActionBar = (LinearLayout)this.findViewById(2131492926);
      this.mActionBar.setBackgroundResource(ThemeManager.getResource(this.mSession, 15));
      RelativeLayout var1 = (RelativeLayout)this.mActionBar.findViewById(2131492956);
      var1.setBackgroundResource(ThemeManager.getResource(this.mSession, 16));
      var1.setOnClickListener(this);
      RelativeLayout var2 = (RelativeLayout)this.mActionBar.findViewById(2131492958);
      var2.setBackgroundResource(ThemeManager.getResource(this.mSession, 16));
      var2.setOnClickListener(this);
      ((TextView)this.mActionBar.findViewById(2131492891)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 17));
      ((TextView)this.mActionBar.findViewById(2131492960)).setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 17));
      ((ImageView)this.findViewById(2131492957)).setBackgroundResource(ThemeManager.getResource(this.mSession, 19));
   }

   private boolean mesureDescription(TextView var1, TextView var2) {
      int var3 = var1.getHeight();
      boolean var4;
      if(var2.getHeight() > var3) {
         var1.setVisibility(0);
         var2.setVisibility(8);
         var4 = true;
      } else {
         var1.setVisibility(8);
         var2.setVisibility(0);
         var4 = false;
      }

      return var4;
   }

   private void recycleImage() {
      LinearLayout var1 = (LinearLayout)this.findViewById(2131492955);
      int var2 = var1.getChildCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         ImageView var4 = (ImageView)var1.getChildAt(var3);
         BitmapDrawable var5 = (BitmapDrawable)var4.getDrawable();
         var4.setImageResource(2130837640);
         if(var5 != null && var5.getBitmap() != null) {
            var5.setCallback((Callback)null);
         }
      }

   }

   private void refreshDownloadingStatus(DownloadInfo var1) {
      synchronized(this) {
         this.mDownloadInfo = var1;
         if(var1 == null) {
            return;
         }
      }

      this.initDownloadStatusBar();
      int var3 = var1.mStatus;
      if(DownloadManager.Impl.isStatusError(var3)) {
         this.mCurrentStatus = this.mInitAppStatus;
         this.mDownloadStatusBar.setVisibility(4);
         this.mDownloadProgress.setProgress(0);
         this.mActionBar.setVisibility(0);
      } else if(DownloadManager.Impl.isStatusWaiting(var1.mControl)) {
         this.mCurrentStatus = 6;
         this.mActionBar.setVisibility(0);
         this.mDownloadStatusBar.setVisibility(4);
      } else if(DownloadManager.Impl.isStatusPending(var3)) {
         this.mCurrentStatus = 5;
         this.mDownloadStatusTitle.setText(2131296559);
         this.mDownloadProgress.setIndeterminate(true);
      } else if(DownloadManager.Impl.isStatusRunning(var3)) {
         this.mDownloadStatusTitle.setText(2131296363);
         this.mDownloadProgress.setIndeterminate(false);
         this.mDownloadProgress.setProgress(var1.mProgressNumber);
         this.mDownloadRemainSize.setText(Utils.calculateRemainBytes(this.getApplicationContext(), (float)var1.mCurrentSize, (float)var1.mTotalSize));
         this.mCurrentStatus = 5;
      } else if(DownloadManager.Impl.isStatusSuccess(var3)) {
         if(this.mInitAppStatus == 2) {
            this.mCurrentStatus = 4;
         } else {
            this.mCurrentStatus = 3;
         }

         this.mProduct.setFilePath(var1.mFilePath);
         this.mDownloadStatusBar.setVisibility(4);
         this.mDownloadProgress.setProgress(0);
         this.mActionBar.setVisibility(0);
      } else if(DownloadManager.Impl.isStatusInformational(var3)) {
         this.mCurrentStatus = this.mInitAppStatus;
         this.mDownloadStatusBar.setVisibility(0);
         this.mActionBar.setVisibility(4);
      } else {
         this.mCurrentStatus = this.mInitAppStatus;
         this.mDownloadStatusBar.setVisibility(4);
         this.mDownloadProgress.setProgress(0);
         this.mActionBar.setVisibility(0);
      }

      this.switchActionBarStatus(this.mCurrentStatus);
   }

   private void refreshRatingView(int var1) {
      if(var1 > 0) {
         this.mMyRating.setRating((float)var1);
      }

      this.mLastRatingTime = 1L;
   }

   private void reloadImage() {
      ArrayList var1 = new ArrayList();
      String[] var2 = this.mProduct.getScreenshotLdpi();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var10 = var2[var4];
         if(!TextUtils.isEmpty(var10)) {
            var1.add(var10);
         }
      }

      LinearLayout var5 = (LinearLayout)this.findViewById(2131492955);
      int var6 = var5.getChildCount();

      for(int var7 = 0; var7 < var6; ++var7) {
         ImageView var8 = (ImageView)var5.getChildAt(var7);
         String var9 = (String)var1.get(var7);
         ImageUtils.downloadDeatilScreenshot(this.getApplicationContext(), var9, var8);
      }

   }

   private void resetActionBar() {
      this.mCurrentStatus = this.mInitAppStatus;
      this.mDownloadStatusBar.setVisibility(4);
      this.mDownloadProgress.setProgress(0);
      this.mActionBar.setVisibility(0);
      this.switchActionBarStatus(this.mCurrentStatus);
   }

   private void switchActionBarStatus(int var1) {
      switch(var1) {
      case 0:
         ((ImageView)this.findViewById(2131492890)).setImageResource(ThemeManager.getResource(this.mSession, 21));
         TextView var2 = (TextView)this.findViewById(2131492891);
         int var3 = this.mProduct.getPayCategory();
         if(1 == var3) {
            var2.setText(2131296440);
         } else if(2 == var3) {
            Object[] var4 = new Object[]{Integer.valueOf(this.mProduct.getPrice())};
            var2.setText(this.getString(2131296441, var4));
         }

         ((ImageView)this.findViewById(2131492959)).setImageResource(ThemeManager.getResource(this.mSession, 22));
         ((TextView)this.findViewById(2131492960)).setText(2131296442);
         break;
      case 1:
         ((ImageView)this.findViewById(2131492890)).setImageResource(ThemeManager.getResource(this.mSession, 24));
         ((TextView)this.findViewById(2131492891)).setText(2131296444);
         ((ImageView)this.findViewById(2131492959)).setImageResource(ThemeManager.getResource(this.mSession, 25));
         ((TextView)this.findViewById(2131492960)).setText(2131296445);
         break;
      case 2:
         ((ImageView)this.findViewById(2131492890)).setImageResource(ThemeManager.getResource(this.mSession, 21));
         ((TextView)this.findViewById(2131492891)).setText(2131296443);
         ((ImageView)this.findViewById(2131492959)).setImageResource(ThemeManager.getResource(this.mSession, 25));
         ((TextView)this.findViewById(2131492960)).setText(2131296445);
         break;
      case 3:
         ((ImageView)this.findViewById(2131492890)).setImageResource(ThemeManager.getResource(this.mSession, 23));
         ((TextView)this.findViewById(2131492891)).setText(2131296446);
         ((ImageView)this.findViewById(2131492959)).setImageResource(ThemeManager.getResource(this.mSession, 25));
         ((TextView)this.findViewById(2131492960)).setText(2131296447);
         break;
      case 4:
         ((ImageView)this.findViewById(2131492890)).setImageResource(ThemeManager.getResource(this.mSession, 23));
         ((TextView)this.findViewById(2131492891)).setText(2131296446);
         ((ImageView)this.findViewById(2131492959)).setImageResource(ThemeManager.getResource(this.mSession, 25));
         ((TextView)this.findViewById(2131492960)).setText(2131296445);
      case 5:
      default:
         break;
      case 6:
         ((ImageView)this.findViewById(2131492890)).setImageResource(ThemeManager.getResource(this.mSession, 44));
         ((TextView)this.findViewById(2131492891)).setText(2131296449);
         ((ImageView)this.findViewById(2131492959)).setImageResource(ThemeManager.getResource(this.mSession, 41));
         ((TextView)this.findViewById(2131492960)).setText(2131296450);
      }

   }

   private void toogleMoreButton() {
      if(this.mIsShortDescription) {
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "点击更多介绍"});
         this.mShortDescription.setVisibility(8);
         this.mLongDescription.setVisibility(0);
         this.mMoreButton.setText(this.getString(2131296425));
      } else {
         this.mShortDescription.setVisibility(0);
         this.mLongDescription.setVisibility(8);
         this.mMoreButton.setText(this.getString(2131296424));
      }

      boolean var1;
      if(!this.mIsShortDescription) {
         var1 = true;
      } else {
         var1 = false;
      }

      this.mIsShortDescription = var1;
   }

   private void tooglePermissionView() {
      if(this.isPermissionShow) {
         this.mSecurityList.setVisibility(8);
         this.mPermissionIndicator.setImageResource(2130837719);
         this.mPermissionIndicator.setBackgroundDrawable((Drawable)null);
      } else {
         this.mPermissionIndicator.setImageResource(2130837718);
         this.mPermissionIndicator.setBackgroundResource(2130837830);
         this.mSecurityList.setVisibility(0);
         this.mScrollView.post(new Runnable() {
            public void run() {
               ProductInfoActivity.this.mScrollView.pageScroll(130);
            }
         });
      }

      boolean var2;
      if(!this.isPermissionShow) {
         var2 = true;
      } else {
         var2 = false;
      }

      this.isPermissionShow = var2;
   }

   protected void onActivityResult(int var1, int var2, Intent var3) {
      super.onActivityResult(var1, var2, var3);
      this.initRatingLayout(this.mProduct);
   }

   public void onClick(View var1) {
      switch(var1.getId()) {
      case 100:
         Utils.gotoProductDeatil(this, (String)var1.getTag());
         Utils.trackEvent(this.getApplicationContext(), new String[]{"详情", "点击发现应用"});
         break;
      case 200:
         RecommendTopic var8 = (RecommendTopic)var1.getTag();
         Utils.gotoMaster(this, var8);
         Context var9 = this.getApplicationContext();
         String[] var10 = new String[]{"详情", null};
         Object[] var11 = new Object[]{var8.title};
         var10[1] = String.format("点击发现达人－>[%s]", var11);
         Utils.trackEvent(var9, var10);
         break;
      case 2131492940:
         this.tooglePermissionView();
         break;
      case 2131492956:
         this.handleLeftAction(this.mCurrentStatus);
         break;
      case 2131492958:
         this.handleRightAction(this.mCurrentStatus);
         break;
      case 2131492962:
         if(this.mCurrentStatus == 6) {
            DownloadInfo var4 = (DownloadInfo)this.mSession.getDownloadingList().get(this.mProduct.getPackageName());
            if(var4 != null) {
               DownloadManager var5 = this.mSession.getDownloadManager();
               long[] var6 = new long[]{var4.id};
               var5.resumeDownload(var6);
            }
         } else {
            DownloadManager var2 = this.mSession.getDownloadManager();
            long[] var3 = new long[]{this.mDownloadInfo.id};
            var2.cancelDownload(var3);
            this.resetActionBar();
         }
         break;
      case 2131493009:
         this.toogleMoreButton();
         break;
      case 2131493021:
         Utils.gotoLogin(this);
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.mSession.addObserver(this);
      this.mProduct = (ProductDetail)this.getIntent().getSerializableExtra("extra.product.detail");
      this.setContentView(2130903072);
      this.initViewsByTheme();
      this.initGallery(this.mProduct);
      this.initDescriptionView(this.mProduct);
      this.initRatingLayout(this.mProduct);
      this.initAppInfo(this.mProduct);
      this.initPermissionView(this.mProduct);
      this.initActionBar(this.mProduct);
      this.switchActionBarStatus(this.mCurrentStatus);
      MarketAPI.getRecommendByApp(this.getApplicationContext(), this, this.mProduct.getPid());
      MarketAPI.getMasterContains(this.getApplicationContext(), this, this.mProduct.getPid());
   }

   protected void onDestroy() {
      super.onDestroy();
      this.mSession.deleteObserver(this);
   }

   public void onError(int var1, int var2) {
      switch(var1) {
      case 50:
         this.isMasterLoaded = true;
         break;
      case 51:
         this.isMoreAppLoaded = true;
      }

   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this.getParent());
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this.getParent());
      if(this.isInit) {
         this.initActionBar(this.mProduct);
         this.switchActionBarStatus(this.mCurrentStatus);
         this.initRatingLayout(this.mProduct);
         this.reloadImage();
      }

   }

   protected void onStop() {
      super.onStop();
      this.recycleImage();
   }

   public void onSuccess(int var1, Object var2) {
      switch(var1) {
      case 4:
         this.mRanking.setText(2131296433);
         break;
      case 8:
         this.refreshRatingView(Utils.getInt((String)var2));
         break;
      case 50:
         this.isMasterLoaded = true;
         this.mMasters = (ArrayList)((HashMap)var2).get("master_contains_list");
         this.initMoreView();
         break;
      case 51:
         this.isMoreAppLoaded = true;
         this.mMoreApps = (ArrayList)((HashMap)var2).get("product_list");
         this.initMoreView();
      }

   }

   public void update(Observable var1, Object var2) {
      if(var2 instanceof ConcurrentHashMap) {
         DownloadInfo var3 = (DownloadInfo)((ConcurrentHashMap)var2).get(this.mProduct.getPackageName());
         if(var3 != null) {
            this.refreshDownloadingStatus(var3);
         }
      }

   }
}
