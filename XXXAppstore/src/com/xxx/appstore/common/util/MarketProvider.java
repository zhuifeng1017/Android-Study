package com.xxx.appstore.common.util;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MarketProvider extends ContentProvider {

   public static final Uri BUY_CONTENT_URI;
   private static final int BUY_PRODUCT = 5;
   private static final int CARD = 6;
   public static final Uri CARD_CONTENT_URI;
   public static final String COLUMN_CARD_ACCOUNTNUM = "card_account_num";
   public static final String COLUMN_CARD_CREDIT = "card_credit";
   public static final String COLUMN_CARD_NAME = "card_name";
   public static final String COLUMN_CARD_PASSWORDNUM = "card_password_num";
   public static final String COLUMN_CARD_PAY_TYPE = "card_pay_type";
   public static final String COLUMN_CHECKED = "checked";
   public static final String COLUMN_CONTENT = "content";
   public static final String COLUMN_DESCRPTION = "description";
   public static final String COLUMN_ID = "_id";
   public static final String COLUMN_LEVEL = "level";
   public static final String COLUMN_MODULE = "module";
   public static final String COLUMN_NETWORK = "network";
   public static final String COLUMN_NID = "nid";
   public static final String COLUMN_P_CATEGORY = "p_category";
   public static final String COLUMN_P_ICON_URL = "p_icon_url";
   public static final String COLUMN_P_ID = "p_id";
   public static final String COLUMN_P_IGNORE = "p_update_ingore";
   public static final String COLUMN_P_IS_RECOMMEND = "p_is_recommend";
   public static final String COLUMN_P_NAME = "p_name";
   public static final String COLUMN_P_NEW_VERSION_CODE = "p_new_version_code";
   public static final String COLUMN_P_NEW_VERSION_NAME = "p_new_version_name";
   public static final String COLUMN_P_PACKAGE_NAME = "p_package_name";
   public static final String COLUMN_P_PAYMENT_TYPE = "p_payment_type";
   public static final String COLUMN_P_PRICE = "p_price";
   public static final String COLUMN_P_SIGNATURE = "p_signature";
   public static final String COLUMN_P_SIZE = "p_size";
   public static final String COLUMN_RULE = "rule";
   public static final String COLUMN_SEARCH_KEY_WORD = "keyword";
   public static final String COLUMN_TIME = "create_time";
   public static final String COLUMN_TITLE = "title";
   public static final String COLUMN_UPDATE_TIME = "update_time";
   private static final String DB_NAME = "market.db";
   private static final int DB_VERSION = 91;
   private static final String ITEM_TYPE = "vnd.android.cursor.item/";
   private static final String LIST_TYPE = "vnd.android.cursor.dir/";
   private static final int LOG = 7;
   public static final Uri LOG_CONTENT_URI;
   private static final int PRODUCTS = 3;
   public static final Uri PRODUCTS_CONTENT_URI;
   private static final int PUSH = 8;
   public static final Uri PUSH_CONTENT_URI;
   public static final Uri SEARCH_CONTENT_URI;
   private static final int SEARCH_HISTORY = 1;
   private static final int SEARCH_HISTORY_ID = 2;
   public static final String TABLE_BUY = "buy";
   public static final String TABLE_CARD = "card";
   public static final String TABLE_LOG = "log";
   public static final String TABLE_PRODUCTS = "products";
   public static final String TABLE_PUSH = "push";
   public static final String TABLE_SEARCH_HISTORY = "search_history";
   public static final String TABLE_UPDATES = "updates";
   public static final Uri UPDATE_CONTENT_URI;
   private static final int UPDATE_PRODUCT = 4;
   private static final UriMatcher sURIMatcher = new UriMatcher(-1);
   private SQLiteOpenHelper mOpenHelper = null;


   static {
      sURIMatcher.addURI("gfan_xxx", "search_history", 1);
      sURIMatcher.addURI("gfan_xxx", "search_history/#", 2);
      sURIMatcher.addURI("gfan_xxx", "products", 3);
      sURIMatcher.addURI("gfan_xxx", "updates", 4);
      sURIMatcher.addURI("gfan_xxx", "buy", 5);
      sURIMatcher.addURI("gfan_xxx", "card", 6);
      sURIMatcher.addURI("gfan_xxx", "log", 7);
      sURIMatcher.addURI("gfan_xxx", "push", 8);
      SEARCH_CONTENT_URI = Uri.parse("content://gfan_xxx/search_history");
      PRODUCTS_CONTENT_URI = Uri.parse("content://gfan_xxx/products");
      UPDATE_CONTENT_URI = Uri.parse("content://gfan_xxx/updates");
      BUY_CONTENT_URI = Uri.parse("content://gfan_xxx/buy");
      CARD_CONTENT_URI = Uri.parse("content://gfan_xxx/card");
      LOG_CONTENT_URI = Uri.parse("content://gfan_xxx/log");
      PUSH_CONTENT_URI = Uri.parse("content://gfan_xxx/push");
   }

   private static String getTableFromUri(Uri var0) {
      return (String)var0.getPathSegments().get(0);
   }

   private static MarketProvider.SqlSelection getWhereClause(Uri var0, String var1, String[] var2) {
      MarketProvider.SqlSelection var3 = new MarketProvider.SqlSelection(null);
      var3.appendClause(var1, var2);
      return var3;
   }

   private static void logVerboseQueryInfo(String[] var0, String var1, String[] var2, String var3, SQLiteDatabase var4) {
      StringBuilder var5 = new StringBuilder();
      var5.append("starting query, database is ");
      if(var4 != null) {
         var5.append("not ");
      }

      var5.append("null; ");
      if(var0 == null) {
         var5.append("projection is null; ");
      } else if(var0.length == 0) {
         var5.append("projection is empty; ");
      } else {
         for(int var8 = 0; var8 < var0.length; ++var8) {
            var5.append("projection[");
            var5.append(var8);
            var5.append("] is ");
            var5.append(var0[var8]);
            var5.append("; ");
         }
      }

      var5.append("selection is ");
      var5.append(var1);
      var5.append("; ");
      if(var2 == null) {
         var5.append("selectionArgs is null; ");
      } else if(var2.length == 0) {
         var5.append("selectionArgs is empty; ");
      } else {
         for(int var17 = 0; var17 < var2.length; ++var17) {
            var5.append("selectionArgs[");
            var5.append(var17);
            var5.append("] is ");
            var5.append(var2[var17]);
            var5.append("; ");
         }
      }

      var5.append("sort is ");
      var5.append(var3);
      var5.append(".");
      Utils.D(var5.toString());
   }

   private void notifyContentChanged(Uri var1, int var2) {
      this.getContext().getContentResolver().notifyChange(var1, (ContentObserver)null);
   }

   public int delete(Uri var1, String var2, String[] var3) {
      int var4 = sURIMatcher.match(var1);
      SQLiteDatabase var5 = this.mOpenHelper.getWritableDatabase();
      String var6 = getTableFromUri(var1);
      MarketProvider.SqlSelection var7 = getWhereClause(var1, var2, var3);
      int var8 = var5.delete(var6, var7.getSelection(), var7.getParameters());
      int var9;
      if(var8 == 0) {
         Utils.D("couldn\'t delete URI " + var1);
         var9 = var8;
      } else {
         this.notifyContentChanged(var1, var4);
         var9 = var8;
      }

      return var9;
   }

   public String getType(Uri var1) {
      String var2;
      switch(sURIMatcher.match(var1)) {
      case 1:
         var2 = "vnd.android.cursor.dir/search_history";
         break;
      case 2:
         var2 = "vnd.android.cursor.item/search_history";
         break;
      case 3:
         var2 = "vnd.android.cursor.dir/products";
         break;
      case 4:
         var2 = "vnd.android.cursor.dir/updates";
         break;
      case 5:
         var2 = "vnd.android.cursor.dir/buy";
         break;
      case 6:
         var2 = "vnd.android.cursor.dir/card";
         break;
      case 7:
      default:
         var2 = null;
         break;
      case 8:
         var2 = "vnd.android.cursor.dir/push";
      }

      return var2;
   }

   public Uri insert(Uri var1, ContentValues var2) {
      int var3 = sURIMatcher.match(var1);
      SQLiteDatabase var4 = this.mOpenHelper.getWritableDatabase();
      String var5 = getTableFromUri(var1);
      long var6 = var4.insert(var5, (String)null, var2);
      Uri var9;
      if(var6 == -1L) {
         Utils.D("couldn\'t insert into " + var5 + " database");
         var9 = null;
      } else {
         Uri var8 = ContentUris.withAppendedId(var1, var6);
         this.notifyContentChanged(var1, var3);
         var9 = var8;
      }

      return var9;
   }

   public boolean onCreate() {
      this.mOpenHelper = new MarketProvider.DatabaseHelper(this.getContext());
      return true;
   }

   public Cursor query(Uri var1, String[] var2, String var3, String[] var4, String var5) {
      SQLiteDatabase var6 = this.mOpenHelper.getReadableDatabase();
      if(sURIMatcher.match(var1) == -1) {
         Utils.D("querying unknown URI: " + var1);
         throw new IllegalArgumentException("Unknown URI: " + var1);
      } else {
         MarketProvider.SqlSelection var7 = getWhereClause(var1, var3, var4);
         logVerboseQueryInfo(var2, var3, var4, var5, var6);
         String var8 = getTableFromUri(var1);

         Cursor var10;
         label19: {
            Cursor var11;
            try {
               var11 = var6.query(var8, var2, var7.getSelection(), var7.getParameters(), (String)null, (String)null, var5);
            } catch (SQLiteException var12) {
               Utils.E("query error", var12);
               var10 = null;
               break label19;
            }

            var10 = var11;
         }

         if(var10 == null) {
            Utils.D("query failed in market database");
         }

         return var10;
      }
   }

   public int update(Uri var1, ContentValues var2, String var3, String[] var4) {
      SQLiteDatabase var5 = this.mOpenHelper.getReadableDatabase();
      if(sURIMatcher.match(var1) == -1) {
         Utils.D("updating unknown URI: " + var1);
         throw new IllegalArgumentException("Unknown URI: " + var1);
      } else {
         return var5.update(getTableFromUri(var1), var2, var3, var4);
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

   private final class DatabaseHelper extends SQLiteOpenHelper {

      public DatabaseHelper(Context var2) {
         super(var2, "market.db", (CursorFactory)null, 91);
      }

      private void createCardTable(SQLiteDatabase var1) {
         try {
            var1.execSQL("DROP TABLE IF EXISTS card");
            var1.execSQL("CREATE TABLE card (_id INTEGER PRIMARY KEY AUTOINCREMENT,card_name TEXT,card_pay_type TEXT,card_account_num INTEGER,card_password_num INTEGER ,card_credit TEXT);");
         } catch (SQLException var3) {
            Utils.D("couldn\'t create card table in market database");
            throw var3;
         }
      }

      private void createLogTable(SQLiteDatabase var1) {
         try {
            var1.execSQL("DROP TABLE IF EXISTS log");
            var1.execSQL("CREATE TABLE log (_id INTEGER PRIMARY KEY AUTOINCREMENT,module TEXT,level INTEGER,content TEXT,network TEXT,create_time INTEGER);");
         } catch (SQLException var3) {
            Utils.D("couldn\'t create log table in market database");
            throw var3;
         }
      }

      private void createPurchesdTable(SQLiteDatabase var1) {
         try {
            var1.execSQL("DROP TABLE IF EXISTS buy");
            var1.execSQL("CREATE TABLE buy (_id INTEGER PRIMARY KEY AUTOINCREMENT ,p_id TEXT ,p_package_name TEXT);");
         } catch (SQLException var3) {
            Utils.D("couldn\'t create buy table in market database");
            throw var3;
         }
      }

      private void createPushAppTable(SQLiteDatabase var1) {
         try {
            var1.execSQL("DROP TABLE IF EXISTS push");
            var1.execSQL("CREATE TABLE push(_id INTEGER PRIMARY KEY AUTOINCREMENT, p_id TEXT,title TEXT,description TEXT, update_time TEXT, nid TEXT, rule TEXT, checked INTEGER);");
         } catch (SQLException var3) {
            Utils.D("couldn\'t create search_history table in market database");
            throw var3;
         }
      }

      private void createSearchHistoryTable(SQLiteDatabase var1) {
         try {
            var1.execSQL("DROP TABLE IF EXISTS search_history");
            var1.execSQL("CREATE TABLE search_history(_id INTEGER PRIMARY KEY AUTOINCREMENT, keyword TEXT);");
         } catch (SQLException var3) {
            Utils.D("couldn\'t create search_history table in market database");
            throw var3;
         }
      }

      private void createUpdateTable(SQLiteDatabase var1) {
         try {
            var1.execSQL("DROP TABLE IF EXISTS updates");
            var1.execSQL("CREATE TABLE updates(_id INTEGER PRIMARY KEY AUTOINCREMENT, p_id TEXT, p_package_name TEXT, p_new_version_name TEXT, p_new_version_code TEXT, p_signature TEXT, p_update_ingore INTEGER);");
         } catch (SQLException var3) {
            Utils.D("couldn\'t create updates table in market database");
            throw var3;
         }
      }

      public void onCreate(SQLiteDatabase var1) {
         Utils.D("create the new database...");
         this.onUpgrade(var1, 0, 91);
      }

      public void onUpgrade(SQLiteDatabase var1, int var2, int var3) {
         Utils.D("update the database...");
         if(var2 < var3) {
            Session.get(MarketProvider.this.getContext()).setUpdataCheckTime(0L);
            Session.get(MarketProvider.this.getContext()).setUpgradeNumber(0);
            var1.execSQL("DROP TABLE IF EXISTS installed");
         }

         this.createSearchHistoryTable(var1);
         this.createUpdateTable(var1);
         this.createPurchesdTable(var1);
         this.createCardTable(var1);
         this.createLogTable(var1);
         this.createPushAppTable(var1);
      }
   }
}
