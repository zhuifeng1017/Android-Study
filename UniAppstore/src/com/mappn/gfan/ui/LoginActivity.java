package com.mappn.gfan.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.TopBar;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.ui.RegisterActivity;
import com.mobclick.android.MobclickAgent;
import java.util.HashMap;

public class LoginActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener, ApiAsyncTask.ApiRequestListener {

   private static final int DIALOG_PROGRESS = 0;
   private static final int ERROR_CODE_PASSWORD_INVALID = 212;
   private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
   private EditText etPassword;
   private EditText etUsername;


   private boolean checkPassword(EditText var1) {
      String var2 = var1.getText().toString();
      boolean var3;
      if(!TextUtils.isEmpty(var2) && !TextUtils.isEmpty(var2.trim())) {
         var1.setError((CharSequence)null);
         var3 = true;
      } else {
         var1.setError(this.getString(2131296286));
         var3 = false;
      }

      return var3;
   }

   private boolean checkUserName(EditText var1) {
      String var2 = var1.getText().toString();
      boolean var3;
      if(!TextUtils.isEmpty(var2) && !TextUtils.isEmpty(var2.trim())) {
         var1.setError((CharSequence)null);
         var3 = true;
      } else {
         this.etUsername.setError(this.getString(2131296281));
         var3 = false;
      }

      return var3;
   }

   private void initView() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035)};
      TopBar.createTopBar(var1, this, var2, new int[]{0}, this.getString(2131296296));
      this.etUsername = (EditText)this.findViewById(2131492909);
      String var3;
      if(TextUtils.isEmpty(this.mSession.getUserName())) {
         var3 = "";
      } else {
         var3 = this.mSession.getUserName();
      }

      this.etUsername.setText(var3);
      this.etUsername.setOnFocusChangeListener(this);
      this.etUsername.requestFocus();
      this.etPassword = (EditText)this.findViewById(2131492910);
      this.etPassword.setOnFocusChangeListener(this);
      if(!TextUtils.isEmpty(var3)) {
         this.etPassword.requestFocus();
      }

      ((Button)this.findViewById(2131492912)).setOnClickListener(this);
      TextView var5 = (TextView)this.findViewById(2131492911);
      CharSequence var6 = var5.getText();
      SpannableString var7 = new SpannableString(var6);
      var7.setSpan(new UnderlineSpan(), var6.length() - 4, var6.length(), 0);
      var5.setText(var7);
      var5.setOnClickListener(this);
   }

   private void login() {
      if(this.checkUserName(this.etUsername) && this.checkPassword(this.etPassword) && !this.isFinishing()) {
         this.showDialog(0);
         String var1 = this.etUsername.getText().toString();
         String var2 = this.etPassword.getText().toString();
         MarketAPI.login(this.getApplicationContext(), this, var1, var2);
         Utils.trackEvent(this.getApplicationContext(), new String[]{"个人中心", "点击登录"});
      }

   }

   private void onClickRegister() {
      Intent var1 = new Intent(this, RegisterActivity.class);
      this.finish();
      this.startActivity(var1);
   }

   private void syncBuyLogOver(boolean var1) {
      this.mSession.setLogin(true);
      Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296297), false);
      this.setResult(-1);

      try {
         this.dismissDialog(0);
      } catch (IllegalArgumentException var3) {
         ;
      }

      this.finish();
   }

   public void onClick(View var1) {
      switch(var1.getId()) {
      case 2131492911:
         this.onClickRegister();
         break;
      case 2131492912:
         this.login();
      }

   }

   public void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903060);
      this.initView();
   }

   protected Dialog onCreateDialog(int var1) {
      Object var2;
      switch(var1) {
      case 0:
         var2 = new ProgressDialog(this);
         ((ProgressDialog)var2).setProgressStyle(0);
         ((ProgressDialog)var2).setMessage(this.getString(2131296298));
         break;
      default:
         var2 = super.onCreateDialog(var1);
      }

      return (Dialog)var2;
   }

   protected void onDestroy() {
      super.onDestroy();
      this.etUsername = null;
      this.etPassword = null;
   }

   public void onError(int var1, int var2) {
      switch(var1) {
      case 0:
         try {
            this.dismissDialog(0);
         } catch (IllegalArgumentException var5) {
            ;
         }

         String var4;
         if(var2 == 211) {
            var4 = this.getString(2131296302);
         } else if(var2 == 212) {
            var4 = this.getString(2131296303);
         } else {
            var4 = this.getString(2131296304);
         }

         Utils.makeEventToast(this.getApplicationContext(), var4, false);
         break;
      case 7:
         this.syncBuyLogOver(false);
      }

   }

   public void onFocusChange(View var1, boolean var2) {
      switch(var1.getId()) {
      case 2131492909:
         if(!var2) {
            this.checkUserName(this.etUsername);
         }
         break;
      case 2131492910:
         if(!var2) {
            this.checkPassword(this.etPassword);
         }
      }

   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      boolean var3;
      if(var1 == 4) {
         this.setResult(0);
         this.finish();
         var3 = true;
      } else {
         var3 = super.onKeyDown(var1, var2);
      }

      return var3;
   }

   protected void onPause() {
      super.onPause();
      MobclickAgent.onPause(this);
   }

   protected void onPrepareDialog(int var1, Dialog var2) {
      super.onPrepareDialog(var1, var2);
      if(var2.isShowing()) {
         var2.dismiss();
      }
   }

   protected void onResume() {
      super.onResume();
      MobclickAgent.onResume(this);
   }

   public void onSuccess(int var1, Object var2) {
      switch(var1) {
      case 0:
         Utils.trackEvent(this.getApplicationContext(), new String[]{"个人中心", "登录成功"});
         HashMap var3 = (HashMap)var2;
         this.mSession.setUid((String)var3.get("uid"));
         this.mSession.setUserName((String)var3.get("name"));
         MarketAPI.syncBuyLog(this.getApplicationContext(), this);
         break;
      case 7:
         this.syncBuyLogOver(true);
      }

   }
}
