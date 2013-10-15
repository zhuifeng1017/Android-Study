package com.xxx.appstore.common.vo;

import java.io.Serializable;

public class RecommendTopic implements Serializable {

   private static final long serialVersionUID = 7388351290381894470L;
   public String description;
   public int down;
   public String experience;
   public int fans;
   public String icon;
   public String id;
   public String title;
   public int up;
   public String user;


   public String toString() {
      return "Title : " + this.title + " up : " + this.up + " down : " + this.down + " description : " + this.description;
   }
}
