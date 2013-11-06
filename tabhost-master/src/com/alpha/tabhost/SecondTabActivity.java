package com.alpha.tabhost;

import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

/*
 * extends ActivityGroup 还是 extends TabActivity 是要注意的！！！！
 * */
public class SecondTabActivity extends ActivityGroup {
	private TabHost mTabHost;
	private LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_second_tabhost);
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 如果该Activity继承了TabActivity,使用该方法
		// tabHost = this.getTabHost();
		// 否则必须使用该方法
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		// 本方法必须调用,在底层去查找TabWidegt和TabContent,所以xml文件中两个节点的id必须和系统的相匹配
		mTabHost.setup(this.getLocalActivityManager());
		
		// 添加标签页
		TabSpec tabSpec1 = mTabHost.newTabSpec("tabSpec1");
		tabSpec1.setIndicator(createView("第一个子项"));
		tabSpec1.setContent(R.id.item1);
		mTabHost.addTab(tabSpec1);

		// 添加第二项
		TabSpec tabSpec2 = mTabHost.newTabSpec("tabSpec2");
		// tabSpec2.setIndicator("第二页",
		// getResources().getDrawable(R.drawable.i2));
		tabSpec2.setIndicator(createView("第二个子项"));
		tabSpec2.setContent(R.id.item2);
		mTabHost.addTab(tabSpec2);
	}
	
	// 创建一个自定义布局
	private View createView(String name) {
		View view = mInflater.inflate(R.layout.custom, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_name.setText(name);
		return view;
	}
}
