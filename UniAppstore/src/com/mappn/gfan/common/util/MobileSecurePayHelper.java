package com.mappn.gfan.common.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import com.mappn.gfan.ui.HomeTabActivity;
import com.mappn.gfan.ui.SplashActivity;

public class MobileSecurePayHelper
{
  private static final String FILE_NAME = "alipay_plugin.apk";
  static final String TAG = "MobileSecurePayHelper";
  Context mContext = null;
  private ProgressDialog mProgress = null;
  private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try
		    {
				if(2==msg.what) {
					closeProgress();
			        String str = (String)msg.obj;
			        showInstallConfirmDialog(mContext, str);
				}
		    }
		    catch (Exception localException)
		    {
		      localException.printStackTrace();
		    }
		}
	  };
	
  public MobileSecurePayHelper(Context paramContext)
  {
    this.mContext = paramContext;
  }

  public static PackageInfo getApkInfo(Context paramContext, String paramString)
  {
    return paramContext.getPackageManager().getPackageArchiveInfo(paramString, 128);
  }

	public String checkNewUpdate(PackageInfo paramPackageInfo) {
		String str = null;
		try {
			JSONObject localJSONObject = sendCheckNewUpdate(paramPackageInfo.versionName);
			if (localJSONObject.getString("needUpdate")
					.equalsIgnoreCase("true")) {
				str = localJSONObject.getString("updateUrl");
			}

		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return str;
	}

	void closeProgress() {
		try {
			if (this.mProgress != null) {
				this.mProgress.dismiss();
				this.mProgress = null;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public boolean detectMobile_sp() {
		boolean bool = isMobile_spExist();
		if (!bool) {
			File localFile = this.mContext.getCacheDir();
			String str = localFile.getAbsolutePath() + "/temp.apk";
			retrieveApkFromAssets(this.mContext, "alipay_plugin.apk", str);
			this.mProgress = BaseHelper.showProgress(this.mContext, null,
					"正在检测安全支付服务版本", false, true);
			new Thread(new Runnable() {
				public void run() {
					File localFile = mContext.getCacheDir();
					String strPath = localFile.getAbsolutePath() + "/temp.apk";
					
					PackageInfo localPackageInfo = MobileSecurePayHelper
							.getApkInfo(mContext, strPath);
					String str = checkNewUpdate(localPackageInfo);
					if (str != null)
						retrieveApkFromNet(mContext, str, strPath);
					Message localMessage = new Message();
					localMessage.what = 2;
					localMessage.obj = strPath;
					mHandler.sendMessage(localMessage);
				}
			}).start();
		}
		return bool;
	}
  
	public boolean isMobile_spExist() {
		boolean bool = false;
		List localList = this.mContext.getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < localList.size(); i++) {
			if (((PackageInfo) localList.get(i)).packageName
					.equalsIgnoreCase("com.alipay.android.app")) {
				bool = true;
				break;
			}
		}
		return bool;
	}

  public boolean retrieveApkFromAssets(Context paramContext, String paramString1, String paramString2)
  {
    InputStream localInputStream;
    FileOutputStream localFileOutputStream;
    boolean bool = true;
    try
    {
      localInputStream = paramContext.getAssets().open(paramString1);
      File localFile = new File(paramString2);
      localFile.createNewFile();
      localFileOutputStream = new FileOutputStream(localFile);
      byte[] arrayOfByte = new byte[1024];
      while (true)
      {
        int i = localInputStream.read(arrayOfByte);
        if (i <= 0)
          break;
        localFileOutputStream.write(arrayOfByte, 0, i);
      }
      localFileOutputStream.close();
      localInputStream.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      bool = false;
    }

    return bool;
  }

  public boolean retrieveApkFromNet(Context paramContext, String paramString1, String paramString2)
  {
    boolean bool1 = false;
    try
    {
    	bool1 = new NetworkManager(this.mContext).urlDownloadToFile(paramContext, paramString1, paramString2);      
    }
    catch (Exception localException)
    {
        localException.printStackTrace();
    }
    return bool1;
  }

	public JSONObject sendCheckNewUpdate(String paramString) {
		JSONObject localObject = null;
		try {
			JSONObject localJSONObject1 = new JSONObject();
			localJSONObject1.put("action", "update");
			JSONObject localJSONObject2 = new JSONObject();
			localJSONObject2.put("platform", "android");
			localJSONObject2.put("version", paramString);
			localJSONObject2.put("partner", "");
			localJSONObject1.put("data", localJSONObject2);
			JSONObject localJSONObject3 = sendRequest(localJSONObject1
					.toString());
			localObject = localJSONObject3;
		} catch (JSONException localJSONException) {
			localJSONException.printStackTrace();
		}
		return localObject;
	}

  public JSONObject sendRequest(String paramString)
  {
	  JSONObject localJSONObject = null;
    try
    {
      synchronized (new NetworkManager(this.mContext))
      {
        String str = (new NetworkManager(mContext)).SendAndWaitResponse(paramString, "https://msp.alipay.com/x.htm");
        localJSONObject = new JSONObject(str);
        if (localJSONObject != null)
          BaseHelper.log("MobileSecurePayHelper", localJSONObject.toString());
      }
    }
    catch (Exception localException)
    {
        localException.printStackTrace();
    }
    return localJSONObject;
  }

  public void showInstallConfirmDialog(Context paramContext, String paramString)
  {
//    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
//    localBuilder.setTitle("安装提示");
//    localBuilder.setMessage("为保证您的交易安全，需要您安装支付宝安全支付服务，才能进行付款。\n\n点击确定，立即安装。");
//    localBuilder.setPositiveButton("确定", new MobileSecurePayHelper.2(this, paramString, paramContext));
//    localBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener(this)
//    {
//    	public void onClick(DialogInterface paramDialogInterface, int paramInt)
//    	  {
//    	  }
//    });
//    localBuilder.show();
  }
}