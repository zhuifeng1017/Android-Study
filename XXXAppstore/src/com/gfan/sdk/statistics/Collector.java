package com.gfan.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Collector
{
  private static boolean DEBUG = false;
  private static final String MAC = "FF9B8CB4-E13B-44a7-B3C6-B385D8EB8167";
  private static final long SESSION_TIMEOUT = 15000L;
  private static final String TYPE = "tongjisdk";
  private static final String URL = "http://data.gfan.com";
  private static final String VERSION = "0.3.2";
  private static long activityPauseTime;
  private static long activityResumeTime;
  private static long appStarttime;
  private static String appkey = "";
  private static Context context;
  private static String cpid = "";
  private static Handler hander;// = new Handler(localHandlerThread.getLooper());
  private static String opid;
  private static String rid;
  private static TelephonyManager telManager;
  private static long timesum;

  static
  {
    activityResumeTime = 0L;
    activityPauseTime = 0L;
    appStarttime = 0L;
    opid = "gfan";
    timesum = 0L;
    rid = "";
    DEBUG = false;
    HandlerThread localHandlerThread = new HandlerThread("Statistics");
    localHandlerThread.start();
    hander= new Handler(localHandlerThread.getLooper());
  }

  private static void buildTail(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    paramStringBuilder.append(",\"mac\":\"").append("FF9B8CB4-E13B-44a7-B3C6-B385D8EB8167").append("\",\"appkey\":\"").append(appkey).append("\",\"cpid\":\"").append(cpid).append("\",\"cpidmac\":\"").append(getCpidMac()).append("\",\"opid\":\"").append(opid).append("\",\"sdkversion\":\"").append(paramString1).append("\",\"type\":\"").append(paramString2).append("\"}");
  }

  private static void clearAppClickCount(Context paramContext, long paramLong)
  {
    ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(paramContext);
    localConnectDBUtil.AppClear(paramLong);
    localConnectDBUtil.close();
  }

  public static void comment(Context paramContext, String paramString, IResponse paramIResponse)
  {
    if (validate())
    {
      try
      {
        if ((!rid.equals("")) && (paramString != null))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("{\"msgtype\":\"plinfo\",\"body\":");
          JSONObject localJSONObject = new JSONObject();
          localJSONObject.put("rid", rid);
          localJSONObject.put("comment", paramString);
          localJSONObject.put("version", getVersion(paramContext));
          localStringBuilder.append(localJSONObject);
          buildTail(localStringBuilder, "0.3.2", "tongjisdk");
          hander.post(new MessageHandler(localStringBuilder.toString(), paramIResponse));
        }
      }
      catch (JSONException localJSONException)
      {
        paramIResponse.onFailed(localJSONException);
        Log.e("SDK", "sendPlInfo failed");
      }
     
    }
    else
    {
    	 paramIResponse.onFailed(new RuntimeException("sdk未初始化"));
         Log.e("error", "the SDK configured failed to initialize");
    }
  }

	private static String getAppClickCount(Context paramContext, long paramLong) {
		StringBuilder localStringBuilder = new StringBuilder();
		ConnectDBUtil localConnectDBUtil = ConnectDBUtil
				.getConnection(paramContext);
		Cursor localCursor = localConnectDBUtil.AppSelect(paramLong);
		if (localCursor.getCount() == 0)
			localStringBuilder.append("[{\"\":\"0\"}]");

		localStringBuilder.append("[");
		localCursor.moveToFirst();
		while (localCursor.moveToNext()) {
			String str = localCursor.getString(localCursor
					.getColumnIndex("clickname"));
			int i = localCursor
					.getInt(localCursor.getColumnIndex("clickcount"));
			localStringBuilder.append("{\"").append(str).append("\":\"")
					.append(i).append("\"},");
		}

		localCursor.close();
		localConnectDBUtil.close();
		return localStringBuilder.substring(0, localStringBuilder.length() - 1)
				+ "]";
	}

	private static String getAppkey(Context paramContext)
			throws PackageManager.NameNotFoundException {
		String str = "";
		ApplicationInfo localApplicationInfo = paramContext.getPackageManager()
				.getApplicationInfo(getPackageName(paramContext), 128);
		try {
			str = localApplicationInfo.metaData.get("gfan_statistics_appkey")
					.toString();
		} catch (NullPointerException localNullPointerException) {
			Log.e("SDK", "The gfan_statistics_appkey must be set.");
		}
		return str;
	}

	private static String getCompanyID() {
		String str;
		String str1 = getSimSerialNumber();

		if (str1 == null)
			str = "no sim";
		else {
			try {
				str = str1.substring(4, 6);
			} catch (Exception localException) {
				str = "no sim";
			}
		}
		return str;
	}

	private static String getCountry() {
		String str;
		String str1 = getSimSerialNumber();
		if (str1 == null)
			str = "no sim";
		else {
			try {
				str = str1.substring(0, 4);
			} catch (Exception localException) {
				str = "no sim";
			}
		}
		return str;
	}

	private static String getCpID(Context paramContext)
			throws PackageManager.NameNotFoundException {
		String str = "";
		ApplicationInfo localApplicationInfo = paramContext.getPackageManager()
				.getApplicationInfo(getPackageName(paramContext), 128);
		try {
			str = localApplicationInfo.metaData.get("gfan_cpid").toString();
		} catch (NullPointerException localNullPointerException) {

			Log.e("SDK", "The cpid must be set.");

		}
		return str;
	}

  private static String getCpidMac()
  {
    return "";
  }

  private static String getDevice()
  {
    return Build.MODEL;
  }

  private static String getIMEI()
  {
    return telManager.getDeviceId();
  }

  private static String getMetricsd(Context context1)
  {
      String s;
      if(context1 instanceof Activity)
      {
          DisplayMetrics displaymetrics = new DisplayMetrics();
          ((Activity)context1).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
          s = (new StringBuilder(String.valueOf(displaymetrics.widthPixels))).append("x").append(displaymetrics.heightPixels).toString();
      } else
      {
          s = "";
      }
      return s;
  }

  private static String getOS()
  {
    return Build.VERSION.SDK;
  }

  private static String getPackageName(Context paramContext)
  {
    return paramContext.getPackageName();
  }

	private static String getProvince() {
		String str;
		String str1 = getSimSerialNumber();
		if (str1 == null)
			str = "no sim";
		else {
			try {
				str = str1.substring(8, 10);
			} catch (Exception localException) {
				str = "no sim";
			}
		}
		return str;
	}

  private static String getRid(Context paramContext)
  {
    ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(paramContext);
    Cursor localCursor = localConnectDBUtil.ridSelect();
    if (localCursor.getCount() > 0)
    {
      localCursor.moveToFirst();
      rid = localCursor.getString(localCursor.getColumnIndex("rid"));
    }
    localCursor.close();
    localConnectDBUtil.close();
    return rid;
  }

  private static String getSimSerialNumber()
  {
    return telManager.getSimSerialNumber();
  }

  private static String getVersion(Context paramContext)
  {
    String str = "";
    try
    {
      str = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
      return str;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      while (true)
        Log.e("SDK", "not found app version");
    }
  }

  public static void onError(Context paramContext)
  {
    Thread.UncaughtExceptionHandler localUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    if (!(localUncaughtExceptionHandler instanceof DefaultExceptionHandler))
      Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(localUncaughtExceptionHandler, paramContext));
  }

	public static void onPause(Context paramContext) {
		if (!(paramContext instanceof Activity))
			return;

		setActivityPauseTime();
		SharedPreferences localSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(paramContext
						.getApplicationContext());
		if (localSharedPreferences != null)
			localSharedPreferences
					.edit()
					.putLong("com.gfan.sdk.appStarttime", appStarttime)
					.putLong("com.gfan.sdk.activityPauseTime",
							activityPauseTime)
					.putLong("com.gfan.sdk.timesum", timesum).commit();
	}

  public static void onResume(Context paramContext)
  {
    if (!(paramContext instanceof Activity))
      return;
      
      context = paramContext;
      activityResumeTime = System.currentTimeMillis();
      
      SharedPreferences localSharedPreferences;
      try
      {
        appkey = getAppkey(paramContext);
        cpid = getCpID(paramContext);
        if ((appkey.length() == 0) || (cpid.length() == 0))
        	return;
        
        if (telManager == null)
          telManager = (TelephonyManager)paramContext.getSystemService("phone");
        localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext.getApplicationContext());
        if (localSharedPreferences == null)
          return;
        appStarttime = localSharedPreferences.getLong("com.gfan.sdk.appStarttime", System.currentTimeMillis());
        activityPauseTime = localSharedPreferences.getLong("com.gfan.sdk.activityPauseTime", 0L);
        timesum = localSharedPreferences.getLong("com.gfan.sdk.timesum", 0L);
        try
        {
          if (getRid(paramContext).length() != 0) {
        	  if ((activityPauseTime == 0L) || (activityResumeTime - activityPauseTime > 15000L))
              {
                sendAppInfo(paramContext, appStarttime);
                long l = localSharedPreferences.getLong("com.gfan.sdk.lastSendAppListTime", 0L);
                if (activityResumeTime > 604800000L + l)
                  sendApkListInfo(paramContext);
                if (activityPauseTime > 0L)
                {
                  sendLeaveInfo(paramContext, appStarttime, timesum);
                  sendErrorsInfo(paramContext);
                  appStarttime = activityResumeTime;
                  timesum = 0L;
                }
              }
          }
          sendMoblieInfo(paramContext, appStarttime);
        }
        catch (Exception localException)
        {
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("SDK", "The did or cpid not set");
      }
  }

  private static void postAppInfo(Context paramContext, final String paramString1, final long paramLong1, final String paramString2, String paramString3, final String paramString4, final String paramString5, final String paramString6, long paramLong2)
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("{\"msgtype\":\"appinfo\",\"body\":");
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("rid", rid).put("version", paramString1).put("starttime", Long.toString(paramLong1)).put("endtime", "0").put("timesum", "0").put("clickcount", new JSONArray());
      localStringBuilder.append(localJSONObject);
      buildTail(localStringBuilder, paramString5, paramString6);
      Handler localHandler = hander;
      MessageHandler localMessageHandler = new MessageHandler(localStringBuilder.toString(), new IResponse()
      {
        public void onFailed(Exception paramAnonymousException)
        {
//          if (this.val$backupId < 0L)
//          {
//            ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(paramString1);
//            localConnectDBUtil.BackupStartInfoInsert(paramLong1, paramString2, paramString4, paramString5, paramString6, this.val$sdkVersion, this.val$sdkType);
//            localConnectDBUtil.close();
//          }
        }

        public void onSuccess(HttpResponse paramAnonymousHttpResponse)
        {
//          if (this.val$backupId > 0L)
//          {
//            ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(paramString1);
//            localConnectDBUtil.BackupStartInfoClear(this.val$backupId);
//            localConnectDBUtil.close();
//          }
        }
      });
      localHandler.post(localMessageHandler);
    }
    catch (JSONException localJSONException)
    {
        Log.e("SDK", "JSONException in postappinfo");
    }
  }

  private static void postLeaveInfo(Context paramContext, final String paramString1, final long paramLong1, long paramLong2, final long paramLong3, final String paramString2, String paramString3, final String paramString4, String paramString5, final String paramString6, long paramLong4)
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("{\"msgtype\":\"leaveinfo\",\"body\":");
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("rid", rid).put("version", paramString1).put("starttime", Long.toString(paramLong1)).put("endtime", Long.toString(paramLong2)).put("timesum", Long.toString(paramLong3));
      localStringBuilder.append(localJSONObject);
      int i = localStringBuilder.length() - 1;
      buildTail(localStringBuilder, paramString5, paramString6);
      localStringBuilder.insert(i, ",\"clickcount\":" + getAppClickCount(paramContext, paramLong1));
      Handler localHandler = hander;
      MessageHandler localMessageHandler = new MessageHandler(localStringBuilder.toString(), new IResponse()
      {
        public void onFailed(Exception paramAnonymousException)
        {
//          if (paramString1 < 0L)
//          {
//            ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(Collector.this);
//            localConnectDBUtil.BackupAppInfoInsert(paramLong3, paramLong1, paramString2, paramString4, paramString6, this.val$cpidmac, this.val$opid, this.val$sdkVersion, this.val$sdkType);
//            localConnectDBUtil.close();
//          }
        }

        public void onSuccess(HttpResponse paramAnonymousHttpResponse)
        {
//          Collector.clearAppClickCount(Collector.this, paramLong1);
//          if (paramString1 > 0L)
//          {
//            ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(Collector.this);
//            localConnectDBUtil.BackupAppInfoClear(paramString1);
//            localConnectDBUtil.close();
//          }
        }
      });
      localHandler.post(localMessageHandler);
    }
    catch (JSONException localJSONException)
    {
        Log.e("SDK", "JSONException in postLeaveInfo");
    }
  }

  private static String[] searchForStackTraces(Context paramContext)
  {
    File localFile = new File(paramContext.getFilesDir().getAbsolutePath() + "/");
    localFile.mkdir();
    return localFile.list(new FilenameFilter()
    {
      public boolean accept(File paramAnonymousFile, String paramAnonymousString)
      {
        return paramAnonymousString.endsWith(".stacktrace");
      }
    });
  }

  public static void sendApkListInfo(Context paramContext)
    throws Exception
  {
    StringBuilder localStringBuilder;
    if (validate())
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("{\"msgtype\":\"applist\",\"body\":");
      try
      {
        JSONObject localJSONObject = new JSONObject();
        localJSONObject.put("rid", rid);
        localJSONObject.put("packagelist", "${packagelist}");
        localStringBuilder.append(localJSONObject);
        buildTail(localStringBuilder, "0.3.2", "tongjisdk");
        hander.post(new ApkInfo(localStringBuilder.toString(), new IResponse()
        {
          public void onFailed(Exception paramAnonymousException)
          {
          }

          public void onSuccess(HttpResponse paramAnonymousHttpResponse)
          {
            SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (localSharedPreferences != null);
            {
              localSharedPreferences.edit().putLong("com.gfan.sdk.lastSendAppListTime", System.currentTimeMillis()).commit();
            }
          }
        }
        , paramContext));
      }
      catch (JSONException localJSONException)
      {
        localJSONException.printStackTrace();
      }
    }
    else
    {
    	Log.e("error", "the SDK configured failed to initialize");
    }
  }

  private static void sendAppInfo(Context paramContext, long paramLong)
    throws Exception
  {
    if (validate())
    {
      ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(paramContext);
      Cursor localCursor = localConnectDBUtil.BackupStartInfoSelect();
      if (localCursor.getCount() > 0)
      {
        localCursor.moveToFirst();
        do
          postAppInfo(paramContext, localCursor.getString(localCursor.getColumnIndex("version")), localCursor.getLong(localCursor.getColumnIndex("starttime")), localCursor.getString(localCursor.getColumnIndex("mac")), localCursor.getString(localCursor.getColumnIndex("cpidmac")), localCursor.getString(localCursor.getColumnIndex("opid")), localCursor.getString(localCursor.getColumnIndex("sdk_version")), localCursor.getString(localCursor.getColumnIndex("sdk_type")), localCursor.getLong(localCursor.getColumnIndex("id")));
        while (localCursor.moveToNext());
      }
      localCursor.close();
      localConnectDBUtil.close();
      postAppInfo(paramContext, getVersion(paramContext), paramLong, "FF9B8CB4-E13B-44a7-B3C6-B385D8EB8167", getCpidMac(), opid, "0.3.2", "tongjisdk", -1L);
    }
    else
    {
      Log.e("SDK", "the SDK configured failed to initialize");
    }
  }

  private static void sendCachedLeaveInfo(Context paramContext)
  {
    ConnectDBUtil localConnectDBUtil = ConnectDBUtil.getConnection(paramContext);
    Cursor localCursor = localConnectDBUtil.BackupAppInfoSelect();
    if (localCursor.getCount() > 0)
    {
      localCursor.moveToFirst();
      do
        postLeaveInfo(paramContext, localCursor.getString(localCursor.getColumnIndex("version")), localCursor.getLong(localCursor.getColumnIndex("starttime")), localCursor.getLong(localCursor.getColumnIndex("endtime")), localCursor.getLong(localCursor.getColumnIndex("timesum")), localCursor.getString(localCursor.getColumnIndex("mac")), localCursor.getString(localCursor.getColumnIndex("cpidmac")), localCursor.getString(localCursor.getColumnIndex("opid")), localCursor.getString(localCursor.getColumnIndex("sdk_version")), localCursor.getString(localCursor.getColumnIndex("sdk_type")), localCursor.getLong(localCursor.getColumnIndex("id")));
      while (localCursor.moveToNext());
    }
    localCursor.close();
    localConnectDBUtil.close();
  }

  private static void sendCpInfo(Context paramContext)
    throws Exception
  {
    if (validate())
    {
      try
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("{\"msgtype\":\"cpinfo\",\"body\":");
        localStringBuilder.append(new JSONObject().put("rid", rid).put("cpid", cpid));
        buildTail(localStringBuilder, "0.3.2", "tongjisdk");
        hander.post(new MessageHandler(localStringBuilder.toString(), new IResponse()
        {
          public void onFailed(Exception paramAnonymousException)
          {
            Log.e("SDK", "sendCpInfo failed");
          }

          public void onSuccess(HttpResponse paramAnonymousHttpResponse)
          {
          }
        }));
      }
      catch (JSONException localJSONException)
      {
        Log.e("SDK", "sendCpInfo failed");
      }
    }
    else
    	Log.e("SDK", "the SDK configured failed to initialize");
  }

  private static void sendErrorsInfo(Context paramContext)
  {
//    String str1;
//    String[] arrayOfString;
//    final String str2;
//    StringBuilder localStringBuilder1;
//    JSONObject localJSONObject1;
//    if (validate())
//    {
//      str1 = getRid(paramContext);
//      arrayOfString = searchForStackTraces(paramContext);
//      if ((arrayOfString != null) && (arrayOfString.length > 0))
//      {
//        str2 = paramContext.getFilesDir().getAbsolutePath();
//        localStringBuilder1 = new StringBuilder();
//        localStringBuilder1.append("{\"msgtype\":\"errorinfo\",\"body\":");
//        localJSONObject1 = new JSONObject();
//      }
//    }
//    try
//    {
//      localJSONObject1.put("rid", str1).put("version", getVersion(paramContext)).put("device", getDevice()).put("os", getOS());
//      JSONArray localJSONArray = new JSONArray();
//      localJSONObject1.put("errors", localJSONArray);
//      int i = 0;
//      int j = arrayOfString.length;
//      if (i >= j)
//      {
//        label131: localStringBuilder1.append(localJSONObject1);
//        buildTail(localStringBuilder1, "0.3.2", "tongjisdk");
//        hander.post(new MessageHandler(localStringBuilder1.toString(), new IResponse()
//        {
//          public void onFailed(Exception paramAnonymousException)
//          {
//            Log.e("SDK", "sendErrorInfo failed");
//          }
//
//          public void onSuccess(HttpResponse paramAnonymousHttpResponse)
//          {
//            int i = 0;
//            try
//            {
//              while (i < Collector.this.length)
//              {
//                new File(str2 + "/" + Collector.this[i]).delete();
//                i++;
//              }
//            }
//            catch (Exception localException)
//            {
//              localException.printStackTrace();
//            }
//          }
//        }));
//        return;
//      }
//      JSONObject localJSONObject2 = new JSONObject();
//      String str3 = str2 + "/" + arrayOfString[i];
//      String str4 = arrayOfString[i].substring(0, arrayOfString[i].indexOf("."));
//      StringBuilder localStringBuilder2 = new StringBuilder();
//      BufferedReader localBufferedReader = new BufferedReader(new FileReader(str3));
//      while (true)
//      {
//        String str5 = localBufferedReader.readLine();
//        if (str5 == null)
//        {
//          localBufferedReader.close();
//          localJSONObject2.put("time", str4).put("error", localStringBuilder2.toString());
//          localJSONArray.put(i, localJSONObject2);
//          i++;
//          break;
//        }
//        localStringBuilder2.append(str5);
//        localStringBuilder2.append(System.getProperty("line.separator"));
//      }
//    }
//    catch (FileNotFoundException localFileNotFoundException)
//    {
//      while (true)
//      {
//        continue;
//        Log.e("SDK", "the SDK configured failed to initialize");
//      }
//    }
//    catch (Exception localException)
//    {
//      break label131;
//    }
//    catch (JSONException localJSONException)
//    {
//      break label131;
//    }
//    catch (IOException localIOException)
//    {
//      break label131;
//    }
  }

  private static void sendLeaveInfo(Context paramContext, long paramLong1, long paramLong2)
    throws Exception
  {
    if (validate())
    {
      sendCachedLeaveInfo(paramContext);
      postLeaveInfo(paramContext, getVersion(paramContext), paramLong1, System.currentTimeMillis(), paramLong2, "FF9B8CB4-E13B-44a7-B3C6-B385D8EB8167", getCpidMac(), opid, "0.3.2", "tongjisdk", -1L);
    }
    else
    {
      Log.e("SDK", "the SDK configured failed to initialize");
    }
  }

	private static boolean sendMessage(String paramString,
			IResponse paramIResponse) {
		HttpPost localHttpPost = new HttpPost("http://data.gfan.com");
		BasicHttpParams localBasicHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 60000);
		HttpConnectionParams.setSoTimeout(localBasicHttpParams, 60000);
		DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient(
				localBasicHttpParams);
		localHttpPost.setHeader("Content-Type", "application/json");
		boolean bool = true;
		try {
			if (DEBUG)
				Log.i("SDK", paramString);
			localHttpPost.setEntity(new StringEntity(paramString, "UTF-8"));
			HttpResponse localHttpResponse = localDefaultHttpClient
					.execute(localHttpPost);
			int i = localHttpResponse.getStatusLine().getStatusCode();
			if (200 == i) {
				if (paramIResponse != null)
					paramIResponse.onSuccess(localHttpResponse);
				localDefaultHttpClient.getConnectionManager().shutdown();
			}

			if (paramIResponse != null)
				paramIResponse.onFailed(new IOException((new StringBuilder(String.valueOf(i))).toString()));
			localDefaultHttpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException localClientProtocolException) {

			if (paramIResponse != null)
				paramIResponse.onFailed(localClientProtocolException);
			localDefaultHttpClient.getConnectionManager().shutdown();
			bool = false;
		} catch (IOException localIOException) {
			if (paramIResponse != null)
				paramIResponse.onFailed(localIOException);
			localDefaultHttpClient.getConnectionManager().shutdown();
			bool = false;
		} catch (Exception localException) {

			if (paramIResponse != null)
				paramIResponse.onFailed(localException);
			localDefaultHttpClient.getConnectionManager().shutdown();
			bool = false;
		} finally {
			localDefaultHttpClient.getConnectionManager().shutdown();
		}
		return bool;
	}

  private static void sendMoblieInfo(Context paramContext, final long paramLong)
    throws Exception
  {
    StringBuilder localStringBuilder;
    JSONObject localJSONObject = null;
    if (validate())
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("{\"msgtype\":\"baseinfo\",\"body\":");
      localJSONObject = new JSONObject();
      try
      {
        localJSONObject.put("country", getCountry()).put("companyid", getCompanyID()).put("device", getDevice()).put("imei", getIMEI()).put("metricsd", getMetricsd(paramContext)).put("os", getOS()).put("province", getProvince()).put("sim", getSimSerialNumber());
        localStringBuilder.append(localJSONObject);
        buildTail(localStringBuilder, "0.3.2", "tongjisdk");
        hander.post(new MessageHandler(localStringBuilder.toString(), new IResponse()
        {
          public void onFailed(Exception paramAnonymousException)
          {
            Log.e("SDK", "sendMoblieInfo failed");
          }

          // ERROR //
          public void onSuccess(HttpResponse paramAnonymousHttpResponse)
          {
            
          }
        }));
      }
      catch (JSONException localJSONException)
      {
        Log.e("SDK", "e", localJSONException);
      }
    }
    else
    {
      Log.e("SDK", "the SDK configured failed to initialize");
    }
  }

  private static void setActivityPauseTime()
  {
    activityPauseTime = System.currentTimeMillis();
    timesum += activityPauseTime - activityResumeTime;
  }

  public static void setAppClickCount(String s)
  {
      if(validate() && context != null)
      {
          ConnectDBUtil connectdbutil = ConnectDBUtil.getConnection(context);
          Cursor cursor = connectdbutil.AppSelectClickname(s, appStarttime);
          if(cursor.getCount() == 0)
          {
              connectdbutil.AppInsert(s, appStarttime, 1);
          } else
          {
              cursor.moveToFirst();
              int i = cursor.getInt(cursor.getColumnIndex("clickcount"));
              connectdbutil.AppUpdate(s, appStarttime, i + 1);
          }
          cursor.close();
          connectdbutil.close();
      } else
      {
          Log.e("SDK", "the SDK configured failed to initialize");
      }
  }

  private static boolean validate()
  {
    if ("".equals(appkey));
    for (boolean bool = false; ; bool = true)
      return bool;
  }

  private static class ApkInfo
    implements Runnable
  {
    private static final Object mutex = new Object();
    private Context context;
    private Collector.IResponse iResponse;
    private String message;

    public ApkInfo(String paramString, Collector.IResponse paramIResponse, Context paramContext)
    {
      this.message = paramString;
      this.iResponse = paramIResponse;
      this.context = paramContext;
    }

    public void run()
    {
      synchronized (mutex)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        List localList = this.context.getPackageManager().getInstalledApplications(0);
        int i = localList.size();
        for (int j = 0; ; j++)
        {
          if (j >= i)
          {
            if (localStringBuilder.length() <= 0)
              break;
            this.message = this.message.replace("${packagelist}", localStringBuilder.substring(0, localStringBuilder.length() - 1));
            Collector.sendMessage(this.message, this.iResponse);
            return;
          }
          localStringBuilder.append(((ApplicationInfo)localList.get(j)).packageName);
          localStringBuilder.append(",");
        }
        this.message = this.message.replace("${packagelist}", "");
      }
    }
  }

  public static abstract interface IResponse
  {
    public abstract void onFailed(Exception paramException);

    public abstract void onSuccess(HttpResponse paramHttpResponse);
  }

  private static final class MessageHandler
    implements Runnable
  {
    private static final Object mutex = new Object();
    private Collector.IResponse iResponse;
    private String message;

    MessageHandler(String paramString, Collector.IResponse paramIResponse)
    {
      this.iResponse = paramIResponse;
      this.message = paramString;
    }

    public void run()
    {
      try
      {
        synchronized (mutex)
        {
          Collector.sendMessage(this.message, this.iResponse);
        }
      }
      catch (Exception localException)
      {
        Log.e("SDK", "Exception occurred when sending message.");
      }
    }
  }
}