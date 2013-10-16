package com.xxx.appstore;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.MarketProvider;
import com.xxx.appstore.common.util.Pair;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.DownloadInfo;
import com.xxx.appstore.common.vo.UpgradeInfo;

public class Session extends Observable {
	private static final String TAG = "Session";
	private static final int CURSOR_CREATED = 0;
	private static final int CURSOR_CHANGED = 1;
	private static final int CURSOR_UPDATE = 2;
	private static final int CURSOR_INSTALL_CHANGED = 3;
	private static Session mInstance;
	private String appName;
	private String buildVersion;
	private String cid;
	private String cpid;
	private int creditCardVersion;
	private String debugType;
	public float density = 1.5F;
	private String deviceId;
	private int imageFormat;
	private String imei;
	private boolean isAutoClearCache;
	public boolean isDebug;
	private boolean isDeviceBinded;
	private boolean isFilterApp;
	private boolean isFirstLogin;
	private boolean isLogin;
	private boolean isUpdateAvailable;
	private int lastVersion;
	private Context mContext;
	private ContentObserver mCursorObserver = new ContentObserver(this.mHandler) {
		public void onChange(boolean paramAnonymousBoolean) {
			Session.this.mHandler.sendEmptyMessage(CURSOR_CHANGED);
		}
	};
	private String mDefaultChargeType;
	private DownloadManager mDownloadManager;
	private Cursor mDownloadingCursor;
	private ConcurrentHashMap<String, DownloadInfo> mDownloadingList;
	public int mGalleryItemHeight = (int) (200.0F * this.density);
	public int mGalleryItemWidth = (int) (116.0F * this.density);
	private Handler mHandler;
	private boolean mHasDownloadingTask;
	private List<PackageInfo> mInstalledAppList;
	private ArrayList<String> mInstalledApps;
	public HashMap<String, String> mNotSameApps = new HashMap<String, String>();
	private SessionManager mSessionManager;
	public int mTabMargin110 = (int) (74.0F * this.density);
	public int mTabMargin72 = (int) (48.0F * this.density);
	public int mTabMargin9 = (int) (6.0F * this.density);
	private ConcurrentHashMap<String, UpgradeInfo> mUpdateApps = new ConcurrentHashMap<String, UpgradeInfo>();
	private String macAddress;
	private String model;
	private int osVersion;
	private String packageName;
	private String password;
	private String screenSize;
	private String sim;
	private long splashId;
	private long splashTime;
	private String uid;
	private long updataCheckTime;
	private long updateId;
	private int updateLevel;
	private String updateUri;
	private int updateVersionCode;
	private String updateVersionDesc;
	private String updateVersionName;
	private int upgradeNumber;
	private String userAgent;
	private String userName;
	private int versionCode;
	private String versionName;

	private Session(Context paramContext) {
		initMessageHandler();
		this.mContext = paramContext;
		this.mHandler.sendEmptyMessage(CURSOR_CREATED);
		this.osVersion = Build.VERSION.SDK_INT;
		this.buildVersion = Build.VERSION.RELEASE;
		try {
			this.model = URLEncoder.encode(Build.MODEL, "UTF-8");
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		this.mDownloadManager = new DownloadManager(
				paramContext.getContentResolver(), getPackageName());
		readSettings();
	}

	private boolean checkInvalidStatus(DownloadInfo downloadinfo, int i) {
		boolean flag;
		if (downloadinfo == null)
			flag = true;
		else if (i == 490) {
			DownloadManager downloadmanager = mDownloadManager;
			long al[] = new long[1];
			al[0] = downloadinfo.id;
			downloadmanager.remove(al);
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	public static Session get(Context paramContext) {
		if (mInstance == null)
			mInstance = new Session(paramContext);
		return mInstance;
	}

	private void getApplicationInfo() {
		PackageManager localPackageManager = this.mContext.getPackageManager();
		try {
			PackageInfo localPackageInfo = localPackageManager.getPackageInfo(
					this.mContext.getPackageName(), 0);
			this.versionName = localPackageInfo.versionName;
			this.versionCode = localPackageInfo.versionCode;
			ApplicationInfo localApplicationInfo = localPackageManager
					.getApplicationInfo(this.mContext.getPackageName(), 128);
			this.cid = localApplicationInfo.metaData.get("gfan_cid").toString();
			this.cpid = localApplicationInfo.metaData.get("gfan_cpid")
					.toString();
			this.debugType = localApplicationInfo.metaData.get("gfan_debug")
					.toString();
			if ("1".equals(this.debugType)) {
				this.isDebug = true;
			} else if ("0".equals(this.debugType)) {
				this.isDebug = false;
			}
			Utils.sDebug = this.isDebug;
			this.appName = String.valueOf(localApplicationInfo
					.loadLabel(localPackageManager));
			Utils.sLogTag = this.appName;
			this.packageName = this.mContext.getPackageName();
			TelephonyManager localTelephonyManager = (TelephonyManager) this.mContext
					.getSystemService("phone");
			this.imei = localTelephonyManager.getDeviceId();
			this.sim = localTelephonyManager.getSimSerialNumber();
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			Log.d(TAG, "met some error when get application info");
		}
	}

	private static void getCompleteStatus(Session session,
			DownloadInfo downloadinfo, Cursor cursor) {
		downloadinfo.mFilePath = cursor.getString(cursor
				.getColumnIndex("_data"));
		if (!TextUtils.isEmpty(downloadinfo.mFilePath)
				&& (new File(downloadinfo.mFilePath)).exists()) {
			downloadinfo.mProgressLevel = 9;
			ConcurrentHashMap<String, UpgradeInfo> concurrenthashmap1 = session.getUpdateList();
			if (concurrenthashmap1.containsKey(downloadinfo.mPackageName))
				((UpgradeInfo) concurrenthashmap1
						.get(downloadinfo.mPackageName)).filePath = downloadinfo.mFilePath;
		} else {
			ConcurrentHashMap<String, UpgradeInfo> concurrenthashmap = session.getUpdateList();
			if (concurrenthashmap.containsKey(downloadinfo.mPackageName))
				((UpgradeInfo) concurrenthashmap.get(downloadinfo.mPackageName)).filePath = "";
			downloadinfo.mStatus = 270;
			downloadinfo.mProgressLevel = 0;
		}
	}

	private static void getRunningStatus(DownloadInfo paramDownloadInfo,
			Cursor paramCursor) {
		long l1 = paramCursor.getInt(paramCursor
				.getColumnIndex("current_bytes"));
		long l2 = paramCursor.getInt(paramCursor.getColumnIndex("total_bytes"));
		paramDownloadInfo.mTotalSize = l2;
		paramDownloadInfo.mCurrentSize = l1;
		int i = (int) (100.0F * ((float) l1 / (float) l2));
		paramDownloadInfo.mProgress = (i + "%");
		paramDownloadInfo.mProgressNumber = i;
		int j = 2 + i / 14;
		if (j > 8)
			j = 8;
		paramDownloadInfo.mProgressLevel = j;
	}

	private void initMessageHandler() {
		int i = 0;
		if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
			i = 1;
			Looper.prepare();
		}
		this.mHandler = new Handler() {
			public void handleMessage(Message paramAnonymousMessage) {
				switch (paramAnonymousMessage.what) {
				default:
					break;
				case CURSOR_CREATED:
					mDownloadingList = new	ConcurrentHashMap<String, DownloadInfo>();
					startQuery();
					break;
				case CURSOR_CHANGED:
					if (mDownloadingCursor != null) {
						refreshDownloadApp(mDownloadingCursor);
					}
					break;
				case CURSOR_UPDATE:
					setChanged();
					notifyObservers(Integer.valueOf(0));
					break;
				case CURSOR_INSTALL_CHANGED:
					setChanged();
					notifyObservers(Integer.valueOf(2));
					break;
				}
			}
		};
		if (i != 0)
			Looper.loop();
	}

	private DownloadInfo newDownloadInfo(long l, String s, String s1, int i,
			String s2) {
		DownloadInfo downloadinfo = new DownloadInfo();
		downloadinfo.id = l;
		downloadinfo.mPackageName = s;
		downloadinfo.mAppName = s1;
		downloadinfo.mSource = i;
		if (i == 1)
			downloadinfo.mIconUrl = mContext.getResources().getDrawable(
					0x7f0200b8);
		else if (i == 2)
			downloadinfo.mIconUrl = mContext.getResources().getDrawable(
					0x7f0200b9);
		else
			downloadinfo.mIconUrl = s2;
		if (TextUtils.isEmpty(s))
			downloadinfo.mKey = String.valueOf(downloadinfo.id);
		else
			downloadinfo.mKey = s;
		synchronized (mDownloadingList) {
			mDownloadingList.put(downloadinfo.mKey, downloadinfo);
		}
		return downloadinfo;
	}

	private void readSettings() {
		this.mSessionManager = SessionManager.get(this.mContext);
		addObserver(this.mSessionManager);
		HashMap<String, Object> localHashMap = this.mSessionManager.readPreference();
		this.uid = ((String) localHashMap.get("pref.uid"));
		this.screenSize = ((String) localHashMap.get("pref.screen.size"));
		this.isLogin = ((Boolean) localHashMap.get("pref.isLogin"))
				.booleanValue();
		this.isAutoClearCache = ((Boolean) localHashMap.get("auto_clear_cache"))
				.booleanValue();
		this.userName = ((String) localHashMap.get("pref.market.username"));
		this.password = ((String) localHashMap.get("pref.market.password"));
		this.upgradeNumber = ((Integer) localHashMap.get("pref.upgrade.num"))
				.intValue();
		this.updataCheckTime = ((Long) localHashMap
				.get("pref.product.update.timestamp")).longValue();
		this.updateId = ((Long) localHashMap.get("pref.update.id")).longValue();
		this.isFilterApp = ((Boolean) localHashMap.get("no_app_filter"))
				.booleanValue();
		this.deviceId = ((String) localHashMap.get("pref.lpns.binded.devid"));
		this.isDeviceBinded = ((Boolean) localHashMap
				.get("pref.lpns.is.binded")).booleanValue();
		this.creditCardVersion = ((Integer) localHashMap
				.get("pref.card.version")).intValue();
		this.lastVersion = ((Integer) localHashMap.get("pref.current.version"))
				.intValue();
		this.splashId = ((Long) localHashMap.get("pref.splash.id")).longValue();
		this.splashTime = ((Long) localHashMap.get("pref.splash.time"))
				.longValue();
		this.mDefaultChargeType = ((String) localHashMap
				.get(SessionManager.P_DEFAULT_CHARGE_TYPE));
		this.isFirstLogin = ((Boolean) localHashMap.get("pref.is.first.login"))
				.booleanValue();
		getApplicationInfo();
	}

	private void refreshUpdateApp(Cursor paramCursor) {
		this.mUpdateApps = new ConcurrentHashMap<String, UpgradeInfo>();
		if ((paramCursor != null) && (paramCursor.getCount() > 0))
			while (paramCursor.moveToNext()) {
				UpgradeInfo localUpgradeInfo = new UpgradeInfo();
				localUpgradeInfo.pid = paramCursor.getString(paramCursor
						.getColumnIndex("p_id"));
				localUpgradeInfo.pkgName = paramCursor.getString(paramCursor
						.getColumnIndex("p_package_name"));
				localUpgradeInfo.versionName = paramCursor
						.getString(paramCursor
								.getColumnIndex("p_new_version_name"));
				localUpgradeInfo.versionCode = paramCursor.getInt(paramCursor
						.getColumnIndex("p_new_version_code"));
				localUpgradeInfo.signature = paramCursor.getString(paramCursor
						.getColumnIndex("p_signature"));
				this.mUpdateApps
						.put(localUpgradeInfo.pkgName, localUpgradeInfo);
			}
		if (paramCursor != null)
			paramCursor.close();
	}

	private void refreshUpdateApp(DownloadInfo downloadinfo, boolean flag) {
		if (mUpdateApps != null && mUpdateApps.containsKey(downloadinfo.mKey)) {
			UpgradeInfo upgradeinfo = (UpgradeInfo) mUpdateApps
					.get(downloadinfo.mKey);
			if (upgradeinfo != null)
				if (downloadinfo.mStatus == 260)
					mUpdateApps.remove(downloadinfo.mKey);
				else if (flag)
					upgradeinfo.status = 0;
				else if (downloadinfo.mProgressLevel == 0)
					upgradeinfo.status = 0;
				else
					upgradeinfo.status = 1;
		}
	}

	private void startQuery() {
		DbStatusRefreshTask localDbStatusRefreshTask = new DbStatusRefreshTask(
				this.mContext.getContentResolver());
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "0";
		localDbStatusRefreshTask.startQuery(1, null,
				MarketProvider.UPDATE_CONTENT_URI, null, "p_update_ingore=?",
				arrayOfString, null);
		localDbStatusRefreshTask
				.startQuery(
						0,
						null,
						DownloadManager.Impl.CONTENT_URI,
						null,
						"((status >= '190' AND status < '600') AND destination = '0' AND mimetype = 'application/vnd.android.package-archive' AND source = '0')",
						null, "_id ASC");
	}

	public void addInstalledApp(String paramString) {
		if (this.mInstalledApps == null)
			this.mInstalledAppList = Utils.getInstalledApps(this.mContext);
		this.mInstalledApps.add(paramString);
		this.mHandler.sendEmptyMessage(CURSOR_INSTALL_CHANGED);
	}

	public void clearData() {
		setDeviceId("");
		setDeviceBinded(false);
		PreferenceManager.getDefaultSharedPreferences(this.mContext).edit()
				.clear().commit();
	}

	public void close() {
		try {
			this.mSessionManager.writePreferenceQuickly();
			if (this.mDownloadingCursor != null) {
				this.mDownloadingCursor
						.unregisterContentObserver(this.mCursorObserver);
				this.mDownloadingCursor.close();
			}
			mInstance = null;
		} finally {

		}
	}

	public String getAppName() {
		return this.appName;
	}

	public String getBuildVersion() {
		return this.buildVersion;
	}

	public String getCid() {
		if (TextUtils.isEmpty(this.cid))
			getApplicationInfo();
		return this.cid;
	}

	public String getCpid() {
		if (TextUtils.isEmpty(this.cpid))
			getApplicationInfo();
		return this.cpid;
	}

	public int getCreditCardVersion() {
		return this.creditCardVersion;
	}

	public String getDebugType() {
		return this.debugType;
	}

	public String getDefaultChargeType() {
		return this.mDefaultChargeType;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public DownloadManager getDownloadManager() {
		if (this.mDownloadManager == null)
			this.mDownloadManager = new DownloadManager(
					this.mContext.getContentResolver(), getPackageName());
		return this.mDownloadManager;
	}

	public ConcurrentHashMap<String, DownloadInfo> getDownloadingList() {
		return this.mDownloadingList;
	}

	public String getIMEI() {
		if (TextUtils.isEmpty(this.imei))
			getApplicationInfo();
		return this.imei;
	}

	public int getImageFormat() {
		return this.imageFormat;
	}

	public ArrayList<String> getInstalledApps() {
		if (this.mInstalledApps == null)
			this.mInstalledAppList = Utils.getInstalledApps(this.mContext);
		return this.mInstalledApps;
	}

	public List<PackageInfo> getInstalledAppsInfo() {
		if (this.mInstalledAppList == null)
			this.mInstalledAppList = Utils.getInstalledApps(this.mContext);
		return this.mInstalledAppList;
	}

	public String getJavaApiUserAgent() {
		String s;
		if (TextUtils.isEmpty(userAgent)) {
			StringBuilder stringbuilder = new StringBuilder();
			stringbuilder.append(getModel()).append("/")
					.append(getBuildVersion()).append("/")
					.append(mContext.getString(0x7f090001)).append("/")
					.append(getVersionName()).append("/").append(getCid())
					.append("/").append(getIMEI()).append("/").append(getSim())
					.append("/").append(getMac());
			s = stringbuilder.toString();
		} else {
			s = userAgent;
		}
		return s;
	}

	public String getLastNotificationTime() {
		return this.mSessionManager.getLastNotificationTime();
	}

	public int getLastVersion() {
		return this.lastVersion;
	}

	public String getMac() {
		if (TextUtils.isEmpty(this.macAddress))
			this.macAddress = ((WifiManager) this.mContext
					.getSystemService("wifi")).getConnectionInfo()
					.getMacAddress();
		return this.macAddress;
	}

	public String getModel() {
		return this.model;
	}

	public int getOsVersion() {
		return this.osVersion;
	}

	public String getPackageName() {
//		if (TextUtils.isEmpty(this.packageName))
//			getApplicationInfo();
//		return this.packageName;
		return new String("com.mappn.gfan");
	}

	public String getPassword() {
		return this.password;
	}

	public String getScreenSize() {
		return new String("480#800");
		//return this.screenSize;
	}

	public String getSim() {
		if (TextUtils.isEmpty(this.sim))
			getApplicationInfo();
		return this.sim;
	}

	public long getSplashId() {
		return this.splashId;
	}

	public long getSplashTime() {
		return this.splashTime;
	}

	public int getTheme() {
		return this.mSessionManager.getTheme();
	}

	public String getUCenterApiUserAgent() {
		return "packageName=com.unistrong.appstore,appName=GFanMobile,channelID=9";
	}

	public String getUid() {
		return this.uid;
	}

	public long getUpdataCheckTime() {
		return this.updataCheckTime;
	}

	public long getUpdateId() {
		return this.updateId;
	}

	public int getUpdateLevel() {
		return this.updateLevel;
	}

	public ConcurrentHashMap<String, UpgradeInfo> getUpdateList() {
		if ((this.mUpdateApps == null) || (this.mUpdateApps.size() == 0))
			this.mUpdateApps = DBUtils.queryUpdateProduct(this.mContext);
		return this.mUpdateApps;
	}

	public String getUpdateUri() {
		return this.updateUri;
	}

	public int getUpdateVersionCode() {
		return this.updateVersionCode;
	}

	public String getUpdateVersionDesc() {
		return this.updateVersionDesc;
	}

	public String getUpdateVersionName() {
		return this.updateVersionName;
	}

	public int getUpgradeNumber() {
		return this.upgradeNumber;
	}

	public String getUserName() {
		return this.userName;
	}

	public int getVersionCode() {
		if (this.versionCode <= 0)
			getApplicationInfo();
		return this.versionCode;
	}

	public String getVersionName() {
		if (TextUtils.isEmpty(this.versionName))
			getApplicationInfo();
		return this.versionName;
	}

	public boolean hasDownloadTask() {
		return this.mHasDownloadingTask;
	}

	public boolean isAutoClearCache() {
		return this.isAutoClearCache;
	}

	public boolean isAutoDelete() {
		return this.mSessionManager.isAutoDelete();
	}

	public boolean isDeviceBinded() {
		return this.isDeviceBinded;
	}

	public int isFilterApps() {
		return this.isFilterApp?1:0;
	}

	public boolean isFirstLogin() {
		return this.isFirstLogin;
	}

	public boolean isLogin() {
		return this.isLogin;
	}

	public boolean isNotificationRecommendApps() {
		return this.mSessionManager.isNotificationRecommendApps();
	}

	public boolean isNotificationUpdateApps() {
		return this.mSessionManager.isNotificationUpdateApps();
	}

	public boolean isStopDownloadImage() {
		return this.mSessionManager.isStopDownloadImage();
	}

	public boolean isUpdateAvailable() {
		return this.isUpdateAvailable;
	}

	public void minusUpgradeNumber() {
		this.upgradeNumber -= 1;
		super.setChanged();
		super.notifyObservers(new Pair<String, Integer>("pref.upgrade.num", Integer
				.valueOf(this.upgradeNumber)));
	}

	public void notifyDataChanged() {
		this.mHandler.sendEmptyMessage(CURSOR_CHANGED);
	}

	void refreshDownloadApp(Cursor cursor) {
		boolean flag = false;
		if (cursor == null)
			return;

		if (cursor.isClosed())
			return;

		HashSet<String> hashset;
		int i = 0;
		if (mDownloadingCursor == null) {
			mDownloadingCursor = cursor;
			cursor.registerContentObserver(mCursorObserver);
		}
		cursor.requery();
		hashset = new HashSet<String>(mDownloadingList.keySet());
		while (cursor.moveToNext()) {
			DownloadInfo downloadinfo;
			int j1 = 0;
			int j = cursor.getInt(cursor.getColumnIndex("_id"));
			String s1 = cursor.getString(cursor.getColumnIndex("package_name"));
			String s2 = cursor.getString(cursor.getColumnIndex("title"));
			int k = cursor.getInt(cursor.getColumnIndex("source"));
			String s3 = cursor.getString(cursor
					.getColumnIndex("notificationextras"));
			int l = cursor.getInt(cursor.getColumnIndex("status"));
			int i1 = cursor.getInt(cursor.getColumnIndex("control"));
			downloadinfo = (DownloadInfo) mDownloadingList.get(s1);
			if (downloadinfo == null) {
				DownloadInfo downloadinfo1 = (DownloadInfo) mDownloadingList
						.get(String.valueOf(j));
				if (downloadinfo1 != null)
					mDownloadingList.remove(downloadinfo1.mKey);
				downloadinfo = newDownloadInfo(j, s1, s2, k, s3);
			} else {
				downloadinfo.id = j;
			}
			downloadinfo.mControl = i1;
			hashset.remove(downloadinfo.mKey);
			if (checkInvalidStatus(downloadinfo, l))
				refreshUpdateApp(downloadinfo, true);
			downloadinfo.mStatus = l;
			if (com.xxx.appstore.common.download.DownloadManager.Impl
					.isStatusRunning(downloadinfo.mStatus)) {
				getRunningStatus(downloadinfo, cursor);
				j1 = i + 1;
			} else if (com.xxx.appstore.common.download.DownloadManager.Impl
					.isStatusPending(downloadinfo.mStatus)) {
				downloadinfo.mProgressLevel = 1;

			} else if (com.xxx.appstore.common.download.DownloadManager.Impl
					.isStatusWaiting(downloadinfo.mControl)) {
				j1 = i;
			} else if (downloadinfo.mStatus == 200) {
				getCompleteStatus(this, downloadinfo, cursor);
				j1 = i;
			} else if (downloadinfo.mStatus == 260) {
				downloadinfo.mProgressLevel = 11;
				j1 = i;
			} else if (com.xxx.appstore.common.download.DownloadManager.Impl
					.isStatusError(downloadinfo.mStatus)) {
				downloadinfo.mProgressLevel = 13;
				j1 = i;
			} else {
				downloadinfo.mProgressLevel = 0;
				j1 = i;
			}
			refreshUpdateApp(downloadinfo, false);
			i = j1;
			flag = true;
		}

		Iterator<String> localIterator = hashset.iterator();
		while (localIterator.hasNext()) {
			String str1 = (String) localIterator.next();
			this.mDownloadingList.remove(str1);
		}

		if (i > 0)
			setDownloadStatus(true);
		else
			setDownloadStatus(false);
		if (flag) {
			setChanged();
			notifyObservers(mDownloadingList);
		}
	}

	public void removeInstalledApp(String paramString) {
		if (this.mInstalledApps == null)
			this.mInstalledAppList = Utils.getInstalledApps(this.mContext);
		this.mInstalledApps.remove(paramString);
		this.mHandler.sendEmptyMessage(CURSOR_INSTALL_CHANGED);
	}

	public void removeUpdateItem(String paramString) {
		this.mUpdateApps.remove(paramString);
		minusUpgradeNumber();
		this.mHandler.sendEmptyMessage(CURSOR_UPDATE);
	}

	public void setCreditCardVersion(int i) {
		if (creditCardVersion != i) {
			creditCardVersion = i;
			super.setChanged();
			super.notifyObservers(new Pair<String, Integer>("pref.card.version", Integer
					.valueOf(i)));
		}
	}

	public void setDefaultChargeType(String paramString) {
		this.mDefaultChargeType = paramString;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>(SessionManager.P_DEFAULT_CHARGE_TYPE,
				paramString));
	}

	public void setDeviceBinded(boolean flag) {
		if (isDeviceBinded != flag) {
			isDeviceBinded = flag;
			super.setChanged();
			super.notifyObservers(new Pair<String, Boolean>("pref.lpns.is.binded", Boolean
					.valueOf(flag)));
		}
	}

	public void setDeviceId(String paramString) {
		this.deviceId = paramString;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>("pref.lpns.binded.devid", paramString));
	}

	public void setDownloadStatus(boolean paramBoolean) {
		this.mHasDownloadingTask = paramBoolean;
	}

	public void setFirstLogin(boolean paramBoolean) {
		super.setChanged();
		super.notifyObservers(new Pair<String, Boolean>("pref.is.first.login", Boolean
				.valueOf(paramBoolean)));
	}

	public void setImageFormat(int paramInt) {
		this.imageFormat = paramInt;
	}

	public void setInstalledApps(ArrayList<String> paramArrayList) {
		this.mInstalledApps = paramArrayList;
	}

	public void setLastVersion(int i) {
		if (i != lastVersion) {
			clearData();
			lastVersion = i;
			super.setChanged();
			super.notifyObservers(new Pair<String, Integer>("pref.current.version", Integer
					.valueOf(i)));
		}
	}

	public void setLogin(boolean flag) {
		if (isLogin != flag) {
			isLogin = flag;
			super.setChanged();
			super.notifyObservers(new Pair<String, Boolean>("pref.isLogin", Boolean
					.valueOf(flag)));
		}
	}

	public void setNotificationTime(String paramString) {
		this.mSessionManager.setNotificationTime(paramString);
	}

	public void setPassword(String paramString) {
		this.password = paramString;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>("pref.market.password", paramString));
	}

	public void setScreenSize(Activity activity)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        String s;
        if(displaymetrics.widthPixels < displaymetrics.heightPixels)
            s = (new StringBuilder()).append(displaymetrics.widthPixels).append("#").append(displaymetrics.heightPixels).toString();
        else
            s = (new StringBuilder()).append(displaymetrics.heightPixels).append("#").append(displaymetrics.widthPixels).toString();
        screenSize = s;
        density = displaymetrics.density;
        mTabMargin110 = (int)(74F * density);
        mTabMargin72 = (int)(48F * density);
        mTabMargin9 = (int)(6F * density);
        mGalleryItemHeight = (int)(200F * density);
        mGalleryItemWidth = (int)(116F * density);
        super.setChanged();
        super.notifyObservers(new Pair<String, String>("pref.screen.size", screenSize));
    }

	public void setSplashId(long paramLong) {
		this.splashId = paramLong;
		super.setChanged();
		super.notifyObservers(new Pair<String, Long>("pref.splash.id", Long
				.valueOf(paramLong)));
	}

	public void setSplashTime(long paramLong) {
		this.splashTime = paramLong;
		super.setChanged();
		super.notifyObservers(new Pair<String, Long>("pref.splash.time", Long
				.valueOf(paramLong)));
	}

	public void setTheme(int paramInt) {
		this.mSessionManager.setTheme(paramInt);
	}

	public void setUid(String paramString) {
		this.uid = paramString;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>("pref.uid", paramString));
	}

	public void setUpdataCheckTime(long l) {
		if (updataCheckTime != l) {
			updataCheckTime = l;
			super.setChanged();
			super.notifyObservers(new Pair<String, Long>("pref.product.update.timestamp",
					Long.valueOf(l)));
		}
	}

	public void setUpdateAvailable(boolean paramBoolean) {
		this.isUpdateAvailable = paramBoolean;
	}

	public void setUpdateID(long l) {
		if (updateId != l) {
			updateId = l;
			super.setChanged();
			super.notifyObservers(new Pair<String, Long>("pref.update.id", Long.valueOf(l)));
		}
	}

	public void setUpdateInfo(String paramString1, int paramInt1,
			String paramString2, String paramString3, int paramInt2) {
		this.isUpdateAvailable = true;
		this.updateVersionName = paramString1;
		this.updateVersionCode = paramInt1;
		this.updateVersionDesc = paramString2;
		this.updateUri = paramString3;
		this.updateLevel = paramInt2;
	}

	public void setUpdateList() {
		this.mUpdateApps = DBUtils.queryUpdateProduct(this.mContext);
		this.mHandler.sendEmptyMessage(CURSOR_UPDATE);
		this.mHandler.sendEmptyMessage(CURSOR_CHANGED);
	}

	public void setUpdateList(
			ConcurrentHashMap<String, UpgradeInfo> paramConcurrentHashMap) {
		this.mUpdateApps = paramConcurrentHashMap;
		this.mHandler.sendEmptyMessage(CURSOR_UPDATE);
	}

	public void setUpgradeNumber(int i) {
		if (upgradeNumber != i) {
			upgradeNumber = i;
			super.setChanged();
			super.notifyObservers(new Pair<String, Integer>("pref.upgrade.num", Integer
					.valueOf(i)));
			mHandler.sendEmptyMessage(CURSOR_UPDATE);
		}
	}

	public void setUserName(String paramString) {
		this.userName = paramString;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>("pref.market.username", paramString));
	}

	private class DbStatusRefreshTask extends AsyncQueryHandler {
		private static final int DOWNLOAD = 0;
		private static final int UPDATE = 1;

		public DbStatusRefreshTask(ContentResolver arg2) {
			super(arg2);
		}

		protected void onQueryComplete(int paramInt, Object paramObject,
				Cursor paramCursor) {
			switch (paramInt) {
			default:
				break;
			case DOWNLOAD:
				Session.this.refreshDownloadApp(paramCursor);
				break;
			case UPDATE:
				Session.this.refreshUpdateApp(paramCursor);
				break;
			}
		}
	}
}