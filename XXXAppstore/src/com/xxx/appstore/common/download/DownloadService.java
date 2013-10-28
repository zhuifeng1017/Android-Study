package com.xxx.appstore.common.download;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.text.TextUtils;

import com.xxx.appstore.common.util.Utils;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DownloadService extends Service
{
  public static final String CLEAR_BAD_NOTIFICATION = "clear";
  private int mActiveTasks;
  private Map<Long, DownloadInfo> mDownloads = new HashMap<Long, DownloadInfo>();
  private DownloadNotification mNotifier;
  private DownloadManagerContentObserver mObserver;
  private boolean mPendingUpdate;
  UpdateThread mUpdateThread;

  /**
   * Removes files that may have been left behind in the cache directory
   */
  private void removeSpuriousFiles() {
      File[] files =  new File(Environment.getExternalStorageDirectory(), Constants.DEFAULT_MARKET_SUBDIR).listFiles();
      if (files == null) {
          // The cache folder doesn't appear to exist (this is likely the case
          // when running the simulator).
          return;
      }
      HashSet<String> fileSet = new HashSet<String>();
      for (int i = 0; i < files.length; i++) {
          if (files[i].getName().equals(Constants.KNOWN_SPURIOUS_FILENAME)) {
              continue;
          }
          if (files[i].getName().equalsIgnoreCase(Constants.RECOVERY_DIRECTORY)) {
              continue;
          }
          fileSet.add(files[i].getPath());
      }

      Cursor cursor = getContentResolver().query(DownloadManager.Impl.CONTENT_URI,
              new String[] { DownloadManager.Impl.COLUMN_DATA }, null, null, null);
      if (cursor != null) {
          if (cursor.moveToFirst()) {
              do {
                  fileSet.remove(cursor.getString(0));
              } while (cursor.moveToNext());
          }
          cursor.close();
      }
      Iterator<String> iterator = fileSet.iterator();
      while (iterator.hasNext()) {
          String filename = iterator.next();
 //         if (Constants.LOGV) {
        	  Utils.E("deleting spurious file " + filename);
 //         }
          new File(filename).delete();
      }
  }

  /**
   * Drops old rows from the database to prevent it from growing too large
   */
  private void trimDatabase() {
      Cursor cursor = getContentResolver().query(DownloadManager.Impl.CONTENT_URI,
              new String[] { DownloadManager.Impl.COLUMN_ID },
              DownloadManager.Impl.COLUMN_STATUS + " >= '200'", null,
              DownloadManager.Impl.COLUMN_LAST_MODIFICATION);
      if (cursor == null) {
          // This isn't good - if we can't do basic queries in our database, nothing's gonna work
    	  Utils.E("null cursor in trimDatabase");
          return;
      }
      if (cursor.moveToFirst()) {
          int numDelete = cursor.getCount() - Constants.MAX_DOWNLOADS;
          int columnId = cursor.getColumnIndexOrThrow(DownloadManager.Impl.COLUMN_ID);
          while (numDelete > 0) {
              Uri downloadUri = ContentUris.withAppendedId(
            		  DownloadManager.Impl.CONTENT_URI, cursor.getLong(columnId));
              getContentResolver().delete(downloadUri, null, null);
              if (!cursor.moveToNext()) {
                  break;
              }
              numDelete--;
          }
      }
      cursor.close();
  }

    /**
     * Keeps a local copy of the info about a download, and initiates the
     * download if appropriate.
     */
  private DownloadInfo insertDownload(DownloadInfo.Reader paramReader, long paramLong)
  {
    DownloadInfo info = paramReader.newDownloadInfo(this);
    this.mDownloads.put(Long.valueOf(info.mId), info);
    info.logVerboseInfo();
    if ((this.mActiveTasks < 3) && (info.startIfReady(paramLong)))
      try
      {
        if (Constants.MIMETYPE_APK.equals(info.mMimeType))
          this.mActiveTasks = (1 + this.mActiveTasks);
      }
      finally
      {
      }
    return info;
  }
  
    /**
     * Updates the local copy of the info about a download.
     */
  private void updateDownload(DownloadInfo.Reader reader, DownloadInfo info, long now) {
      int oldVisibility = info.mVisibility;
      int oldStatus = info.mStatus;

      reader.updateFromDatabase(info);

      boolean lostVisibility =
              oldVisibility == DownloadManager.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
              && info.mVisibility != DownloadManager.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
              && DownloadManager.Impl.isStatusCompleted(info.mStatus);
      boolean justCompleted =
              !DownloadManager.Impl.isStatusCompleted(oldStatus)
              && DownloadManager.Impl.isStatusCompleted(info.mStatus);
      if (lostVisibility || justCompleted) {
    	  mNotifier.cancelNotification(info.mId);
      }

      if(!justCompleted) {
    	  if(mActiveTasks >= 3 || !info.startIfReady(now)) {
    		  return;
    	  }
    	  else {
    		  if(Constants.MIMETYPE_APK.equals(info.mMimeType))
              mActiveTasks = 1 + mActiveTasks;
    	  }
      }
      else {
    	  if(Constants.MIMETYPE_APK.equals(info.mMimeType))
              mActiveTasks = mActiveTasks - 1;
      }
  }

  /**
   * Removes the local copy of the info about a download.
   */
  private void deleteDownload(long id) {
      DownloadInfo info = mDownloads.get(id);
//      if (info.shouldScanFile()) {
//          scanFile(info, false, false);
//      }
      if (info.mStatus == DownloadManager.Impl.STATUS_RUNNING) {
          info.mStatus = DownloadManager.Impl.STATUS_CANCELED;
      }
      if (info.mDestination != DownloadManager.Impl.DESTINATION_EXTERNAL && info.mFileName != null) {
          new File(info.mFileName).delete();
      }
      mNotifier.cancelNotification(info.mId);
      mDownloads.remove(info.mId);
      
      if (Constants.MIMETYPE_APK.equals(info.mMimeType))
      {
        this.mActiveTasks -= 1;
        if (this.mActiveTasks < 0)
          this.mActiveTasks = 0;
      }
  }

  private void updateFromProvider()
  {
    try
    {
      this.mPendingUpdate = true;
      if (this.mUpdateThread == null)
      {
        this.mUpdateThread = new UpdateThread();
        this.mUpdateThread.start();
      }
    }
    finally
    {
    }
  }

  public IBinder onBind(Intent paramIntent)
  {
    throw new UnsupportedOperationException("Cannot bind to Download Manager Service");
  }

  public void onCreate()
  {
    super.onCreate();
    Utils.D("Service onCreate");
    this.mObserver = new DownloadManagerContentObserver(new Handler());
    getContentResolver().registerContentObserver(DownloadManager.Impl.CONTENT_URI, true, this.mObserver);
    this.mNotifier = new DownloadNotification(this);
    updateFromProvider();
  }

  public void onDestroy()
  {
    getContentResolver().unregisterContentObserver(this.mObserver);
    Utils.D("Service onDestroy");
    super.onDestroy();
  }

  public void onStart(Intent paramIntent, int paramInt)
  {
    Utils.D("Service onStart");
    if ((paramIntent != null) && (paramIntent.getIntExtra(CLEAR_BAD_NOTIFICATION, -1) > 0))
      new finishThread().start();
    else
      updateFromProvider();
  }

  private class DownloadManagerContentObserver extends ContentObserver
  {
    public DownloadManagerContentObserver(Handler handler)
    {
      super(handler);
    }

    public void onChange(boolean paramBoolean)
    {
      Utils.D("Service ContentObserver received notification");
      DownloadService.this.updateFromProvider();
    }
  }

  private class UpdateThread extends Thread
  {
    public UpdateThread()
    {
      super();
    }
    
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        trimDatabase();
        removeSpuriousFiles();

        boolean keepService = false;
        // for each update from the database, remember which download is
        // supposed to get restarted soonest in the future
        long wakeUp = Long.MAX_VALUE;
        for (;;) {
            synchronized (DownloadService.this) {
                if (mUpdateThread != this) {
                    throw new IllegalStateException(
                            "multiple UpdateThreads in DownloadService");
                }
                if (!mPendingUpdate) {
                    mUpdateThread = null;
                    if (!keepService) {
                        stopSelf();
                    }
                    if (wakeUp != Long.MAX_VALUE) {
                        scheduleAlarm(wakeUp);
                    }
                    return;
                }
                mPendingUpdate = false;
            }

            long now = System.currentTimeMillis();
            keepService = false;
            wakeUp = Long.MAX_VALUE;
            Set<Long> idsNoLongerInDatabase = new HashSet<Long>(mDownloads.keySet());

            Cursor cursor = getContentResolver().query(DownloadManager.Impl.CONTENT_URI,
                    null, null, null, null);
            if (cursor == null) {
                continue;
            }
            try {
            	DownloadInfo.Reader reader =
                        new DownloadInfo.Reader(cursor);
                int idColumn = cursor.getColumnIndexOrThrow(DownloadManager.Impl.COLUMN_ID);

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    idsNoLongerInDatabase.remove(id);
                    DownloadInfo info = mDownloads.get(id);
                    if (info != null) {
                        updateDownload(reader, info, now);
                    } else {
                        info = insertDownload(reader, now);
                    }

                    if (info.hasCompletionNotification()) {
                        keepService = true;
                    }
                    long next = info.nextAction(now);
                    if (next == 0) {
                        keepService = true;
                    } else if (next > 0 && next < wakeUp) {
                        wakeUp = next;
                    }
                }
            } finally {
                cursor.close();
            }

            for (Long id : idsNoLongerInDatabase) {
                deleteDownload(id);
            }

            // is there a need to start the DownloadService? yes, if there are rows to be
            // deleted.
            for (DownloadInfo info : mDownloads.values()) {
                if (info.mDeleted /*&& TextUtils.isEmpty(info.mMediaProviderUri)*/) {
                    keepService = true;
                    break;
                }
            }
            
            mNotifier.updateNotification();

            // look for all rows with deleted flag set and delete the rows
    		// from the database
    		// permanently
    		for (DownloadInfo info : mDownloads.values()) {
    		    if (info.mDeleted) {
//    		    	Helper.deleteFile(getContentResolver(), info.mId,
//    		    			info.mFileName, info.mMimeType);
    		    }
    		}
        }
    }


    private void scheduleAlarm(long paramLong)
    {
      AlarmManager localAlarmManager = (AlarmManager)DownloadService.this.getSystemService("alarm");
      if (localAlarmManager == null)
        Utils.E("couldn't get alarm manager");
      else
      {
        Utils.D("scheduling retry in " + paramLong + "ms");
        Intent localIntent = new Intent("gfan.intent.action.DOWNLOAD_WAKEUP");
        localIntent.setClassName("com.xxx.appstore", DownloadReceiver.class.getName());
        localAlarmManager.set(0, paramLong + System.currentTimeMillis(), PendingIntent.getBroadcast(DownloadService.this, 0, localIntent, 1073741824));
      }
    }
  }

  private class finishThread extends Thread
  {
    private finishThread()
    {
    }

    public void run()
    {
      Process.setThreadPriority(10);
      DownloadService.this.mNotifier.clearBadNotification();
      if (DownloadService.this.mUpdateThread == null)
        DownloadService.this.stopSelf();
    }
  }
}