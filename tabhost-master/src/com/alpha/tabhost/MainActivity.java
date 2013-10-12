package com.alpha.tabhost;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TabHost mTabHost;

	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 如果该Activity继承了TabActivity,使用该方法
		// tabHost = this.getTabHost();
		// 否则必须使用该方法
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		// 本方法必须调用,在底层去查找TabWidegt和TabContent,所以xml文件中两个节点的id必须和系统的相匹配
		mTabHost.setup();

		// 添加标签页
		TabSpec tabSpec1 = mTabHost.newTabSpec("tabSpec1");
		// 指定显示的标题
		// tabSpec1.setIndicator("首页",
		// getResources().getDrawable(R.drawable.i1));
		tabSpec1.setIndicator(createView("首页"));
		// 1. 该方法只设置标题
		// tabSpec1.setIndicator("zhuye");
		// 2. 该方法设置标题和图标
		// tabSpec1.setIndicator(label, icon);
		// 3. 该方法设置自定义的标题
		// tabSpec1.setIndicator(view);
		// 指定显示的内容
		tabSpec1.setContent(R.id.line1);
		// 添加该TabSpec
		mTabHost.addTab(tabSpec1);

		// 添加第二页
		TabSpec tabSpec2 = mTabHost.newTabSpec("tabSpec2");
		// tabSpec2.setIndicator("第二页",
		// getResources().getDrawable(R.drawable.i2));
		tabSpec2.setIndicator(createView("第二页"));
		tabSpec2.setContent(R.id.line2);
		mTabHost.addTab(tabSpec2);

		// 添加第三页
		TabSpec tabSpec3 = mTabHost.newTabSpec("tabSpec3");
		// tabSpec3.setIndicator("第三页",
		// getResources().getDrawable(R.drawable.i7));
		tabSpec3.setIndicator(createView("第三页"));
		tabSpec3.setContent(R.id.line3);
		mTabHost.addTab(tabSpec3);

	}

	// 创建一个自定义布局
	private View createView(String name) {
		View view = mInflater.inflate(R.layout.custom, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_name.setText(name);
		return view;
	}
}