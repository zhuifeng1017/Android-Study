package com.zhao.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

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
	
   public static void installApk(Context context, File file) {
	      if(file.exists()) {
	         Intent intent = new Intent("android.intent.action.VIEW");
	         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	         intent.setDataAndType(Uri.fromFile(file), Constants.MIMETYPE_APK);
	         ((ContextWrapper)context).startActivity(intent);
	      } else {
	    	  Toast toast = Toast.makeText(context, "安装包不存在", Toast.LENGTH_SHORT);
	  		  toast.show();
	      }
	   }
   
   public static void InstallAPK(String filename){
	    File file = new File(filename); 
	    if(file.exists()){
	        try {   
	            String command;
	            //filename = StringUtil.insertEscape(filename);
	            command = "adb install -r " + filename;
	            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
	            proc.waitFor();
	        } catch (Exception e) {
	        e.printStackTrace();
	        }
	     }
	  }
}
