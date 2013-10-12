package com.alpha.tabhost;


import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/*���tabhost ʹ�� setContent(Intent intent)������Ҫextends ActivityGroup��
 * addTab֮ǰһ��Ҫ���� android.widget.TabHost.setup(LocalActivityManager activityGroup)
 * */
public class MainActivity extends ActivityGroup {

	private TabHost mTabHost;
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// �����Activity�̳���TabActivity,ʹ�ø÷���
		// tabHost = this.getTabHost();
		// �������ʹ�ø÷���
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		// �������������,�ڵײ�ȥ����TabWidegt��TabContent,����xml�ļ��������ڵ��id�����ϵͳ����ƥ��
		//mTabHost.setup();
		mTabHost.setup(this.getLocalActivityManager());
		
		// ���ӱ�ǩҳ
		TabSpec tabSpec1 = mTabHost.newTabSpec("tabSpec1");
		// ָ����ʾ�ı���
		// tabSpec1.setIndicator("��ҳ",
		// getResources().getDrawable(R.drawable.i1));
		tabSpec1.setIndicator(createView("��ҳ"));
		// 1. �÷���ֻ���ñ���
		// tabSpec1.setIndicator("zhuye");
		// 2. �÷������ñ����ͼ��
		// tabSpec1.setIndicator(label, icon);
		// 3. �÷��������Զ���ı���
		// tabSpec1.setIndicator(view);
		// ָ����ʾ������
		tabSpec1.setContent(R.id.line1);
		// ���Ӹ�TabSpec
		mTabHost.addTab(tabSpec1);

		// ���ӵڶ�ҳ
		TabSpec tabSpec2 = mTabHost.newTabSpec("tabSpec2");
		// tabSpec2.setIndicator("�ڶ�ҳ",
		// getResources().getDrawable(R.drawable.i2));
		tabSpec2.setIndicator(createView("�ڶ�ҳ"));
		tabSpec2.setContent(R.id.line2);
		mTabHost.addTab(tabSpec2);

		// ���ӵ���ҳ
		if (true){
			TabSpec tabSpec3 = mTabHost.newTabSpec("tabSpec3");
			tabSpec3.setIndicator(createView("����ҳ"));
			tabSpec3.setContent(new Intent(this, SecondTabActivity.class));
			mTabHost.addTab(tabSpec3);
		}
		
		// ���ӵ���ҳ
		if (true) {
			TabSpec tabSpec4 = mTabHost.newTabSpec("tabSpec4");
			tabSpec4.setIndicator(createView("����ҳ"));
			tabSpec4.setContent(new Intent(this, ListActivityImpl.class));
			mTabHost.addTab(tabSpec4);
		}
	}
	
	// ����һ���Զ��岼��
	private View createView(String name) {
		View view = mInflater.inflate(R.layout.custom, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_name.setText(name);
		return view;
	}
}