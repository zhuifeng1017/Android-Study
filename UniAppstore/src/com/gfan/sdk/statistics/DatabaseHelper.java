package com.gfan.sdk.statistics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHelper extends SQLiteOpenHelper {

   private static final String database_name = "InfoDB.db";
   private static final int database_version = 6;


   DatabaseHelper(Context var1) {
      super(var1, "InfoDB.db", (CursorFactory)null, 6);
   }

   public void onCreate(SQLiteDatabase var1) {
      var1.execSQL("create table appbase(id integer primary key autoincrement,clickname text,clickcount integer, app_start_time integer)");
      var1.execSQL("create table app_backup_base(id integer primary key autoincrement, version text,starttime integer,endtime integer,timesum integer,mac text,cpidmac text,opid text,sdk_version text,sdk_type text)");
      var1.execSQL("create table ridbase(id integer primary key autoincrement,rid text)");
      var1.execSQL("create table app_backup_start(id integer primary key autoincrement, version text,starttime integer,mac text,cpidmac text,opid text,sdk_version text,sdk_type text)");
   }

   public void onUpgrade(SQLiteDatabase var1, int var2, int var3) {
      var1.execSQL("drop table if exists appbase");
      var1.execSQL("drop table if exists app_backup_base");
      var1.execSQL("drop table if exists ridbase");
      var1.execSQL("drop table if exists app_backup_start");
      this.onCreate(var1);
   }
}
