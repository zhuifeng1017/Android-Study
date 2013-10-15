package com.xxx.appstore.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.ui.PayMainActivity;

import java.util.HashMap;

public class ChargeTypeListActivity extends ListActivity {

   public static final String TYPE_ALIPAY = "alipay";
   public static final String TYPE_PHONECARD = "phonecard";
   private HashMap<String, String> mChargeTypes;
   private TextView mTvInfoTitle;


   private String[] getChargeTypeStrings() {
      this.mChargeTypes = new HashMap(2);
      String var1 = this.getString(2131296523);
      String var2 = this.getString(2131296532);
      this.mChargeTypes.put("alipay", var1);
      this.mChargeTypes.put("phonecard", var2);
      return new String[]{var1, var2};
   }

   private void showError(Intent var1) {
      if(var1.hasExtra("error")) {
         String var2 = this.getChargeType(this.getIntent().getStringExtra("error"));
         this.mTvInfoTitle.setText(this.getString(2131296522, new Object[]{var2}));
         this.mTvInfoTitle.setVisibility(0);
      } else {
         this.mTvInfoTitle.setVisibility(8);
      }

   }

   public String getChargeType(String var1) {
      return (String)this.mChargeTypes.get(var1);
   }

   protected String getType(int var1) {
      String var2;
      switch(var1) {
      case 0:
         var2 = "alipay";
         break;
      case 1:
         var2 = "phonecard";
         break;
      default:
         var2 = null;
      }

      return var2;
   }

   protected void onActivityResult(int var1, int var2, Intent var3) {
      super.onActivityResult(var1, var2, var3);
      if(-1 == var2 && var1 == 0) {
         this.setResult(-1);
         this.finish();
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903103);
      Session var2 = Session.get(this.getApplicationContext());
      View[] var3 = new View[]{this.findViewById(2131493035)};
      TopBar.createTopBar(var2, this, var3, new int[]{0}, this.getString(2131296519));
      this.mTvInfoTitle = (TextView)this.findViewById(2131492980);
      this.showError(this.getIntent());
      TextView var4 = new TextView(this);
      var4.setHeight(1);
      this.getListView().addFooterView(var4, (Object)null, true);
      this.setListAdapter(new ArrayAdapter(this, 2130903108, 2131492868, this.getChargeTypeStrings()));
   }

   protected void onListItemClick(ListView var1, View var2, int var3, long var4) {
      super.onListItemClick(var1, var2, var3, var4);
      Intent var6 = new Intent(this, PayMainActivity.class);
      var6.putExtra("type", this.getType(var3));
      if(this.getIntent().hasExtra("balance")) {
         var6.putExtra("balance", this.getIntent().getIntExtra("balance", 0));
      }

      this.startActivityForResult(var6, 0);
      this.mTvInfoTitle.setVisibility(8);
   }

   protected void onNewIntent(Intent var1) {
      super.onNewIntent(var1);
      if(var1.hasExtra("balance")) {
         this.getIntent().putExtra("balance", var1.getExtras().getInt("balance"));
      }

      this.showError(var1);
   }
}
