package net.oschina.app.common;
import android.util.Log;

public class LogUtils {
	   public static boolean sDebug = true;
	   public static String sLogTag = "oshina-log";
	   
	   public static void D(String str) {
	      if(sDebug) {
	         Log.d(sLogTag, str);
	      }
	   }

	   public static void D(String str, Throwable throwable) {
	      if(sDebug) {
	         Log.d(sLogTag, str, throwable);
	      }
	   }

	   public static void E(String str) {
	      if(sDebug) {
	         Log.e(sLogTag, str);
	      }
	   }

	   public static void E(String str, Throwable throwable) {
	      if(sDebug) {
	         Log.e(sLogTag, str, throwable);
	      }
	   }

	   public static void I(String str) {
	      if(sDebug) {
	         Log.i(sLogTag, str);
	      }
	   }

	   public static void I(String str, Throwable throwable) {
	      if(sDebug) {
	         Log.i(sLogTag, str, throwable);
	      }
	   }

	   public static void V(String str) {
	      if(sDebug) {
	         Log.v(sLogTag, str);
	      }
	   }

	   public static void V(String str, Throwable var1) {
	      if(sDebug) {
	         Log.v(sLogTag, str, var1);
	      }
	   }

	   public static void W(String str) {
	      if(sDebug) {
	         Log.w(sLogTag, str);
	      }
	   }

	   public static void W(String str, Throwable throwable) {
	      if(sDebug) {
	         Log.w(sLogTag, str, throwable);
	      }
	   }
}
