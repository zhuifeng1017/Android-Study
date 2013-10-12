package com.gfan.sdk.statistics;

import android.content.Context;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

   private Context context;
   private UncaughtExceptionHandler defaultExceptionHandler;


   public DefaultExceptionHandler(UncaughtExceptionHandler var1, Context var2) {
      this.defaultExceptionHandler = var1;
      this.context = var2;
   }

   public static void postCustomException(Context var0, Throwable var1) {
      StringWriter var2 = new StringWriter();
      var1.printStackTrace(new PrintWriter(var2));

      try {
         String var4 = String.valueOf(System.currentTimeMillis());
         BufferedWriter var5 = new BufferedWriter(new FileWriter(var0.getFilesDir().getAbsolutePath() + "/" + var4 + ".stacktrace"));
         var5.write(var2.toString());
         var5.flush();
         var5.close();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public void uncaughtException(Thread var1, Throwable var2) {
      StringWriter var3 = new StringWriter();
      var2.printStackTrace(new PrintWriter(var3));

      try {
         String var5 = String.valueOf(System.currentTimeMillis());
         BufferedWriter var6 = new BufferedWriter(new FileWriter(this.context.getFilesDir().getAbsolutePath() + "/" + var5 + ".stacktrace"));
         var6.write(var3.toString());
         var6.flush();
         var6.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      this.defaultExceptionHandler.uncaughtException(var1, var2);
   }
}
