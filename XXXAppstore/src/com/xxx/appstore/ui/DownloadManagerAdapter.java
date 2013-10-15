package com.xxx.appstore.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.download.DownloadManager.Impl;
import com.xxx.appstore.common.util.DialogUtil;
import com.xxx.appstore.common.util.ImageUtils;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.util.DialogUtil.WarningDialogListener;
import com.xxx.appstore.common.vo.DownloadInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadManagerAdapter extends BaseAdapter
  implements Observer
{
  static final String ITEM_DOWNLOAD_TITLE = "download_title";
  static final String ITEM_INSTALLED_TITLE = "installed_title";
  static final String ITEM_UPDATE_ALL = "update_all";
  private static final int REFRESH_DOWNLOADING = 0;
  private static final int REFRESH_INTERVAL = 200;
  private static final int REMOVING_ITEM = 1;
  static final String TITLE_DOWNLOADED = "title_downloaded";
  static final String TITLE_DOWNLOADING = "title_downloading";
  static final String TITLE_WAITING = "title_waiting";
  static final int VIEW_TYPE_DOWNLOADED = 2;
  static final int VIEW_TYPE_DOWNLOADING = 1;
  static final int VIEW_TYPE_TITLE = 0;
  static final int WEIGHT_DOWNLOADED_ITEM = 6;
  static final int WEIGHT_DOWNLOADED_TITLE = 5;
  static final int WEIGHT_DOWNLOADING_ITEM = 3;
  static final int WEIGHT_DOWNLOADING_PENDING_ITEM = 4;
  static final int WEIGHT_DOWNLOADING_TITLE = 2;
  static final int WEIGHT_WAITING_ITEM = 1;
 // static final int WEIGHT_WAITING_TITLE;
  private DownloadManagerActivity mActivity;
  private Context mContext;
  private ListOrderedMap mDataSource;
  private DownloadManager mDownloadManager;
  private ConcurrentHashMap<String, DownloadInfo> mDownloadingList;
  private LayoutInflater mInflater;
  private boolean mIsRefreshing;
  private long mLastModified;
  private View.OnClickListener mOperationListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = ((Integer)paramAnonymousView.getTag()).intValue();
      DownloadManagerAdapter.AppItem localAppItem = DownloadManagerAdapter.this.mDataSource.getValue(i);
      if (localAppItem.mWeight == 1)
      {
        DownloadInfo localDownloadInfo = (DownloadInfo)DownloadManagerAdapter.this.mDownloadingList.get(localAppItem.mPackageName);
        if (localDownloadInfo != null)
        {
          DownloadManager localDownloadManager = DownloadManagerAdapter.this.mDownloadManager;
          long[] arrayOfLong = new long[1];
          arrayOfLong[0] = localDownloadInfo.id;
          localDownloadManager.resumeDownload(arrayOfLong);
        }
      }
        else if (localAppItem.mWeight == 6)
        {
          if (!TextUtils.isEmpty(localAppItem.mFilePath))
            if (new File(localAppItem.mFilePath).exists())
              DownloadManagerAdapter.this.judgeInstallStatus(localAppItem);
            else
              Utils.makeEventToast(DownloadManagerAdapter.this.mContext, DownloadManagerAdapter.this.mContext.getString(2131296368), false);
        }
        else if ((localAppItem.mWeight == 3) || (localAppItem.mWeight == 4))
          DownloadManagerAdapter.this.cancelDownloadItem(localAppItem);
    }
  };
  private RefreshThread mRefreshThread;
  private long mRefreshTime;
  private Session mSession;
  private Handler mUiHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
//      switch (paramAnonymousMessage.what)
//      {
//      default:
//      case 0:
//      case 1:
//      }
//      while (true)
//      {
//        return;
//        ArrayList localArrayList = (ArrayList)paramAnonymousMessage.obj;
//        if ((localArrayList == null) || (localArrayList.size() == 0))
//        {
//          DownloadManagerAdapter.this.mActivity.refreshNoFiles(true);
//        }
//        else
//        {
//          DownloadManagerAdapter.this.mActivity.refreshNoFiles(false);
//          Iterator localIterator = localArrayList.iterator();
//          while (localIterator.hasNext())
//          {
//            DownloadManagerAdapter.AppItem localAppItem2 = (DownloadManagerAdapter.AppItem)localIterator.next();
//            DownloadManagerAdapter.this.mDataSource.put(localAppItem2.mPackageName, localAppItem2);
//          }
//          DownloadManagerAdapter.this.notifyDataSetChanged();
//          continue;
//          DownloadManagerAdapter.AppItem localAppItem1 = (DownloadManagerAdapter.AppItem)paramAnonymousMessage.obj;
//          DownloadManagerAdapter.this.mDataSource.remove(localAppItem1.mPackageName);
//          DownloadManagerAdapter.this.notifyDataSetChanged();
//        }
//      }
    }
  };

  public DownloadManagerAdapter(Context paramContext, ListOrderedMap paramListOrderedMap)
  {
    if (paramListOrderedMap == null);
    for (this.mDataSource = new ListOrderedMap(); ; this.mDataSource = paramListOrderedMap)
    {
      this.mContext = paramContext;
      this.mSession = Session.get(paramContext);
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mSession.addObserver(this);
      this.mDownloadManager = this.mSession.getDownloadManager();
      this.mDownloadingList = this.mSession.getDownloadingList();
      onDownloadingChanged(this.mDownloadingList);
      return;
    }
  }

  private void bindDownloadingView(int paramInt, View[] paramArrayOfView, AppItem paramAppItem)
  {
    if ((paramAppItem.mIcon instanceof Drawable))
      setImageView((ImageView)paramArrayOfView[0], (Drawable)paramAppItem.mIcon);
      else if ((paramAppItem.mIcon instanceof String))
        setImageView((ImageView)paramArrayOfView[0], (String)paramAppItem.mIcon);
    setTextView((TextView)paramArrayOfView[1], paramAppItem.mAppName);
    setProgressBar((ProgressBar)paramArrayOfView[2], paramAppItem.mProgress);
    setTextView((TextView)paramArrayOfView[3], paramAppItem.mInfo);
    setTextView(paramInt, (TextView)paramArrayOfView[4], paramAppItem.mWeight);
  }

  private void bindTitleView(View[] paramArrayOfView, AppItem paramAppItem)
  {
    setTextView((TextView)paramArrayOfView[0], paramAppItem.mTitle);
    setTextView((TextView)paramArrayOfView[1], paramAppItem.mInfo);
  }

  private void bindUninstalledView(int paramInt, View[] paramArrayOfView, AppItem paramAppItem)
  {
    if ((paramAppItem.mIcon instanceof Drawable))
      setImageView((ImageView)paramArrayOfView[0], (Drawable)paramAppItem.mIcon);
    else if ((paramAppItem.mIcon instanceof String))
            setImageView((ImageView)paramArrayOfView[0], (String)paramAppItem.mIcon);
      setTextView((TextView)paramArrayOfView[1], paramAppItem.mAppName);
      setTextView((TextView)paramArrayOfView[2], paramAppItem.mInfo);
      setTextView(paramInt, (TextView)paramArrayOfView[3], paramAppItem.mWeight);
  }

  private void bindView(int paramInt1, View paramView, int paramInt2)
  {
    AppItem localAppItem = this.mDataSource.getValue(paramInt1);
    if (localAppItem == null)
    	return;

      View[] arrayOfView = (View[])paramView.getTag();
      if (paramInt2 == 0)
        bindTitleView(arrayOfView, localAppItem);
      else if (paramInt2 == 1)
        bindDownloadingView(paramInt1, arrayOfView, localAppItem);
      else if (paramInt2 == 2)
        bindUninstalledView(paramInt1, arrayOfView, localAppItem);
  }

  private void cancelDownloadItem(AppItem paramAppItem)
  {
    DownloadInfo localDownloadInfo = (DownloadInfo)this.mDownloadingList.get(paramAppItem.mKey);
    if (localDownloadInfo == null)
      return;
      
      localDownloadInfo.mStatus = 490;
      DownloadManager localDownloadManager = this.mDownloadManager;
      long[] arrayOfLong = new long[1];
      arrayOfLong[0] = paramAppItem.mId;
      localDownloadManager.cancelDownload(arrayOfLong);
      Message localMessage = this.mUiHandler.obtainMessage();
      localMessage.obj = paramAppItem;
      localMessage.what = 1;
      this.mUiHandler.sendMessage(localMessage);
  }

  private boolean isPlaceHolder(int paramInt)
  {
    if (this.mDataSource == null)
      return false;

    boolean bool;
      if (paramInt >= this.mDataSource.size())
        bool = false;
      else if (this.mDataSource.getValue(paramInt).mViewType == 0)
        bool = true;
      else
        bool = false;
      return bool;
  }

  private void judgeInstallStatus(final AppItem paramAppItem)
  {
    if (!TextUtils.isEmpty(paramAppItem.mFilePath))
    {
      if (!Utils.compareFileWithPathAndPkg(this.mContext, paramAppItem.mFilePath, paramAppItem.mPackageName))
      {
    	  if (!this.mActivity.isFinishing())
    	        DialogUtil.createComfirmDownloadDialog(this.mActivity.getParent(), false, new DialogUtil.WarningDialogListener()
    	        {
    	          public void onWarningDialogCancel(int paramAnonymousInt)
    	          {
    	          }

    	          public void onWarningDialogOK(int paramAnonymousInt)
    	          {
    	            DownloadManagerAdapter.this.mSession.mNotSameApps.put(paramAppItem.mPackageName, paramAppItem.mFilePath);
    	            Utils.uninstallApk(DownloadManagerAdapter.this.mContext, paramAppItem.mPackageName);
    	          }
    	        }).show();
      }
      Utils.installApk(this.mContext, new File(paramAppItem.mFilePath));
    }
  }

  private View newView(int paramInt1, ViewGroup paramViewGroup, int paramInt2)
  {
	  View localObject1;
    Object localObject2;
    if (paramInt2 == 0)
    {
      View localView3 = this.mInflater.inflate(2130903045, paramViewGroup, false);
      View[] arrayOfView3 = new View[2];
      arrayOfView3[0] = localView3.findViewById(2131492875);
      arrayOfView3[1] = localView3.findViewById(2131492876);
      localObject1 = localView3;
      localObject2 = arrayOfView3;
    }
    else if (paramInt2 == 1)
      {
        View localView2 = this.mInflater.inflate(2130903042, paramViewGroup, false);
        View[] arrayOfView2 = new View[5];
        arrayOfView2[0] = localView2.findViewById(2131492867);
        arrayOfView2[1] = localView2.findViewById(2131492868);
        arrayOfView2[2] = localView2.findViewById(2131492869);
        arrayOfView2[3] = localView2.findViewById(2131492866);
        arrayOfView2[4] = localView2.findViewById(2131492871);
        localObject1 = localView2;
        localObject2 = arrayOfView2;
      }
      else if (paramInt2 == 2)
      {
        View localView1 = this.mInflater.inflate(2130903046, paramViewGroup, false);
        View[] arrayOfView1 = new View[4];
        arrayOfView1[0] = localView1.findViewById(2131492867);
        arrayOfView1[1] = localView1.findViewById(2131492868);
        arrayOfView1[2] = localView1.findViewById(2131492866);
        arrayOfView1[3] = localView1.findViewById(2131492871);
        localObject1 = localView1;
        localObject2 = arrayOfView1;
      }
      else
      {
        localObject1 = null;
        localObject2 = null;
      }
    ((View)localObject1).setTag(localObject2);
    return localObject1;
  }

  private void onDownloadingChanged(ConcurrentHashMap<String, DownloadInfo> paramConcurrentHashMap)
  {
    try
    {
      this.mDownloadingList = paramConcurrentHashMap;
      this.mLastModified = System.currentTimeMillis();
      if (!this.mIsRefreshing)
      {
        this.mRefreshThread = new RefreshThread();
        this.mRefreshThread.setPriority(10);
        this.mRefreshThread.start();
      }
    }
    finally
    {
    }
  }

  private void requestRefresh()
  {
    if (this.mDownloadingList == null)
      return;
    Collection localCollection = new HashMap(this.mDownloadingList).values();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    HashSet localHashSet = new HashSet(this.mDataSource.map.keySet());
    Iterator localIterator1 = localCollection.iterator();
    int j = 0;
    int k = 0;
    while (localIterator1.hasNext())
    {
      DownloadInfo localDownloadInfo = (DownloadInfo)localIterator1.next();
      if (localDownloadInfo.mProgressLevel == 13)
      {
        AppItem localAppItem8 = new AppItem();
        localAppItem8.mPackageName = localDownloadInfo.mPackageName;
        Message localMessage6 = this.mUiHandler.obtainMessage();
        localMessage6.obj = localAppItem8;
        localMessage6.what = 1;
        this.mUiHandler.sendMessage(localMessage6);
      }
      else
      {
        AppItem localAppItem9 = this.mDataSource.getValue(localDownloadInfo.mPackageName);
        if (localAppItem9 == null)
        {
          localAppItem9 = new AppItem();
          localAppItem9.mAppName = localDownloadInfo.mAppName;
          localAppItem9.mPackageName = localDownloadInfo.mPackageName;
          localAppItem9.mKey = localDownloadInfo.mKey;
          localAppItem9.mIcon = localDownloadInfo.mIconUrl;
        }
        localAppItem9.mId = localDownloadInfo.id;
        int n;
        int i1;
        int i2;
        if (DownloadManager.Impl.isStatusRunning(localDownloadInfo.mStatus))
        {
          int i6 = i + 1;
          localAppItem9.mViewType = 1;
          localAppItem9.mWeight = 3;
          localAppItem9.mProgress = localDownloadInfo.mProgressNumber;
          localAppItem9.mInfo = Utils.calculateRemainBytes(this.mContext, (float)localDownloadInfo.mCurrentSize, (float)localDownloadInfo.mTotalSize);
          n = k;
          i1 = i6;
          i2 = j;
        }

        else if (DownloadManager.Impl.isStatusWaiting(localDownloadInfo.mControl))
          {
            int i4 = j + 1;
            localAppItem9.mViewType = 2;
            localAppItem9.mWeight = 1;
            localAppItem9.mInfo = this.mContext.getString(2131296561);
            int i5 = k;
            i1 = i;
            i2 = i4;
            n = i5;
          }
          else if (DownloadManager.Impl.isStatusPending(localDownloadInfo.mStatus))
          {
            int i3 = i + 1;
            localAppItem9.mViewType = 1;
            localAppItem9.mWeight = 4;
            localAppItem9.mProgress = -1;
            localAppItem9.mInfo = this.mContext.getString(2131296559);
            i2 = j;
            n = k;
            i1 = i3;
          }
          else
          {
            if ((localDownloadInfo.mStatus != 200) || ((!TextUtils.isEmpty(localDownloadInfo.mFilePath)) && (!new File(localDownloadInfo.mFilePath).exists())))
              break;
            int m = k + 1;
            localAppItem9.mViewType = 2;
            localAppItem9.mWeight = 6;
            localAppItem9.mFilePath = localDownloadInfo.mFilePath;
            localAppItem9.mInfo = this.mContext.getString(2131296567);
            n = m;
            i1 = i;
            i2 = j;
          }
	        localArrayList.add(localAppItem9);
	        localHashSet.remove(localAppItem9.mKey);
	        j = i2;
	        i = i1;
	        k = n;
        }
      }

    Iterator localIterator2 = localHashSet.iterator();
    while (localIterator2.hasNext())
    {
      String str = (String)localIterator2.next();
      if ((!str.equals("title_downloading")) && (!str.equals("title_waiting")) && (!str.equals("title_downloaded")))
      {
        AppItem localAppItem7 = new AppItem();
        localAppItem7.mPackageName = str;
        Message localMessage5 = this.mUiHandler.obtainMessage();
        localMessage5.obj = localAppItem7;
        localMessage5.what = 1;
        this.mUiHandler.sendMessage(localMessage5);
      }
    }
    if (j > 0)
    {
      AppItem localAppItem1 = new AppItem();
      localAppItem1.mTitle = this.mContext.getString(2131296560);
      Context localContext1 = this.mContext;
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = Integer.valueOf(j);
      localAppItem1.mInfo = localContext1.getString(2131296556, arrayOfObject1);
      localAppItem1.mViewType = 0;
      localAppItem1.mWeight = 0;
      localAppItem1.mPackageName = "title_waiting";
      localArrayList.add(localAppItem1);
    }
      else
      {
    	  AppItem localAppItem6 = new AppItem();
          localAppItem6.mPackageName = "title_waiting";
          Message localMessage4 = this.mUiHandler.obtainMessage();
          localMessage4.obj = localAppItem6;
          localMessage4.what = 1;
          this.mUiHandler.sendMessage(localMessage4);  
      }
       if (i > 0)
       {
      AppItem localAppItem2 = new AppItem();
      localAppItem2.mTitle = this.mContext.getString(2131296566);
      Context localContext2 = this.mContext;
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = Integer.valueOf(i);
      localAppItem2.mInfo = localContext2.getString(2131296556, arrayOfObject2);
      localAppItem2.mViewType = 0;
      localAppItem2.mWeight = 2;
      localAppItem2.mPackageName = "title_downloading";
      localArrayList.add(localAppItem2);
       }
       else
       {
    	   AppItem localAppItem5 = new AppItem();
    	      localAppItem5.mPackageName = "title_downloading";
    	      Message localMessage3 = this.mUiHandler.obtainMessage();
    	      localMessage3.obj = localAppItem5;
    	      localMessage3.what = 1;
    	      this.mUiHandler.sendMessage(localMessage3);
       }
      if (k > 0)
      {
    	  AppItem localAppItem3 = new AppItem();
          localAppItem3.mTitle = this.mContext.getString(2131296565);
          Context localContext3 = this.mContext;
          Object[] arrayOfObject3 = new Object[1];
          arrayOfObject3[0] = Integer.valueOf(k);
          localAppItem3.mInfo = localContext3.getString(2131296556, arrayOfObject3);
          localAppItem3.mViewType = 0;
          localAppItem3.mWeight = 5;
          localAppItem3.mPackageName = "title_downloaded";
          localArrayList.add(localAppItem3);
      }
      else
      {
    	  AppItem localAppItem4 = new AppItem();
          localAppItem4.mPackageName = "title_downloaded";
          Message localMessage2 = this.mUiHandler.obtainMessage();
          localMessage2.obj = localAppItem4;
          localMessage2.what = 1;
          this.mUiHandler.sendMessage(localMessage2);
      }

    Message localMessage1 = this.mUiHandler.obtainMessage();
    localMessage1.what = 0;
    localMessage1.obj = localArrayList;
    this.mUiHandler.sendMessage(localMessage1);
  }

  private static void setImageView(ImageView paramImageView, Drawable paramDrawable)
  {
    paramImageView.setVisibility(0);
    Drawable localDrawable = paramImageView.getDrawable();
    if (localDrawable != null)
      localDrawable.setCallback(null);
    paramImageView.setImageDrawable(paramDrawable);
  }

  private void setImageView(ImageView paramImageView, String paramString)
  {
    paramImageView.setVisibility(0);
    Drawable localDrawable = paramImageView.getDrawable();
    if (localDrawable != null)
      localDrawable.setCallback(null);
    if ((paramString.startsWith("http")) || (paramString.startsWith("HTTP")))
      ImageUtils.download(this.mContext, paramString, paramImageView);
    else
    {
      ImageUtils.download(this.mContext, paramString, paramImageView, 2130837640);
    }
  }

  private static void setProgressBar(ProgressBar paramProgressBar, int paramInt)
  {
    if (paramInt < 0)
    {
      paramProgressBar.setIndeterminate(true);
      paramProgressBar.setVisibility(0);
    }
    else
    {
      paramProgressBar.setIndeterminate(false);
      paramProgressBar.setProgress(paramInt);
      paramProgressBar.setVisibility(0);
    }
  }

  private void setTextView(int paramInt1, TextView paramTextView, int paramInt2)
  {
    if (6 == paramInt2)
      if (!TextUtils.isEmpty(this.mDataSource.getValue(paramInt1).mFilePath))
      {
        paramTextView.setText(2131296364);
        paramTextView.setCompoundDrawablesWithIntrinsicBounds(null, this.mContext.getResources().getDrawable(2130837586), null, null);
        paramTextView.setVisibility(0);
      }
      else if (1 == paramInt2)
      {
        paramTextView.setText(2131296451);
        paramTextView.setCompoundDrawablesWithIntrinsicBounds(null, this.mContext.getResources().getDrawable(2130837578), null, null);
        paramTextView.setVisibility(0);
      }
      else if ((3 == paramInt2) || (4 == paramInt2))
      {
        paramTextView.setText(2131296568);
        paramTextView.setCompoundDrawablesWithIntrinsicBounds(null, this.mContext.getResources().getDrawable(2130837560), null, null);
        paramTextView.setVisibility(0);
      }
      else
      {
        paramTextView.setVisibility(4);
      }
    paramTextView.setTag(Integer.valueOf(paramInt1));
    paramTextView.setOnClickListener(this.mOperationListener);
    paramTextView.setFocusable(false);
    paramTextView.setFocusableInTouchMode(false);
  }

  private static void setTextView(TextView paramTextView, CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      paramTextView.setText(paramCharSequence);
      paramTextView.setVisibility(0);
    }
    else
    {
      paramTextView.setVisibility(8);
    }
  }

  public boolean areAllItemsEnabled()
  {
    return false;
  }

  void cancelDownloadItem(int paramInt)
  {
    cancelDownloadItem(this.mDataSource.getValue(paramInt));
  }

  public void clearData()
  {
    if (this.mDataSource != null)
    {
      this.mDataSource.clear();
      notifyDataSetChanged();
    }
  }

  void close()
  {
    this.mSession.deleteObserver(this);
    this.mDownloadingList = null;
    this.mDownloadManager = null;
  }

  void delApp(int paramInt)
  {
    AppItem localAppItem = this.mDataSource.getValue(paramInt);
    if (localAppItem != null)
    {
      DownloadManager localDownloadManager = this.mDownloadManager;
      long[] arrayOfLong = new long[1];
      arrayOfLong[0] = localAppItem.mId;
      localDownloadManager.deleteDownloadedFile(arrayOfLong);
      Message localMessage = this.mUiHandler.obtainMessage();
      localMessage.obj = localAppItem;
      localMessage.what = 1;
      this.mUiHandler.sendMessage(localMessage);
    }
  }

  public int getCount()
  {
    if (this.mDataSource == null);
    for (int i = 0; ; i = this.mDataSource.size())
      return i;
  }

  public Object getItem(int paramInt)
  {
    if ((this.mDataSource != null) && (paramInt < getCount()));
    for (AppItem localAppItem = this.mDataSource.getValue(paramInt); ; localAppItem = null)
      return localAppItem;
  }

  public long getItemId(int paramInt)
  {
    return paramInt;
  }

  public int getItemViewType(int paramInt)
  {
    AppItem localAppItem = this.mDataSource.getValue(paramInt);
    if (localAppItem == null);
    for (int i = 2; ; i = localAppItem.mViewType)
      return i;
  }

  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    int i = this.mDataSource.getValue(paramInt).mViewType;
    if (paramView == null);
    for (View localView = newView(paramInt, paramViewGroup, i); ; localView = paramView)
    {
      bindView(paramInt, localView, i);
      return localView;
    }
  }

  public int getViewTypeCount()
  {
    return 3;
  }

  void installAppWithPackageName(String paramString)
  {
    AppItem localAppItem = this.mDataSource.getValue(paramString);
    if (localAppItem == null)
      return;
    
      if ((this.mSession.isAutoDelete()) && (!TextUtils.isEmpty(localAppItem.mFilePath)))
        new File(localAppItem.mFilePath).delete();
      Message localMessage = this.mUiHandler.obtainMessage();
      localMessage.obj = localAppItem;
      localMessage.what = 1;
      this.mUiHandler.sendMessage(localMessage);
  }

  public boolean isEmpty()
  {
    if ((this.mDataSource == null) || (this.mDataSource.size() == 0));
    for (boolean bool = true; ; bool = super.isEmpty())
      return bool;
  }

  public boolean isEnabled(int paramInt)
  {
	 boolean bool = true;
    if (!isPlaceHolder(paramInt))
    	bool = false;
      return bool;
  }

  public void setActivity(DownloadManagerActivity paramDownloadManagerActivity)
  {
    this.mActivity = paramDownloadManagerActivity;
  }

  public void update(Observable paramObservable, Object paramObject)
  {
    if ((paramObject instanceof ConcurrentHashMap))
      onDownloadingChanged((ConcurrentHashMap)paramObject);
  }

  public static class AppItem
  {
    public CharSequence mAppName;
    public String mCurrentVersion;
    public String mCurrentVersionString;
    public Object mData;
    public String mFilePath;
    public Object mIcon;
    public long mId;
    public String mInfo;
    public String mInfo2;
    public boolean mIsSystemApp;
    public boolean mIsUpdate;
    public String mKey;
    public String mNewVersion;
    public String mNewVersionString;
    public String mPackageName;
    public String mProductId;
    public int mProgress;
    public String mRsaMd5;
    public String mTitle;
    public int mViewType;
    public int mWeight;

    public String toString()
    {
      return "[" + this.mAppName + "] weight " + this.mWeight + " viewType " + this.mViewType;
    }
  }

  public static class ListOrderedMap
  {
    private boolean isRefreshed;
    private ArrayList<DownloadManagerAdapter.AppItem> list;
    private Comparator<DownloadManagerAdapter.AppItem> mSortComparator;// = new Comparator<DownloadManagerAdapter.AppItem>(this)
//    		{
//    	public int compare(DownloadManagerAdapter.AppItem paramAppItem1, DownloadManagerAdapter.AppItem paramAppItem2)
//    	  {
//    	    return paramAppItem1.mWeight - paramAppItem2.mWeight;
//    	  }
//    		};
    private HashMap<String, DownloadManagerAdapter.AppItem> map = new HashMap();

    private void refresh()
    {
      synchronized (this.map)
      {
        Collection localCollection = this.map.values();
        if (localCollection != null)
        {
          ArrayList localArrayList = new ArrayList(localCollection);
          Collections.sort(localArrayList, this.mSortComparator);
          this.list = localArrayList;
          this.isRefreshed = true;
        }
      }
    }

    public void clear()
    {
      synchronized (this.map)
      {
        this.isRefreshed = false;
        this.map.clear();
        this.list.clear();
        return;
      }
    }

    // ERROR //
    public DownloadManagerAdapter.AppItem getValue(int paramInt)
    {
      return null;
    }

    public DownloadManagerAdapter.AppItem getValue(String paramString)
    {
      synchronized (this.map)
      {
        DownloadManagerAdapter.AppItem localAppItem = (DownloadManagerAdapter.AppItem)this.map.get(paramString);
        return localAppItem;
      }
    }

    public DownloadManagerAdapter.AppItem put(String paramString, DownloadManagerAdapter.AppItem paramAppItem)
    {
      synchronized (this.map)
      {
        this.isRefreshed = false;
        DownloadManagerAdapter.AppItem localAppItem = (DownloadManagerAdapter.AppItem)this.map.put(paramString, paramAppItem);
        return localAppItem;
      }
    }

    public DownloadManagerAdapter.AppItem remove(String paramString)
    {
      synchronized (this.map)
      {
        this.isRefreshed = false;
        DownloadManagerAdapter.AppItem localAppItem = (DownloadManagerAdapter.AppItem)this.map.remove(paramString);
        return localAppItem;
      }
    }

    public int size()
    {
      int j;
      synchronized (this.map)
      {
        if (this.map == null)
        {
          j = 0;
        }
        else
        {
          int i = this.map.size();
          j = i;
        }
      }
      return j;
    }
  }

  static boolean access$302(DownloadManagerAdapter downloadmanageradapter, boolean flag)
  {
      downloadmanageradapter.mIsRefreshing = flag;
      return flag;
  }

  static long access$502(DownloadManagerAdapter downloadmanageradapter, long l)
  {
      downloadmanageradapter.mRefreshTime = l;
      return l;
  }

  private class RefreshThread extends Thread
  {
    private RefreshThread()
    {
    }

    public void run()
    {
      DownloadManagerAdapter.access$302(DownloadManagerAdapter.this, true);
      while (true)
      {
        try
        {
          Thread.sleep(200L);
          if (DownloadManagerAdapter.this.mLastModified < DownloadManagerAdapter.this.mRefreshTime)
          {
            DownloadManagerAdapter.access$302(DownloadManagerAdapter.this, false);
            return;
          }
          else
          {
        	  DownloadManagerAdapter.access$502(DownloadManagerAdapter.this, System.currentTimeMillis());
              DownloadManagerAdapter.this.requestRefresh();
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          Utils.E("refresh downloading apps", localInterruptedException);
        }
      }
    }
  }
}