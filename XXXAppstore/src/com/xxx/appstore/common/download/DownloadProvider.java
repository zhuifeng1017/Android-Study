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


/**
 * Allows application to interact with the download manager.
 */
public final class DownloadProvider extends ContentProvider {
    /** Database filename */
   private static final String DB_NAME = "downloads.db";
    /** Name of table in the database */
   private static final String DB_TABLE = "downloads";
    /** Current database version */
   private static final int DB_VERSION = 109;
    /** MIME type for the entire download list */
   private static final String DOWNLOAD_LIST_TYPE = "vnd.android.cursor.dir/download";
    /** MIME type for an individual download */
   private static final String DOWNLOAD_TYPE = "vnd.android.cursor.item/download";

    /** URI matcher used to recognize URIs sent by applications */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /** URI matcher constant for the URI of all downloads belonging to the calling UID */
   private static final int MY_DOWNLOADS = 1;
    /** URI matcher constant for the URI of an individual download belonging to the calling UID */
   private static final int MY_DOWNLOADS_ID = 2;
   private SQLiteOpenHelper mOpenHelper = null;


   static {
      sURIMatcher.addURI("gfan_downloads_xxx", "my_downloads", MY_DOWNLOADS);
      sURIMatcher.addURI("gfan_downloads_xxx", "my_downloads/#", MY_DOWNLOADS_ID);
   }

   private static final void copyInteger(String key, ContentValues from, ContentValues to) {
       Integer i = from.getAsInteger(key);
       if (i != null) {
           to.put(key, i);
       }
   }

   private static final void copyString(String key, ContentValues from, ContentValues to) {
       String s = from.getAsString(key);
       if (s != null) {
           to.put(key, s);
       }
   }

   private static final void copyStringWithDefault(String key, ContentValues from,
           ContentValues to, String defaultValue) {
       copyString(key, from, to);
       if (!to.containsKey(key)) {
           to.put(key, defaultValue);
       }
   }

   private String getDownloadIdFromUri(final Uri uri) {
       return uri.getPathSegments().get(1);
   }

   private SqlSelection getWhereClause(final Uri uri, final String where, final String[] whereArgs,
           int uriMatch) {
       SqlSelection selection = new SqlSelection();
       selection.appendClause(where, whereArgs);
       if (uriMatch == MY_DOWNLOADS_ID) {
           selection.appendClause(DownloadManager.Impl.COLUMN_ID + " = ?", getDownloadIdFromUri(uri));
       }

       return selection;
   }
   
   private void logVerboseQueryInfo(String[] projection, final String selection,
           final String[] selectionArgs, final String sort, SQLiteDatabase db) {
       java.lang.StringBuilder sb = new java.lang.StringBuilder();
       sb.append("starting query, database is ");
       if (db != null) {
           sb.append("not ");
       }
       sb.append("null; ");
       if (projection == null) {
           sb.append("projection is null; ");
       } else if (projection.length == 0) {
           sb.append("projection is empty; ");
       } else {
           for (int i = 0; i < projection.length; ++i) {
               sb.append("projection[");
               sb.append(i);
               sb.append("] is ");
               sb.append(projection[i]);
               sb.append("; ");
           }
       }
       sb.append("selection is ");
       sb.append(selection);
       sb.append("; ");
       if (selectionArgs == null) {
           sb.append("selectionArgs is null; ");
       } else if (selectionArgs.length == 0) {
           sb.append("selectionArgs is empty; ");
       } else {
           for (int i = 0; i < selectionArgs.length; ++i) {
               sb.append("selectionArgs[");
               sb.append(i);
               sb.append("] is ");
               sb.append(selectionArgs[i]);
               sb.append("; ");
           }
       }
       sb.append("sort is ");
       sb.append(sort);
       sb.append(".");
       Utils.V(sb.toString());
   }

   /**
    * Notify of a change through both URIs (/my_downloads and /all_downloads)
    * @param uri either URI for the changed download(s)
    * @param uriMatch the match ID from {@link #sURIMatcher}
    */
   private void notifyContentChanged(final Uri uri, int uriMatch) {
       Long downloadId = null;
       if (uriMatch == MY_DOWNLOADS_ID) {
           downloadId = Long.parseLong(getDownloadIdFromUri(uri));
       }
       Uri uriToNotify = DownloadManager.Impl.CONTENT_URI;
       if (downloadId != null) {
           uriToNotify = ContentUris.withAppendedId(uriToNotify, downloadId);
       }
       getContext().getContentResolver().notifyChange(uriToNotify, null);
   }

   /**
    * Deletes a row in the database
    */
   @Override
   public int delete(final Uri uri, final String where,
           final String[] whereArgs) {

       SQLiteDatabase db = mOpenHelper.getWritableDatabase();
       int count;
       int match = sURIMatcher.match(uri);
       switch (match) {
           case MY_DOWNLOADS:
           case MY_DOWNLOADS_ID:
               SqlSelection selection = getWhereClause(uri, where, whereArgs, match);
               count = db.delete(DB_TABLE, selection.getSelection(), selection.getParameters());
               break;
           default:
        	   Utils.D("deleting unknown/invalid URI: " + uri);
               throw new UnsupportedOperationException("Cannot delete URI: " + uri);
       }
       notifyContentChanged(uri, match);
       return count;
   }

   /**
    * Returns the content-provider-style MIME types of the various
    * types accessible through this content provider.
    */
   @Override
   public String getType(final Uri uri) {
       int match = sURIMatcher.match(uri);
       switch (match) {
           case MY_DOWNLOADS: {
               return DOWNLOAD_LIST_TYPE;
           }
           case MY_DOWNLOADS_ID: {
               return DOWNLOAD_TYPE;
           }
           default: {
               Utils.D("calling getType on an unknown URI: " + uri);
               throw new IllegalArgumentException("Unknown URI: " + uri);
           }
       }
   }
   
   /**
    * Inserts a row in the database
    */
   @Override
   public Uri insert(final Uri uri, final ContentValues values) {
      SQLiteDatabase db = mOpenHelper.getWritableDatabase();

      ContentValues filteredValues = new ContentValues();
      copyString(DownloadManager.Impl.COLUMN_URI, values, filteredValues);
      copyString("entity", values, filteredValues);
      copyString("hint", values, filteredValues);
      copyString("mimetype", values, filteredValues);
      copyString("package_name", values, filteredValues);
      copyString("md5", values, filteredValues);
      copyInteger("destination", values, filteredValues);
      copyInteger("visibility", values, filteredValues);
      copyInteger("control", values, filteredValues);
      copyInteger("source", values, filteredValues);
      copyInteger("allow_network", values, filteredValues);
      filteredValues.put("status", Integer.valueOf(190));
      filteredValues.put("lastmod", Long.valueOf(System.currentTimeMillis()));
      String pckg = values.getAsString(DownloadManager.Impl.COLUMN_NOTIFICATION_PACKAGE);
      String clazz = values.getAsString(DownloadManager.Impl.COLUMN_NOTIFICATION_CLASS);
      if(pckg != null) {
    	  filteredValues.put("notificationpackage", pckg);
         if(clazz != null) {
        	 filteredValues.put("notificationclass",clazz);
         }
      }

      copyString("notificationextras", values, filteredValues);
      copyStringWithDefault("title", values, filteredValues, "");
      copyStringWithDefault("description", values, filteredValues, "");
      filteredValues.put("total_bytes", Integer.valueOf(-1));
      filteredValues.put("current_bytes", Integer.valueOf(0));
      Context context = getContext();
      context.startService(new Intent(context, DownloadService.class));
      long rowID = db.insert(DB_TABLE, (String)null, filteredValues);

      if(rowID == -1L) {
         Utils.D("couldn\'t insert into downloads database");
         return null;
      } else {
    	  context.startService(new Intent(context, DownloadService.class));
         notifyContentChanged(uri, sURIMatcher.match(uri));
      }

      return ContentUris.withAppendedId(DownloadManager.Impl.CONTENT_URI, rowID);
   }

   public boolean onCreate() {
      mOpenHelper = new DownloadProvider.DatabaseHelper(getContext());
      return true;
   }

   /**
    * Starts a database query
    */
   @Override
   public Cursor query(final Uri uri, String[] projection,
            final String selection, final String[] selectionArgs,
            final String sort) {
       SQLiteDatabase db = mOpenHelper.getReadableDatabase();

       int match = sURIMatcher.match(uri);
       if (match == -1) {
           Utils.V("querying unknown URI: " + uri);
           throw new IllegalArgumentException("Unknown URI: " + uri);
       }

       SqlSelection fullSelection = getWhereClause(uri, selection, selectionArgs, match);
       logVerboseQueryInfo(projection, selection, selectionArgs, sort, db);

       Cursor ret = db.query(DB_TABLE, projection, fullSelection.getSelection(),
               fullSelection.getParameters(), null, null, sort);

       if (ret != null) {
          ret = new ReadOnlyCursorWrapper(ret);
       }

       if (ret != null) {
           ret.setNotificationUri(getContext().getContentResolver(), uri);
       } else {
    	   Utils.D("query failed in downloads database");
       }

       return ret;
   }
   
   /**
    * Updates a row in the database
    */
   @Override
   public int update(final Uri uri, final ContentValues values,
           final String where, final String[] whereArgs) {

       SQLiteDatabase db = mOpenHelper.getWritableDatabase();

       int count;
       boolean startService = false;

       if (values.containsKey(DownloadManager.Impl.COLUMN_DELETED)) {
           if (values.getAsInteger(DownloadManager.Impl.COLUMN_DELETED) == 1) {
               // some rows are to be 'deleted'. need to start DownloadService.
               startService = true;
           }
       }

       String filename = values.getAsString(DownloadManager.Impl.COLUMN_DATA);
       if (filename != null) {
           Cursor c = query(uri, new String[]
                   { DownloadManager.Impl.COLUMN_TITLE }, null, null, null);
           if (!c.moveToFirst() || TextUtils.isEmpty(c.getString(0))) {
               values.put(DownloadManager.Impl.COLUMN_TITLE, new File(filename).getName());
           }
           c.close();
       }

       Integer status = values.getAsInteger(DownloadManager.Impl.COLUMN_STATUS);
       boolean isRestart = status != null && status == DownloadManager.Impl.STATUS_PENDING;
//       boolean isUserBypassingSizeLimit =
//           values.containsKey(DownloadManager.Impl.COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT);
       if (isRestart/* || isUserBypassingSizeLimit*/) {
           startService = true;
       }
 
       ContentValues filteredValues = values;
       int match = sURIMatcher.match(uri);
       switch (match) {
           case MY_DOWNLOADS:
           case MY_DOWNLOADS_ID:
               SqlSelection selection = getWhereClause(uri, where, whereArgs, match);
               if (filteredValues.size() > 0) {
            	   Utils.D("update database values  : " + filteredValues);
                   count = db.update(DB_TABLE, filteredValues, selection.getSelection(),
                           selection.getParameters());
               } else {
                   count = 0;
               }
               break;

           default:
        	   Utils.D("updating unknown/invalid URI: " + uri);
               throw new UnsupportedOperationException("Cannot update URI: " + uri);
       }

       notifyContentChanged(uri, match);
       if (startService) {
           Context context = getContext();
           context.startService(new Intent(context, DownloadService.class));
       }
       return count;
   }

   private final class DatabaseHelper extends SQLiteOpenHelper {

	   public DatabaseHelper(final Context context) {
           super(context, DB_NAME, null, DB_VERSION);
       }

      private void createDownloadsTable(SQLiteDatabase db) {
         try {
        	 db.execSQL("DROP TABLE IF EXISTS downloads");
        	 db.execSQL("CREATE TABLE downloads(_id INTEGER PRIMARY KEY AUTOINCREMENT,uri TEXT, redirectcount INTEGER, entity TEXT, hint TEXT, _data TEXT, mimetype TEXT, destination INTEGER, visibility INTEGER, control INTEGER, status INTEGER, numfailed INTEGER, lastmod BIGINT, notificationpackage TEXT, notificationclass TEXT, notificationextras TEXT, total_bytes INTEGER DEFAULT -1, current_bytes INTEGER DEFAULT 0, etag TEXT, md5 TEXT, package_name TEXT, allow_network INTEGER, title TEXT, description TEXT, deleted BOOLEAN NOT NULL DEFAULT 0, source INTEGER);");
//        	 db.execSQL("CREATE TABLE " + DB_TABLE + "(" +
//           DownloadManager.Impl.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//           DownloadManager.Impl.COLUMN_URI + " TEXT, " +
//           DownloadManager.Impl.COLUMN_RETRY_AFTER_REDIRECT_COUNT + " INTEGER, " +
//           DownloadManager.Impl.COLUMN_APP_DATA + " TEXT, " +
//           DownloadManager.Impl.COLUMN_FILE_NAME_HINT + " TEXT, " +
//           DownloadManager.Impl.COLUMN_DATA + " TEXT, " +
//           DownloadManager.Impl.COLUMN_MIME_TYPE + " TEXT, " +
//           DownloadManager.Impl.COLUMN_DESTINATION + " INTEGER, " +
//           DownloadManager.Impl.COLUMN_VISIBILITY + " INTEGER, " +
//           DownloadManager.Impl.COLUMN_CONTROL + " INTEGER, " +
//           DownloadManager.Impl.COLUMN_STATUS + " INTEGER, " +
//           DownloadManager.Impl.COLUMN_FAILED_CONNECTIONS + " INTEGER, " +
//           DownloadManager.Impl.COLUMN_LAST_MODIFICATION + " BIGINT, " +
//           DownloadManager.Impl.COLUMN_NOTIFICATION_PACKAGE + " TEXT, " +
//           DownloadManager.Impl.COLUMN_NOTIFICATION_CLASS + " TEXT, " +
//           DownloadManager.Impl.COLUMN_NOTIFICATION_EXTRAS + " TEXT, " +
//           DownloadManager.Impl.COLUMN_TOTAL_BYTES + " INTEGER DEFAULT -1, " +
//           DownloadManager.Impl.COLUMN_CURRENT_BYTES + " INTEGER DEFAULT 0, " +
//           DownloadManager.Impl.COLUMN_ETAG + " TEXT, " +
//           DownloadManager.Impl.COLUMN_MD5 + " TEXT, " +
//           DownloadManager.Impl.COLUMN_PACKAGE_NAME + " TEXT, " +
//           DownloadManager.Impl.COLUMN_TITLE + " TEXT, " +
//           DownloadManager.Impl.COLUMN_DESCRIPTION + " TEXT, " +
//           Constants.MEDIA_SCANNED + " BOOLEAN);");
         } catch (SQLException ex) {
            Utils.E("couldn\'t create table in downloads database");
            throw ex;
         }
      }

      /**
       * Creates database the first time we try to open it.
       */
      public void onCreate(final SQLiteDatabase db) {
         Utils.D("populating new database");
         onUpgrade(db, 0, 109);
      }

      public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
         createDownloadsTable(db);
      }
   }

   /**
    * This class encapsulates a SQL where clause and its parameters.  It makes it possible for
    * shared methods (like {@link DownloadProvider#getWhereClause(Uri, String, String[], int)})
    * to return both pieces of information, and provides some utility logic to ease piece-by-piece
    * construction of selections.
    */
   private static class SqlSelection {
       public StringBuilder mWhereClause = new StringBuilder();
       public List<String> mParameters = new ArrayList<String>();

       public <T> void appendClause(String newClause, final T... parameters) {
           if (newClause == null || TextUtils.isEmpty(newClause)) {
               return;
           }
           if (mWhereClause.length() != 0) {
               mWhereClause.append(" AND ");
           }
           mWhereClause.append("(");
           mWhereClause.append(newClause);
           mWhereClause.append(")");
           if (parameters != null) {
               for (Object parameter : parameters) {
                   mParameters.add(parameter.toString());
               }
           }
       }

       public String getSelection() {
           return mWhereClause.toString();
       }

       public String[] getParameters() {
           String[] array = new String[mParameters.size()];
           return mParameters.toArray(array);
       }
   }

   private class ReadOnlyCursorWrapper extends CursorWrapper implements CrossProcessCursor {

      private CrossProcessCursor mCursor;

      public ReadOnlyCursorWrapper(Cursor cursor) {
          super(cursor);
          mCursor = (CrossProcessCursor) cursor;
      }

      public void fillWindow(int pos, CursorWindow window) {
          mCursor.fillWindow(pos, window);
      }

      public CursorWindow getWindow() {
          return mCursor.getWindow();
      }

      public boolean onMove(int oldPosition, int newPosition) {
          return mCursor.onMove(oldPosition, newPosition);
      }
   }
}
