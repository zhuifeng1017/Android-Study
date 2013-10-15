package com.xxx.appstore.common.vo;

import java.io.Serializable;

public class ProductDetail implements Serializable {

   private static final long serialVersionUID = 9027701566317108365L;
   private int appSize;
   private String authorName;
   private int commentsCount;
   private int downloadCount;
   private String iconUrl;
   private String iconUrlLdpi;
   public boolean isPendingDownload;
   private String longDescription;
   private String mFilePath;
   private String name;
   private String packageName;
   private int payCategory;
   private String permission;
   private String pid;
   private int price;
   private String productType;
   private long publishTime;
   private float rating;
   private int ratingCount;
   private String rsaMd5;
   private String[] screenshot = new String[5];
   private String[] screenshotLdpi;
   private String shotDes;
   private String sourceType;
   private String upReason;
   private long upTime;
   private int versionCode;
   private String versionName;


   public int getAppSize() {
      return this.appSize;
   }

   public String getAuthorName() {
      return this.authorName;
   }

   public int getCommentsCount() {
      return this.commentsCount;
   }

   public int getDownloadCount() {
      return this.downloadCount;
   }

   public String getFilePath() {
      return this.mFilePath;
   }

   public String getIconUrl() {
      return this.iconUrl;
   }

   public String getIconUrlLdpi() {
      return this.iconUrlLdpi;
   }

   public String getLongDescription() {
      return this.longDescription;
   }

   public String getName() {
      return this.name;
   }

   public String getPackageName() {
      return this.packageName;
   }

   public int getPayCategory() {
      return this.payCategory;
   }

   public String getPermission() {
      return this.permission;
   }

   public String getPid() {
      return this.pid;
   }

   public int getPrice() {
      return this.price;
   }

   public String getProductType() {
      return this.productType;
   }

   public long getPublishTime() {
      return this.publishTime;
   }

   public float getRating() {
      return this.rating;
   }

   public int getRatingCount() {
      return this.ratingCount;
   }

   public String getRsaMd5() {
      return this.rsaMd5;
   }

   public String[] getScreenshot() {
      return this.screenshot;
   }

   public String[] getScreenshotLdpi() {
      return this.screenshotLdpi;
   }

   public String getShotDes() {
      return this.shotDes;
   }

   public String getSourceType() {
      return this.sourceType;
   }

   public String getUpReason() {
      return this.upReason;
   }

   public long getUpTime() {
      return this.upTime;
   }

   public int getVersionCode() {
      return this.versionCode;
   }

   public String getVersionName() {
      return this.versionName;
   }

   public void setAppSize(int var1) {
      this.appSize = var1;
   }

   public void setAuthorName(String var1) {
      this.authorName = var1;
   }

   public void setCommentsCount(int var1) {
      this.commentsCount = var1;
   }

   public void setDownloadCount(int var1) {
      this.downloadCount = var1;
   }

   public void setFilePath(String var1) {
      this.mFilePath = var1;
   }

   public void setIconUrl(String var1) {
      this.iconUrl = var1;
   }

   public void setIconUrlLdpi(String var1) {
      this.iconUrlLdpi = var1;
   }

   public void setLongDescription(String var1) {
      this.longDescription = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setPackageName(String var1) {
      this.packageName = var1;
   }

   public void setPayCategory(int var1) {
      this.payCategory = var1;
   }

   public void setPermission(String var1) {
      this.permission = var1;
   }

   public void setPid(String var1) {
      this.pid = var1;
   }

   public void setPrice(int var1) {
      this.price = var1;
   }

   public void setProductType(String var1) {
      this.productType = var1;
   }

   public void setPublishTime(long var1) {
      this.publishTime = var1;
   }

   public void setRating(float var1) {
      this.rating = var1;
   }

   public void setRatingCount(int var1) {
      this.ratingCount = var1;
   }

   public void setRsaMd5(String var1) {
      this.rsaMd5 = var1;
   }

   public void setScreenshot(String[] var1) {
      this.screenshot = var1;
   }

   public void setScreenshotLdpi(String[] var1) {
      this.screenshotLdpi = var1;
   }

   public void setShotDes(String var1) {
      this.shotDes = var1;
   }

   public void setSourceType(String var1) {
      this.sourceType = var1;
   }

   public void setUpReason(String var1) {
      this.upReason = var1;
   }

   public void setUpTime(long var1) {
      this.upTime = var1;
   }

   public void setVersionCode(int var1) {
      this.versionCode = var1;
   }

   public void setVersionName(String var1) {
      this.versionName = var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("ProductDetail[");
      var1.append(this.name);
      var1.append("]");
      return var1.toString();
   }
}
