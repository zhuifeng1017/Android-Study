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
 * extends ActivityGroup ���� extends TabActivity ��Ҫע��ģ�������
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
		// �����Activity�̳���TabActivity,ʹ�ø÷���
		// tabHost = this.getTabHost();
		// �������ʹ�ø÷���
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		// �������������,�ڵײ�ȥ����TabWidegt��TabContent,����xml�ļ��������ڵ��id�����ϵͳ����ƥ��
		mTabHost.setup(this.getLocalActivityManager());
		
		// ��ӱ�ǩҳ
		TabSpec tabSpec1 = mTabHost.newTabSpec("tabSpec1");
		tabSpec1.setIndicator(createView("��һ������"));
		tabSpec1.setContent(R.id.item1);
		mTabHost.addTab(tabSpec1);

		// ��ӵڶ���
		TabSpec tabSpec2 = mTabHost.newTabSpec("tabSpec2");
		// tabSpec2.setIndicator("�ڶ�ҳ",
		// getResources().getDrawable(R.drawable.i2));
		tabSpec2.setIndicator(createView("�ڶ�������"));
		tabSpec2.setContent(R.id.item2);
		mTabHost.addTab(tabSpec2);
	}
	
	// ����һ���Զ��岼��
	private View createView(String name) {
		View view = mInflater.inflate(R.layout.custom, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_name.setText(name);
		return view;
	}
}
