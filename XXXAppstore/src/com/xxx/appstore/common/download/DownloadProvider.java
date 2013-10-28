package com.xxx.appstore.common.download;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.download.DownloadService;
import com.xxx.appstore.common.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class DownloadProvider extends ContentProvider {

   private static final String DB_NAME = "downloads.db";
   private static final String DB_TABLE = "downloads";
   private static final int DB_VERSION = 109;
   private static final String DOWNLOAD_LIST_TYPE = "vnd.android.cursor.dir/download";
   private static final String DOWNLOAD_TYPE = "vnd.android.cursor.item/download";
   private static final int MY_DOWNLOADS = 1;
   private static final int MY_DOWNLOADS_ID = 2;
   private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
   private SQLiteOpenHelper mOpenHelper = null;


   static {
      sURIMatcher.addURI("gfan_downloads_xxx", "my_downloads", 1);
      sURIMatcher.addURI("gfan_downloads_xxx", "my_downloads/#", 2);
   }

   private static final void copyInteger(String var0, ContentValues var1, ContentValues var2) {
      Integer var3 = var1.getAsInteger(var0);
      if(var3 != null) {
         var2.put(var0, var3);
      }

   }

   private static final void copyString(String var0, ContentValues var1, ContentValues var2) {
      String var3 = var1.getAsString(var0);
      if(var3 != null) {
         var2.put(var0, var3);
      }

   }

   private static final void copyStringWithDefault(String var0, ContentValues var1, ContentValues var2, String var3) {
      copyString(var0, var1, var2);
      if(!var2.containsKey(var0)) {
         var2.put(var0, var3);
      }

   }

   private String getDownloadIdFromUri(Uri var1) {
      return (String)var1.getPathSegments().get(1);
   }

   private DownloadProvider.SqlSelection getWhereClause(Uri var1, String var2, String[] var3, int var4) {
      DownloadProvider.SqlSelection var5 = new DownloadProvider.SqlSelection(null);
      var5.appendClause(var2, var3);
      if(var4 == 2) {
         String[] var6 = new String[]{this.getDownloadIdFromUri(var1)};
         var5.appendClause("_id = ?", var6);
      }

      return var5;
   }

   private void logVerboseQueryInfo(String[] var1, String var2, String[] var3, String var4, SQLiteDatabase var5) {
      StringBuilder var6 = new StringBuilder();
      var6.append("starting query, database is ");
      if(var5 != null) {
         var6.append("not ");
      }

      var6.append("null; ");
      if(var1 == null) {
         var6.append("projection is null; ");
      } else if(var1.length == 0) {
         var6.append("projection is empty; ");
      } else {
         for(int var9 = 0; var9 < var1.length; ++var9) {
            var6.append("projection[");
            var6.append(var9);
            var6.append("] is ");
            var6.append(var1[var9]);
            var6.append("; ");
         }
      }

      var6.append("selection is ");
      var6.append(var2);
      var6.append("; ");
      if(var3 == null) {
         var6.append("selectionArgs is null; ");
      } else if(var3.length == 0) {
         var6.append("selectionArgs is empty; ");
      } else {
         for(int var18 = 0; var18 < var3.length; ++var18) {
            var6.append("selectionArgs[");
            var6.append(var18);
            var6.append("] is ");
            var6.append(var3[var18]);
            var6.append("; ");
         }
      }

      var6.append("sort is ");
      var6.append(var4);
      var6.append(".");
      Utils.V(var6.toString());
   }

   private void notifyContentChanged(Uri var1, int var2) {
      Long var3;
      if(var2 == 2) {
         var3 = Long.valueOf(Long.parseLong(this.getDownloadIdFromUri(var1)));
      } else {
         var3 = null;
      }

      Uri var4 = DownloadManager.Impl.CONTENT_URI;
      Uri var5;
      if(var3 != null) {
         var5 = ContentUris.withAppendedId(var4, var3.longValue());
      } else {
         var5 = var4;
      }

      this.getContext().getContentResolver().notifyChange(var5, (ContentObserver)null);
   }

   public int delete(Uri var1, String var2, String[] var3) {
      SQLiteDatabase var4 = this.mOpenHelper.getWritableDatabase();
      int var5 = sURIMatcher.match(var1);
      switch(var5) {
      case 1:
      case 2:
         DownloadProvider.SqlSelection var6 = this.getWhereClause(var1, var2, var3, var5);
         int var7 = var4.delete(DB_TABLE, var6.getSelection(), var6.getParameters());
         this.notifyContentChanged(var1, var5);
         return var7;
      default:
         Utils.D("deleting unknown/invalid URI: " + var1);
         throw new UnsupportedOperationException("Cannot delete URI: " + var1);
      }
   }

   public String getType(Uri var1) {
      String var2;
      switch(sURIMatcher.match(var1)) {
      case 1:
         var2 = "vnd.android.cursor.dir/download";
         break;
      case 2:
         var2 = "vnd.android.cursor.item/download";
         break;
      default:
         Utils.D("calling getType on an unknown URI: " + var1);
         throw new IllegalArgumentException("Unknown URI: " + var1);
      }

      return var2;
   }

   public Uri insert(Uri var1, ContentValues var2) {
      SQLiteDatabase var3 = this.mOpenHelper.getWritableDatabase();
      ContentValues var4 = new ContentValues();
      copyString("uri", var2, var4);
      copyString("entity", var2, var4);
      copyString("hint", var2, var4);
      copyString("mimetype", var2, var4);
      copyString("package_name", var2, var4);
      copyString("md5", var2, var4);
      copyInteger("destination", var2, var4);
      copyInteger("visibility", var2, var4);
      copyInteger("control", var2, var4);
      copyInteger("source", var2, var4);
      copyInteger("allow_network", var2, var4);
      var4.put("status", Integer.valueOf(190));
      var4.put("lastmod", Long.valueOf(System.currentTimeMillis()));
      String var5 = var2.getAsString("notificationpackage");
      String var6 = var2.getAsString("notificationclass");
      if(var5 != null) {
         var4.put("notificationpackage", var5);
         if(var6 != null) {
            var4.put("notificationclass", var6);
         }
      }

      copyString("notificationextras", var2, var4);
      copyStringWithDefault("title", var2, var4, "");
      copyStringWithDefault("description", var2, var4, "");
      var4.put("total_bytes", Integer.valueOf(-1));
      var4.put("current_bytes", Integer.valueOf(0));
      Context var7 = this.getContext();
      var7.startService(new Intent(var7, DownloadService.class));
      long var9 = var3.insert(DB_TABLE, (String)null, var4);
      Uri var12;
      if(var9 == -1L) {
         Utils.D("couldn\'t insert into downloads database");
         var12 = null;
      } else {
         var7.startService(new Intent(var7, DownloadService.class));
         this.notifyContentChanged(var1, sURIMatcher.match(var1));
         var12 = ContentUris.withAppendedId(DownloadManager.Impl.CONTENT_URI, var9);
      }

      return var12;
   }

   public boolean onCreate() {
      this.mOpenHelper = new DownloadProvider.DatabaseHelper(this.getContext());
      return true;
   }

   public Cursor query(Uri var1, String[] var2, String var3, String[] var4, String var5) {
      SQLiteDatabase var6 = this.mOpenHelper.getReadableDatabase();
      int var7 = sURIMatcher.match(var1);
      if(var7 == -1) {
         Utils.D("querying unknown URI: " + var1);
         throw new IllegalArgumentException("Unknown URI: " + var1);
      } else {
         DownloadProvider.SqlSelection var8 = this.getWhereClause(var1, var3, var4, var7);
         this.logVerboseQueryInfo(var2, var3, var4, var5, var6);
         Object var9 = var6.query(DB_TABLE, var2, var8.getSelection(), var8.getParameters(), (String)null, (String)null, var5);
         if(var9 != null) {
            DownloadProvider.ReadOnlyCursorWrapper var10 = new DownloadProvider.ReadOnlyCursorWrapper((Cursor)var9);
            var9 = var10;
         }

         if(var9 != null) {
            ContentResolver var11 = this.getContext().getContentResolver();
            ((Cursor)var9).setNotificationUri(var11, var1);
         } else {
            Utils.D("query failed in downloads database");
         }

         return (Cursor)var9;
      }
   }

   public int update(Uri var1, ContentValues var2, String var3, String[] var4) {
      SQLiteDatabase var5 = this.mOpenHelper.getWritableDatabase();
      boolean var6;
      if(var2.containsKey("deleted") && var2.getAsInteger("deleted").intValue() == 1) {
         var6 = true;
      } else {
         var6 = false;
      }

      String var7 = var2.getAsString("_data");
      if(var7 != null) {
         Cursor var16 = this.query(var1, new String[]{"title"}, (String)null, (String[])null, (String)null);
         if(!var16.moveToFirst() || TextUtils.isEmpty(var16.getString(0))) {
            var2.put("title", (new File(var7)).getName());
         }

         var16.close();
      }

      Integer var8 = var2.getAsInteger("status");
      boolean var9;
      if(var8 != null && var8.intValue() == 190) {
         var9 = true;
      } else {
         var9 = false;
      }

      boolean var10;
      if(var9) {
         var10 = true;
      } else {
         var10 = var6;
      }

      int var11 = sURIMatcher.match(var1);
      switch(var11) {
      case 1:
      case 2:
         DownloadProvider.SqlSelection var12 = this.getWhereClause(var1, var3, var4, var11);
         int var13;
         if(var2.size() > 0) {
            Utils.D("update database values  : " + var2);
            var13 = var5.update(DB_TABLE, var2, var12.getSelection(), var12.getParameters());
            if(var13 > 0) {
               var10 = true;
            }
         } else {
            var13 = 0;
         }

         this.notifyContentChanged(var1, var11);
         if(var10) {
            Context var14 = this.getContext();
            var14.startService(new Intent(var14, DownloadService.class));
         }

         return var13;
      default:
         Utils.D("updating unknown/invalid URI: " + var1);
         throw new UnsupportedOperationException("Cannot update URI: " + var1);
      }
   }

   private final class DatabaseHelper extends SQLiteOpenHelper {

      public DatabaseHelper(Context var2) {
         super(var2, DB_NAME, (CursorFactory)null, 109);
      }

      private void createDownloadsTable(SQLiteDatabase var1) {
         try {
            var1.execSQL("DROP TABLE IF EXISTS downloads");
            var1.execSQL("CREATE TABLE downloads(_id INTEGER PRIMARY KEY AUTOINCREMENT,uri TEXT, redirectcount INTEGER, entity TEXT, hint TEXT, _data TEXT, mimetype TEXT, destination INTEGER, visibility INTEGER, control INTEGER, status INTEGER, numfailed INTEGER, lastmod BIGINT, notificationpackage TEXT, notificationclass TEXT, notificationextras TEXT, total_bytes INTEGER DEFAULT -1, current_bytes INTEGER DEFAULT 0, etag TEXT, md5 TEXT, package_name TEXT, allow_network INTEGER, title TEXT, description TEXT, deleted BOOLEAN NOT NULL DEFAULT 0, source INTEGER);");
         } catch (SQLException var3) {
            Utils.E("couldn\'t create table in downloads database");
            throw var3;
         }
      }

      public void onCreate(SQLiteDatabase var1) {
         Utils.D("populating new database");
         this.onUpgrade(var1, 0, 109);
      }

      public void onUpgrade(SQLiteDatabase var1, int var2, int var3) {
         this.createDownloadsTable(var1);
      }
   }

   private static class SqlSelection {

      public List<String> mParameters;
      public StringBuilder mWhereClause;


      private SqlSelection() {
         this.mWhereClause = new StringBuilder();
         this.mParameters = new ArrayList();
      }

      // $FF: synthetic method
      SqlSelection(Object var1) {
         this();
      }

      public <T extends Object> void appendClause(String var1, T ... var2) {
         if(!TextUtils.isEmpty(var1)) {
            if(this.mWhereClause.length() != 0) {
               this.mWhereClause.append(" AND ");
            }

            this.mWhereClause.append("(");
            this.mWhereClause.append(var1);
            this.mWhereClause.append(")");
            if(var2 != null) {
               int var6 = var2.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  Object var8 = var2[var7];
                  this.mParameters.add(var8.toString());
               }
            }
         }

      }

      public String[] getParameters() {
         String[] var1 = new String[this.mParameters.size()];
         return (String[])this.mParameters.toArray(var1);
      }

      public String getSelection() {
         return this.mWhereClause.toString();
      }
   }

   private class ReadOnlyCursorWrapper extends CursorWrapper implements CrossProcessCursor {

      private CrossProcessCursor mCursor;


      public ReadOnlyCursorWrapper(Cursor var2) {
         super(var2);
         this.mCursor = (CrossProcessCursor)var2;
      }

      public void fillWindow(int var1, CursorWindow var2) {
         this.mCursor.fillWindow(var1, var2);
      }

      public CursorWindow getWindow() {
         return this.mCursor.getWindow();
      }

      public boolean onMove(int var1, int var2) {
         return this.mCursor.onMove(var1, var2);
      }
   }
}
