package com.xxx.appstore.common.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import com.alipay.android.app.IAlixPay;
import com.alipay.android.app.IRemoteServiceCallback;

public class MobileSecurePayer {

   static String TAG = "pay";
   Integer lock = Integer.valueOf(0);
   Activity mActivity = null;
   IAlixPay mAlixPay = null;
   private ServiceConnection mAlixPayConnection = new ServiceConnection() {
      public void onServiceConnected(ComponentName var1, IBinder var2) {
         Integer var3 = MobileSecurePayer.this.lock;
         synchronized(var3) {
            MobileSecurePayer.this.mAlixPay = IAlixPay.Stub.asInterface(var2);
            MobileSecurePayer.this.lock.notify();
         }
      }
      public void onServiceDisconnected(ComponentName var1) {
         MobileSecurePayer.this.mAlixPay = null;
      }
   };
   private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
      public void startActivity(String var1, String var2, int var3, Bundle var4) throws RemoteException {
         Intent var5 = new Intent("android.intent.action.MAIN", (Uri)null);
         Bundle var6;
         if(var4 == null) {
            var6 = new Bundle();
         } else {
            var6 = var4;
         }

         try {
            var6.putInt("CallingPid", var3);
            var5.putExtras(var6);
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         var5.setClassName(var1, var2);
         MobileSecurePayer.this.mActivity.startActivity(var5);
      }

	@Override
	public boolean isHideLoadingScreen() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void payEnd(boolean arg0, String arg1) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
   };
   boolean mbPaying = false;


   // $FF: synthetic method
   static IRemoteServiceCallback access$000(MobileSecurePayer var0) {
      return var0.mCallback;
   }

   // $FF: synthetic method
   static ServiceConnection access$100(MobileSecurePayer var0) {
      return var0.mAlixPayConnection;
   }

   public boolean pay(final String var1, final Handler var2, final int var3, Activity var4) {
      boolean var5;
      if(this.mbPaying) {
         var5 = false;
      } else {
         this.mbPaying = true;
         this.mActivity = var4;
         if(this.mAlixPay == null) {
            this.mActivity.bindService(new Intent(IAlixPay.class.getName()), this.mAlixPayConnection, 1);
         }

         (new Thread(new Runnable() {
            public void run() {
               // $FF: Couldn't be decompiled
            }
         })).start();
         var5 = true;
      }

      return var5;
   }
}
