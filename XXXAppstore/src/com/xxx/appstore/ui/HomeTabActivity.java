package com.xxx.appstore.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.HttpHost;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;
import com.xxx.appstore.R;
import com.xxx.appstore.Session;
import com.xxx.appstore.common.HttpClientFactory;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.ResponseCacheManager;
import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.download.DownloadService;
import com.xxx.appstore.common.util.AlarmManageUtils;
import com.xxx.appstore.common.util.CacheManager;
import com.xxx.appstore.common.util.ThemeManager;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.UpdateInfo;
import com.xxx.appstore.common.widget.BaseTabActivity;

// Referenced classes of package com.xxx.appstore.ui:
//            HomeActivity, CategoryActivity, MasterTabActivity, RankTabActivity, 
//            AppManagerActivity, InstallNecessaryActivity, PersonalAccountActivity, ClientPreferenceActivity, 
//            MoreInfoActivity

public class HomeTabActivity extends BaseTabActivity implements
		com.xxx.appstore.common.ApiAsyncTask.ApiRequestListener,
		android.widget.TabHost.OnTabChangeListener, Observer,
		android.view.View.OnClickListener {

	private static final int DIALOG_EXIT = 1;
	private static final int DIALOG_FORCE_UPDATE = 3;
	private static final int DIALOG_OPT_UPDATE = 2;
	private static final int HIDE_NOTIFICATION = 1;
	private static final int NOTIFICATION_DURATION = 5000;
	private static final String TAB_APP = " app";
	private static final String TAB_CATEGORY = "category";
	private static final String TAB_HOME = "home";
	private static final String TAB_RANK = "rank";
	private static final String TAB_RECOMMEND = "recommend";
	private static int sStartPosArrayL[] = new int[5];
	private static int sStartPosArrayP[] = new int[5];
	private static int sWidthL = 0;
	private static int sWidthP = 0;
	private Handler mHandler;
	private BroadcastReceiver mInstallReceiver;
	private boolean mIsLandscape;
	private boolean mIsLandscapeInit;
	private boolean mIsPortraitInit;
	private boolean mIsRestore;
	private boolean mIsRotated;
	private int mLastTab;
	private ImageView mMover;
	private BroadcastReceiver mNetworkReceiver;
	private TextView mNotificationContent;
	private RelativeLayout mNotificationView;
	private int mStartX;
	private TabHost mTabHost;
	private BroadcastReceiver mThemeReceiver;
	private int mUpdateCounter;
	private BroadcastReceiver mUpdateReceiver;

	public HomeTabActivity() {
		mNetworkReceiver = new BroadcastReceiver() {
			public void onReceive(Context paramContext, Intent paramIntent) {
				HttpHost localHttpHost = Utils
						.detectProxy(getApplicationContext());
				if (localHttpHost != null)
					HttpClientFactory.get().getHttpClient()
							.useProxyConnection(localHttpHost);
				else {
					HttpClientFactory.get().getHttpClient()
							.useDefaultConnection();
				}
			}
		};
		mThemeReceiver = new BroadcastReceiver() {
			public void onReceive(Context paramContext, Intent paramIntent) {
				initSkin();
			}
		};
		mInstallReceiver = new BroadcastReceiver() {
			public void onReceive(Context paramContext, Intent paramIntent) {
				String str1 = paramIntent.getAction();
				String str2 = paramIntent.getData().getSchemeSpecificPart();
				if ("android.intent.action.PACKAGE_ADDED".equals(str1)) {
					Session localSession = Session.get(getApplicationContext());
					localSession.addInstalledApp(str2);
					localSession.getDownloadManager()
							.completeInstallation(str2);
				}
			}
		};
		mUpdateReceiver = new BroadcastReceiver() {
			public void onReceive(Context paramContext, Intent paramIntent) {
				String str = paramIntent.getAction();
				if (str.equals("com.xxx.appstore.broadcast.FORCE_EXIT")) {
					exit();
				} else if (!str
						.equals("com.xxx.appstore.broadcast.REMIND_LATTER")) {
					if (str.equals("com.xxx.appstore.broadcast.DOWNLOAD_OPT")) {
						DownloadManager.Request localRequest1 = new DownloadManager.Request(
								Uri.parse(mSession.getUpdateUri()));
						localRequest1.setPackageName(mSession.getPackageName());
						localRequest1.setTitle(mSession.getAppName());
						localRequest1.setShowRunningNotification(true);
						localRequest1.setSourceType(3);
						localRequest1
								.setMimeType("application/vnd.android.package-archive");
						mSession.getDownloadManager().enqueue(paramContext,
								localRequest1,
								new DownloadManager.EnqueueListener() {
									public void onFinish(long paramLong) {
										mSession.setUpdateID(paramLong);
									}
								});
					} else if (str.equals("com.xxx.appstore.broadcast.DOWNLOAD")) {
						DownloadManager.Request localRequest2 = new DownloadManager.Request(
								Uri.parse(mSession.getUpdateUri()));
						localRequest2.setPackageName(mSession.getPackageName());
						localRequest2.setTitle(mSession.getAppName());
						localRequest2.setShowRunningNotification(true);
						localRequest2.setSourceType(3);
						localRequest2
								.setMimeType("application/vnd.android.package-archive");
						mSession.getDownloadManager().enqueue(paramContext,
								localRequest2,
								new DownloadManager.EnqueueListener() {
									public void onFinish(long paramLong) {
										mSession.setUpdateID(paramLong);
									}
								});
						finish();
					}
				}
			}
		};
		mHandler = new Handler() {
			public void handleMessage(Message paramMessage) {
				if (1 == paramMessage.what) {
					mNotificationView.setVisibility(8);
				}
			}
		};
	}

	private void checkFollowedNews(HashMap hashmap) {
		if (((Integer) hashmap.get("total_unread")).intValue() > 0) {
			ImageView imageview = (ImageView) mTabHost.getTabWidget()
					.getChildAt(2).findViewById(R.id.tab_widget_icon);
			AnimationDrawable animationdrawable = (AnimationDrawable) getResources()
					.getDrawable(ThemeManager.getResource(mSession, 20));
			imageview.setImageDrawable(animationdrawable);
			animationdrawable.start();
		}
	}

	private void checkNewSplash() {
		sendBroadcast(new Intent(
				"com.mappn.market.broadcast.splash.CHECK_UPGRADE"));
	}

	private void checkUpdateAppsNotification() {
		if (mSession.isNotificationUpdateApps()
				&& mSession.getUpgradeNumber() > 0) {
			NotificationManager notificationmanager = (NotificationManager) getSystemService("notification");
			Notification notification = new Notification();
			notification.icon = R.drawable.notification_icon;
			Object aobj[] = new Object[1];
			aobj[0] = Integer.valueOf(mSession.getUpgradeNumber());
			notification.tickerText = getString(R.string.notification_update_info, aobj);
			notification.when = System.currentTimeMillis();
			Intent intent = new Intent("com.xxx.appstore.download.intent");
			PendingIntent pendingintent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, intent, 0);
			Context context = getApplicationContext();
			String s = getString(R.string.notification_update_info_title);
			Object aobj1[] = new Object[1];
			aobj1[0] = Integer.valueOf(mSession.getUpgradeNumber());
			notification.setLatestEventInfo(context, s,
					getString(R.string.notification_update_info, aobj1), pendingintent);
			notification.flags = 0x10 | notification.flags;
			notificationmanager.notify(R.drawable.bbs_icon, notification);
		}
	}

	private View createTabView(Context context, String s, int i, View view) {
		View view1;
		if (view == null)
			view1 = LayoutInflater.from(context).inflate(R.layout.activity_home_tab_view, null);
		else
			view1 = view;
		if (i == -1) {
			ImageView imageview = (ImageView) view1
					.findViewById(R.id.tab_widget_icon);

			if (mSession.getUpgradeNumber() > 0)
				drawUpdateCount(this, getResources(), imageview);
			else
				imageview.setImageResource(ThemeManager
						.getResource(mSession, 5));
		} else {
			((ImageView) view1.findViewById(R.id.tab_widget_icon))
					.setImageResource(ThemeManager.getResource(mSession, i));
		}
		TextView textview;
		textview = (TextView) view1.findViewById(R.id.tab_widget_content);
		textview.setText(s);
		textview.setTextAppearance(getApplicationContext(),
				ThemeManager.getResource(mSession, 11));
		return view1;
	}

	private Bitmap drawBitmap(DisplayMetrics displaymetrics, Bitmap bitmap,
			Bitmap bitmap1) {
		Canvas canvas = new Canvas();
		int i = bitmap.getScaledHeight(displaymetrics);
		int j = bitmap.getScaledWidth(displaymetrics);
		Bitmap bitmap2 = Bitmap.createBitmap(j, i,
				android.graphics.Bitmap.Config.ARGB_8888);
		canvas.setBitmap(bitmap2);
		Paint paint = new Paint(1);
		canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);
		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_OVER));
		canvas.drawBitmap(bitmap1, j - bitmap1.getScaledWidth(displaymetrics),
				0.0F, paint);
		canvas.save();
		return bitmap2;
	}

	private Bitmap drawText(int i, DisplayMetrics displaymetrics,
			Resources resources, Bitmap bitmap, int j) {
		int k = bitmap.getScaledHeight(displaymetrics);
		int l = bitmap.getScaledWidth(displaymetrics);
		Bitmap bitmap1 = Bitmap.createBitmap(l, k,
				android.graphics.Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap1);
		canvas.drawBitmap(bitmap, new Matrix(), new Paint());
		Paint paint = new Paint(1);
		float f;
		if (i == R.style.gfan_theme_dark)
			paint.setColor(resources.getColor(R.color.orange));
		else
			paint.setColor(resources.getColor(R.color.color_e));
		paint.setTextSize(12F * displaymetrics.scaledDensity);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		f = paint.measureText(String.valueOf(j)) / 2.0F;
		canvas.drawText(String.valueOf(j), (float) (l / 2) - f, (float) (k / 2)
				+ 6F * displaymetrics.scaledDensity, paint);
		canvas.save();
		return bitmap1;
	}

	private void drawUpdateCount(Activity activity, Resources resources,
			ImageView imageview) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		Bitmap bitmap = BitmapFactory.decodeResource(resources, 0x7f0200b7);
		int i = mSession.getTheme();
		Bitmap bitmap3;
		Bitmap bitmap4;
		StateListDrawable statelistdrawable;
		Bitmap bitmap5;
		Bitmap bitmap6;
		Bitmap bitmap7;
		int ai[];
		int ai1[];
		if (i == R.style.gfan_theme_dark) {
			Bitmap bitmap8 = BitmapFactory
					.decodeResource(resources, R.drawable.main_tab_app_selected);
			Bitmap bitmap9 = BitmapFactory
					.decodeResource(resources, R.drawable.main_tab_app_normal);
			bitmap3 = bitmap8;
			bitmap4 = bitmap9;
		} else {
			Bitmap bitmap1 = BitmapFactory
					.decodeResource(resources, R.drawable.main_tab_app_selected_light);
			Bitmap bitmap2 = BitmapFactory
					.decodeResource(resources, R.drawable.main_tab_app_normal_light);
			bitmap3 = bitmap1;
			bitmap4 = bitmap2;
		}
		statelistdrawable = new StateListDrawable();
		bitmap5 = drawText(i, displaymetrics, resources, bitmap,
				mSession.getUpgradeNumber());
		bitmap6 = drawBitmap(displaymetrics, bitmap4, bitmap5);
		bitmap7 = drawBitmap(displaymetrics, bitmap3, bitmap5);
		ai = new int[1];
		ai[0] = -0x10100a1;
		statelistdrawable.addState(ai, new BitmapDrawable(resources, bitmap6));
		ai1 = new int[1];
		ai1[0] = 0x10100a1;
		statelistdrawable.addState(ai1, new BitmapDrawable(resources, bitmap7));
		imageview.setImageDrawable(statelistdrawable);
	}

	private void exit() {
		ResponseCacheManager.getInstance().clear();
		CacheManager.getInstance().clearFromMemory();
		CacheManager.getInstance().clearFromFile(getApplicationContext());
		Intent intent = new Intent(getApplicationContext(),
				DownloadService.class);
		intent.putExtra("clear", 1);
		startService(intent);
		HttpClientFactory.get().close();
		if (mSession != null) {
			mSession.deleteObservers();
			if (mSession.isAutoClearCache())
				Utils.clearCache(getApplicationContext());
			mSession.close();
			mSession = null;
		}
		finish();
	}

	private void handleInstallApp(ArrayList arraylist) {
		int i = 0;
		ArrayList arraylist1 = mSession.getInstalledApps();
		Iterator iterator = arraylist.iterator();
		while (iterator.hasNext() && (i < 5)) {
			if (arraylist1.contains((String) ((HashMap) iterator.next())
					.get("packagename"))) {
				i++;
			} else {
				i = 0;
			}
		}
		showNotification(i);
		// ArrayList arraylist1;
		// Iterator iterator;
		// int i;
		// arraylist1 = mSession.getInstalledApps();
		// iterator = arraylist.iterator();
		// i = 0;
		// _L5:
		// if(!iterator.hasNext()) goto _L2; else goto _L1
		// _L1:
		// if(!arraylist1.contains((String)((HashMap)iterator.next()).get("packagename")))
		// goto _L4; else goto _L3
		// _L3:
		// int j;
		// j = i + 1;
		// if(j < 5)
		// continue; /* Loop/switch isn't completed */
		// _L6:
		// return;
		// _L4:
		// j = i;
		// i = j;
		// goto _L5
		// _L2:
		// showNotification(i);
		// goto _L6
	}

	private void handleUpdate(UpdateInfo updateinfo) {
		if (mSession == null && updateinfo == null)
			return;

		int i = updateinfo.getUpdageLevel();
		mSession.setUpdateInfo(updateinfo.getVersionName(),
				updateinfo.getVersionCode(), updateinfo.getDescription(),
				updateinfo.getApkUrl(), i);
		if (2 == i)
			showDialog(3);
		else if (1 == i)
			showDialog(2);
	}

	private void initSkin() {
		findViewById(R.id.tab_frame_layout).setBackgroundResource(
				ThemeManager.getResource(mSession, 31));
		mMover.setImageResource(ThemeManager.getResource(mSession, 43));

		View view;
		for (int i = 0; i < 5; i++) {
			view = mTabHost.getTabWidget().getChildTabViewAt(i);
			switch (i) {
			case 0:
				createTabView(getApplicationContext(),
						getString(R.string.main_tab_index), 1, view);
				break;
			case 1:
				createTabView(getApplicationContext(), getString(R.string.main_tab_sort),
						2, view);
				break;
			case 2:
				createTabView(getApplicationContext(), getString(R.string.main_tab_promotion),
						3, view);
				break;
			case 3:
				createTabView(getApplicationContext(), getString(R.string.main_tab_rank),
						4, view);
				break;
			case 4:
				createTabView(getApplicationContext(), getString(R.string.main_tab_app),
						-1, view);
				break;
			default:
				createTabView(getApplicationContext(), getString(R.string.main_tab_app),
						-1, view);
				break;
			}
		}
	}

	private void initTabAnimationParameter(boolean flag) {
		int i = mTabHost.getCurrentTabView().getWidth();
		if (flag) {
			for (int k = 0; k < 5; k++)
				sStartPosArrayL[k] = 0 + i * k;

			sWidthL = i;
			mIsLandscapeInit = true;
		} else {
			for (int j = 0; j < 5; j++)
				sStartPosArrayP[j] = 0 + i * j;

			sWidthP = i;
			mIsPortraitInit = true;
		}
	}

	private void initTabMover(boolean flag) {
		mMover = (ImageView) findViewById(0x7f0c0026);
		mMover.setImageResource(ThemeManager.getResource(mSession, 43));
		int i = mTabHost.getCurrentTabView().getHeight();
		if (flag)
			mMover.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
					sWidthL, i));
		else
			mMover.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
					sWidthP, i));
	}

	private void initView(HashMap hashmap) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		findViewById(R.id.tab_frame_layout).setBackgroundResource(
				ThemeManager.getResource(mSession, 31));
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("extra.home.data", hashmap);
		android.widget.TabHost.TabSpec tabspec = mTabHost
				.newTabSpec(TAB_HOME)
				.setIndicator(
						createTabView(getApplicationContext(),
								getString(R.string.main_tab_index), 1, null))
				.setContent(intent);
		mTabHost.addTab(tabspec);
		android.widget.TabHost.TabSpec tabspec1 = mTabHost
				.newTabSpec(TAB_CATEGORY)
				.setIndicator(
						createTabView(getApplicationContext(),
								getString(R.string.main_tab_sort), 2, null))
				.setContent(new Intent(this, CategoryActivity.class));
		mTabHost.addTab(tabspec1);
		android.widget.TabHost.TabSpec tabspec2 = mTabHost
				.newTabSpec(TAB_RECOMMEND)
				.setIndicator(
						createTabView(getApplicationContext(),
								getString(R.string.main_tab_promotion), 3, null))
				.setContent(new Intent(this, MasterTabActivity.class));
		mTabHost.addTab(tabspec2);
		android.widget.TabHost.TabSpec tabspec3 = mTabHost
				.newTabSpec(TAB_RANK)
				.setIndicator(
						createTabView(getApplicationContext(),
								getString(R.string.main_tab_rank), 4, null))
				.setContent(new Intent(this, RankTabActivity.class));
		mTabHost.addTab(tabspec3);
		android.widget.TabHost.TabSpec tabspec4 = mTabHost
				.newTabSpec(TAB_APP)
				.setIndicator(
						createTabView(getApplicationContext(),
								getString(R.string.main_tab_app), -1, null))
				.setContent(new Intent(this, AppManagerActivity.class));
		mTabHost.addTab(tabspec4);
		mTabHost.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					public boolean onPreDraw() {
						// if ((!HomeTabActivity.access$1200(this.this$0)) &&
						// (!HomeTabActivity.access$1300(this.this$0)))
						// {
						// HomeTabActivity.access$1400(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$1500(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$1600(this.this$0,
						// this.this$0.getTabHost().getCurrentTab());
						// }
						//
						// else if ((HomeTabActivity.access$1200(this.this$0))
						// && (!HomeTabActivity.access$1700(this.this$0)))
						// {
						// HomeTabActivity.access$1400(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$1500(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$1600(this.this$0,
						// this.this$0.getTabHost().getCurrentTab());
						// }
						// else if (HomeTabActivity.access$1800(this.this$0))
						// {
						// if ((!HomeTabActivity.access$1300(this.this$0)) ||
						// (!HomeTabActivity.access$1700(this.this$0)))
						// HomeTabActivity.access$1400(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$1500(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$1600(this.this$0,
						// this.this$0.getTabHost().getCurrentTab());
						// HomeTabActivity.access$1802(this.this$0, false);
						// }
						// else if (HomeTabActivity.access$1900(this.this$0))
						// {
						// HomeTabActivity.access$1902(this.this$0, false);
						// HomeTabActivity.access$1400(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$1500(this.this$0,
						// HomeTabActivity.access$1200(this.this$0));
						// HomeTabActivity.access$2100(this.this$0).setCurrentTab(HomeTabActivity.access$2000(this.this$0));
						// }
						return true;
					}
				});
		mTabHost.setOnTabChangedListener(this);
	}

	private void onTabMoved(int i) {
		if (mMover == null)
			initTabMover(mIsLandscape);
		int j;
		TranslateAnimation translateanimation;
		if (mIsLandscape)
			j = sStartPosArrayL[i];
		else
			j = sStartPosArrayP[i];
		translateanimation = new TranslateAnimation(mStartX, j, 0.0F, 0.0F);
		translateanimation.setDuration(400L);
		translateanimation.setFillAfter(true);
		mMover.startAnimation(translateanimation);
		mStartX = j;
	}

	private void registerReceivers() {
		IntentFilter intentfilter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mNetworkReceiver, intentfilter);
		IntentFilter intentfilter1 = new IntentFilter();
		intentfilter1.addAction("android.intent.action.PACKAGE_ADDED");
		intentfilter1.addAction("android.intent.action.PACKAGE_REMOVED");
		intentfilter1.addAction("android.intent.action.PACKAGE_CHANGED");
		intentfilter1.addDataScheme("package");
		registerReceiver(mInstallReceiver, intentfilter1);
		IntentFilter intentfilter2 = new IntentFilter();
		intentfilter2.addAction("com.xxx.appstore.broadcast.FORCE_EXIT");
		intentfilter2.addAction("com.xxx.appstore.broadcast.REMIND_LATTER");
		intentfilter2.addAction("com.xxx.appstore.broadcast.DOWNLOAD_OPT");
		intentfilter2.addAction("com.xxx.appstore.broadcast.DOWNLOAD");
		registerReceiver(mUpdateReceiver, intentfilter2);
		IntentFilter intentfilter3 = new IntentFilter("com.xxx.appstore.theme");
		registerReceiver(mThemeReceiver, intentfilter3);
	}

	private void showNotification(int i) {
		mNotificationView = (RelativeLayout) findViewById(0x7f0c008c);
		mNotificationContent = (TextView) findViewById(0x7f0c008d);
		mNotificationView.setOnClickListener(this);
		mNotificationView.setVisibility(0);
		mHandler.sendEmptyMessageDelayed(1, 5000L);
		Object aobj[] = new Object[1];
		aobj[0] = Integer.valueOf(i);
		SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder(
				getString(0x7f090006, aobj));
		spannablestringbuilder.setSpan(new TextAppearanceSpan(
				getApplicationContext(), 0x7f0a0015), 5, 6, 33);
		mNotificationContent.setText(spannablestringbuilder);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mNetworkReceiver);
		unregisterReceiver(mInstallReceiver);
		unregisterReceiver(mUpdateReceiver);
		unregisterReceiver(mThemeReceiver);
	}

	public void onClick(View view) {
		if (2131493004 == view.getId()) {
			startActivity(new Intent(getApplicationContext(),
					InstallNecessaryActivity.class));
		}
	}

	public void onConfigurationChanged(Configuration configuration) {
		super.onConfigurationChanged(configuration);
		if (2 == configuration.orientation
				|| 1 == configuration.hardKeyboardHidden)
			mIsLandscape = true;
		else
			mIsLandscape = false;
		mIsRotated = true;
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		UMFeedbackService.enableNewReplyNotification(getApplicationContext(),
				NotificationType.NotificationBar);
		setTheme(mSession.getTheme());
		mSession.addObserver(this);
		registerReceivers();
		setContentView(R.layout.activity_home_tab_main);
		initView((HashMap) getIntent().getSerializableExtra("extra.home.data"));
		if (Utils.isNeedCheckUpgrade(getApplicationContext())) {
			mSession.setUpdataCheckTime(System.currentTimeMillis());
			MarketAPI.submitAllInstalledApps(getApplicationContext());
		}
		Intent intent = getIntent();
		if (intent != null)
			if (intent.getBooleanExtra("click.downloading", false))
				mTabHost.setCurrentTabByTag(TAB_APP);
			else
				MarketAPI.checkUpgrade(getApplicationContext(), this);
		MarketAPI.checkUpdate(getApplicationContext(), this);
		MarketAPI.getFollowedRecommend(getApplicationContext(), this);
		checkNewSplash();
		if (mSession.isFirstLogin()) {
			mSession.setFirstLogin(false);
			MarketAPI.getRequired(getApplicationContext(), this);
			AlarmManageUtils.notifyPushService(getApplicationContext(), false);
		}
	}

	class HomeTabActivity6 implements DialogInterface.OnClickListener {
		HomeTabActivity6(int paramInt) {
		}

		public void onClick(DialogInterface paramDialogInterface, int paramInt) {
			try {
				dismissDialog(paramInt);
			} catch (IllegalArgumentException localIllegalArgumentException) {
			}
		}
	}

	class HomeTabActivity9 implements DialogInterface.OnClickListener {
		HomeTabActivity9(int paramInt) {
		}

		public void onClick(DialogInterface paramDialogInterface, int paramInt) {
			sendBroadcast(new Intent("com.xxx.appstore.broadcast.DOWNLOAD_OPT"));
			removeDialog(paramInt);
		}
	}

	class HomeTabActivity10 implements DialogInterface.OnClickListener {
		HomeTabActivity10(int paramInt) {
		}

		public void onClick(DialogInterface paramDialogInterface, int paramInt) {
			sendBroadcast(new Intent("com.xxx.appstore.broadcast.FORCE_EXIT"));
			removeDialog(paramInt);
		}
	}

	class HomeTabActivity11 implements DialogInterface.OnClickListener {
		HomeTabActivity11(int paramInt) {
		}

		public void onClick(DialogInterface paramDialogInterface, int paramInt) {
			sendBroadcast(new Intent("com.xxx.appstore.broadcast.FORCE_EXIT"));
			removeDialog(paramInt);
		}
	}

	protected Dialog onCreateDialog(int i) {
		Dialog obj = super.onCreateDialog(i);
		switch (i) {
		default:
			break;
		case 1:
			obj = (new android.app.AlertDialog.Builder(this))
					.setIcon(0x108009b)
					.setTitle(getString(0x7f090196))
					.setPositiveButton(0x7f09007a,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									if ((mNotificationView != null)
											&& (mSession.hasDownloadTask()))
										moveTaskToBack(false);
									else {
										exit();
									}
								}
							})
					.setNegativeButton(R.string.no, new HomeTabActivity6(i))
					.create();
			break;
		case 2: {
			String s2 = mSession.getUpdateVersionName();
			String s3 = mSession.getUpdateVersionDesc().replace("\r", "");
			android.app.AlertDialog.Builder builder1 = (new android.app.AlertDialog.Builder(
					this)).setIcon(0x108009b).setTitle(R.string.find_new_version);
			StringBuilder stringbuilder1 = new StringBuilder();
			Object aobj1[] = new Object[1];
			aobj1[0] = s2;
			obj = builder1
					.setMessage(
							stringbuilder1.append(getString(R.string.update_prompt, aobj1))
									.append(s3).toString())
					.setPositiveButton(R.string.btn_yes, new HomeTabActivity9(i))
					.setNegativeButton(R.string.btn_next_time, new HomeTabActivity9(i))
					.create();
		}
			break;
		case 3: {
			String s = mSession.getUpdateVersionName();
			String s1 = mSession.getUpdateVersionDesc().replace("\r", "");
			android.app.AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(
					this)).setIcon(0x108009b).setTitle(R.string.find_new_version);
			StringBuilder stringbuilder = new StringBuilder();
			Object aobj[] = new Object[1];
			aobj[0] = s;
			obj = builder
					.setMessage(
							stringbuilder.append(getString(R.string.update_prompt_stronger, aobj))
									.append(s1).toString())
					.setPositiveButton(R.string.btn_yes, new HomeTabActivity11(i))
					.setNegativeButton(R.string.btn_exit, new HomeTabActivity10(i))
					.create();

		}
			break;
		}
		return obj;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(0x7f0b0000, menu);
		return super.onCreateOptionsMenu(menu);
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
		mIsPortraitInit = false;
		mIsLandscapeInit = false;
		getLocalActivityManager().removeAllActivities();
	}

	public void onError(int i, int j) {
		if (i == 17)
			Utils.D((new StringBuilder())
					.append("check new version fail because of status : ")
					.append(j).toString());
	}

	public boolean onKeyDown(int i, KeyEvent keyevent) {
		boolean flag;
		if (4 == i) {
			if (!isFinishing())
				showDialog(1);
			flag = true;
		} else {
			flag = super.onKeyDown(i, keyevent);
		}
		return flag;
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null
				&& intent.getBooleanExtra("click.downloading", false))
			mTabHost.setCurrentTabByTag(TAB_APP);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		super.onOptionsItemSelected(menuitem);
		Intent intent = new Intent();
		switch (menuitem.getItemId()) {
		case 2131493060: {
			Context context5 = getApplicationContext();
			String as5[] = new String[2];
			as5[0] = "菜单";
			as5[1] = "点击用户中心";
			Utils.trackEvent(context5, as5);
			intent.setClass(getApplicationContext(),
					PersonalAccountActivity.class);
			startActivity(intent);
		}
			break;
		case 2131493061: {
			Context context4 = getApplicationContext();
			String as4[] = new String[2];
			as4[0] = "菜单";
			as4[1] = "点击设置";
			Utils.trackEvent(context4, as4);
			intent.setClass(getApplicationContext(),
					ClientPreferenceActivity.class);
			startActivity(intent);
		}
			break;
		case 2131493062: {
			Context context1 = getApplicationContext();
			String as1[] = new String[2];
			as1[0] = "菜单";
			as1[1] = "点击社区";
			Utils.trackEvent(context1, as1);
			intent.setAction("android.intent.action.VIEW");
			intent.setData(Uri.parse("http://bbs.gfan.com/mobile/"));
			intent.setFlags(0x10000000);
			startActivity(intent);
		}
			break;
		case 2131493063: {
			Context context2 = getApplicationContext();
			String as2[] = new String[2];
			as2[0] = "菜单";
			as2[1] = "点击反馈";
			Utils.trackEvent(context2, as2);
			UMFeedbackService.openUmengFeedbackSDK(this);
		}
			break;
		case 2131493064: {
			Context context3 = getApplicationContext();
			String as3[] = new String[2];
			as3[0] = "菜单";
			as3[1] = "点击关于页";
			Utils.trackEvent(context3, as3);
			intent.setClass(getApplicationContext(), MoreInfoActivity.class);
			startActivity(intent);
		}
			break;
		case 2131493065: {
			Context context = getApplicationContext();
			String as[] = new String[2];
			as[0] = "菜单";
			as[1] = "点击退出";
			Utils.trackEvent(context, as);
			if (!isFinishing())
				showDialog(1);
		}
			break;
		}
		return true;
	}

	protected void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		mLastTab = bundle.getInt("currentTab");
		mIsRestore = true;
		mTabHost.setCurrentTab(mLastTab);
	}

	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt("currentTab", mTabHost.getCurrentTab());
	}

	public void onSuccess(int i, Object obj) {
		if (isFinishing())
			return;
		if (i == 17)
			handleUpdate((UpdateInfo) obj);
		else if (i == 38)
			handleInstallApp((ArrayList) obj);
		else if (i == 19)
			checkUpdateAppsNotification();
		else if (i == 53)
			checkFollowedNews((HashMap) obj);
	}

	public void onTabChanged(String s) {
		if (TAB_APP.equals(s)) {
			Context context4 = getApplicationContext();
			String as4[] = new String[2];
			as4[0] = "主菜单";
			as4[1] = "点击管理TAB";
			Utils.trackEvent(context4, as4);
		} else if (TAB_CATEGORY.equals(s)) {
			Context context3 = getApplicationContext();
			String as3[] = new String[2];
			as3[0] = "主菜单";
			as3[1] = "点击分类TAB";
			Utils.trackEvent(context3, as3);
		} else if (TAB_RECOMMEND.equals(s)) {
			Context context2 = getApplicationContext();
			String as2[] = new String[2];
			as2[0] = "主菜单";
			as2[1] = "点击达人TAB";
			Utils.trackEvent(context2, as2);
			((ImageView) mTabHost.getTabWidget().getChildAt(2)
					.findViewById(R.id.tab_widget_icon))
					.setImageResource(ThemeManager.getResource(mSession, 3));
		} else if (TAB_RANK.equals(s)) {
			Context context1 = getApplicationContext();
			String as1[] = new String[2];
			as1[0] = "主菜单";
			as1[1] = "点击排行TAB";
			Utils.trackEvent(context1, as1);
		} else if (TAB_HOME.equals(s)) {
			Context context = getApplicationContext();
			String as[] = new String[2];
			as[0] = "主菜单";
			as[1] = "点击首页TAB";
			Utils.trackEvent(context, as);
		}
		onTabMoved(getTabHost().getCurrentTab());
	}

	public void update(Observable observable, Object obj) {
		if (!(obj instanceof Integer) || ((Integer) obj).intValue() != 0)
			return;

		int i = mSession.getUpgradeNumber();
		if (i <= 0 || i == mUpdateCounter) {
			if (i == 0)
				((ImageView) getTabHost().getTabWidget().getChildTabViewAt(4)
						.findViewById(R.id.tab_widget_icon))
						.setImageResource(ThemeManager.getResource(mSession, 5));
		} else {
			mUpdateCounter = i;
			View view = getTabHost().getTabWidget().getChildTabViewAt(4);
			drawUpdateCount(this, getResources(),
					(ImageView) (ImageView) view
							.findViewById(R.id.tab_widget_icon));
		}
	}

	/*
	 * static boolean access$1802(HomeTabActivity hometabactivity, boolean flag)
	 * { hometabactivity.mIsRotated = flag; return flag; }
	 */

	/*
	 * static boolean access$1902(HomeTabActivity hometabactivity, boolean flag)
	 * { hometabactivity.mIsRestore = flag; return flag; }
	 */

}