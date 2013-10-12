package com.alpha.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListActivityImpl extends ListActivity {
	private Button bt_add;
	private EditText et_item;
	private ArrayList<HashMap<String, Object>> listItems; // 存放文字、图片信息
	private SimpleAdapter listItemAdapter; // 适配器

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.layout_listactivityimpl);
						
		bt_add = (Button) findViewById(R.id.bt_add);
		et_item = (EditText) findViewById(R.id.et_item);
		initListView();
		this.setListAdapter(listItemAdapter);
		bt_add.setOnClickListener(new ClickEvent());
	}

	/**
	 * 设置适配器内容
	 */
	private void initListView() {
		listItems = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "Music： " + i); // 文字
			map.put("ItemImage", R.drawable.select);// 图片
			listItems.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItems,// 数据源
				R.layout.list_item,// ListItem的XML布局实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemTitle", "ItemImage" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemTitle, R.id.ItemImage });
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Log.e("position", "" + position);
		setTitle("你点击第" + position + "行");
	}

	class ClickEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 添加多一项
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "Music： " + et_item.getText().toString());
			map.put("ItemImage", R.drawable.select);
			listItems.add(map);
			// 重新设置适配器
			ListActivityImpl.this.setListAdapter(listItemAdapter);
		}
	}
}
