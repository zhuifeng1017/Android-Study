package com.mappn.gfan.common.vo;


public class UpdateInfo {

   String apkUrl;
   String description;
   int updageLevel;
   int versionCode;
   String versionName;


   public String getApkUrl() {
      return this.apkUrl;
   }

   public String getDescription() {
      return this.description;
   }

   public int getUpdageLevel() {
      return this.updageLevel;
   }

   public int getVersionCode() {
      return this.versionCode;
   }

   public String getVersionName() {
      return this.versionName;
   }

   public void setApkUrl(String var1) {
      this.apkUrl = var1;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public void setUpdageLevel(int var1) {
      this.updageLevel = var1;
   }

   public void setVersionCode(int var1) {
      this.versionCode = var1;
   }

   public void setVersionName(String var1) {
      this.versionName = var1;
   }
}
