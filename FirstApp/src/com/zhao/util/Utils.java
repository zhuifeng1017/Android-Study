package com.zhao.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;

public class Utils {
	public static DisplayMetrics getDevInfo(Activity act) {
		Display disp = act.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		disp.getMetrics(dm);
		return dm;
	}

	public static boolean isNetworkActived(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		}
		return true;
	}
}
