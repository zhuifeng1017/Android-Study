package com.zhao.firstapp;

import java.io.File;
import java.util.Calendar;
import org.apache.http.protocol.HTTP;

//import android.database.sqlite.*;


import com.zhao.firstapp.R;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;


@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	public static final String TAG = "com.example.myfirstapp.LOG";
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	static final int PICK_CONTACT_REQUEST = 1;
	private ShareActionProvider mShareActionProvider;
	private long mId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 注册广播
		IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				long refId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if (refId != -1 && mId==refId){
					Log.d(TAG, "下载完成！");
				}
			}
		};
		registerReceiver(receiver, filter);
		
		// 查询已完成的下载
		DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
		Cursor cursor = dm.query(query);
		int cnt = cursor.getCount(); 
		cursor.moveToFirst();
		Log.d(TAG, "success count:" + cnt);
		while (!cursor.isAfterLast()) {
			String localURI = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
			String localFilename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
			Log.d(TAG,  localURI);
			Log.d(TAG,  localFilename);
			long id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
			dm.remove(id);
			cursor.moveToNext();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
		// Inflate menu resource file.
	    getMenuInflater().inflate(R.menu.main, menu);

	    // Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.action_settings);

	    // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) item.getActionProvider();

	    // Return true to display menu
	    return true;
	}
	
	// Call to update the share intent
	@SuppressWarnings("unused")
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    // Do something in response to button
		Log.i(TAG, "sendMessage");
		Log.i(TAG, "app file dir:  "  + getFilesDir().getAbsolutePath());
		Log.i(TAG, "app cache dir:  " + getCacheDir().getAbsolutePath());
		
		File file = new File( getFilesDir(), "1.txt");
		
		Intent intent = new Intent(this, DisplayMessageActivity.class);
	    EditText editText = (EditText) findViewById(R.id.edit_message);
	    String message = editText.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE, message);
	    startActivity(intent);
	}
	
	public void testIntent(View view) {
		if (false){ // tel
			Uri number = Uri.parse("tel:51538888");
			Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			startActivity(callIntent);
			return;
		}
		
		if (false){ // web 
			Uri webpage = Uri.parse("http://www.android.com");
			Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
			startActivity(webIntent);
			return;
		}
		
		if (false){ // map
			// Map point based on address
			Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
			// Or map point based on latitude/longitude
			// Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
			Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
			startActivity(mapIntent);
			return;
		}
		
		if (false){
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			// The intent does not have a URI, so declare the "text/plain" MIME type
			emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jon@example.com"}); // recipients
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));
			// You can also attach multiple items by passing an ArrayList of Uris
			startActivity(emailIntent);
			return;
		}
		
		if (false){
			Intent intent = new Intent(Intent.ACTION_SEND);
			// Always use string resources for UI text. This says something like "Share this photo with"
			String title = "分享照片";
			// Create and start the chooser
			Intent chooser = Intent.createChooser(intent, title);
			startActivity(chooser);
		}
		
		if (true){
			String title = "分享照片";
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, title));
		}
	}
	
	public void testResultActivity(View view){
		Intent pickContactIntent = new Intent(Intent.ACTION_PICK,  Uri.parse("content://contacts"));
	    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
	    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
	}
	
	public void testNavi(View view) {
		Log.v(TAG, "testNavi");	// 调用第三方地图软件，暂时还没有实现
	}
	
	public void testListView(View view) {
		Intent localIntent = new Intent(this,MyListActivity.class);
		startActivity(localIntent);
	}
	
	public void testToast(View view) {
		Toast toast = Toast.makeText(this, "请稍后....", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void TestDownload(View view) {
		DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = 
				new DownloadManager.Request(Uri.parse("http://cdn6.down.apk.gfan.com/asdf/Pfiles/2013/9/29/257211_b4bbfe9a-8cda-4c92-9d64-40d2a36f1466.apk"));
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		request.setVisibleInDownloadsUi(false);		
		long downloadID = dm.enqueue(request);
		mId = downloadID;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String str = String.format("rezquestCode: %d resultCode:%d ", requestCode, resultCode);
		Log.i(TAG, str);
		 // Check which request it is that we're responding to
	    if (requestCode == PICK_CONTACT_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            // Get the URI that points to the selected contact
	            Uri contactUri = data.getData();
	            // We only need the NUMBER column, because there will be only one row in the result
	            String[] projection = {Phone.NUMBER};

	            // Perform the query on the contact to get the NUMBER column
	            // We don't need a selection or sort order (there's only one result for the given URI)
	            // CAUTION: The query() method should be called from a separate thread to avoid blocking
	            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
	            // Consider using CursorLoader to perform the query.
	            Cursor cursor = getContentResolver()
	                    .query(contactUri, projection, null, null, null);
	            cursor.moveToFirst();

	            // Retrieve the phone number from the NUMBER column
	            int column = cursor.getColumnIndex(Phone.NUMBER);
	            String number = cursor.getString(column);
	            // Do something with the phone number...
	            Log.i(TAG,  "number : " + number);
	        }
	    }
	}
}
