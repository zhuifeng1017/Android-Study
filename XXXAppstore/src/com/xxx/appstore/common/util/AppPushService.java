package com.xxx.appstore.common.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.ui.PreloadActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AppPushService extends Service
  implements com.xxx.appstore.common.ApiAsyncTask.ApiRequestListener
{
  private Session mSession;

	private static boolean isMatchRule(Context paramContext,
			Session paramSession, String paramString1, String paramString2) {
		PackageItemChecker localPackageItemChecker = new PackageItemChecker(
				paramSession.getInstalledApps());
		DBUtils.markItemChecked(paramContext, paramString1);
		boolean bool1 = false;
		try {
			bool1 = new BooleanLogicUtil(paramString2)
					.excute(localPackageItemChecker);
		} catch (Exception localException) {
			Utils.E("parse push rule exception", localException);
		}
		return bool1;
	}

  private void showRecommendAppsNotification(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    NotificationManager localNotificationManager = (NotificationManager)getSystemService("notification");
    Notification localNotification = new Notification();
    localNotification.icon = 2130837722;
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = paramString1;
    localNotification.tickerText = getString(2131296263, arrayOfObject1);
    localNotification.when = System.currentTimeMillis();
    Intent localIntent = new Intent(getApplicationContext(), PreloadActivity.class);
    localIntent.putExtra("extra.key.pid", paramString3);
    localIntent.putExtra("extra.app.push", true);
    localIntent.putExtra("extra.app.nid", paramString4);
    localIntent.putExtra("extra.app.rule", paramString5);
    PendingIntent localPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, localIntent, 16);
    Context localContext = getApplicationContext();
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = paramString1;
    localNotification.setLatestEventInfo(localContext, getString(2131296263, arrayOfObject2), paramString2, localPendingIntent);
    localNotification.flags = (0x10 | localNotification.flags);
    localNotificationManager.notify(2130837548, localNotification);
  }

  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }

  public void onCreate()
  {
    this.mSession = Session.get(getApplicationContext());
  }

  public void onError(int paramInt1, int paramInt2)
  {
    stopSelf();
  }

  public void onStart(Intent paramIntent, int paramInt)
  {
    super.onStart(paramIntent, paramInt);
    MarketAPI.getNotificationRecommend(getApplicationContext(), this);
    AlarmManageUtils.notifyPushService(getApplicationContext(), true);
  }

	public void onSuccess(int paramInt, Object paramObject) {
		if (paramInt == 49)
			DBUtils.queryPushItems(getApplicationContext(),
					new DBUtils.DbOperationResultListener() {
						protected void onQueryResult(
								ArrayList<HashMap<String, Object>> paramAnonymousArrayList) {
							Iterator localIterator = paramAnonymousArrayList
									.iterator();
							while (localIterator.hasNext()) {
								String str1;
								String str2;
								String str3;
								String str4;
								String str5;

								HashMap localHashMap = (HashMap) localIterator
										.next();
								str1 = (String) localHashMap.get("id");
								str2 = (String) localHashMap.get("nid");
								str3 = (String) localHashMap.get("title");
								str4 = (String) localHashMap.get("description");
								str5 = (String) localHashMap.get("rule");
								if (!TextUtils.isEmpty(str5)
										&& (!AppPushService.isMatchRule(
												AppPushService.this
														.getApplicationContext(),
												AppPushService.this.mSession,
												str2, str5))) {

									MarketAPI.reportIftttResult(
											AppPushService.this
													.getApplicationContext(),
											str1, str2, str5, 0);
									AppPushService.this
											.showRecommendAppsNotification(
													str3, str4, str1, str2,
													str5);
									break;
								} else

									AppPushService.this
											.showRecommendAppsNotification(
													str3, str4, str1, null,
													null);
							}
						}
					});
		stopSelf();
	}

  static class PackageItemChecker
    implements BooleanLogicUtil.CallBack<String, Boolean>
  {
    private Set<String> packageSet;

    public PackageItemChecker(Collection<String> paramCollection)
    {
      this.packageSet = new HashSet(paramCollection);
    }

    public Boolean run(String paramString)
    {
      return Boolean.valueOf(this.packageSet.contains(paramString));
    }
  }
}