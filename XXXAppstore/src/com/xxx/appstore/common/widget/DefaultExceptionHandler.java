package com.xxx.appstore.common.widget;

import android.content.Context;

import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.Pair;
import com.xxx.appstore.common.vo.LogEntity;

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

   public void uncaughtException(Thread var1, Throwable var2) {
      StringWriter var3 = new StringWriter();
      var2.printStackTrace(new PrintWriter(var3));
      LogEntity var4 = new LogEntity(this.context, "crash_mobile", 5);
      var4.addLogContent(new Pair("exception", var3.toString()));
      DBUtils.insertLog(this.context, var4);
      this.defaultExceptionHandler.uncaughtException(var1, var2);
   }
}
