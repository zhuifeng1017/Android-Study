package com.xxx.appstore.common.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.download.DownloadManager;
import com.xxx.appstore.common.download.DownloadManager.Impl;
import com.xxx.appstore.common.download.DownloadManager.Request;
import com.xxx.appstore.common.util.DialogUtil;
import com.xxx.appstore.common.util.ImageUtils;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.DownloadInfo;
import com.xxx.appstore.common.vo.DownloadItem;
import com.xxx.appstore.common.vo.UpgradeInfo;
import com.xxx.appstore.ui.LoginActivity;
import com.xxx.appstore.ui.PreloadActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

public class AppListAdapter extends BaseAdapter
  implements Observer, ApiAsyncTask.ApiRequestListener
{
  private Activity mActivity;
  private CompoundButton.OnCheckedChangeListener mCheckChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      int i = ((Integer)paramAnonymousCompoundButton.getTag()).intValue();
      HashMap localHashMap = (HashMap)AppListAdapter.this.mDataSource.get(i);
      localHashMap.put("is_checked", Boolean.valueOf(paramAnonymousBoolean));
      if (paramAnonymousBoolean) {
        AppListAdapter.this.mCheckedList.put((String)localHashMap.get("p_id"), localHashMap);
        paramAnonymousCompoundButton.setChecked(paramAnonymousBoolean);
      }
      else {
        AppListAdapter.this.mCheckedList.remove((String)localHashMap.get("p_id"));
      }
    }
  };
  private HashMap<String, HashMap<String, Object>> mCheckedList;
  private Comparator mComparator;
  private Context mContext;
  private ArrayList<HashMap<String, Object>> mDataSource;
  private int mDividerResource;
  private HashMap<String, HashMap<String, Object>> mDownloadExtraInfo;
  private View.OnClickListener mDownloadListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Utils.V("onClick");
      int i = ((Integer)paramAnonymousView.getTag()).intValue();
      HashMap localHashMap = (HashMap)AppListAdapter.this.mDataSource.get(i);
      int j = ((Integer)localHashMap.get("product_download")).intValue();
      
      Object localObject = localHashMap.get("pay_category");
      int k = ((Integer)localObject).intValue();
      if (localObject != null)
      {
        if (12 == j)
        {
          String str7 = (String)localHashMap.get("packagename");
          DownloadInfo localDownloadInfo2 = (DownloadInfo)AppListAdapter.this.mDownloadingTask.get(str7);
          if (localDownloadInfo2 != null)
          {
            DownloadManager localDownloadManager = AppListAdapter.this.mDownloadManager;
            long[] arrayOfLong = new long[1];
            arrayOfLong[0] = localDownloadInfo2.id;
            localDownloadManager.resumeDownload(arrayOfLong);
          }
        }
        if (true)
        {
          if ((j == 0) || (10 == j))
          {
            if (AppListAdapter.this.mIsNeedSort)
            {
              Context localContext1 = AppListAdapter.this.mContext;
              String[] arrayOfString1 = new String[2];
              arrayOfString1[0] = "应用管理";
              arrayOfString1[1] = "点击更新";
              Utils.trackEvent(localContext1, arrayOfString1);
            }
            paramAnonymousView.setEnabled(false);
            if (2 == k)
            {
              if (Session.get(AppListAdapter.this.mContext).isLogin())
              {
                Intent localIntent1 = new Intent(AppListAdapter.this.mContext, PreloadActivity.class);
                localIntent1.putExtra("extra.key.pid", (String)localHashMap.get("p_id"));
                localIntent1.putExtra("is_buy", true);
                localIntent1.setFlags(268435456);
                AppListAdapter.this.mContext.startActivity(localIntent1);
              }
              else
              {
                Intent localIntent2 = new Intent(AppListAdapter.this.mContext, LoginActivity.class);
                localIntent2.setFlags(268435456);
                AppListAdapter.this.mContext.startActivity(localIntent2);
              }
            }
            else if (10 == j)
            {
              String str1 = (String)localHashMap.get("rsa_md5");
              String str2 = (String)localHashMap.get("packagename");
              if (!Utils.isSameSign(AppListAdapter.this.mContext, str2, str1))
              {
                if (AppListAdapter.this.mActivity.isFinishing())
                  return;
                //DialogUtil.createComfirmDownloadDialog(AppListAdapter.this.mActivity, true, new AppListAdapter.2.1(this, localHashMap)).show();
              }
            }
            else
            {
              AppListAdapter.this.startDownload(localHashMap);
            }
          }
          else if (9 == j)
          {
            String str5 = (String)localHashMap.get("packagename");
            String str6 = (String)localHashMap.get("info");
            DownloadInfo localDownloadInfo1 = (DownloadInfo)AppListAdapter.this.mDownloadingTask.get(str5);
            if (localDownloadInfo1 != null)
              str6 = localDownloadInfo1.mFilePath;
            if (!TextUtils.isEmpty(str6)){
              if (Utils.compareFileWithPathAndPkg(AppListAdapter.this.mContext, str6, str5))
                Utils.installApk(AppListAdapter.this.mContext, new File(str6));
            }
              //else
                //DialogUtil.createComfirmDownloadDialog(AppListAdapter.this.mActivity, false, new AppListAdapter(this, str5, str6)).show();
          }
          else if (11 == j)
          {
            if (AppListAdapter.this.mIsNeedSort)
            {
              Context localContext2 = AppListAdapter.this.mContext;
              String[] arrayOfString2 = new String[2];
              arrayOfString2[0] = "应用管理";
              arrayOfString2[1] = "点击卸载";
              Utils.trackEvent(localContext2, arrayOfString2);
              String str4 = (String)localHashMap.get("packagename");
              Utils.uninstallApk(AppListAdapter.this.mContext, str4);
            }
            else
            {
              String str3 = (String)localHashMap.get("packagename");
              Utils.openApk(AppListAdapter.this.mActivity, str3);
            }
          }
        }
      }
    }
  };
  
  private DownloadManager mDownloadManager;
  private ConcurrentHashMap<String, DownloadInfo> mDownloadingTask;
  private String[] mFrom;
  private boolean mHasGroup;
  private HashMap<String, String> mIconCache;
  private LayoutInflater mInflater;
  private ArrayList<String> mInstalledList;
  private boolean mIsAllDisabled;
  private boolean mIsLazyLoad;
  private boolean mIsNeedSort;
  private boolean mIsProductList;
  private boolean mIsRankList;
  private LazyloadListener mLazyloadListener;
  private int mResource;
  private Session mSession;
  private int[] mTo;
  private ConcurrentHashMap<String, UpgradeInfo> mUpdateList;

	public AppListAdapter(Context paramContext,
			ArrayList<HashMap<String, Object>> paramArrayList, int paramInt,
			String[] paramArrayOfString, int[] paramArrayOfInt) {
		if (paramArrayList == null)
			this.mDataSource = new ArrayList();
		else
			this.mDataSource = paramArrayList;

		this.mContext = paramContext;
		this.mResource = paramInt;
		this.mFrom = paramArrayOfString;
		this.mTo = paramArrayOfInt;
		this.mInflater = ((LayoutInflater) paramContext
				.getSystemService("layout_inflater"));
		this.mCheckedList = new HashMap();
		this.mIconCache = new HashMap();
		this.mSession = Session.get(paramContext);

		if (this.mIsNeedSort)
			Collections.sort(this.mDataSource, this.mComparator);

	}

  private void bindView(int i, View view)
  {
      HashMap hashmap = (HashMap)mDataSource.get(i);
      if(hashmap != null)
      {
          View aview[] = (View[])(View[])view.getTag();
          String as[] = mFrom;
          int j = mTo.length;
          int k = 0;
          while(k < j) 
          {
              View view1 = aview[k];
              if(view1 != null)
              {
                  Object obj = hashmap.get(as[k]);
                  if(obj == null)
                  {
                      view1.setVisibility(8);
                  } else
                  {
                      view1.setVisibility(0);
                      if(view1 instanceof Checkable)
                      {
                          view1.setTag(Integer.valueOf(i));
                          if(obj instanceof Boolean)
                              ((Checkable)view1).setChecked(((Boolean)obj).booleanValue());
                          else
                              throw new IllegalStateException((new StringBuilder()).append(view1.getClass().getName()).append(" should be bound to a Boolean, not a ").append(obj.getClass()).toString());
                      } else
                      if(view1 instanceof Button)
                          view1.setTag(obj);
                      else
                      if(!(view1 instanceof ImageButton))
                          if(view1 instanceof ImageView)
                              setViewImage(i, (ImageView)view1, obj);
                          else
                          if(view1 instanceof RatingBar)
                              setViewRating((RatingBar)view1, obj);
                          else
                          if(view1 instanceof TextView)
                              setViewText(i, (TextView)view1, obj);
                          else
                              throw new IllegalStateException((new StringBuilder()).append(view1.getClass().getName()).append(" is not a ").append(" view that can be bounds by this SimpleAdapter").toString());
                  }
              }
              k++;
          }
      }
  }

  private boolean isPlaceHolder(int paramInt)
  {
    return ((Boolean)((HashMap)this.mDataSource.get(paramInt)).get("place_holder")).booleanValue();
  }

  private View newView(int i, ViewGroup viewgroup)
  {
      View view;
      int ai[];
      int k;
      View aview[];
      if(mHasGroup && isPlaceHolder(i))
      {
          view = mInflater.inflate(mDividerResource, viewgroup, false);
      } else
      {
          view = mInflater.inflate(mResource, viewgroup, false);
          ((ViewGroup)view).setDescendantFocusability(0x60000);
      }
      ai = mTo;
      k = ai.length;
      aview = new View[k];
      for(int j = 0; j < k; j++)
      {
          aview[j] = view.findViewById(ai[j]);
          if(0x7f0c002b == ai[j] && aview[j] != null)
              ((CheckBox)aview[j]).setOnCheckedChangeListener(mCheckChangeListener);
      }

      view.setTag(aview);
      return view;
  }

	private void setViewImage(int i, ImageView imageview, Object obj) {
		Drawable drawable = imageview.getDrawable();
		if (drawable != null)
			drawable.setCallback(null);
		if (obj instanceof Drawable) {
			imageview.setImageDrawable((Drawable) obj);
			return;
		}

		if (obj instanceof String) {
			if (imageview.getId() == 0x7f0c000e)
				ImageUtils.download(mContext, (String) obj, imageview,
						0x7f020041, false);
			else
				ImageUtils.download(mContext, (String) obj, imageview);
		} else if (obj instanceof Boolean) {
			if (((Boolean) obj).booleanValue())
				imageview.setVisibility(0);
			else
				imageview.setVisibility(4);
		} else if (obj instanceof PackageInfo)
			ImageUtils.download(mContext, ((PackageInfo) obj).packageName,
					imageview, 0x7f020088);
	}

  private void setViewRating(RatingBar paramRatingBar, Object paramObject)
  {
    if ((paramObject instanceof Float))
      paramRatingBar.setRating(((Float)paramObject).floatValue());
  }

	private void setViewText(int i, TextView textview, Object obj) {
		if (obj instanceof byte[]) {
			textview.setText(Utils.getUTF8String((byte[]) (byte[]) obj));
			return;
		}

		int j;
		if (obj instanceof CharSequence) {
			if (mIsRankList && textview.getId() == 0x7f0c0004)
				textview.setText((new StringBuilder()).append(i + 1)
						.append(". ").append((CharSequence) obj).toString());
			else
				textview.setText((CharSequence) obj);
		}
		if (!(obj instanceof Integer))
			return;
		textview.setTag(Integer.valueOf(i));
		j = ((Integer) obj).intValue();
		textview.setCompoundDrawablesWithIntrinsicBounds(0, 0x7f020032, 0, 0);
		textview.getCompoundDrawables()[1].setLevel(j);

		textview.setText((String) ((HashMap) mDataSource.get(i)).get("price"));
		textview.setEnabled(true);

		if (1 == j)
			textview.setText(0x7f09006b);
		else if (9 == j) {
			textview.setText(0x7f09006c);
			textview.setEnabled(true);
		} else if (11 == j) {
			if (mIsNeedSort) {
				textview.setText(0x7f090139);
				textview.setCompoundDrawablesWithIntrinsicBounds(null, mContext
						.getResources().getDrawable(0x7f020038), null, null);
			} else {
				textview.setText(0x7f09006e);
			}
			textview.setEnabled(true);
		} else if (10 == j) {
			textview.setText(0x7f09013a);
			textview.setEnabled(true);
		} else if (12 == j) {
			textview.setText(0x7f0900c3);
			textview.setEnabled(true);
		} else {
			textview.setText((String) ((HashMap) mDataSource.get(i))
					.get("info"));
		}

		textview.setOnClickListener(mDownloadListener);
	}

	private void startDownload(HashMap hashmap)
    {
        if(!Utils.isNetworkAvailable(mContext))
        {
            Utils.makeEventToast(mContext, mContext.getString(0x7f0900a0), false);
        } else
        {
            String s = (String)hashmap.get("packagename");
            String s1 = (String)hashmap.get("p_id");
            String s2 = (String)hashmap.get("icon_url");
            hashmap.put("product_download", Integer.valueOf(1));
            mIconCache.put(s, s2);
            MarketAPI.getDownloadUrl(mContext, this, s1, "0");
            mDownloadExtraInfo.put(s1, hashmap);
            notifyDataSetChanged();
        }
    }

  public void addData(ArrayList<HashMap<String, Object>> paramArrayList)
  {
    if ((paramArrayList != null) && (paramArrayList.size() > 0))
    {
      this.mDataSource.addAll(getCount(), paramArrayList);
      if (this.mIsNeedSort)
        Collections.sort(this.mDataSource, this.mComparator);
      notifyDataSetChanged();
    }
  }

	public void addData(HashMap hashmap) {
		if (hashmap == null)
			return;

		if (!mIsNeedSort) {
			mDataSource.add(getCount(), hashmap);
			notifyDataSetChanged();
			return;
		}

		Iterator iterator = mDataSource.iterator();
		int i = 0;
		String s;
		String s1;
		boolean flag = false;
		while (iterator.hasNext()) {
			s = (String) ((HashMap) iterator.next()).get("packagename");
			s1 = (String) hashmap.get("packagename");
			if (!TextUtils.isEmpty(s) && s.equalsIgnoreCase(s1))
				flag = true;
		}
		if (flag)
			mDataSource.remove(i);
		mDataSource.add(getCount(), hashmap);
		notifyDataSetInvalidated();
		Collections.sort(mDataSource, mComparator);
	}

  public boolean areAllItemsEnabled()
  {
    return false;
  }

  public void clearData()
  {
    if (this.mDataSource != null)
    {
      this.mDataSource.clear();
      notifyDataSetChanged();
    }
  }

  public HashMap<String, HashMap<String, Object>> getCheckedList()
  {
    return this.mCheckedList;
  }

  public int getCount()
  {
      return (this.mDataSource == null)?0:this.mDataSource.size();
  }

  public Object getItem(int paramInt)
  {
	  Object localObject = null;
    if ((this.mDataSource != null) && (paramInt < getCount())) {
    	localObject = this.mDataSource.get(paramInt);
    }
      return localObject;
  }

  public long getItemId(int paramInt)
  {
    return paramInt;
  }

  public int getItemViewType(int paramInt)
  {
      return ((this.mHasGroup) && (isPlaceHolder(paramInt)))?1:0;
  }

  public View getView(int i, View view, ViewGroup viewgroup)
  {
	  if ((this.mIsLazyLoad) && (!this.mLazyloadListener.isEnd()) && (i == getCount() - 4)) {
      if(mLazyloadListener.isLoadOver())
          mLazyloadListener.lazyload();
	  }
      View view1;
      if(view == null)
          view1 = newView(i, viewgroup);
      else
          view1 = view;
      if(mIsProductList && mDownloadingTask != null)
      {
          HashMap hashmap = (HashMap)mDataSource.get(i);
          String s = (String)hashmap.get("packagename");
          if(mDownloadingTask.containsKey(s))
          {
              DownloadInfo downloadinfo = (DownloadInfo)mDownloadingTask.get(s);
              hashmap.put("info", downloadinfo.mProgress);
              Exception exception;
              if(downloadinfo.mProgressLevel == 11)
                  hashmap.put("product_download", Integer.valueOf(11));
              else
              if(downloadinfo.mProgressLevel == 13)
              {
                  if(mUpdateList.containsKey(s))
                  {
                      hashmap.put("rsa_md5", ((UpgradeInfo)mUpdateList.get(s)).signature);
                      hashmap.put("product_download", Integer.valueOf(10));
                  } else
                  {
                      hashmap.put("product_download", Integer.valueOf(0));
                  }
              } else
              if(com.xxx.appstore.common.download.DownloadManager.Impl.isStatusWaiting(downloadinfo.mControl))
                  hashmap.put("product_download", Integer.valueOf(12));
              else
              if(!TextUtils.isEmpty(downloadinfo.mFilePath) && !(new File(downloadinfo.mFilePath)).exists())
              {
                  if(mUpdateList.containsKey(s))
                  {
                      hashmap.put("rsa_md5", ((UpgradeInfo)mUpdateList.get(s)).signature);
                      hashmap.put("product_download", Integer.valueOf(10));
                  } else
                  {
                      hashmap.put("product_download", Integer.valueOf(0));
                  }
              } else
              {
                  hashmap.put("product_download", Integer.valueOf(downloadinfo.mProgressLevel));
              }
          } else
          if(mInstalledList.contains(s))
          {
              if(mUpdateList.containsKey(s))
              {
                  hashmap.put("product_download", Integer.valueOf(10));
                  hashmap.put("rsa_md5", ((UpgradeInfo)mUpdateList.get(s)).signature);
              } else
              {
                  hashmap.put("product_download", Integer.valueOf(11));
              }
          } else
          {
              hashmap.put("product_download", Integer.valueOf(0));
          }
      }
      bindView(i, view1);
      return view1;
  }

  public int getViewTypeCount()
  {
      return (this.mHasGroup)?2:1;
  }

  public void insertData(HashMap<String, Object> paramHashMap)
  {
    if (paramHashMap != null)
    {
      this.mDataSource.add(0, paramHashMap);
      if (this.mIsNeedSort)
        Collections.sort(this.mDataSource, this.mComparator);
      notifyDataSetChanged();
    }
  }

  public boolean isEmpty()
  {
      boolean flag;
      if(mDataSource == null || mDataSource.size() == 0)
          flag = true;
      else
          flag = super.isEmpty();
      return flag;
  }

  public boolean isEnabled(int i)
  {
      boolean flag;
      if(mIsAllDisabled)
          flag = false;
      else
      if(mHasGroup)
      {
          if(!isPlaceHolder(i))
              flag = true;
          else
              flag = false;
      } else
      {
          flag = true;
      }
      return flag;
  }

  public void onError(int paramInt1, int paramInt2)
  {
    Utils.makeEventToast(this.mContext, this.mContext.getString(2131296415), false);
  }

	public void onSuccess(int paramInt, Object paramObject) {

		DownloadItem localDownloadItem = (DownloadItem) paramObject;
		HashMap localHashMap = (HashMap) this.mDownloadExtraInfo
				.get(localDownloadItem.pId);
		DownloadManager.Request localRequest = new DownloadManager.Request(
				Uri.parse(localDownloadItem.url));
		localRequest.setTitle((String) localHashMap.get("name"));
		localRequest.setPackageName(localDownloadItem.packageName);
		localRequest.setIconUrl((String) this.mIconCache
				.get(localDownloadItem.packageName));
		localRequest.setSourceType(0);
		localRequest.setMD5(localDownloadItem.fileMD5);
		this.mDownloadManager.enqueue(this.mContext, localRequest, null);
		Utils.submitDownloadLog(this.mContext, 0, 0, localDownloadItem.url,
				localDownloadItem.packageName);
		Utils.makeEventToast(this.mContext,
				this.mContext.getString(2131296370), false);

	}

  public void removeData(int paramInt)
  {
    if (this.mDataSource != null)
    {
      this.mDataSource.remove(paramInt);
      notifyDataSetChanged();
    }
  }

  public void removeData(HashMap<String, Object> paramHashMap)
  {
    if (this.mDataSource != null)
    {
      this.mDataSource.remove(paramHashMap);
      notifyDataSetChanged();
    }
  }

	public void removeDataWithPackageName(String s) {
		if (mDataSource == null)
			return;

		int i = 0;
		boolean flag = false;
		int j = 0;
		Iterator iterator = mDataSource.iterator();
		String s1;
		while (iterator.hasNext()) {
			HashMap hashmap = (HashMap) iterator.next();
			i++;
			s1 = (String) hashmap.get("packagename");
			if (!TextUtils.isEmpty(s1) && s1.equalsIgnoreCase(s)) {
				flag = true;
				j = i;
			}
		}

		if (flag)
			mDataSource.remove(j - 1);
		notifyDataSetChanged();
	}

  public void setActivity(Activity paramActivity)
  {
    this.mActivity = paramActivity;
  }

  public void setAllDisabled()
  {
    this.mIsAllDisabled = true;
  }

  public void setContainsPlaceHolder(boolean paramBoolean)
  {
    this.mHasGroup = paramBoolean;
  }

  public void setLazyloadListener(LazyloadListener paramLazyloadListener)
  {
    this.mIsLazyLoad = true;
    this.mLazyloadListener = paramLazyloadListener;
  }

  public void setNeedSort(Comparator paramComparator)
  {
    this.mIsNeedSort = true;
    this.mComparator = paramComparator;
  }

  public void setPlaceHolderResource(int paramInt)
  {
    this.mDividerResource = paramInt;
  }

  public void setProductList()
  {
    this.mIsProductList = true;
    Session localSession = Session.get(this.mContext);
    localSession.addObserver(this);
    this.mDownloadManager = localSession.getDownloadManager();
    this.mInstalledList = localSession.getInstalledApps();
    this.mDownloadingTask = localSession.getDownloadingList();
    this.mUpdateList = localSession.getUpdateList();
    this.mDownloadExtraInfo = new HashMap();
  }

  public void setRankList()
  {
    this.mIsRankList = true;
  }

  protected void setViewResource(View paramView, int paramInt, int[] paramArrayOfInt)
  {
    if ((paramView instanceof ImageView))
      ((ImageView)paramView).setImageResource(paramArrayOfInt[((Integer)((HashMap)this.mDataSource.get(paramInt)).get(String.valueOf(paramInt))).intValue()]);
  }

  public void sort()
  {
    Collections.sort(this.mDataSource, this.mComparator);
    notifyDataSetChanged();
  }

	public void update(Observable observable, Object obj) {
		if (obj instanceof ConcurrentHashMap) {
			mDownloadingTask = (ConcurrentHashMap) obj;
			notifyDataSetChanged();
			return;
		}

		if (obj instanceof Integer)
			notifyDataSetChanged();
	}

  public void updateAll()
  {
    Iterator localIterator = this.mDataSource.iterator();
    while (localIterator.hasNext())
    {
      HashMap localHashMap = (HashMap)localIterator.next();
      String str = (String)localHashMap.get("packagename");
      if (this.mUpdateList.containsKey(str))
      {
        UpgradeInfo localUpgradeInfo = (UpgradeInfo)this.mUpdateList.get(str);
        if (((TextUtils.isEmpty(localUpgradeInfo.filePath)) || (!new File(localUpgradeInfo.filePath).exists())) && ((!this.mDownloadingTask.containsKey(str)) || (!DownloadManager.Impl.isStatusRunning(((DownloadInfo)this.mDownloadingTask.get(str)).mStatus))))
          startDownload(localHashMap);
      }
    }
  }

  public static abstract interface LazyloadListener
  {
    public abstract boolean isEnd();

    public abstract boolean isLoadOver();

    public abstract void lazyload();
  }
}