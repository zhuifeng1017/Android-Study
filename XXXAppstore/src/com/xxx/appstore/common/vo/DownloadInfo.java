package com.xxx.appstore.common.vo;


public class DownloadInfo {

   public long id;
   public String mAppName;
   public int mControl;
   public long mCurrentSize;
   public String mFilePath;
   public Object mIconUrl;
   public String mKey;
   public String mPackageName;
   public String mProgress;
   public int mProgressLevel;
   public int mProgressNumber;
   public int mSource;
   public int mStatus;
   public long mTotalSize;


   public String toString() {
      return "packagename : " + this.mPackageName + " status " + this.mStatus + " progress " + this.mProgress + " level " + this.mProgressLevel;
   }
}
