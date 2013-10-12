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
	private ArrayList<HashMap<String, Object>> listItems; // ������֡�ͼƬ��Ϣ
	private SimpleAdapter listItemAdapter; // ������

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
	 * ��������������
	 */
	private void initListView() {
		listItems = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "Music�� " + i); // ����
			map.put("ItemImage", R.drawable.select);// ͼƬ
			listItems.add(map);
		}
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		listItemAdapter = new SimpleAdapter(this, listItems,// ����Դ
				R.layout.list_item,// ListItem��XML����ʵ��
				// ��̬������ImageItem��Ӧ������
				new String[] { "ItemTitle", "ItemImage" },
				// ImageItem��XML�ļ������һ��ImageView,����TextView ID
				new int[] { R.id.ItemTitle, R.id.ItemImage });
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Log.e("position", "" + position);
		setTitle("������" + position + "��");
	}

	class ClickEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// ��Ӷ�һ��
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "Music�� " + et_item.getText().toString());
			map.put("ItemImage", R.drawable.select);
			listItems.add(map);
			// ��������������
			ListActivityImpl.this.setListAdapter(listItemAdapter);
		}
	}
}
