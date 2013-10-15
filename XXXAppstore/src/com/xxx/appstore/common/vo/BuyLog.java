package com.xxx.appstore.common.vo;

import android.content.ContentValues;

public class BuyLog {

   public String pId;
   public String packageName;


   public void onAddToDatabase(ContentValues var1) {
      var1.put("p_id", this.pId);
      var1.put("p_package_name", this.packageName);
   }

   public String toString() {
      return this.pId + " " + this.packageName;
   }
}
