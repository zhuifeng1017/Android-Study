package com.mappn.gfan.common.vo;

import android.content.ContentValues;

public class CardsVerification {

   public int accountNum;
   public String credit;
   public String name;
   public int passwordNum;
   public String pay_type;


   public void onAddToDatabase(ContentValues var1) {
      var1.put("card_name", this.name);
      var1.put("card_pay_type", this.pay_type);
      var1.put("card_account_num", Integer.valueOf(this.accountNum));
      var1.put("card_password_num", Integer.valueOf(this.passwordNum));
      var1.put("card_credit", this.credit);
   }
}
