package com.xxx.appstore.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xxx.appstore.R;
import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.UpgradeInfo;
import com.xxx.appstore.common.widget.AppListAdapter;
import com.xxx.appstore.common.widget.BaseActivity;
import com.xxx.appstore.common.widget.LoadingDrawable;
import com.mobclick.android.MobclickAgent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

public class AppsUpdateActivity extends BaseActivity implements Observer,
		AdapterView.OnItemClickListener {
	private static final int CONTEXT_MENU_DELETE_FILE = 0;
	private static final int CONTEXT_MENU_IGNORE_UPDATE = 1;
	private static Comparator<HashMap<String, Object>> sComparator = new Comparator() {
		public int compare(HashMap<String, Object> paramAnonymousHashMap1,
				HashMap<String, Object> paramAnonymousHashMap2) {
			boolean bool1 = ((Boolean) paramAnonymousHashMap1.get("is_upgrade"))
					.booleanValue();
			boolean bool2 = ((Boolean) paramAnonymousHashMap2.get("is_upgrade"))
					.booleanValue();
			int i;
			if ((bool1) && (!bool2))
				i = -1;
			else if ((bool2) && (!bool1)) {
				i = 1;
			} else {
				String str1 = (String) paramAnonymousHashMap1.get("name");
				String str2 = (String) paramAnonymousHashMap2.get("name");
				i = Collator.getInstance(Locale.CHINESE).compare(str1, str2);
			}

			return i;
		}

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	private AppListAdapter mAdapter;
	private BroadcastReceiver mInstallReceiver = new BroadcastReceiver() {
		public void onReceive(Context paramAnonymousContext,
				Intent paramAnonymousIntent) {
			String str1 = paramAnonymousIntent.getAction();
			String str2 = paramAnonymousIntent.getData()
					.getSchemeSpecificPart();
			if ("android.intent.action.PACKAGE_ADDED".equals(str1)) {
				AppsUpdateActivity.this.mAdapter
						.addData(AppsUpdateActivity.getApkInfo(
								AppsUpdateActivity.this.getApplicationContext(),
								str2));
			} else if ("android.intent.action.PACKAGE_REMOVED".equals(str1)) {
				AppsUpdateActivity.this.mAdapter
						.removeDataWithPackageName(str2);
			}
		}
	};
	ListView mList;
	private FrameLayout mLoading;
	private int mLongClickPos;
	private ProgressBar mProgress;

	private static HashMap<String, Object> getApkInfo(Context paramContext,
			String paramString) {
		HashMap localHashMap1 = null;
		PackageManager localPackageManager = paramContext.getPackageManager();
		try {
			PackageInfo localPackageInfo = localPackageManager.getPackageInfo(
					paramString, 0);
			HashMap localHashMap2 = new HashMap();
			localHashMap2.put("packagename", localPackageInfo.packageName);
			localHashMap2.put("icon_url", paramString);
			localHashMap2.put("name", localPackageInfo.applicationInfo
					.loadLabel(localPackageManager));
			Object[] arrayOfObject = new Object[1];
			arrayOfObject[0] = localPackageInfo.versionName;
			localHashMap2.put("version_name", paramContext.getString(
					R.string.current_version, arrayOfObject));
			localHashMap2.put("ldpi_icon_url", localPackageInfo);
			localHashMap2.put("new_version_name", "");
			localHashMap2.put("is_upgrade", Boolean.valueOf(false));
			localHashMap1 = localHashMap2;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			Utils.E("getApkInfo NameNotFoundException for " + paramString,
					localNameNotFoundException);
		}
		return localHashMap1;
	}

	private void ignoreUpgrade(int paramInt) {
		HashMap localHashMap = (HashMap) this.mAdapter.getItem(paramInt);
		String str = (String) localHashMap.get("packagename");
		localHashMap.put("new_version_name", "");
		localHashMap.put("is_upgrade", Boolean.valueOf(false));
		DBUtils.ignoreUpdate(getApplicationContext(), str);
		this.mAdapter.sort();
	}

	private void uninstall(int paramInt) {
		String str = (String) ((HashMap) this.mAdapter.getItem(paramInt))
				.get("packagename");
		Utils.uninstallApk(getApplicationContext(), str);
	}

	public AppListAdapter doInitListAdapter() {
		Context localContext = getApplicationContext();
		String[] arrayOfString = new String[5];
		arrayOfString[0] = "ldpi_icon_url";
		arrayOfString[1] = "name";
		arrayOfString[2] = "version_name";
		arrayOfString[3] = "new_version_name";
		arrayOfString[4] = "product_download";
		int[] arrayOfInt = new int[5];
		arrayOfInt[0] = R.id.iv_logo;
		arrayOfInt[1] = R.id.tv_name;
		arrayOfInt[2] = R.id.tv_current_version;
		arrayOfInt[3] = R.id.tv_update_version;
		arrayOfInt[4] = R.id.tv_operation;
		this.mAdapter = new AppListAdapter(localContext, null,
				R.layout.activity_apps_manager_installed_item, arrayOfString,
				arrayOfInt);
		this.mAdapter.setProductList();
		this.mAdapter.setNeedSort(sComparator);
		this.mAdapter.setActivity(this);
		return this.mAdapter;
	}
	
	private void initTopBar() {
//	      Session var1 = this.mSession;
//	      final boolean[] bshow=new boolean[]{false,true,true};
//	      final String[] str=new String[]{"",this.getString(R.string.main_tab_app),this.getString(R.string.updateAll)};
//	      TopBar.createTopBar(var1, this, bshow,str);
	}

	public boolean doInitView(Bundle paramBundle) {
		this.mLoading = ((FrameLayout) this.findViewById(R.id.loading));
		this.mProgress = ((ProgressBar) this.mLoading
				.findViewById(R.id.progressbar));
		this.mProgress.setIndeterminateDrawable(new LoadingDrawable(
				getApplicationContext()));
		this.mProgress.setVisibility(View.VISIBLE);
		this.mList = ((ListView) findViewById(android.R.id.list));
		this.mList.setEmptyView(this.mLoading);
		this.mList.setOnItemClickListener(this);
		this.mList.setItemsCanFocus(true);
		this.mAdapter = doInitListAdapter();
//		this.mList.addHeaderView(createUpdateAllView(), null, false);
		this.mList.setAdapter(this.mAdapter);
		registerForContextMenu(this.mList);
		return true;
	}

	public boolean onContextItemSelected(MenuItem paramMenuItem) {
		switch (paramMenuItem.getItemId()) {
		default:
			break;
		case 0:
			uninstall(this.mLongClickPos);
			break;
		case 1:
			ignoreUpgrade(this.mLongClickPos);
			break;
		}

		return super.onContextItemSelected(paramMenuItem);
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.setContentView(R.layout.activity_apps_manager_layout);
		initTopBar();
		doInitView(paramBundle);
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
		localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
		localIntentFilter.addDataScheme("package");
		registerReceiver(this.mInstallReceiver, localIntentFilter);
		this.mSession.addObserver(this);
		new LoadAppTask().execute(new Void[0]);
	}

	public void onCreateContextMenu(ContextMenu paramContextMenu,
			View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo) {
		this.mLongClickPos = (((AdapterView.AdapterContextMenuInfo) paramContextMenuInfo).position - this.mList
				.getHeaderViewsCount());
		if (((Boolean) ((HashMap) this.mAdapter.getItem(this.mLongClickPos))
				.get("is_upgrade")).booleanValue()) {
			paramContextMenu.add(0, 1, 0, R.string.ignore_update);
			paramContextMenu.add(0, 0, 0, R.string.operation_uninstall);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(this.mInstallReceiver);
		this.mAdapter = null;
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {
		// HashMap localHashMap = (HashMap)this.mAdapter.getItem(paramInt -
		// this.mList.getHeaderViewsCount());
		// if (localHashMap == null)
		// return;
		// String str = (String)localHashMap.get("packagename");
		// Intent localIntent = new Intent(getApplicationContext(),
		// PreloadActivity.class);
		// localIntent.putExtra("extra.key.package.name", str);
		// startActivity(localIntent);
		// Context localContext = getApplicationContext();
		// String[] arrayOfString = new String[2];
		// arrayOfString[0] = "应用管理";
		// arrayOfString[1] = "点击应用详情";
		// Utils.trackEvent(localContext, arrayOfString);
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		return getParent().onKeyDown(paramInt, paramKeyEvent);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(getParent());
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(getParent());
	}

	public void update(Observable paramObservable, Object paramObject) {
	}

	private class LoadAppTask extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {
		private int mUpdateCounter;

		private LoadAppTask() {
		}

		protected ArrayList<HashMap<String, Object>> doInBackground(
				Void[] paramArrayOfVoid) {
			Context localContext = AppsUpdateActivity.this
					.getApplicationContext();
			ConcurrentHashMap localConcurrentHashMap = AppsUpdateActivity.this.mSession
					.getUpdateList();
			List localList = AppsUpdateActivity.this.mSession
					.getInstalledAppsInfo();
			PackageManager localPackageManager = AppsUpdateActivity.this
					.getPackageManager();
			ArrayList localArrayList = new ArrayList();
			this.mUpdateCounter = 0;
			Iterator localIterator = localList.iterator();
			while (localIterator.hasNext()) {
				PackageInfo localPackageInfo = (PackageInfo) localIterator
						.next();
				HashMap localHashMap = new HashMap();
				localHashMap.put("packagename", localPackageInfo.packageName);
				localHashMap.put("ldpi_icon_url", localPackageInfo);
				localHashMap.put("name", localPackageInfo.applicationInfo
						.loadLabel(localPackageManager));
				Object[] arrayOfObject1 = new Object[1];
				arrayOfObject1[0] = localPackageInfo.versionName;
				localHashMap.put("version_name", localContext.getString(
						R.string.current_version, arrayOfObject1));
				if (localConcurrentHashMap
						.containsKey(localPackageInfo.packageName)) {
					UpgradeInfo localUpgradeInfo = (UpgradeInfo) localConcurrentHashMap
							.get(localPackageInfo.packageName);
					localHashMap.put("p_id", localUpgradeInfo.pid);
					localHashMap.put("icon_url", localPackageInfo.packageName);
					Object[] arrayOfObject2 = new Object[1];
					arrayOfObject2[0] = localUpgradeInfo.versionName;
					localHashMap.put("new_version_name", localContext
							.getString(R.string.new_version, arrayOfObject2));
					localHashMap.put("is_upgrade", Boolean.valueOf(true));
					if (TextUtils.isEmpty(localUpgradeInfo.filePath))
						this.mUpdateCounter = (1 + this.mUpdateCounter);
					localArrayList.add(localHashMap);
				}				
			}
			return localArrayList;
		}

		protected void onPostExecute(
				ArrayList<HashMap<String, Object>> paramArrayList) {
			if (AppsUpdateActivity.this.mAdapter != null) {
				AppsUpdateActivity.this.mAdapter.addData(paramArrayList);
			}
		}
	}
}