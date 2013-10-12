package com.mappn.gfan.ui;

import com.mappn.gfan.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.AndroidHttpClient;
import com.mappn.gfan.common.ApiAsyncTask.ApiRequestListener;
import com.mappn.gfan.common.HttpClientFactory;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mobclick.android.MobclickAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpHost;

public class SplashActivity extends BaseActivity implements ApiRequestListener {
	private static final int LOAD = 2;
	private static final int VALID = 1;
	private HashMap<String, Object> mContent;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {	// 消息处理函数
			switch (msg.what) {
			default:
				break;
			case 1: {
				if ((!isFinishing()) && (mPreloadResult == 2)) {
					Intent localIntent = new Intent(
							SplashActivity.this.getApplicationContext(),
							HomeTabActivity.class);
					if ((mContent != null)
							&& (mContent.get("extra.home.data.top") != null)
							&& (mContent.get("extra.home.data.bottom") != null))
						localIntent.putExtra("extra.home.data", mContent);
					startActivity(localIntent);
					finish();
				}
			}
				break;
			case 2: {
				if (!isFinishing()) {
					preload(); // 加载数据
				}
			}
				break;
			}
		}
	};
	private int mPreloadResult;

	private void handleHomeRecommend(HashMap hashmap) {
		ArrayList arraylist = (ArrayList) hashmap.get("product_list");
		if (arraylist == null || arraylist.size() <= 0) {
			if (mContent != null)
				mContent = null;
		} else {
			if (mContent == null)
				mContent = new HashMap();
			mContent.put("extra.home.data.bottom", arraylist);
		}
	}

	private void handleTopRecommend(Object obj) {
		ArrayList arraylist = (ArrayList) obj;
		if (arraylist != null && arraylist.size() > 0) {
			if (mContent == null)
				mContent = new HashMap();
			mContent.put("extra.home.data.top", arraylist);
		}
	}

	private void initSplashBg() {
		try {
			Bitmap localBitmap = null;
			File localFile = new File(getFilesDir(), "splash.png");
			if (localFile.exists()) {
				localBitmap = BitmapFactory.decodeFile(localFile
						.getAbsolutePath());
			}
			if (localBitmap != null) {
				setSplashBitmap(localBitmap);
			} else {
				setSplashBitmap(BitmapFactory.decodeResource(getResources(),
						2130837762));
				this.mSession
						.setImageFormat(getWindow().getAttributes().format);
				this.mSession.setSplashTime(0L);
			}
		} catch (OutOfMemoryError localOutOfMemoryError) {
			Utils.E("initSplashBg OutOfMemoryError", localOutOfMemoryError);
		}
	}

	private void preload() {
		mSession.setScreenSize(this);
		mSession.getInstalledApps();
		// 检查网络
		if (!Utils.isNetworkAvailable(getApplicationContext())) {
			mPreloadResult = 2;
			mHandler.sendEmptyMessageDelayed(1, 800L);
		} else {
			org.apache.http.HttpHost httphost = Utils.detectProxy(getApplicationContext());
			if (httphost != null)
				HttpClientFactory.get().getHttpClient().useProxyConnection(httphost);
			else
				HttpClientFactory.get().getHttpClient().useDefaultConnection();
			
			// 发送异步网络请求，一个请求对应一个连接和task
			MarketAPI.getHomeMasterRecommend(getApplicationContext(), this);
			MarketAPI.getHomeRecommend(getApplicationContext(), this, 0, 50);
		}
	}

	private void setSplashBitmap(Bitmap bitmap) {
		Bitmap bitmap1 = ImageUtils
				.scaleBitmap(getApplicationContext(), bitmap);
		ImageView imageview = (ImageView) findViewById(0x7f0c0046);
		if (bitmap1 == null)
			imageview.setImageBitmap(bitmap);
		else
			imageview.setImageBitmap(bitmap1);
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_splash_layout);
		
		// 初始化背景图片
		initSplashBg(); 
		
		// 显示等待
		((ProgressBar) findViewById(R.id.splash_loading))
				.setIndeterminateDrawable(new LoadingDrawable(
						getApplicationContext(), 0, R.color.color_e, R.color.splash_notification_bg, 200));
		
		// 发送消息
		this.mHandler.sendEmptyMessage(2);
	}

	public void onError(int paramInt1, int paramInt2) {
		this.mPreloadResult = (1 + this.mPreloadResult);
		this.mHandler.sendEmptyMessage(1);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onSuccess(int paramInt, Object paramObject) {
		this.mPreloadResult = (1 + this.mPreloadResult);
		if (paramInt == 54) {
			handleTopRecommend(paramObject);
		} else if (paramInt == 16) {
			handleHomeRecommend((HashMap) paramObject);
		}
		this.mHandler.sendEmptyMessage(1);
	}
}