package com.zhao.firstapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import com.zhao.firstapp.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mResourceId;
	ArrayList<HashMap<String, String>> mDataSource;
	
	
	public MyListAdapter(Context context, int resourceId) {
		super();
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResourceId = resourceId;
		mDataSource = new ArrayList<HashMap<String,String>>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView != null)
			return convertView;
		
		View v = mInflater.inflate(mResourceId, null);
		ImageView imgView = (ImageView) v.findViewById(R.id.iv_logo);
		TextView tv = (TextView) v.findViewById(R.id.tv_download);
		tv.setVisibility(View.VISIBLE);
		tv.setText("обть");
		
		imgView.setVisibility(View.VISIBLE);
		imgView.setBackgroundResource(R.drawable.hao);
		
		 ((TextView)v.findViewById(R.id.tv_size)).setText("10.6M");
		
		return v;
	}

}
