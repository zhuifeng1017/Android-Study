package com.zhao.firstapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MyListActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		ListView lv = (ListView) findViewById(R.id.list);
		MyListAdapter adapter = new MyListAdapter(this, R.layout.list_item);
		lv.setAdapter(adapter);
	}
}
