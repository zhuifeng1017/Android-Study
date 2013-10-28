package com.xxx.appstore.ui;

import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.ThemeManager;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.widget.BaseTabActivity;

import java.util.ArrayList;

public class SearchActivity extends BaseTabActivity
  implements View.OnClickListener, View.OnFocusChangeListener, ApiAsyncTask.ApiRequestListener
{
  private static final int MARGIN_LEFT_RIGHT = 15;
  private static final int MARGIN_TOP_BOTTOM = 20;
  private static final int PADING_LEFT_RIGHT = 20;
  private static final int PADING_TOP_BOTTOM = 15;
  private static final String TAB_BBS_ID = "bbs";
  private static final String TAB_PRODUCT_ID = "product";
  private static int[] sHotBackgound;
  private AutoCompleteTextView mAutoCompleteTextView;
  private ArrayList<String> mHistory;
  private LinearLayout mKeywordsLayout;
  private ArrayAdapter<String> mSearchHistoryAdapter;
  private TabHost mTabHost;
  private BroadcastReceiver mThemeReceiver = null;//new SearchActivity.1(this);
  private ImageButton searchBtn;

  private TextView createTextView(int paramInt, String paramString)
  {
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2);
    localLayoutParams.leftMargin = 15;
    localLayoutParams.rightMargin = 15;
    localLayoutParams.topMargin = 20;
    localLayoutParams.bottomMargin = 20;
    TextView localTextView = new TextView(getApplicationContext());
    localTextView.setText(paramString);
    localTextView.setLayoutParams(localLayoutParams);
    localTextView.setGravity(16);
    localTextView.setOnClickListener(this);
    localTextView.setBackgroundResource(sHotBackgound[paramInt]);
    localTextView.setPadding(20, 15, 20, 15);
    localTextView.setTextAppearance(getApplicationContext(), 2131361805);
    localTextView.setFocusableInTouchMode(false);
    localTextView.setGravity(17);
    return localTextView;
  }

  private void doSearch()
  {
    String str = this.mAutoCompleteTextView.getText().toString().trim();
    if (TextUtils.isEmpty(str))
      resetCurrentActivity();
    else
    {
      if (!this.mTabHost.isShown())
      {
        this.mTabHost.setVisibility(0);
        this.mKeywordsLayout.setVisibility(8);
        this.mTabHost.setCurrentTabByTag("product");
      }
      storeToAdapter(str);
      toogleInputMethod(false);
      SearchResultActivity localSearchResultActivity = (SearchResultActivity)getCurrentActivity();
      localSearchResultActivity.setSearchKeyword(str);
      localSearchResultActivity.lazyload();
    }
  }

  private LinearLayout getHorizontalLinearLayout(LinearLayout.LayoutParams paramLayoutParams)
  {
    LinearLayout localLinearLayout = new LinearLayout(getApplicationContext());
    localLinearLayout.setLayoutParams(paramLayoutParams);
    localLinearLayout.setGravity(17);
    return localLinearLayout;
  }

  private void initData()
  {
    DBUtils.querySearchHistory(getApplicationContext(), new DBUtils.DbOperationResultListener()
    {
    	protected void onQueryResult(ArrayList<String> paramArrayList)
    	  {
//    	    if (SearchActivity.access$200(this.this$0) == null)
//    	      SearchActivity.access$202(this.this$0, paramArrayList);
//    	    while (true)
//    	    {
//    	      SearchActivity.access$302(this.this$0, new ArrayAdapter(getApplicationContext(), 2130903076, mHistory));
//    	      SearchActivity.access$400(this.this$0).setAdapter(SearchActivity.access$300(this.this$0));
//    	      SearchActivity.access$400(this.this$0).setThreshold(1);
//    	      MarketAPI.getSearchKeywords(this.this$0.getApplicationContext(), this.this$0);
//    	      return;
//    	      SearchActivity.access$200(this.this$0).addAll(paramArrayList);
//    	    }
    	  }
    });
  }

	private void initSearchKeywordsView(ArrayList<String> paramArrayList) {
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				-1, -2);
		int i = getWindowManager().getDefaultDisplay().getWidth();
		int j = paramArrayList.size();
		float f = 0.0F;

		Object localObject1 = null;
		TextView localTextView;
		for (int k = 0; k < j; k++) {
			String str = (String) paramArrayList.get(k);
			localTextView = createTextView(k, str);
			f += 30.0F + (40.0F + localTextView.getPaint().measureText(str));
			if (f > i) {
				break;
			} else {
				LinearLayout localLinearLayout = getHorizontalLinearLayout(localLayoutParams);
				this.mKeywordsLayout.addView(localLinearLayout);
				localLinearLayout.addView(localTextView);
			}
		}
	}

	private void initSkin() {
		findViewById(2131492901).setBackgroundResource(
				ThemeManager.getResource(this.mSession, 14));

		for (int i = 0; i < 2; i++) {
			TextView localTextView = (TextView) this.mTabHost.getTabWidget()
					.getChildTabViewAt(i);
			if (i == 0)
				Utils.createTabView(getApplicationContext(), this.mSession,
						getString(2131296264), -1, localTextView);
			else if (i == 1)
				Utils.createTabView(getApplicationContext(), this.mSession,
						getString(2131296265), 1, localTextView);
		}
	}

  private void initTabView()
  {
    this.mTabHost = ((TabHost)findViewById(16908306));
    this.mTabHost.setup();
    this.mTabHost.getTabWidget().setPadding(this.mSession.mTabMargin72, 0, this.mSession.mTabMargin72, 0);
    ((FrameLayout)this.mTabHost.findViewById(2131492901)).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));
    Intent localIntent1 = new Intent(getApplicationContext(), SearchResultActivity.class);
    localIntent1.putExtra("extra.search.type", 0);
    TabHost.TabSpec localTabSpec1 = this.mTabHost.newTabSpec("product").setIndicator(Utils.createTabView(getApplicationContext(), this.mSession, getString(2131296264), -1, null)).setContent(localIntent1);
    this.mTabHost.addTab(localTabSpec1);
    Intent localIntent2 = new Intent(getApplicationContext(), SearchResultActivity.class);
    localIntent2.putExtra("extra.search.type", 1);
    TabHost.TabSpec localTabSpec2 = this.mTabHost.newTabSpec("bbs").setIndicator(Utils.createTabView(getApplicationContext(), this.mSession, getString(2131296265), 1, null)).setContent(localIntent2);
    this.mTabHost.addTab(localTabSpec2);
    this.mTabHost.setOnTabChangedListener(new OnTabChangeListener()
    {
    	public void onTabChanged(String paramString)
    	  {
    	    if ("bbs".equals(paramString))
    	    {
    	      Context localContext = getApplicationContext();
    	      String[] arrayOfString = new String[2];
    	      arrayOfString[0] = "搜索";
    	      arrayOfString[1] = "点击论坛TAB";
    	      Utils.trackEvent(localContext, arrayOfString);
    	    }
//    	    SearchActivity.access$500(this.this$0);
    	  }
    });
  }

  private void initTopBar()
  {
    Session localSession = this.mSession;
    View[] arrayOfView = new View[2];
    arrayOfView[0] = findViewById(2131493036);
    arrayOfView[1] = findViewById(2131493033);
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 0;
    arrayOfInt[1] = 0;
    TopBar.createTopBar(localSession, this, arrayOfView, arrayOfInt, "");
    findViewById(2131493033).setOnClickListener(this);
  }

  private void initView()
  {
    this.searchBtn = ((ImageButton)findViewById(2131493033));
    this.searchBtn.setOnClickListener(this);
    this.mAutoCompleteTextView = ((AutoCompleteTextView)findViewById(2131493036));
    this.mAutoCompleteTextView.setOnFocusChangeListener(this);
    int[] arrayOfInt = new int[15];
    arrayOfInt[0] = 2130837624;
    arrayOfInt[1] = 2130837624;
    arrayOfInt[2] = 2130837624;
    arrayOfInt[3] = 2130837625;
    arrayOfInt[4] = 2130837625;
    arrayOfInt[5] = 2130837625;
    arrayOfInt[6] = 2130837626;
    arrayOfInt[7] = 2130837626;
    arrayOfInt[8] = 2130837626;
    arrayOfInt[9] = 2130837627;
    arrayOfInt[10] = 2130837627;
    arrayOfInt[11] = 2130837627;
    arrayOfInt[12] = 2130837628;
    arrayOfInt[13] = 2130837628;
    arrayOfInt[14] = 2130837628;
    sHotBackgound = arrayOfInt;
    this.mKeywordsLayout = ((LinearLayout)findViewById(2131492933));
    initTabView();
  }

  private void resetCurrentActivity()
  {
    ((SearchResultActivity)getCurrentActivity()).resetSearchResult();
  }

  private void showTabView()
  {
    resetCurrentActivity();
    this.mTabHost.setVisibility(0);
    this.mKeywordsLayout.setVisibility(8);
  }

  private void storeToAdapter(String paramString)
  {
    if (this.mHistory == null)
      this.mHistory = new ArrayList();
    if (!this.mHistory.contains(paramString))
    {
      this.mHistory.add(paramString);
      this.mSearchHistoryAdapter.add(paramString);
      this.mSearchHistoryAdapter.notifyDataSetChanged();
      DBUtils.addSearchItem(getApplicationContext(), paramString);
    }
  }

  private void toogleInputMethod(boolean paramBoolean)
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)getSystemService("input_method");
    if (paramBoolean)
    {
      localInputMethodManager.showSoftInputFromInputMethod(this.mAutoCompleteTextView.getWindowToken(), 0);
      showTabView();
    }
    else
    {
      localInputMethodManager.hideSoftInputFromWindow(this.mAutoCompleteTextView.getWindowToken(), 0);
    }
  }

  public String getKeyword()
  {
    if (this.mAutoCompleteTextView != null);
    for (String str = this.mAutoCompleteTextView.getText().toString(); ; str = null)
      return str;
  }

  public void onClick(View paramView)
  {
	  Context localContext = getApplicationContext();
	  String[] arrayOfString = new String[2];
    if (2131493033 == paramView.getId())
    {
    	arrayOfString[0] = "搜索";
    	arrayOfString[1] = "点击热门关键词";
      this.mAutoCompleteTextView.setText(((TextView)paramView).getText());
    }
    else
    {
      arrayOfString[0] = "搜索";
      arrayOfString[1] = "点击搜索按钮";
    }
    Utils.trackEvent(localContext, arrayOfString);
    doSearch();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903078);
    initTopBar();
    initView();
    initData();
    IntentFilter localIntentFilter = new IntentFilter("com.xxx.appstore.theme");
    registerReceiver(this.mThemeReceiver, localIntentFilter);
  }

  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(this.mThemeReceiver);
    LocalActivityManager localLocalActivityManager = getLocalActivityManager();
    localLocalActivityManager.removeAllActivities();
    localLocalActivityManager.dispatchDestroy(true);
  }

  public void onError(int paramInt1, int paramInt2)
  {
    Utils.D("fetch keywords fail because of status " + paramInt2);
  }

  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    if (2131493036 == paramView.getId())
    {
      toogleInputMethod(paramBoolean);
    }
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.mTabHost != null) && (this.mTabHost.isShown()))
    {
      this.mAutoCompleteTextView.setText("");
      this.mTabHost.setCurrentTabByTag("product");
      this.mTabHost.requestFocus();
      this.mTabHost.setVisibility(8);
      resetCurrentActivity();
      this.mKeywordsLayout.setVisibility(0);
    }
    for (boolean bool = true; ; bool = super.onKeyDown(paramInt, paramKeyEvent))
      return bool;
  }

  public void onSuccess(int paramInt, Object paramObject)
  {
	  if(33 == paramInt)
    {

      initSearchKeywordsView((ArrayList)paramObject);
    }
  }

  public void setKeyword(String paramString)
  {
    if (this.mAutoCompleteTextView != null)
      this.mAutoCompleteTextView.setText(paramString);
  }
}