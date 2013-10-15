package com.xxx.appstore.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.widget.BaseActivity;
import com.xxx.appstore.ui.LoginActivity;
import com.mobclick.android.MobclickAgent;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener, ApiAsyncTask.ApiRequestListener {

   private static final int DIALOG_REGISTERING = 0;
   public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+");
   private static final int ERROR_CODE_EMAIL_EXIST = 216;
   private static final int ERROR_CODE_EMAIL_INVALID_FORMAT = 215;
   private static final int ERROR_CODE_PASSWORD_INVALID = 217;
   private static final int ERROR_CODE_USERNAME_EXIST = 214;
   private static final int ERROR_CODE_USERNAME_INVALID = 213;
   private EditText etEmail;
   private EditText etPassword;
   private EditText etPassword2;
   private EditText etUsername;


   private boolean checkEmail() {
      String var1 = this.etEmail.getText().toString();
      boolean var3;
      if(TextUtils.isEmpty(var1)) {
         this.etEmail.setError(this.getString(2131296283));
         var3 = false;
      } else {
         this.etEmail.setError((CharSequence)null);
         int var2 = var1.length();
         if(var2 >= 6 && var2 <= 32) {
            this.etEmail.setError((CharSequence)null);
            if(!EMAIL_ADDRESS_PATTERN.matcher(var1).find()) {
               this.etEmail.setError(this.getString(2131296284));
               var3 = false;
            } else {
               this.etEmail.setError((CharSequence)null);
               var3 = true;
            }
         } else {
            this.etEmail.setError(this.getString(2131296285));
            var3 = false;
         }
      }

      return var3;
   }

   private boolean checkPassword(EditText var1) {
      String var2 = var1.getText().toString();
      boolean var3;
      if(TextUtils.isEmpty(var2)) {
         var1.setError(this.getString(2131296286));
         var3 = false;
      } else {
         var1.setError((CharSequence)null);
         if(var2.length() > 16) {
            var1.setError(this.getString(2131296287));
            var3 = false;
         } else {
            var1.setError((CharSequence)null);
            var3 = true;
         }
      }

      return var3;
   }

   private boolean checkPasswordSame() {
      boolean var1;
      if(!this.etPassword.getText().toString().equals(this.etPassword2.getText().toString())) {
         this.etPassword2.setError(this.getString(2131296288));
         var1 = false;
      } else {
         this.etPassword2.setError((CharSequence)null);
         var1 = true;
      }

      return var1;
   }

   private boolean checkUserName() {
      String var1 = this.etUsername.getText().toString();
      boolean var2;
      if(!TextUtils.isEmpty(var1) && !TextUtils.isEmpty(var1.trim())) {
         this.etUsername.setError((CharSequence)null);

         int var4;
         try {
            var4 = var1.getBytes("UTF8").length;
         } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
            var4 = 0;
         }

         if(var4 >= 3 && var4 <= 15) {
            this.etUsername.setError((CharSequence)null);
            var2 = true;
         } else {
            this.etUsername.setError(this.getString(2131296282));
            var2 = false;
         }
      } else {
         this.etUsername.setError(this.getString(2131296281));
         var2 = false;
      }

      return var2;
   }

   private void initView() {
      Session var1 = this.mSession;
      View[] var2 = new View[]{this.findViewById(2131493035)};
      TopBar.createTopBar(var1, this, var2, new int[]{0}, this.getString(2131296273));
      this.etUsername = (EditText)this.findViewById(2131492909);
      this.etUsername.setOnFocusChangeListener(this);
      this.etUsername.requestFocus();
      this.etEmail = (EditText)this.findViewById(2131492930);
      this.etEmail.setOnFocusChangeListener(this);
      this.etPassword = (EditText)this.findViewById(2131492910);
      this.etPassword.setOnFocusChangeListener(this);
      this.etPassword2 = (EditText)this.findViewById(2131492931);
      this.etPassword2.setOnFocusChangeListener(this);
      ((Button)this.findViewById(2131492911)).setOnClickListener(this);
      TextView var4 = (TextView)this.findViewById(2131492932);
      CharSequence var5 = var4.getText();
      SpannableString var6 = new SpannableString(var5);
      var6.setSpan(new UnderlineSpan(), var5.length() - 4, var5.length(), 0);
      var4.setText(var6);
      var4.setOnClickListener(this);
   }

   private void onClickRegister() {
      if(this.checkUserName() && this.checkEmail() && this.checkPassword(this.etPassword) && this.checkPassword(this.etPassword2) && this.checkPasswordSame()) {
         String var1 = this.etUsername.getText().toString();
         String var2 = this.etPassword.getText().toString();
         String var3 = this.etEmail.getText().toString();
         MarketAPI.register(this.getApplicationContext(), this, var1, var2, var3);
         if(!this.isFinishing()) {
            this.showDialog(0);
         }
      }

   }

   public void onClick(View var1) {
      switch(var1.getId()) {
      case 2131492911:
         this.onClickRegister();
         break;
      case 2131492932:
         Intent var2 = new Intent(this.getApplicationContext(), LoginActivity.class);
         this.finish();
         this.startActivity(var2);
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903075);
      this.initView();
   }

   protected Dialog onCreateDialog(int var1) {
      Object var2;
      if(var1 == 0) {
         var2 = new ProgressDialog(this);
         ((ProgressDialog)var2).setProgressStyle(0);
         ((ProgressDialog)var2).setMessage(this.getString(2131296275));
      } else {
         var2 = super.onCreateDialog(var1);
      }

      return (Dialog)var2;
   }

   protected void onDestroy() {
      super.onDestroy();
      this.etUsername = null;
      this.etEmail = null;
      this.etPassword = null;
      this.etPassword2 = null;
   }

   public void onError(int var1, int var2) {
      try {
         this.dismissDialog(0);
      } catch (IllegalArgumentException var5) {
         ;
      }

      String var4;
      switch(var2) {
      case 213:
         var4 = this.getString(2131296289);
         break;
      case 214:
         var4 = this.getString(2131296290);
         break;
      case 215:
         var4 = this.getString(2131296291);
         break;
      case 216:
         var4 = this.getString(2131296292);
         break;
      case 217:
         var4 = this.getString(2131296293);
         break;
      default:
         var4 = this.getString(2131296294);
      }

      Utils.makeEventToast(this.getApplicationContext(), var4, false);
   }

   public void onFocusChange(View var1, boolean var2) {
      switch(var1.getId()) {
      case 2131492909:
         if(!var2) {
            this.checkUserName();
         }
         break;
      case 2131492910:
         if(!var2) {
            this.checkPassword(this.etPassword);
         }
         break;
      case 2131492930:
         if(!var2) {
            this.checkEmail();
         }
         break;
      case 2131492931:
         if(!var2) {
            this.checkPassword(this.etPassword2);
         }
      }

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
      try {
         this.dismissDialog(0);
      } catch (IllegalArgumentException var5) {
         ;
      }

      Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296274), false);
      HashMap var4 = (HashMap)var2;
      this.mSession.setUid((String)var4.get("uid"));
      this.mSession.setUserName((String)var4.get("name"));
      this.setResult(-1);
      this.mSession.setLogin(true);
      Utils.trackEvent(this.getApplicationContext(), new String[]{"个人中心", "注册成功"});
      this.finish();
   }
}
