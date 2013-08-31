package com.zhao.firstapp;

import java.io.File;
//import android.database.sqlite.*;

import com.zhao.firstapp.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {
	
	public static final String TAG = "com.example.myfirstapp.LOG";
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    // Do something in response to button
		Log.v(TAG, "sendMessage");
		Log.v(TAG, "app file dir:  "  + getFilesDir().getAbsolutePath());
		Log.v(TAG, "app cache dir:  " + getCacheDir().getAbsolutePath());
		
		File file = new File( getFilesDir(), "1.txt");
		
		
		Intent intent = new Intent(this, DisplayMessageActivity.class);
	    EditText editText = (EditText) findViewById(R.id.edit_message);
	    String message = editText.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE, message);
	    startActivity(intent);
	}
	
	public void testIntent(View view) {
		Uri number = Uri.parse("tel:51538888");
		Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
		startActivity(callIntent);
	}
}
