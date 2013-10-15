package com.xxx.appstore.common.util;

import android.content.ContentUris;
import android.net.Uri;
import android.text.TextUtils;

public class SqlArguments {

   public final String[] args;
   public final String table;
   public final String where;


   public SqlArguments(Uri var1) {
      if(var1.getPathSegments().size() == 1) {
         this.table = (String)var1.getPathSegments().get(0);
         this.where = null;
         this.args = null;
      } else {
         throw new IllegalArgumentException("Invalid URI: " + var1);
      }
   }

   public SqlArguments(Uri var1, String var2, String[] var3) {
      if(var1.getPathSegments().size() == 1) {
         this.table = (String)var1.getPathSegments().get(0);
         this.where = var2;
         this.args = var3;
      } else {
         if(var1.getPathSegments().size() != 2) {
            throw new IllegalArgumentException("Invalid URI: " + var1);
         }

         if(!TextUtils.isEmpty(var2)) {
            throw new UnsupportedOperationException("WHERE clause not supported: " + var1);
         }

         this.table = (String)var1.getPathSegments().get(0);
         this.where = "_id=" + ContentUris.parseId(var1);
         this.args = null;
      }

   }
}
