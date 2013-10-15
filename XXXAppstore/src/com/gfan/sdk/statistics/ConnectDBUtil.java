package com.gfan.sdk.statistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.gfan.sdk.statistics.DatabaseHelper;
import java.util.Iterator;
import java.util.Vector;

public class ConnectDBUtil {

   private static Vector<ConnectDBUtil> connects = new Vector();
   private SQLiteDatabase db;
   private DatabaseHelper mDatabaseHelper;


   private ConnectDBUtil(Context var1) {
      this.mDatabaseHelper = new DatabaseHelper(var1);
      this.db = this.mDatabaseHelper.getWritableDatabase();
   }

   public static ConnectDBUtil getConnection(Context var0) {
      synchronized(ConnectDBUtil.class){}
      boolean var8 = false;

      ConnectDBUtil var5;
      label57: {
         ConnectDBUtil var3;
         label56: {
            ConnectDBUtil var6;
            try {
               var8 = true;
               Iterator var2 = connects.iterator();

               do {
                  if(!var2.hasNext()) {
                     var3 = new ConnectDBUtil(var0);
                     connects.add(var3);
                     var8 = false;
                     break label56;
                  }

                  var6 = (ConnectDBUtil)var2.next();
               } while(var6.db.isOpen());

               var6.db = var6.mDatabaseHelper.getWritableDatabase();
               var8 = false;
            } finally {
               if(var8) {
                  ;
               }
            }

            var5 = var6;
            break label57;
         }

         var5 = var3;
      }

      return var5;
   }

   public boolean AppClear(long var1) {
      synchronized(this){}
      boolean var7 = false;

      int var4;
      try {
         var7 = true;
         var4 = this.db.delete("appbase", "app_start_time=" + var1, (String[])null);
         var7 = false;
      } finally {
         if(var7) {
            ;
         }
      }

      boolean var5;
      if(var4 > 0) {
         var5 = true;
      } else {
         var5 = false;
      }

      return var5;
   }

   public long AppInsert(String var1, long var2, int var4) {
      synchronized(this){}

      long var7;
      try {
         ContentValues var5 = new ContentValues();
         var5.put("clickname", var1);
         var5.put("clickcount", Integer.valueOf(var4));
         var5.put("app_start_time", Long.valueOf(var2));
         var7 = this.db.insert("appbase", (String)null, var5);
      } finally {
         ;
      }

      return var7;
   }

   public Cursor AppSelect(long var1) {
      synchronized(this){}

      Cursor var4;
      try {
         var4 = this.db.query("appbase", new String[]{"clickname", "clickcount", "app_start_time"}, "app_start_time=" + var1, (String[])null, (String)null, (String)null, (String)null);
      } finally {
         ;
      }

      return var4;
   }

   public Cursor AppSelectClickname(String var1, long var2) {
      synchronized(this){}

      Cursor var5;
      try {
         var5 = this.db.query("appbase", new String[]{"clickcount"}, "clickname =\'" + var1 + "\' and app_start_time=" + var2, (String[])null, (String)null, (String)null, (String)null);
      } finally {
         ;
      }

      return var5;
   }

   public long AppUpdate(String var1, long var2, int var4) {
      synchronized(this){}
      boolean var11 = false;

      int var7;
      try {
         var11 = true;
         ContentValues var5 = new ContentValues();
         var5.put("clickcount", Integer.valueOf(var4));
         var7 = this.db.update("appbase", var5, "clickname = \'" + var1 + "\' and app_start_time=" + var2, (String[])null);
         var11 = false;
      } finally {
         if(var11) {
            ;
         }
      }

      long var8 = (long)var7;
      return var8;
   }

   public int BackupAppInfoClear(long var1) {
      synchronized(this){}

      int var4;
      try {
         var4 = this.db.delete("app_backup_base", "id=" + var1, (String[])null);
      } finally {
         ;
      }

      return var4;
   }

   public long BackupAppInfoInsert(String var1, long var2, long var4, long var6, String var8, String var9, String var10, String var11, String var12) {
      synchronized(this){}

      long var15;
      try {
         ContentValues var13 = new ContentValues();
         var13.put("version", var1);
         var13.put("starttime", Long.valueOf(var2));
         var13.put("endtime", Long.valueOf(var4));
         var13.put("timesum", Long.valueOf(var6));
         var13.put("mac", var8);
         var13.put("cpidmac", var9);
         var13.put("opid", var10);
         var13.put("sdk_version", var11);
         var13.put("sdk_type", var12);
         var15 = this.db.insert("app_backup_base", (String)null, var13);
      } finally {
         ;
      }

      return var15;
   }

   public Cursor BackupAppInfoSelect() {
      synchronized(this){}

      Cursor var2;
      try {
         var2 = this.db.query("app_backup_base", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
      } finally {
         ;
      }

      return var2;
   }

   public int BackupStartInfoClear(long var1) {
      synchronized(this){}

      int var4;
      try {
         var4 = this.db.delete("app_backup_start", "id=" + var1, (String[])null);
      } finally {
         ;
      }

      return var4;
   }

   public long BackupStartInfoInsert(String var1, long var2, String var4, String var5, String var6, String var7, String var8) {
      synchronized(this){}

      long var11;
      try {
         ContentValues var9 = new ContentValues();
         var9.put("version", var1);
         var9.put("starttime", Long.valueOf(var2));
         var9.put("mac", var4);
         var9.put("cpidmac", var5);
         var9.put("opid", var6);
         var9.put("sdk_version", var7);
         var9.put("sdk_type", var8);
         var11 = this.db.insert("app_backup_start", (String)null, var9);
      } finally {
         ;
      }

      return var11;
   }

   public Cursor BackupStartInfoSelect() {
      synchronized(this){}

      Cursor var2;
      try {
         var2 = this.db.query("app_backup_start", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
      } finally {
         ;
      }

      return var2;
   }

   public void close() {
      synchronized(this){}

      try {
         if(this.mDatabaseHelper != null) {
            this.mDatabaseHelper.close();
         }
      } finally {
         ;
      }

   }

   public long ridInsert(String var1) {
      synchronized(this){}
      boolean var10 = false;

      long var7;
      label55: {
         long var5;
         try {
            var10 = true;
            Cursor var3 = this.db.query("ridbase", (String[])null, "rid=\'" + var1 + "\'", (String[])null, (String)null, (String)null, (String)null);
            if(var3.getCount() > 0) {
               var3.close();
               var10 = false;
               break label55;
            }

            var3.close();
            ContentValues var4 = new ContentValues();
            var4.put("rid", var1);
            var5 = this.db.insert("ridbase", (String)null, var4);
            var10 = false;
         } finally {
            if(var10) {
               ;
            }
         }

         var7 = var5;
         return var7;
      }

      var7 = -1L;
      return var7;
   }

   public Cursor ridSelect() {
      synchronized(this){}

      Cursor var2;
      try {
         var2 = this.db.query("ridbase", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
      } finally {
         ;
      }

      return var2;
   }
}
