package com.mappn.gfan.common.util;

import android.text.TextUtils;
import com.mappn.gfan.common.util.Utils;
import java.text.DecimalFormat;

public class StringUtils {

   private static final char[] HEX_DIGIST = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


   public static String formatSize(long var0) {
      String var2;
      if(var0 < 1048576L) {
         var2 = (new DecimalFormat("##0")).format((double)((float)var0 / 1024.0F)) + "K";
      } else if(var0 < 1073741824L) {
         var2 = (new DecimalFormat("###0.##")).format((double)((float)var0 / 1048576.0F)) + "M";
      } else {
         var2 = (new DecimalFormat("#######0.##")).format((double)((float)var0 / 1.07374182E9F)) + "G";
      }

      return var2;
   }

   public static String formatSize(String var0) {
      return formatSize(Utils.getLong(var0));
   }

   public static byte[] fromHexString(String var0) {
      byte[] var1;
      if(var0 != null && (1 & var0.length()) == 0) {
         char[] var2 = var0.toLowerCase().toCharArray();
         byte[] var3 = new byte[var2.length >>> 1];
         int var4 = 0;

         while(true) {
            if(var4 < var3.length) {
               int var5 = getAsciiCode(var2[var4 << 1]);
               int var6 = getAsciiCode(var2[1 + (var4 << 1)]);
               if(var5 != -1 && var6 != -1) {
                  var3[var4] = (byte)(var6 + (var5 << 4));
                  ++var4;
                  continue;
               }

               var1 = null;
               break;
            }

            var1 = var3;
            break;
         }
      } else {
         var1 = null;
      }

      return var1;
   }

   private static int getAsciiCode(char var0) {
      int var1 = var0 - 48;
      if(var1 > 9) {
         var1 = var0 - 87;
      }

      if(var1 < 0 || var1 > 15) {
         var1 = -1;
      }

      return var1;
   }

   public static String getDownloadInterval(int var0) {
      String var1;
      if(var0 < 50) {
         var1 = "小于50";
      } else if(var0 >= 50 && var0 < 100) {
         var1 = "50 - 100";
      } else if(var0 >= 100 && var0 < 500) {
         var1 = "100 - 500";
      } else if(var0 >= 500 && var0 < 1000) {
         var1 = "500 - 1,000";
      } else if(var0 >= 1000 && var0 < 5000) {
         var1 = "1,000 - 5,000";
      } else if(var0 >= 5000 && var0 < 10000) {
         var1 = "5,000 - 10,000";
      } else if(var0 >= 10000 && var0 < '\uc350') {
         var1 = "10,000 - 50,000";
      } else if(var0 >= '\uc350' && var0 < 250000) {
         var1 = "50,000 - 250,000";
      } else {
         var1 = "大于250,000";
      }

      return var1;
   }

   public static String getFileNameFromUrl(String var0) {
      String var1;
      if(TextUtils.isEmpty(var0)) {
         var1 = "";
      } else {
         var1 = var0.substring(1 + var0.lastIndexOf("/"));
      }

      return var1;
   }

   public static String toHexString(byte[] var0, boolean var1) {
      int var2 = 0;
      String var6;
      if(var0 == null) {
         var6 = "";
      } else {
         char[] var3 = new char[var0.length << 1];

         for(int var4 = 0; var2 < var0.length; ++var2) {
            byte var7 = var0[var2];
            int var8 = var4 + 1;
            var3[var4] = HEX_DIGIST[15 & var7 >>> 4];
            var4 = var8 + 1;
            var3[var8] = HEX_DIGIST[var7 & 15];
         }

         String var5 = new String(var3);
         if(var1) {
            var6 = var5.toUpperCase();
         } else {
            var6 = var5;
         }
      }

      return var6;
   }
}
