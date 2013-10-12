package com.mappn.gfan.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mappn.gfan.common.util.TopBar;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mobclick.android.MobclickAgent;

public class DownloadManagerActivity extends BaseActivity
  implements AdapterView.OnItemClickListener
{
  private static final int CONTEXT_MENU_CANCEL = 1;
  //private static final int CONTEXT_MENU_DELETE_FILE;
  private DownloadManagerAdapter mAdapter;
  private BroadcastReceiver mInstallReceiver = new BroadcastReceiver(){
	  public void onReceive(Context paramContext, Intent paramIntent)
	  {
	    String str1 = paramIntent.getAction();
	    String str2 = paramIntent.getData().getSchemeSpecificPart();
	    if ("android.intent.action.PACKAGE_ADDED".equals(str1))
	    	mAdapter.installAppWithPackageName(str2);
	  }
  };
  ListView mList;
  private FrameLayout mLoading;
  private int mLongClickPos;
  private TextView mNoData;
  private ProgressBar mProgress;

  public DownloadManagerAdapter doInitListAdapter()
  {
    return new DownloadManagerAdapter(getApplicationContext(), null);
  }

  public boolean doInitView(Bundle paramBundle)
  {
    this.mLoading = ((FrameLayout)findViewById(2131492978));
    this.mProgress = ((ProgressBar)this.mLoading.findViewById(2131492869));
    this.mProgress.setIndeterminateDrawable(new LoadingDrawable(getApplicationContext()));
    this.mProgress.setVisibility(0);
    this.mNoData = ((TextView)this.mLoading.findViewById(2131492979));
    this.mList = ((ListView)findViewById(16908298));
    this.mList.setEmptyView(this.mLoading);
    this.mList.setOnItemClickListener(this);
    this.mList.setItemsCanFocus(true);
    this.mAdapter = doInitListAdapter();
    this.mAdapter.setActivity(this);
    this.mList.setAdapter(this.mAdapter);
    this.mList.setItemsCanFocus(false);
    registerForContextMenu(this.mList);
    return true;
  }

  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    boolean bool;
    switch (paramMenuItem.getItemId())
    {
    default:
      break;
    case 0:
      this.mAdapter.delApp(this.mLongClickPos);
      break;
    case 1:
    	this.mAdapter.cancelDownloadItem(this.mLongClickPos);
    	break;
    }
    bool = super.onContextItemSelected(paramMenuItem);
    return bool;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903044);
    doInitView(paramBundle);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addDataScheme("package");
    registerReceiver(this.mInstallReceiver, localIntentFilter);
  }

  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    this.mLongClickPos = ((AdapterView.AdapterContextMenuInfo)paramContextMenuInfo).position;
    DownloadManagerAdapter.AppItem localAppItem = (DownloadManagerAdapter.AppItem)this.mAdapter.getItem(this.mLongClickPos);
    if (localAppItem.mWeight == 1)
      paramContextMenu.add(0, 1, 0, 2131296574);
    else if (localAppItem.mViewType == 2)
        paramContextMenu.add(0, 0, 0, 2131296574);
  }

  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(this.mInstallReceiver);
    this.mAdapter.close();
    this.mAdapter = null;
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    DownloadManagerAdapter.AppItem localAppItem = (DownloadManagerAdapter.AppItem)this.mAdapter.getItem(paramInt);
    Intent localIntent = new Intent(getApplicationContext(), PreloadActivity.class);
    localIntent.putExtra("extra.key.package.name", localAppItem.mPackageName);
    startActivity(localIntent);
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return getParent().onKeyDown(paramInt, paramKeyEvent);
  }

  protected void onPause()
  {
    super.onPause();
    MobclickAgent.onPause(getParent());
  }

  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    super.onPrepareDialog(paramInt, paramDialog);
    if (paramDialog.isShowing())
      paramDialog.dismiss();
  }

  protected void onResume()
  {
    super.onResume();
    MobclickAgent.onResume(getParent());
  }

  void refreshNoFiles(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mNoData.setCompoundDrawablesWithIntrinsicBounds(0, 2130837720, 0, 0);
      this.mNoData.setText(2131296551);
      this.mNoData.setCompoundDrawablePadding(10);
      this.mNoData.setVisibility(0);
      this.mProgress.setVisibility(8);
    }
    else
    {
      this.mNoData.setVisibility(8);
      this.mProgress.setVisibility(0);
    }
  }
}