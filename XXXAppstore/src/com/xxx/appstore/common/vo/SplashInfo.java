package com.xxx.appstore.common.vo;

import java.io.Serializable;

public class SplashInfo implements Serializable {

   public static final String TIMESTAMP = "time";
   public static final String URL = "url";
   private static final long serialVersionUID = 4970809950944283716L;
   public long timestamp;
   public String url;


   public String toString() {
      return "SplashInfo [timestamp : " + this.timestamp + " url : " + this.url + "]";
   }
}
