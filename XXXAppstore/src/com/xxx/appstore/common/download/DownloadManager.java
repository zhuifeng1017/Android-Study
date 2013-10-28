package com.xxx.appstore.common.download;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.xxx.appstore.common.download.Helper;
import com.xxx.appstore.ui.AppsUpdateActivity;
import java.util.ArrayList;
import java.util.Iterator;

public class DownloadManager {

   public static final int STATUS_FAILED = 16;
   public static final int STATUS_PAUSED = 4;
   public static final int STATUS_PENDING = 1;
   public static final int STATUS_RUNNING = 2;
   public static final int STATUS_SUCCESSFUL = 8;
   private Uri mBaseUri;
   private String mPackageName;
   private ContentResolver mResolver;


   public DownloadManager(ContentResolver var1, String var2) {
      this.mBaseUri = DownloadManager.Impl.CONTENT_URI;
      this.mResolver = var1;
      this.mPackageName = var2;
   }

   private void doEnqueue(Context context, DownloadManager.Request request, final DownloadManager.EnqueueListener listener) {
      ContentValues var4 = request.toContentValues(this.mPackageName);
      var4.put("hint", (String)request.mTitle);
      var4.put("package_name", request.mPackageName);
      var4.put(Impl.COLUMN_NOTIFICATION_CLASS, AppsUpdateActivity.class.getName());
      var4.put("md5", request.mMD5);
      var4.put("allow_network", Helper.getActiveNetworkType(context));
      if(request.mSourceType == 3) {
         var4.put("destination", Integer.valueOf(1));
      } else {
         var4.put("destination", Integer.valueOf(request.mDestination));
      }

      (new AsyncQueryHandler(this.mResolver) {
         protected void onInsertComplete(int var1, Object var2, Uri uri) {
            if(listener != null) {
               long var4;
               if(uri == null) {
                  var4 = -1L;
               } else {
                  var4 = Long.parseLong(uri.getLastPathSegment());
               }

               listener.onFinish(var4);
            }

         }
      }).startInsert(0, (Object)null, DownloadManager.Impl.CONTENT_URI, var4);
   }

   static String[] getWhereArgsForIds(long[] var0) {
      String[] var1 = new String[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = Long.toString(var0[var2]);
      }

      return var1;
   }

   static String[] getWhereArgsForPackageName(String var0) {
      String[] var1 = new String[]{String.valueOf(200), var0};
      return var1;
   }

   static String getWhereClauseForIds(long[] var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append("(");

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if(var3 > 0) {
            var1.append("OR ");
         }

         var1.append("_id");
         var1.append(" = ? ");
      }

      var1.append(")");
      return var1.toString();
   }

   static String getWhereClauseForPackageName() {
      StringBuilder var0 = new StringBuilder();
      var0.append("(");
      var0.append("status");
      var0.append(" = ? AND ");
      var0.append("package_name");
      var0.append(" = ? )");
      return var0.toString();
   }

   public void cancelDownload(long ... var1) {
      if(var1 != null && var1.length != 0) {
         ContentValues var2 = new ContentValues();
         var2.put("status", Integer.valueOf(Impl.STATUS_CANCELED));
         (new AsyncQueryHandler(this.mResolver) {
         }).startUpdate(0, (Object)null, this.mBaseUri, var2, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
      } else {
         throw new IllegalArgumentException("input param \'ids\' can\'t be null");
      }
   }

   public void completeInstallation(String var1) {
      if(!TextUtils.isEmpty(var1)) {
         ContentValues var2 = new ContentValues();
         var2.put("visibility", Integer.valueOf(2));
         var2.put("status", Integer.valueOf(260));
         var2.put("deleted", Integer.valueOf(1));
         (new AsyncQueryHandler(this.mResolver) {
         }).startUpdate(0, (Object)null, this.mBaseUri, var2, getWhereClauseForPackageName(), getWhereArgsForPackageName(var1));
      }

   }

   public void deleteDownloadedFile(long ... var1) {
      if(var1 != null && var1.length != 0) {
         ContentValues var2 = new ContentValues();
         var2.put("_data", "");
         var2.put("visibility", Integer.valueOf(2));
         var2.put("status", Integer.valueOf(270));
         var2.put("deleted", Integer.valueOf(1));
         (new AsyncQueryHandler(this.mResolver) {
         }).startUpdate(0, (Object)null, this.mBaseUri, var2, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
      } else {
         throw new IllegalArgumentException("input param \'ids\' can\'t be null");
      }
   }

   public void enqueue(final Context var1, final DownloadManager.Request var2, final DownloadManager.EnqueueListener var3) {
      if(Constants.MIMETYPE_APK.equals(var2.mMimeType)) {
         String[] var4 = new String[]{var2.mPackageName, String.valueOf(192)};
         (new AsyncQueryHandler(this.mResolver) {
            protected void onQueryComplete(int var1x, Object var2x, Cursor var3x) {
               if(var3x != null && var3x.getCount() > 0) {
                  if(var3x.getCount() > 0) {
                     var3x.close();
                     return;
                  }

                  var3x.close();
               }

               DownloadManager.this.doEnqueue(var1, var2, var3);
            }
         }).startQuery(0, (Object)null, DownloadManager.Impl.CONTENT_URI, (String[])null, "package_name = ? && status = ?", var4, (String)null);
      } else {
         this.doEnqueue(var1, var2, var3);
      }
   }

   public void enqueueWaitRequest(DownloadManager.Request var1, final DownloadManager.EnqueueListener var2) {
      ContentValues var3 = var1.toContentValues(this.mPackageName);
      var3.put("hint", (String)var1.mTitle);
      var3.put("package_name", var1.mPackageName);
      var3.put(Impl.COLUMN_NOTIFICATION_CLASS, AppsUpdateActivity.class.getName());
      var3.put("md5", var1.mMD5);
      var3.put("destination", Integer.valueOf(var1.mDestination));
      var3.put("visibility", Integer.valueOf(2));
      var3.put("control", Integer.valueOf(2));
      (new AsyncQueryHandler(this.mResolver) {
         protected void onInsertComplete(int var1, Object var2x, Uri var3) {
            if(var2 != null) {
               long var4;
               if(var3 == null) {
                  var4 = -1L;
               } else {
                  var4 = Long.parseLong(var3.getLastPathSegment());
               }

               var2.onFinish(var4);
            }

         }
      }).startInsert(0, (Object)null, DownloadManager.Impl.CONTENT_URI, var3);
   }

   public Cursor getDownloadErrorApks() {
      return this.mResolver.query(this.mBaseUri, new String[]{"_id", "title", "status"}, "((status >= \'400\' AND status < \'500\') AND destination = \'0\' AND mimetype = \'application/vnd.android.package-archive\')", (String[])null, (String)null);
   }

   public Cursor getDownloadingApks() {
      return this.mResolver.query(this.mBaseUri, new String[]{"_id", "_data", "title", "description", "current_bytes", "total_bytes", "status", "package_name", "notificationextras"}, "(((status >= \'190\' AND status <= \'200\') OR status = \'490\') AND destination = \'0\' AND mimetype = \'application/vnd.android.package-archive\')", (String[])null, (String)null);
   }

   public int hideDownload(long ... var1) {
      if(var1 != null && var1.length != 0) {
         ContentValues var2 = new ContentValues();
         var2.put("visibility", Integer.valueOf(2));
         return this.mResolver.update(this.mBaseUri, var2, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
      } else {
         throw new IllegalArgumentException("input param \'ids\' can\'t be null");
      }
   }

   public int markRowDeleted(long ... var1) {
      if(var1 != null && var1.length != 0) {
         ContentValues var2 = new ContentValues();
         var2.put("deleted", Integer.valueOf(1));
         return this.mResolver.update(this.mBaseUri, var2, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
      } else {
         throw new IllegalArgumentException("input param \'ids\' can\'t be null");
      }
   }

   public int pauseDownload(long ... var1) {
      if(var1 != null && var1.length != 0) {
         ContentValues var2 = new ContentValues();
         var2.put("control", Integer.valueOf(1));
         return this.mResolver.update(this.mBaseUri, var2, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
      } else {
         throw new IllegalArgumentException("input param \'ids\' can\'t be null");
      }
   }

   public Cursor query(DownloadManager.Query var1) {
      return var1.runQuery(this.mResolver, (String[])null, this.mBaseUri);
   }

   public void remove(long ... var1) {
      if(var1 != null && var1.length != 0) {
         (new AsyncQueryHandler(this.mResolver) {
         }).startDelete(0, (Object)null, this.mBaseUri, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
      } else {
         throw new IllegalArgumentException("input param \'ids\' can\'t be null");
      }
   }

   public void replacePackageName(String var1, String var2) {
      ContentValues var3 = new ContentValues();
      var3.put("package_name", var2);
      this.mResolver.update(this.mBaseUri, var3, getWhereClauseForPackageName(), getWhereArgsForPackageName(var1));
   }

   public void restartDownload(long ... var1) {
      Cursor var2 = this.query((new DownloadManager.Query()).setFilterById(var1));

      try {
         var2.moveToFirst();

         while(!var2.isAfterLast()) {
            int var7 = var2.getInt(var2.getColumnIndex("status"));
            if(var7 != 8 && var7 != 16) {
               throw new IllegalArgumentException("Cannot restart incomplete download: " + var2.getLong(var2.getColumnIndex("_id")));
            }

            var2.moveToNext();
         }
      } finally {
         var2.close();
      }

      ContentValues var5 = new ContentValues();
      var5.put("current_bytes", Integer.valueOf(0));
      var5.put("total_bytes", Integer.valueOf(-1));
      var5.putNull("_data");
      var5.put("status", Integer.valueOf(190));
      this.mResolver.update(this.mBaseUri, var5, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
   }

   public int resumeDownload(long ... var1) {
      if(var1 != null && var1.length != 0) {
         ContentValues var2 = new ContentValues();
         var2.put("control", Integer.valueOf(0));
         var2.put("visibility", Integer.valueOf(1));
         return this.mResolver.update(this.mBaseUri, var2, getWhereClauseForIds(var1), getWhereArgsForIds(var1));
      } else {
         throw new IllegalArgumentException("input param \'ids\' can\'t be null");
      }
   }

   public static final class Impl implements BaseColumns {

      public static final String COLUMN_ALLOW_NETWORK_TYPE = "allow_network";
      public static final String COLUMN_APP_DATA = "entity";
      public static final String COLUMN_CONTROL = "control";
      public static final String COLUMN_CURRENT_BYTES = "current_bytes";
      public static final String COLUMN_DATA = "_data";
      public static final String COLUMN_DELETED = "deleted";
      public static final String COLUMN_DESCRIPTION = "description";
      public static final String COLUMN_DESTINATION = "destination";
      public static final String COLUMN_ETAG = "etag";
      public static final String COLUMN_FAILED_CONNECTIONS = "numfailed";
      public static final String COLUMN_FILE_NAME_HINT = "hint";
      public static final String COLUMN_ID = "_id";
      public static final String COLUMN_LAST_MODIFICATION = "lastmod";
      public static final String COLUMN_MD5 = "md5";
      public static final String COLUMN_MIME_TYPE = "mimetype";
      public static final String COLUMN_NOTIFICATION_CLASS = "notificationclass";
      public static final String COLUMN_NOTIFICATION_EXTRAS = "notificationextras";
      public static final String COLUMN_NOTIFICATION_PACKAGE = "notificationpackage";
      public static final String COLUMN_PACKAGE_NAME = "package_name";
      public static final String COLUMN_RETRY_AFTER_REDIRECT_COUNT = "redirectcount";
      public static final String COLUMN_SOURCE = "source";
      public static final String COLUMN_STATUS = "status";
      public static final String COLUMN_TITLE = "title";
      public static final String COLUMN_TOTAL_BYTES = "total_bytes";
      public static final String COLUMN_URI = "uri";
      public static final String COLUMN_VISIBILITY = "visibility";
      public static final Uri CONTENT_URI = Uri.parse("content://gfan_downloads_xxx/my_downloads");
      public static final int CONTROL_PAUSED = 1;
      public static final int CONTROL_PENDING = 2;
      public static final int CONTROL_RUN = 0;
      public static final int DESTINATION_CACHE_PARTITION = 1;
      public static final int DESTINATION_CACHE_PARTITION_PURGEABLE = 2;
      public static final int DESTINATION_EXTERNAL = 0;
      public static final int DESTINATION_FILE_URI = 3;
      public static final int STATUS_BAD_REQUEST = 400;
      public static final int STATUS_CANCELED = 490;
      public static final int STATUS_CANNOT_RESUME = 489;
      public static final int STATUS_DEVICE_NOT_FOUND_ERROR = 499;
      public static final int STATUS_FILE_ALREADY_EXISTS_ERROR = 488;
      public static final int STATUS_FILE_ERROR = 492;
      public static final int STATUS_FILE_MD5_ERROR = 486;
      public static final int STATUS_HTTP_DATA_ERROR = 495;
      public static final int STATUS_HTTP_EXCEPTION = 496;
      public static final int STATUS_INSTALLED = 260;
      public static final int STATUS_INSUFFICIENT_SPACE_ERROR = 498;
      public static final int STATUS_LENGTH_REQUIRED = 411;
      public static final int STATUS_MIN_ARTIFICIAL_ERROR_STATUS = 487;
      public static final int STATUS_NOT_ACCEPTABLE = 406;
      public static final int STATUS_PAUSED_BY_APP = 193;
      public static final int STATUS_PENDING = 190;
      public static final int STATUS_PRECONDITION_FAILED = 412;
      public static final int STATUS_QUEUED_FOR_WIFI = 196;
      public static final int STATUS_REMOVED = 270;
      public static final int STATUS_RUNNING = 192;
      public static final int STATUS_SUCCESS = 200;
      public static final int STATUS_TOO_MANY_REDIRECTS = 497;
      public static final int STATUS_UNHANDLED_HTTP_CODE = 494;
      public static final int STATUS_UNHANDLED_REDIRECT = 493;
      public static final int STATUS_UNKNOWN_ERROR = 491;
      public static final int STATUS_WAITING_FOR_NETWORK = 195;
      public static final int STATUS_WAITING_TO_RETRY = 194;
      public static final int VISIBILITY_HIDDEN = 2;
      public static final int VISIBILITY_VISIBLE = 0;
      public static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1;


      public static boolean isStatusClientError(int var0) {
         boolean var1;
         if(var0 >= 400 && var0 < 500) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public static boolean isStatusCompleted(int var0) {
         boolean var1;
         if((var0 < 200 || var0 >= 300) && (var0 < 400 || var0 >= 600)) {
            var1 = false;
         } else {
            var1 = true;
         }

         return var1;
      }

      public static boolean isStatusError(int var0) {
         boolean var1;
         if(var0 >= 400 && var0 < 600) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public static boolean isStatusInformational(int var0) {
         boolean var1;
         if(var0 >= 100 && var0 < 200) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public static boolean isStatusPending(int var0) {
         boolean var1;
         if(var0 != 190 && var0 != 193 && var0 != 194 && var0 != 195 && var0 != 196) {
            var1 = false;
         } else {
            var1 = true;
         }

         return var1;
      }

      public static boolean isStatusRunning(int var0) {
         boolean var1;
         if(var0 == 192) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public static boolean isStatusServerError(int var0) {
         boolean var1;
         if(var0 >= 500 && var0 < 600) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public static boolean isStatusSuccess(int var0) {
         boolean var1;
         if(var0 == 200) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public static boolean isStatusWaiting(int var0) {
         boolean var1;
         if(var0 == 2) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }
   }

   public interface EnqueueListener {

      void onFinish(long var1);
   }

   public static class Request {

      // $FF: synthetic field
      static final boolean $assertionsDisabled;
      private CharSequence mDescription;
      private int mDestination = 0;
      private String mIconUrl;
      private String mMD5;
      private String mMimeType = Constants.MIMETYPE_APK;
      private String mPackageName;
      private boolean mShowNotification = true;
      private int mSourceType;
      private CharSequence mTitle;
      private Uri mUri;


      static {
         boolean var0;
         if(!DownloadManager.class.desiredAssertionStatus()) {
            var0 = true;
         } else {
            var0 = false;
         }

         $assertionsDisabled = var0;
      }

      public Request(Uri var1) {
         if(var1 == null) {
            throw new NullPointerException();
         } else {
            String var2 = var1.getScheme();
            if(var2 != null && var2.equals("http")) {
               this.mUri = var1;
            } else {
               throw new IllegalArgumentException("Can only download HTTP URIs: " + var1);
            }
         }
      }

      private void putIfNonNull(ContentValues var1, String var2, Object var3) {
         if(var3 != null) {
            var1.put(var2, var3.toString());
         }

      }

      public DownloadManager.Request setDescription(CharSequence var1) {
         this.mDescription = var1;
         return this;
      }

      public DownloadManager.Request setDestination(int var1) {
         this.mDestination = var1;
         return this;
      }

      public DownloadManager.Request setIconUrl(String var1) {
         this.mIconUrl = var1;
         return this;
      }

      public DownloadManager.Request setMD5(String var1) {
         this.mMD5 = var1;
         return this;
      }

      public DownloadManager.Request setMimeType(String var1) {
         this.mMimeType = var1;
         return this;
      }

      public DownloadManager.Request setPackageName(String var1) {
         this.mPackageName = var1;
         return this;
      }

      public DownloadManager.Request setShowRunningNotification(boolean var1) {
         this.mShowNotification = var1;
         return this;
      }

      public DownloadManager.Request setSourceType(int var1) {
         this.mSourceType = var1;
         return this;
      }

      public DownloadManager.Request setTitle(CharSequence var1) {
         this.mTitle = var1;
         return this;
      }

      ContentValues toContentValues(String var1) {
         ContentValues var2 = new ContentValues();
         if(!$assertionsDisabled && this.mUri == null) {
            throw new AssertionError();
         } else {
            var2.put("uri", this.mUri.toString());
            var2.put("notificationpackage", var1);
            var2.put("mimetype", this.mMimeType);
            var2.put("notificationextras", this.mIconUrl);
            var2.put("source", Integer.valueOf(this.mSourceType));
            this.putIfNonNull(var2, "title", this.mTitle);
            this.putIfNonNull(var2, "description", this.mDescription);
            byte var3;
            if(this.mShowNotification) {
               var3 = 1;
            } else {
               var3 = 2;
            }

            var2.put("visibility", Integer.valueOf(var3));
            if(this.mSourceType == 3 && Constants.MIMETYPE_APK.equals(this.mMimeType)) {
               var2.put("visibility", Integer.valueOf(0));
            }

            return var2;
         }
      }
   }

   public static class Query {

      public static final int ORDER_ASCENDING = 1;
      public static final int ORDER_DESCENDING = 2;
      private long[] mIds = null;
      private String mOrderByColumn = "lastmod";
      private int mOrderDirection = 2;
      private Integer mStatusFlags = null;


      private String joinStrings(String var1, Iterable<String> var2) {
         StringBuilder var3 = new StringBuilder();
         boolean var4 = true;

         for(Iterator<String> var5 = var2.iterator(); var5.hasNext(); var4 = false) {
            String var6 = (String)var5.next();
            if(!var4) {
               var3.append(var1);
            }

            var3.append(var6);
         }

         return var3.toString();
      }

      private String statusClause(String var1, int var2) {
         return "status" + var1 + "\'" + var2 + "\'";
      }

      public DownloadManager.Query orderBy(String var1, int var2) {
         if(var2 != 1 && var2 != 2) {
            throw new IllegalArgumentException("Invalid direction: " + var2);
         } else {
            if(var1.equals("lastmod")) {
               this.mOrderByColumn = "lastmod";
            } else {
               if(!var1.equals("total_bytes")) {
                  throw new IllegalArgumentException("Cannot order by " + var1);
               }

               this.mOrderByColumn = "total_bytes";
            }

            this.mOrderDirection = var2;
            return this;
         }
      }

      Cursor runQuery(ContentResolver var1, String[] var2, Uri var3) {
         ArrayList<String> var4 = new ArrayList<String>();
         String[] var5;
         if(this.mIds != null) {
            var4.add(DownloadManager.getWhereClauseForIds(this.mIds));
            var5 = DownloadManager.getWhereArgsForIds(this.mIds);
         } else {
            var5 = null;
         }

         if(this.mStatusFlags != null) {
            ArrayList<String> var6 = new ArrayList<String>();
            if((1 & this.mStatusFlags.intValue()) != 0) {
               var6.add(this.statusClause("=", 190));
            }

            if((2 & this.mStatusFlags.intValue()) != 0) {
               var6.add(this.statusClause("=", 192));
            }

            if((4 & this.mStatusFlags.intValue()) != 0) {
               var6.add(this.statusClause("=", 193));
               var6.add(this.statusClause("=", 194));
               var6.add(this.statusClause("=", 195));
               var6.add(this.statusClause("=", 196));
            }

            if((8 & this.mStatusFlags.intValue()) != 0) {
               var6.add(this.statusClause("=", 200));
            }

            if((16 & this.mStatusFlags.intValue()) != 0) {
               var6.add("(" + this.statusClause(">=", 400) + " AND " + this.statusClause("<", 600) + ")");
            }

            var4.add(this.joinStrings(" OR ", var6));
         }

         var4.add("deleted != \'1\'");
         String var9 = this.joinStrings(" AND ", var4);
         String var10;
         if(this.mOrderDirection == 1) {
            var10 = "ASC";
         } else {
            var10 = "DESC";
         }

         return var1.query(var3, var2, var9, var5, this.mOrderByColumn + " " + var10);
      }

      public DownloadManager.Query setFilterById(long ... var1) {
         this.mIds = var1;
         return this;
      }

      public DownloadManager.Query setFilterByStatus(int var1) {
         this.mStatusFlags = Integer.valueOf(var1);
         return this;
      }
   }
}
