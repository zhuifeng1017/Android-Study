package com.mappn.gfan.common.codec.language;

import com.mappn.gfan.common.codec.EncoderException;
import com.mappn.gfan.common.codec.StringEncoder;
import java.util.Locale;

final class SoundexUtils {

   static String clean(String var0) {
      String var1;
      if(var0 != null && var0.length() != 0) {
         int var2 = var0.length();
         char[] var3 = new char[var2];
         int var4 = 0;

         int var5;
         for(var5 = 0; var4 < var2; ++var4) {
            if(Character.isLetter(var0.charAt(var4))) {
               int var6 = var5 + 1;
               var3[var5] = var0.charAt(var4);
               var5 = var6;
            }
         }

         if(var5 == var2) {
            var1 = var0.toUpperCase(Locale.ENGLISH);
         } else {
            var1 = (new String(var3, 0, var5)).toUpperCase(Locale.ENGLISH);
         }
      } else {
         var1 = var0;
      }

      return var1;
   }

   static int difference(StringEncoder var0, String var1, String var2) throws EncoderException {
      return differenceEncoded(var0.encode(var1), var0.encode(var2));
   }

   static int differenceEncoded(String var0, String var1) {
      int var2 = 0;
      int var3;
      if(var0 != null && var1 != null) {
         int var4 = Math.min(var0.length(), var1.length());

         for(int var5 = 0; var5 < var4; ++var5) {
            if(var0.charAt(var5) == var1.charAt(var5)) {
               ++var2;
            }
         }

         var3 = var2;
      } else {
         var3 = 0;
      }

      return var3;
   }
}
