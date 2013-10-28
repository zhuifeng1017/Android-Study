package com.xxx.appstore.common.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.AndroidHttpClient;
import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.download.Helper;
import com.xxx.appstore.common.util.StreamUtil;
import com.xxx.appstore.common.util.StringUtils;
import com.xxx.appstore.common.util.ThemeManager;
import com.xxx.appstore.common.vo.RecommendTopic;
import com.xxx.appstore.common.widget.DefaultExceptionHandler;
import com.xxx.appstore.ui.LoginActivity;
import com.xxx.appstore.ui.PreloadActivity;
import com.xxx.appstore.ui.RecommendActivity;
import com.mobclick.android.MobclickAgent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

public class Utils {

   private static final String ENCODING_UTF8 = "UTF-8";
   public static final int REPORT_TYPE_CANCEL = 2;
   public static final int REPORT_TYPE_OVER = 1;
   public static final int REPORT_TYPE_START = 0;
   private static final String TAG = "Utils";
   private static WeakReference<Calendar> calendar;
   public static boolean sDebug;
   public static String sLogTag;


   public static void D(String var0) {
      if(sDebug) {
         Log.d(sLogTag, var0);
      }
   }

   public static void D(String var0, Throwable var1) {
      if(sDebug) {
         Log.d(sLogTag, var0, var1);
      }
   }

   public static void E(String var0) {
      if(sDebug) {
         Log.e(sLogTag, var0);
      }
   }

   public static void E(String var0, Throwable var1) {
      if(sDebug) {
         Log.e(sLogTag, var0, var1);
      }
   }

   public static void I(String var0) {
      if(sDebug) {
         Log.i(sLogTag, var0);
      }
   }

   public static void I(String var0, Throwable var1) {
      if(sDebug) {
         Log.i(sLogTag, var0, var1);
      }
   }

   public static void V(String var0) {
      if(/*Debug*/true) {
    	 sLogTag = "com.xxx.appstore.v";
         Log.v(sLogTag, var0);
      }
   }

   public static void V(String var0, Throwable var1) {
      if(sDebug) {
         Log.v(sLogTag, var0, var1);
      }
   }

   public static void W(String var0) {
      if(sDebug) {
         Log.w(sLogTag, var0);
      }
   }

   public static void W(String var0, Throwable var1) {
      if(sDebug) {
         Log.w(sLogTag, var0, var1);
      }
   }

   public static String calculateRemainBytes(Context var0, float var1, float var2) {
      float var3 = var2 - var1;
      if(var3 <= 0.0F) {
         var3 = 0.0F;
      }

      String var5;
      if(var3 == 0.0F) {
         var5 = "";
      } else if(var3 > 1000000.0F) {
         Object[] var8 = new Object[2];
         Object[] var9 = new Object[]{Float.valueOf(var3 / 1000000.0F)};
         var8[0] = String.format("%.02f", var9);
         var8[1] = "M";
         var5 = var0.getString(2131296369, var8);
      } else if(var3 > 1000.0F) {
         Object[] var6 = new Object[2];
         Object[] var7 = new Object[]{Float.valueOf(var3 / 1000.0F)};
         var6[0] = String.format("%.02f", var7);
         var6[1] = "K";
         var5 = var0.getString(2131296369, var6);
      } else {
         Object[] var4 = new Object[]{Integer.valueOf((int)var3), "B"};
         var5 = var0.getString(2131296369, var4);
      }

      return var5;
   }

   public static String checkMarketFile(Context var0) {
      String var2;
      if(PreferenceManager.getDefaultSharedPreferences(var0).getLong("pref.update.id", -1L) == -1L) {
         var2 = "";
      } else {
         Cursor var1 = var0.getContentResolver().query(DownloadManager.Impl.CONTENT_URI, (String[])null, "status >= \'200\' AND source == \'3\' AND mimetype == \'application/vnd.android.package-archive\'", (String[])null, (String)null);
         if(var1 != null) {
            String var3 = "";
            if(var1.moveToFirst() && var1.getInt(var1.getColumnIndex("status")) == 200) {
               var3 = var1.getString(var1.getColumnIndex("_data"));
            }

            var1.close();
            var2 = var3;
         } else {
            var2 = "";
         }
      }

      return var2;
   }

   public static void clearCache(Context var0) {
      File[] var1 = Environment.getDownloadCacheDirectory().listFiles();
      if(var1 != null) {
         int var6 = var1.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            var1[var7].delete();
         }
      }

      File[] var2 = var0.getCacheDir().listFiles();
      if(var2 != null) {
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            var2[var4].delete();
         }
      }
   }

   public static boolean compareFileWithPathAndPkg(Context var0, String var1, String var2) {
      String var3 = getPathWithPackageName(var0, var2);
      boolean var4;
      if(TextUtils.isEmpty(var3)) {
         var4 = true;
      } else {
         var4 = compareFileWithSignature(var1, var3);
      }

      return var4;
   }

   public static boolean compareFileWithSignature(String var0, String var1) {
      long var2 = System.currentTimeMillis();
      boolean var4;
      if(!TextUtils.isEmpty(var0) && !TextUtils.isEmpty(var1)) {
         byte[] var5 = getFileSignatureMd5(var0);
         byte[] var6 = getFileSignatureMd5(var1);
         V("compareFileWithSignature total time is " + (System.currentTimeMillis() - var2));
         var4 = isEqual(var5, var6);
      } else {
         var4 = false;
      }

      return var4;
   }

   public static void copyFile(InputStream var0, FileOutputStream var1) throws IOException {
      byte[] var2 = new byte[8192];

      while(true) {
         int var3 = var0.read(var2);
         if(var3 <= 0) {
            var0.close();
            var1.close();
            return;
         }

         var1.write(var2, 0, var3);
      }
   }

   public static boolean copyMarketFile(Context var0, String var1, String var2) {
      boolean var4;
      if(!(new File(var1)).exists()) {
         var4 = false;
      } else {
         label25: {
            try {
               FileOutputStream var6 = var0.openFileOutput(var2, 1);
               copyFile(new FileInputStream(var1), var6);
               break label25;
            } catch (FileNotFoundException var7) {
               var7.printStackTrace();
            } catch (IOException var8) {
               var8.printStackTrace();
            }

            var4 = false;
            return var4;
         }

         var4 = true;
      }

      return var4;
   }

   public static void copyStreamInner(InputStream var0, OutputStream var1) throws IOException {
      byte[] var2 = new byte[4096];

      while(true) {
         int var3 = var0.read(var2);
         if(var3 < 0) {
            return;
         }

         var1.write(var2, 0, var3);
         var1.flush();
      }
   }

   public static View createTabView(Context var0, Session var1, String var2, int var3, TextView var4) {
      TextView var5;
      if(var4 == null) {
         var5 = (TextView)LayoutInflater.from(var0).inflate(2130903095, (ViewGroup)null);
      } else {
         var5 = var4;
      }

      var5.setText(var2);
      if(var3 == -1) {
         var5.setBackgroundResource(ThemeManager.getResource(var1, 7));
      } else if(var3 == 1) {
         var5.setBackgroundResource(ThemeManager.getResource(var1, 8));
      } else {
         var5.setBackgroundResource(ThemeManager.getResource(var1, 6));
      }

      var5.setTextAppearance(var0, ThemeManager.getResource(var1, 12));
      return var5;
   }

   public static boolean deleteFile(String var0) {
      boolean var1;
      if(TextUtils.isEmpty(var0)) {
         var1 = false;
      } else {
         var1 = (new File(var0)).delete();
      }

      return var1;
   }

   public static HttpHost detectProxy(Context var0) {
      NetworkInfo var1 = ((ConnectivityManager)var0.getSystemService("connectivity")).getActiveNetworkInfo();
      HttpHost var2;
      if(var1 != null && var1.isAvailable() && var1.getType() == 0) {
         String var3 = Proxy.getDefaultHost();
         int var4 = Proxy.getDefaultPort();
         if(var3 != null) {
            var2 = new HttpHost(var3, var4, "http");
            return var2;
         }
      }

      var2 = null;
      return var2;
   }

   public static String formatDate(long var0) {
      if(calendar == null || calendar.get() == null) {
         calendar = new WeakReference(Calendar.getInstance());
      }

      Calendar var2 = (Calendar)calendar.get();
      var2.setTimeInMillis(var0);
      return (new SimpleDateFormat("yyyy-MM-dd")).format(var2.getTime());
   }

   public static String formatTime(long var0) {
      return (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date(var0));
   }

   public static List<PackageInfo> getAllInstalledApps(Context var0) {
      List var1 = var0.getPackageManager().getInstalledPackages(0);
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         PackageInfo var5 = (PackageInfo)var4.next();
         if(!"com.unistrong.appstore".equals(var5.packageName)) {
            var2.add(var5);
            var3.add(var5.packageName);
         }
      }

      Session.get(var0).setInstalledApps(var3);
      return var2;
   }

   public static HashMap<String, Object> getApkInfo(Context var0, File var1) {
      PackageManager var2 = var0.getPackageManager();
      String var3 = var1.getAbsolutePath();
      PackageInfo var4 = var2.getPackageArchiveInfo(var3, 1);
      HashMap var14;
      if(var4 == null) {
         var14 = null;
      } else {
         ApplicationInfo var5 = var4.applicationInfo;
         var4.applicationInfo.sourceDir = var3;
         var4.applicationInfo.publicSourceDir = var3;
         Drawable var6 = var2.getApplicationIcon(var5);
         HashMap var7 = new HashMap();
         var7.put("icon_url", var6);
         var7.put("name", var1.getName());
         var7.put("info", var3);
         var7.put("product_description", var1.getAbsolutePath());
         var7.put("pay_category", Integer.valueOf(1));
         var7.put("place_holder", Boolean.valueOf(false));
         var14 = var7;
      }

      return var14;
   }

   private static void getApkList(Context var0, File var1, ArrayList<HashMap<String, Object>> var2) {
      boolean var4;
      int var5;
      label99: {
         File var3 = new File(var1, "market");
         if(var3.exists()) {
            File[] var30 = var3.listFiles();
            if(var30.length > 0) {
               int var31 = var30.length;
               int var32 = 0;

               int var33;
               for(var33 = 0; var32 < var31; ++var32) {
                  File var37 = var30[var32];
                  if(!var37.isDirectory() && var37.getName().endsWith(".apk")) {
                     HashMap var38 = getApkInfo(var0, var37);
                     if(var38 != null) {
                        ++var33;
                        var2.add(var38);
                     }
                  }
               }

               if(var33 > 0) {
                  HashMap var34 = new HashMap();
                  var34.put("name", var0.getString(2131296547) + "(" + var3.getAbsolutePath() + ")");
                  var34.put("place_holder", Boolean.valueOf(true));
                  var2.add(0, var34);
                  var4 = true;
                  var5 = var33;
               } else {
                  var5 = var33;
                  var4 = false;
               }
               break label99;
            }
         }

         var4 = false;
         var5 = 0;
      }

      int var7;
      boolean var8;
      label88: {
         File var6 = new File(var1, "bbs");
         if(var6.exists()) {
            File[] var20 = var6.listFiles();
            if(var20.length > 0) {
               int var21 = var20.length;
               int var22 = 0;

               int var23;
               for(var23 = var5; var22 < var21; ++var22) {
                  File var27 = var20[var22];
                  if(!var27.isDirectory() && var27.getName().endsWith(".apk")) {
                     HashMap var28 = getApkInfo(var0, var27);
                     if(var28 != null) {
                        ++var23;
                        var2.add(var28);
                     }
                  }
               }

               if(var23 > var5) {
                  HashMap var24 = new HashMap();
                  var24.put("name", var0.getString(2131296548) + "(" + var6.getAbsolutePath() + ")");
                  var24.put("place_holder", Boolean.valueOf(true));
                  if(var4) {
                     var2.add(var5 + 1, var24);
                     var8 = true;
                     var7 = var23;
                  } else {
                     var2.add(var5, var24);
                     var8 = true;
                     var7 = var23;
                  }
               } else {
                  var7 = var23;
                  var8 = false;
               }
               break label88;
            }
         }

         var7 = var5;
         var8 = false;
      }

      File var9 = new File(var1, "cloud");
      if(var9.exists()) {
         File[] var10 = var9.listFiles();
         if(var10.length > 0) {
            int var11 = var10.length;
            int var12 = 0;

            int var13;
            for(var13 = var7; var12 < var11; ++var12) {
               File var17 = var10[var12];
               if(!var17.isDirectory() && var17.getName().endsWith(".apk")) {
                  HashMap var18 = getApkInfo(var0, var17);
                  if(var18 != null) {
                     ++var13;
                     var2.add(var18);
                  }
               }
            }

            if(var13 > var7) {
               HashMap var14 = new HashMap();
               var14.put("name", var0.getString(2131296549) + "(" + var9.getAbsolutePath() + ")");
               var14.put("place_holder", Boolean.valueOf(true));
               if(var4 && var8) {
                  var2.add(var7 + 2, var14);
               } else if(var4 | var8) {
                  var2.add(var7 + 1, var14);
               } else {
                  var2.add(var7, var14);
               }
            }
         }
      }

   }

   public static String getCarrier(Context var0) {
      Cursor var1 = var0.getContentResolver().query(Uri.parse("content://telephony/carriers"), new String[]{"apn"}, "current=1", (String[])null, (String)null);
      String var2;
      if(var1 != null) {
         boolean var9 = false;

         label57: {
            String var5;
            boolean var6;
            try {
               var9 = true;
               if(!var1.moveToFirst()) {
                  var9 = false;
                  break label57;
               }

               var5 = var1.getString(0);
               var6 = TextUtils.isEmpty(var5);
               var9 = false;
            } catch (Exception var10) {
               E("Can not get Network info", var10);
               var9 = false;
               break label57;
            } finally {
               if(var9) {
                  var1.close();
               }
            }

            if(!var6) {
               var1.close();
               var2 = var5;
               return var2;
            }
         }

         var1.close();
      }

      var2 = "";
      return var2;
   }

   public static byte[] getFileSignatureMd5(String filePath) {
       try {
           JarFile jarFile = new JarFile(filePath);
           JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");
           if (jarEntry != null) {
               try {
                   Certificate[] certs = jarEntry.getCertificates();
                   byte[] certBytes = certs[0].getEncoded();
                   byte[] main = getMd5(certBytes);
                   return main;
               } catch (CertificateEncodingException localCertificateEncodingException) {

               }
           }
       } catch (IOException e) {
    	   W("occur IOException when get file signature", e);
       } catch (Exception e) {
    	   W("occur other Exception when get file signature", e);
       }
       return null;
   }

   public static String getFingerPrint(Context var0) {
      Session var1 = Session.get(var0);
      return getMD5(var1.getIMEI() + var1.getMac());
   }

   public static float getFloat(String var0) {
      float var2;
      if(var0 == null) {
         var2 = 0.0F;
      } else {
         float var3;
         try {
            var3 = Float.parseFloat(var0.trim());
         } catch (NumberFormatException var4) {
            var2 = 0.0F;
            return var2;
         }

         var2 = var3;
      }

      return var2;
   }

	public static String getGzipStringResponse(HttpResponse httpresponse) {
		InputStream inputstream = null;
		ByteArrayOutputStream bytearrayoutputstream = null;

		org.apache.http.HttpEntity httpentity = httpresponse.getEntity();
		if (httpentity == null)
			return null;

		try {
			inputstream = AndroidHttpClient.getUngzippedContent(httpentity);
			bytearrayoutputstream = new ByteArrayOutputStream();
			copyStreamInner(inputstream, bytearrayoutputstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			D("getStringResponse meet IOException", e);
		}
		if (bytearrayoutputstream == null)
			return null;

		String s = null;
		try {
			try {
				s = new String(bytearrayoutputstream.toByteArray(), "utf8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParseException parseexception) {
			// TODO Auto-generated catch block
			D("getStringResponse meet ParseException", parseexception);
		}

		return s;
	}

   public static InputStream getInputStreamResponse(HttpResponse var0) {
      HttpEntity var1 = var0.getEntity();
      InputStream var3;
      if(var1 == null) {
         var3 = null;
      } else {
         InputStream var5;
         label25: {
            try {
               var5 = AndroidHttpClient.getUngzippedContent(var1);
               break label25;
            } catch (IllegalStateException var6) {
               D("getInputStreamResponse meet IllegalStateException", var6);
            } catch (IOException var7) {
               D("getInputStreamResponse meet IOException", var7);
            }

            var3 = null;
            return var3;
         }

         var3 = var5;
      }

      return var3;
   }

   public static List<PackageInfo> getInstalledApps(Context var0) {
      PackageManager var1 = var0.getPackageManager();
      String var2 = Session.get(var0).getPackageName();
      List var3 = var1.getInstalledPackages(0);
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         PackageInfo var7 = (PackageInfo)var6.next();
         if((1 & var7.applicationInfo.flags) == 0 && !var2.equals(var7.packageName)) {
            var4.add(var7);
            var5.add(var7.packageName);
         }
      }

      Session.get(var0).setInstalledApps(var5);
      return var4;
   }

   public static int getInt(String var0) {
      int var2;
      if(TextUtils.isEmpty(var0)) {
         var2 = 0;
      } else {
         int var3;
         try {
            var3 = Integer.parseInt(var0.trim(), 10);
         } catch (NumberFormatException var4) {
            var2 = 0;
            return var2;
         }

         var2 = var3;
      }

      return var2;
   }

   public static LayoutAnimationController getLayoutAnimation() {
      AnimationSet var0 = new AnimationSet(true);
      AlphaAnimation var1 = new AlphaAnimation(0.0F, 1.0F);
      var1.setDuration(50L);
      var0.addAnimation(var1);
      TranslateAnimation var2 = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, -1.0F, 1, 0.0F);
      var2.setDuration(100L);
      var0.addAnimation(var2);
      return new LayoutAnimationController(var0, 0.5F);
   }

   public static ArrayList<HashMap<String, Object>> getLocalApks(Context var0) {
      ArrayList var3;
      if("mounted".equals(Environment.getExternalStorageState())) {
         File var1 = new File(Environment.getExternalStorageDirectory(), "gfan");
         ArrayList var2 = new ArrayList();
         getApkList(var0, var1, var2);
         var3 = var2;
      } else {
         var3 = null;
      }

      return var3;
   }

   public static long getLong(String var0) {
      long var2;
      if(var0 == null) {
         var2 = 0L;
      } else {
         long var4;
         try {
            var4 = Long.parseLong(var0.trim());
         } catch (NumberFormatException var6) {
            var2 = 0L;
            return var2;
         }

         var2 = var4;
      }

      return var2;
   }

   public static String getMD5(String var0) {
      String var2;
      String var6;
      label23: {
         try {
            byte[] var4 = var0.getBytes("utf8");
            MessageDigest var5 = MessageDigest.getInstance("MD5");
            var5.update(var4, 0, var4.length);
            var6 = StringUtils.toHexString(var5.digest(), false);
            break label23;
         } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
         } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
         }

         var2 = "";
         return var2;
      }

      var2 = var6;
      return var2;
   }

   public static byte[] getMd5(String var0) {
      byte[] var2;
      if(var0 == null) {
         var2 = null;
      } else {
         byte[] var3;
         try {
            var3 = getMd5(var0.getBytes(ENCODING_UTF8));
         } catch (UnsupportedEncodingException var4) {
            var2 = null;
            return var2;
         }

         var2 = var3;
      }

      return var2;
   }

   public static byte[] getMd5(byte[] var0) {
      byte[] var1 = null;
      if(var0 != null) {
         StreamUtil var2 = new StreamUtil(true);

         try {
            var2.copyStreamInner(new ByteArrayInputStream(var0), (OutputStream)null);
         } catch (IOException var4) {
            ;
         }

         var1 = var2.getMD5();
      }

      return var1;
   }

   public static String getPackageName(Context var0, String var1) {
      String var3;
      if(TextUtils.isEmpty(var1)) {
         var3 = "";
      } else {
         PackageInfo var2 = var0.getPackageManager().getPackageArchiveInfo(var1, 1);
         if(var2 == null) {
            var3 = "";
         } else {
            var3 = var2.packageName;
         }
      }

      return var3;
   }

   public static String getPathWithPackageName(Context var0, String var1) {
      PackageManager var2 = var0.getPackageManager();

      PackageInfo var4;
      label18: {
         PackageInfo var6;
         try {
            var6 = var2.getPackageInfo(var1, 1);
         } catch (NameNotFoundException var7) {
            var7.printStackTrace();
            var4 = null;
            break label18;
         }

         var4 = var6;
      }

      String var5;
      if(var4 == null) {
         var5 = "";
      } else {
         var5 = var4.applicationInfo.publicSourceDir;
      }

      return var5;
   }

   public static String getStringResponse(HttpResponse var0) {
      String var2;
      if(var0.getEntity() == null) {
         var2 = null;
      } else {
         String var4;
         label25: {
            try {
               var4 = EntityUtils.toString(var0.getEntity());
               break label25;
            } catch (ParseException var5) {
               D("getStringResponse meet ParseException", var5);
            } catch (IOException var6) {
               D("getStringResponse meet IOException", var6);
            }

            var2 = null;
            return var2;
         }

         var2 = var4;
      }

      return var2;
   }

   public static String getTodayDate() {
      if(calendar == null || calendar.get() == null) {
         calendar = new WeakReference(Calendar.getInstance());
      }

      Calendar var0 = (Calendar)calendar.get();
      return (new SimpleDateFormat("yyyy-MM-dd")).format(var0.getTime());
   }

   public static byte[] getUTF8Bytes(String var0) {
      byte[] var1;
      if(var0 == null) {
         var1 = new byte[0];
      } else {
         byte[] var7;
         try {
            var7 = var0.getBytes(ENCODING_UTF8);
         } catch (UnsupportedEncodingException var9) {
            try {
               ByteArrayOutputStream var3 = new ByteArrayOutputStream();
               DataOutputStream var4 = new DataOutputStream(var3);
               var4.writeUTF(var0);
               byte[] var6 = var3.toByteArray();
               var3.close();
               var4.close();
               var1 = new byte[var6.length - 2];
               System.arraycopy(var6, 2, var1, 0, var1.length);
            } catch (IOException var8) {
               var1 = new byte[0];
            }

            return var1;
         }

         var1 = var7;
      }

      return var1;
   }

   public static String getUTF8String(byte[] var0) {
      String var1;
      if(var0 == null) {
         var1 = "";
      } else {
         var1 = getUTF8String(var0, 0, var0.length);
      }

      return var1;
   }

   public static String getUTF8String(byte[] var0, int var1, int var2) {
      String var3;
      if(var0 == null) {
         var3 = "";
      } else {
         try {
            var3 = new String(var0, var1, var2, ENCODING_UTF8);
         } catch (UnsupportedEncodingException var5) {
            var3 = "";
         }
      }

      return var3;
   }

   public static void gotoLogin(Activity var0) {
      var0.startActivityForResult(new Intent(var0, LoginActivity.class), 0);
   }

   public static void gotoMaster(Activity var0, RecommendTopic var1) {
      Intent var2 = new Intent(var0, RecommendActivity.class);
      var2.putExtra("extra.recommend.detail", var1);
      var0.startActivityForResult(var2, 0);
   }

   public static void gotoProductDeatil(Activity var0, String var1) {
      Intent var2 = new Intent(var0, PreloadActivity.class);
      var2.putExtra("extra.key.package.name", var1);
      var0.startActivity(var2);
   }

   public static void installApk(Context var0, File var1) {
      if(var1.exists()) {
         Intent var2 = new Intent("android.intent.action.VIEW");
         var2.setFlags(268435456);
         var2.setDataAndType(Uri.fromFile(var1), "application/vnd.android.package-archive");
         ((ContextWrapper)var0).startActivity(var2);
      } else {
         makeEventToast(var0, var0.getString(2131296368), false);
      }

   }

   public static boolean isEqual(byte[] var0, byte[] var1) {
      boolean var2;
      if(var0 != null && var1 != null) {
         if(var0.length != var1.length) {
            var2 = false;
         } else {
            int var3 = 0;

            while(true) {
               if(var3 >= var1.length) {
                  var2 = true;
                  break;
               }

               if(var0[var3] != var1[var3]) {
                  var2 = false;
                  break;
               }

               ++var3;
            }
         }
      } else if(var0 == var1) {
         var2 = true;
      } else {
         var2 = false;
      }

      return var2;
   }

   public static boolean isFileExist(String var0) {
      boolean var1;
      if(TextUtils.isEmpty(var0)) {
         var1 = false;
      } else {
         var1 = (new File(var0)).exists();
      }

      return var1;
   }

   public static boolean isMobileNetwork(Context var0) {
      ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService("connectivity");
      boolean var3;
      if(var1 != null) {
         NetworkInfo var2 = var1.getActiveNetworkInfo();
         if(var2 != null && var2.getType() == 0) {
            var3 = true;
            return var3;
         }
      }

      var3 = false;
      return var3;
   }

   public static boolean isNeedCheckUpgrade(Context var0) {
      boolean var1;
      if(System.currentTimeMillis() - Session.get(var0).getUpdataCheckTime() > 86400000L) {
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   public static boolean isNetworkAvailable(Context var0) {
      ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService("connectivity");
      boolean var5;
      if(var1 == null) {
         Log.w(TAG, "couldn\'t get connectivity manager");
      } else {
         NetworkInfo[] var2 = var1.getAllNetworkInfo();
         if(var2 != null) {
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               if(var2[var4].getState() == State.CONNECTED) {
                  var5 = true;
                  return var5;
               }
            }
         }
      }

      var5 = false;
      return var5;
   }

   public static boolean isSameSign(Context var0, String var1, String var2) {
      byte[] var3 = StringUtils.fromHexString(var2);
      String var4 = getPathWithPackageName(var0, var1);
      boolean var5;
      if(!TextUtils.isEmpty(var4)) {
         var5 = isEqual(var3, getFileSignatureMd5(var4));
      } else {
         var5 = true;
      }

      return var5;
   }

   public static boolean isSdcardReadable() {
      String var0 = Environment.getExternalStorageState();
      boolean var1;
      if(!"mounted_ro".equals(var0) && !"mounted".equals(var0)) {
         var1 = false;
      } else {
         var1 = true;
      }

      return var1;
   }

   public static boolean isSdcardWritable() {
      boolean var0;
      if("mounted".equals(Environment.getExternalStorageState())) {
         var0 = true;
      } else {
         var0 = false;
      }

      return var0;
   }

   public static boolean isSystemApp(PackageInfo var0) {
      boolean var1;
      if((1 & var0.applicationInfo.flags) != 0) {
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   public static void makeEventToast(Context var0, String var1, boolean var2) {
      Toast var3;
      if(var2) {
         var3 = Toast.makeText(var0, "", 1);
      } else {
         var3 = Toast.makeText(var0, "", 0);
      }

      View var4 = LayoutInflater.from(var0).inflate(2130903126, (ViewGroup)null);
      ((TextView)var4.findViewById(2131493032)).setText(var1);
      var3.setView(var4);
      var3.show();
   }

   public static RecommendTopic mapToTopic(HashMap<String, Object> var0) {
      RecommendTopic var1 = new RecommendTopic();
      var1.id = (String)var0.get("id");
      var1.icon = (String)var0.get("icon_url");
      var1.title = (String)var0.get("name");
      var1.description = (String)var0.get("description");
      var1.up = getInt((String)var0.get("like"));
      var1.down = getInt((String)var0.get("dislike"));
      var1.experience = (String)var0.get("experience");
      var1.user = (String)var0.get("user");
      var1.fans = getInt((String)var0.get("fans"));
      return var1;
   }

   public static void onError(Context var0) {
      UncaughtExceptionHandler var1 = Thread.getDefaultUncaughtExceptionHandler();
      if(!(var1 instanceof DefaultExceptionHandler)) {
         Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(var1, var0));
      }

   }

   public static void openApk(Context var0, String var1) {
      Intent var2 = var0.getPackageManager().getLaunchIntentForPackage(var1);
      if(var2 == null) {
         makeEventToast(var0, var0.getString(2131296387), false);
      } else {
         try {
            var0.startActivity(var2);
         } catch (ActivityNotFoundException var4) {
            makeEventToast(var0, var0.getString(2131296387), false);
         }
      }

   }

   public static HashMap<String, String> parserUri(Uri var0) {
      HashMap var1 = new HashMap();
      String[] var2 = var0.getQuery().split("&");
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if(var5.indexOf("=") == -1) {
            var1 = null;
            break;
         }

         String[] var6 = var5.split("=");
         var1.put(var6[0], var6[1]);
      }

      return var1;
   }

   public static void share(Context var0, String var1, String var2) {
      Intent var3 = new Intent("android.intent.action.SEND");
      var3.setType("text/plain");
      String var5 = String.format("我从机锋市场下载了“%s”，这里有超过近10万的免费Android软件和游戏，快来一起玩吧。", new Object[]{var1});
      String var6 = String.format("http://apk.gfan.com/Product/App%s.html", new Object[]{var2});
      var3.putExtra("android.intent.extra.TEXT", var5 + var6);
      var0.startActivity(Intent.createChooser(var3, "分享"));
   }

   public static void submitDownloadLog(Context var0, int var1, int var2, String var3, String var4) {
      String var5;
      if(var2 == 0) {
         var5 = "0";
      } else if(var2 == 2) {
         var5 = "p";
      } else if(var2 == 1) {
         var5 = "b";
      } else if(var2 == 3) {
         var5 = "o";
      } else {
         var5 = "0";
      }

      String var6 = "";
      String var7;
      if(Helper.isNetworkAvailable(var0)) {
         int var8 = Helper.getActiveNetworkType(var0).intValue();
         if(var8 == 1) {
            var6 = "network is wifi";
         } else if(var8 == 0) {
            var6 = "network is mobile [Carrier 2.0]->" + getCarrier(var0);
         }

         var7 = var6;
      } else {
         var7 = "network is not available";
      }

      MarketAPI.submitDownloadLog(var0, (ApiAsyncTask.ApiRequestListener)null, "", var5, var3, var7, var1, var4);
   }

	public static String submitLogs() {
		BufferedReader bufferedreader = null;
		Runtime runtime = Runtime.getRuntime();
		String as[] = new String[3];
		as[0] = "logcat";
		as[1] = "-d";
		as[2] = "\u673A\u950B\u5E02\u573A:v";
		try {
			bufferedreader = new BufferedReader(new InputStreamReader(runtime
					.exec(as).getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		StringBuilder stringbuilder = new StringBuilder();;
		String s1 = System.getProperty("line.separator");
		String s2 = null;
		try {
			while ((s2 = bufferedreader.readLine()) != null) {
				stringbuilder.append(s2);
				stringbuilder.append(s1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String s = stringbuilder.toString();
		if (bufferedreader != null)
			try {
				bufferedreader.close();
			}
			// Misplaced declaration of an exception variable
			catch (IOException ioexception4) {
			}
		return s;
	}

   public static void trackEvent(Context var0, String ... var1) {
      if(var1 != null && var1.length == 2) {
         MobclickAgent.onEvent(var0, var1[0], var1[1]);
      }

   }

   public static void uninstallApk(Context var0, String var1) {
      Intent var2 = new Intent("android.intent.action.DELETE", Uri.parse("package:" + var1));
      var2.setFlags(268435456);
      var0.startActivity(var2);
   }
}
