package com.mappn.gfan.common.download;

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
import com.mappn.gfan.common.util.Utils;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class DownloadService extends Service
{
  public static final String CLEAR_BAD_NOTIFICATION = "clear";
  private int mActiveTasks;
  private Map<Long, DownloadInfo> mDownloads = new HashMap();
  private DownloadNotification mNotifier;
  private DownloadManagerContentObserver mObserver;
  private boolean mPendingUpdate;
  UpdateThread mUpdateThread;

  private void deleteDownload(long paramLong)
  {
    DownloadInfo localDownloadInfo = (DownloadInfo)this.mDownloads.get(Long.valueOf(paramLong));
    if (localDownloadInfo.mStatus == 192)
      localDownloadInfo.mStatus = 490;
    if ((localDownloadInfo.mDestination != 0) && (localDownloadInfo.mFileName != null))
      new File(localDownloadInfo.mFileName).delete();
    this.mNotifier.cancelNotification(localDownloadInfo.mId);
    this.mDownloads.remove(Long.valueOf(localDownloadInfo.mId));
    try
    {
      if ("application/vnd.android.package-archive".equals(localDownloadInfo.mMimeType))
      {
        this.mActiveTasks -= 1;
        if (this.mActiveTasks < 0)
          this.mActiveTasks = 0;
      }
    }
    finally
    {
    }
  }

  private DownloadInfo insertDownload(DownloadInfo.Reader paramReader, long paramLong)
  {
    DownloadInfo localDownloadInfo = paramReader.newDownloadInfo(this);
    this.mDownloads.put(Long.valueOf(localDownloadInfo.mId), localDownloadInfo);
    localDownloadInfo.logVerboseInfo();
    if ((this.mActiveTasks < 3) && (localDownloadInfo.startIfReady(paramLong)))
      try
      {
        if ("application/vnd.android.package-archive".equals(localDownloadInfo.mMimeType))
          this.mActiveTasks = (1 + this.mActiveTasks);
      }
      finally
      {
      }
    return localDownloadInfo;
  }

  private void removeSpuriousFiles()
  {
//    File[] arrayOfFile = new File(Environment.getExternalStorageDirectory(), "gfan/market").listFiles();
//    if (arrayOfFile == null);
//    while (true)
//    {
//      return;
//      HashSet localHashSet = new HashSet();
//      int i = 0;
//      if (i < arrayOfFile.length)
//      {
//        if (arrayOfFile[i].getName().equals("lost+found"));
//        while (true)
//        {
//          i++;
//          break;
//          if (!arrayOfFile[i].getName().equalsIgnoreCase("recovery"))
//            localHashSet.add(arrayOfFile[i].getPath());
//        }
//      }
//      ContentResolver localContentResolver = getContentResolver();
//      Uri localUri = DownloadManager.Impl.CONTENT_URI;
//      String[] arrayOfString = new String[1];
//      arrayOfString[0] = "_data";
//      Cursor localCursor = localContentResolver.query(localUri, arrayOfString, null, null, null);
//      if (localCursor != null)
//      {
//        if (localCursor.moveToFirst())
//          do
//            localHashSet.remove(localCursor.getString(0));
//          while (localCursor.moveToNext());
//        localCursor.close();
//      }
//      Iterator localIterator = localHashSet.iterator();
//      while (localIterator.hasNext())
//      {
//        String str = (String)localIterator.next();
//        Utils.D("deleting spurious file " + str);
//        new File(str).delete();
//      }
//    }
  }

  private void trimDatabase()
  {
//    ContentResolver localContentResolver = getContentResolver();
//    Uri localUri1 = DownloadManager.Impl.CONTENT_URI;
//    String[] arrayOfString = new String[1];
//    arrayOfString[0] = "_id";
//    Cursor localCursor = localContentResolver.query(localUri1, arrayOfString, "status >= '200'", null, "lastmod");
//    if (localCursor == null)
//    {
//      Utils.E("null cursor in trimDatabase");
//      return;
//    }
//    int i;
//    int j;
//    if (localCursor.moveToFirst())
//    {
//      i = localCursor.getCount() - 1000;
//      j = localCursor.getColumnIndexOrThrow("_id");
//    }
//    while (true)
//    {
//      if (i > 0)
//      {
//        Uri localUri2 = ContentUris.withAppendedId(DownloadManager.Impl.CONTENT_URI, localCursor.getLong(j));
//        getContentResolver().delete(localUri2, null, null);
//        if (localCursor.moveToNext());
//      }
//      else
//      {
//        localCursor.close();
//        break;
//      }
//      i--;
//    }
  }

  // ERROR //
  private void updateDownload(DownloadInfo.Reader paramReader, DownloadInfo paramDownloadInfo, long paramLong)
  {
    
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
    if ((paramIntent != null) && (paramIntent.getIntExtra("clear", -1) > 0))
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

    private void scheduleAlarm(long paramLong)
    {
      AlarmManager localAlarmManager = (AlarmManager)DownloadService.this.getSystemService("alarm");
      if (localAlarmManager == null)
        Utils.E("couldn't get alarm manager");
      else
      {
        Utils.D("scheduling retry in " + paramLong + "ms");
        Intent localIntent = new Intent("gfan.intent.action.DOWNLOAD_WAKEUP");
        localIntent.setClassName("com.mappn.gfan", DownloadReceiver.class.getName());
        localAlarmManager.set(0, paramLong + System.currentTimeMillis(), PendingIntent.getBroadcast(DownloadService.this, 0, localIntent, 1073741824));
      }
    }

    // ERROR //
    public void run()
    {
      
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