package com.xxx.appstore.common.vo;

import java.io.Serializable;

public class DownloadItem implements Serializable {

   private static final long serialVersionUID = 4092080932832253395L;
   public String fileMD5;
   public String pId;
   public String packageName;
   public int sourceType;
   public String url;


   public String toString() {
      return this.pId + " " + this.url + " " + this.fileMD5;
   }
}
