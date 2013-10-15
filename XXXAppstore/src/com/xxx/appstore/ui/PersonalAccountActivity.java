package com.xxx.appstore.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
//import com.unistrong.appstore.common.hudee.HudeeUtils;
import com.xxx.appstore.Session;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.ApiAsyncTask.ApiRequestListener;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.PayAndChargeLog;
import com.xxx.appstore.common.vo.PayAndChargeLogs;
import com.xxx.appstore.common.widget.BaseActivity;
import com.xxx.appstore.common.widget.LoadingDrawable;
import com.mobclick.android.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PersonalAccountActivity extends BaseActivity
  implements AdapterView.OnItemClickListener, ApiRequestListener
{
  private static final int ACCOUNT_BIND = 4;
  private static final int ACCOUNT_REGIST = 0;
  public static final int CLOUD_BIND = 2;
  public static final int CLOUD_UNBIND = 3;
  public static final int REGIST = 1;
  private static final int REQUEST_CODE = 20;
  private boolean isBinding;
  private boolean isFirstAccess = true;
  private PersonalAccountAdapter mAdapter;
  private Handler mHandler = new Handler()
  {
	  public void handleMessage(Message paramMessage)
	  {
	    switch (paramMessage.what)
	    {
	    default:
	    	break;
	    case 1:
//	    	ArrayList localArrayList = PersonalAccountActivity.access$100(this.this$0);
//		      PersonalAccountActivity.access$200(this.this$0).changeDataSource(localArrayList);
		      break;
	    case 2:
//	    	this.this$0.showDialog(4);
	    	break;
	    case 3:
//	    	PersonalAccountActivity.access$300(this.this$0);
	    	break;
	    }
	  }
  };
  private ListView mList;
  private FrameLayout mLoading;
  private ProgressBar mProgress;
  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
	  public void onReceive(Context paramContext, Intent paramIntent)
	  {
//	    String str1;
//	    String str3;
//	    if ("com.hudee.pns.intent.REGISTRATION".equals(paramIntent.getAction()))
//	    {
//	      str1 = paramIntent.getStringExtra("error");
//	      String str2 = paramIntent.getStringExtra("unregistered");
//	      str3 = paramIntent.getStringExtra("registration_id");
//	      if (str2 == null)
//	        break label41;
//	    }
//	    while (true)
//	    {
//	      return;
//	      label41: if (str1 != null)
//	      {
//	        Utils.makeEventToast(this.this$0.getApplicationContext(), this.this$0.getString(2131296582), true);
//	      }
//	      else
//	      {
//	        PersonalAccountActivity.access$000(this.this$0).setDeviceId(str3);
//	        MarketAPI.bindAccount(this.this$0.getApplicationContext(), this.this$0);
//	      }
//	    }
	  }
  };

  private ArrayList<HashMap<String, Object>> doInitFuncData()
  {
    ArrayList localArrayList = new ArrayList();
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = 2130837729;
    arrayOfInt[1] = 2130837728;
    arrayOfInt[2] = 2130837730;
    String[] arrayOfString1 = new String[3];
    arrayOfString1[0] = getString(2131296331);
    arrayOfString1[1] = getString(2131296332);
    arrayOfString1[2] = getString(2131296340);
    String[] arrayOfString2 = new String[3];
    arrayOfString2[0] = getString(2131296333);
    arrayOfString2[1] = getString(2131296334);
    arrayOfString2[2] = getString(2131296335);
    for (int i = 0; i < 3; i++)
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("icon", Integer.valueOf(arrayOfInt[i]));
      localHashMap.put("title", arrayOfString1[i]);
      localHashMap.put("desc", arrayOfString2[i]);
      localHashMap.put("download", Integer.valueOf(2130837566));
      localHashMap.put("arrow", Integer.valueOf(2130837717));
      localHashMap.put("account_type", Integer.valueOf(9));
      localArrayList.add(localHashMap);
    }
    return localArrayList;
  }

  private PersonalAccountAdapter doInitPayAdapter()
  {
    ArrayList localArrayList = doInitFuncData();
    String[] arrayOfString = new String[6];
    arrayOfString[0] = "icon";
    arrayOfString[1] = "title";
    arrayOfString[2] = "desc";
    arrayOfString[3] = "time";
    arrayOfString[4] = "download";
    arrayOfString[5] = "arrow";
    int[] arrayOfInt = new int[6];
    arrayOfInt[0] = 2131492864;
    arrayOfInt[1] = 2131492868;
    arrayOfInt[2] = 2131492905;
    arrayOfInt[3] = 2131492908;
    arrayOfInt[4] = 2131492916;
    arrayOfInt[5] = 2131492917;
    return new PersonalAccountAdapter(this, localArrayList, 2130903064, arrayOfString, arrayOfInt, this.mHandler);
  }

  private void initTopBar()
  {
    Session localSession = this.mSession;
    View[] arrayOfView = new View[1];
    arrayOfView[0] = findViewById(2131493035);
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 0;
    TopBar.createTopBar(localSession, this, arrayOfView, arrayOfInt, getString(2131296329));
  }

  private void initView()
  {
    this.mList = ((ListView)findViewById(16908298));
    this.mLoading = ((FrameLayout)findViewById(2131492978));
    this.mProgress = ((ProgressBar)this.mLoading.findViewById(2131492869));
    this.mProgress.setIndeterminateDrawable(new LoadingDrawable(getApplicationContext()));
    this.mAdapter = doInitPayAdapter();
    this.mList.setAdapter(this.mAdapter);
    this.mList.setItemsCanFocus(false);
    this.mList.setChoiceMode(2);
    this.mList.setOnItemClickListener(this);
  }

  private ArrayList transferDataType(PayAndChargeLogs payandchargelogs)
  {
      ArrayList arraylist = null;
      ArrayList arraylist1 = payandchargelogs.payAndChargeLogList;
      if(payandchargelogs != null && arraylist1.size() > 0)
      {
          ArrayList arraylist2 = new ArrayList(1 + payandchargelogs.totalSize);
          HashMap hashmap = new HashMap();
          hashmap.put("account_type", Integer.valueOf(8));
          String s = getString(0x7f090052);
          Object aobj[] = new Object[1];
          aobj[0] = Integer.valueOf(payandchargelogs.totalSize);
          hashmap.put("time", String.format(s, aobj));
          hashmap.put("title", getString(0x7f090055));
          hashmap.put("place_holder", Boolean.valueOf(true));
          arraylist2.add(hashmap);
          HashMap hashmap1;
          for(Iterator iterator = arraylist1.iterator(); iterator.hasNext(); arraylist2.add(hashmap1))
          {
              PayAndChargeLog payandchargelog = (PayAndChargeLog)iterator.next();
              hashmap1 = new HashMap();
              hashmap1.put("icon", payandchargelog.iconUrl);
              hashmap1.put("title", payandchargelog.name);
              String s1 = getString(0x7f090102);
              Object aobj1[] = new Object[1];
              aobj1[0] = Integer.valueOf(payandchargelog.payment);
              hashmap1.put("desc", String.format(s1, aobj1));
              hashmap1.put("time", (new StringBuilder()).append(payandchargelog.time).append(" ").append(getString(0x7f090053)).toString());
              hashmap1.put("account_type", Integer.valueOf(payandchargelog.type));
          }

          arraylist = arraylist2;
      }
      return arraylist;
  }

  private void unBindAccount()
  {
 //   HudeeUtils.unregisterLPNS(getApplicationContext(), this.mSession.getDeviceId());
    MarketAPI.unbindAccount(getApplicationContext(), this);
  }

  public boolean getCurrentBindStatue()
  {
    return this.isBinding;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903063);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.hudee.pns.intent.REGISTRATION");
    localIntentFilter.addCategory("com.unistrong.appstore");
    registerReceiver(this.mReceiver, localIntentFilter, null, null);
    initTopBar();
    initView();
  }

  protected Dialog onCreateDialog(int paramInt)
  {
	  Dialog localObject = super.onCreateDialog(paramInt);
    switch (paramInt)
    {
    default:
      break;
    case 0:
 //   	localObject = new AlertDialog.Builder(this).setIcon(17301659).setTitle(getString(2131296348)).setPositiveButton(2131296378, new PersonalAccountActivity.4(this)).setNegativeButton(2131296379, new PersonalAccountActivity.3(this, paramInt)).create();
    	break;
    case 4:
 //  	localObject = new AlertDialog.Builder(this).setIcon(17301659).setTitle(getString(2131296351)).setMessage(2131296349).setPositiveButton(2131296386, new PersonalAccountActivity.6(this)).setNegativeButton(2131296375, new PersonalAccountActivity.5(this, paramInt)).create();
    	break;
    }
    
     return localObject;
  }

  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(this.mReceiver);
  }

  public void onError(int paramInt1, int paramInt2)
  {
    if (20 == paramInt1)
    {
      this.mAdapter.notifyDataSetChanged();
      return;
    }
      
      Utils.W("bind account error");
      Utils.makeEventToast(getApplicationContext(), getString(2131296350), true);
      ((HashMap)this.mAdapter.getDataSource().get(1)).put("download", Integer.valueOf(2130837566));
      this.mAdapter.notifyDataSetChanged();
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    switch (paramInt)
    {
    default:
    	break;
    case 0:
    	if (!this.mSession.isLogin())
        {
          startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 20);
        }
        else if (this.mSession.isLogin())
        {
          showDialog(0);
        }
    	break;
    case 1:
    	if (this.mSession.isDeviceBinded())
        {
          unBindAccount();
        }
        else if (!this.isBinding)
        {
          showDialog(4);
        }
    	break;
    case 2:
    {
    	Context localContext = getApplicationContext();
        String[] arrayOfString = new String[2];
        arrayOfString[0] = "个人中心";
        arrayOfString[1] = "点击充值";
        Utils.trackEvent(localContext, arrayOfString);
        String str = this.mSession.getDefaultChargeType();
        if (str == null)
        {
          Intent localIntent1 = new Intent(this, ChargeTypeListActivity.class);
          localIntent1.setFlags(268435456);
          startActivity(localIntent1);
        }
        else
        {
          Intent localIntent2 = new Intent(this, PayMainActivity.class);
          localIntent2.setFlags(268435456);
          localIntent2.putExtra("type", str);
          startActivity(localIntent2);
        }
    }
    	break;
    }
    
  }

  protected void onPause()
  {
    if (this.mSession.isLogin())
      this.isFirstAccess = false;
    super.onPause();
    MobclickAgent.onPause(this);
  }

  protected void onResume()
  {
    if ((this.mSession.isLogin()) && (this.isFirstAccess))
    {
      this.mProgress.setVisibility(0);
      MarketAPI.getBalance(getApplicationContext(), this);
      MarketAPI.getPayLog(getApplicationContext(), this, 0, 10);
    }
    super.onResume();
    MobclickAgent.onResume(this);
  }

  public void onSuccess(int paramInt, Object paramObject)
  {
    switch (paramInt)
    {
    default:
    	break;
    case 20:
    	this.mSession.setDeviceBinded(true);
        Utils.makeEventToast(getApplicationContext(), getString(2131296353), true);
        ((HashMap)this.mAdapter.getDataSource().get(1)).put("download", Integer.valueOf(2130837567));
        this.mAdapter.notifyDataSetChanged();
        this.isBinding = false;
    	break;
    case 21:
    	HashMap localHashMap1 = (HashMap)this.mAdapter.getDataSource().get(2);
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = paramObject.toString();
        localHashMap1.put("desc", getString(2131296336, arrayOfObject));
        this.mAdapter.notifyDataSetChanged();
    	break;
    case 22:
    	PayAndChargeLogs localPayAndChargeLogs = (PayAndChargeLogs)paramObject;
        if ((localPayAndChargeLogs != null) && (localPayAndChargeLogs.totalSize > 0))
        {
          ArrayList localArrayList = transferDataType(localPayAndChargeLogs);
          this.mAdapter.addData(localArrayList);
          this.mProgress.setVisibility(8);
        }
        else
        {
          this.mProgress.setVisibility(8);
          HashMap localHashMap2 = new HashMap();
          localHashMap2.put("account_type", Integer.valueOf(10));
          localHashMap2.put("title", getString(2131296342));
          this.mAdapter.addData(localHashMap2);
        }
    	break;
    case 29:
    	 this.mSession.setDeviceBinded(false);
         ((HashMap)this.mAdapter.getDataSource().get(1)).put("download", Integer.valueOf(2130837566));
         this.mAdapter.notifyDataSetChanged();
    	break;
    }
  }
}