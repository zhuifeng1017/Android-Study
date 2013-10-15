package com.xxx.appstore.common.vo;

import android.content.ContentValues;

public class UpgradeInfo {

   public String filePath;
   public String name;
   public String pid;
   public String pkgName;
   public String signature;
   public int status;
   public int update;
   public int versionCode;
   public String versionName;


   public ContentValues getContentValues() {
      ContentValues var1 = new ContentValues();
      var1.put("p_id", this.pid);
      var1.put("p_new_version_name", this.versionName);
      var1.put("p_new_version_code", Integer.valueOf(this.versionCode));
      var1.put("p_package_name", this.pkgName);
      var1.put("p_signature", this.signature);
      var1.put("p_update_ingore", Integer.valueOf(this.update));
      return var1;
   }
}
