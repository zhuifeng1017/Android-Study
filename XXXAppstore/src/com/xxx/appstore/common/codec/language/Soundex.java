package com.xxx.appstore.common.codec.language;

import com.xxx.appstore.common.codec.EncoderException;
import com.xxx.appstore.common.codec.StringEncoder;
import com.xxx.appstore.common.codec.language.SoundexUtils;

public class Soundex implements StringEncoder {

   public static final Soundex US_ENGLISH = new Soundex();
   public static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
   public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
   private int maxLength = 4;
   private final char[] soundexMapping;


   public Soundex() {
      this.soundexMapping = US_ENGLISH_MAPPING;
   }

   public Soundex(String var1) {
      this.soundexMapping = var1.toCharArray();
   }

   public Soundex(char[] var1) {
      this.soundexMapping = new char[var1.length];
      System.arraycopy(var1, 0, this.soundexMapping, 0, var1.length);
   }

   private char getMappingCode(String var1, int var2) {
      char var3 = this.map(var1.charAt(var2));
      if(var2 > 1 && var3 != 48) {
         char var4 = var1.charAt(var2 - 1);
         if(72 == var4 || 87 == var4) {
            char var5 = var1.charAt(var2 - 2);
            if(this.map(var5) == var3 || 72 == var5 || 87 == var5) {
               var3 = 0;
            }
         }
      }

      return var3;
   }

   private char[] getSoundexMapping() {
      return this.soundexMapping;
   }

   private char map(char var1) {
      int var2 = var1 - 65;
      if(var2 >= 0 && var2 < this.getSoundexMapping().length) {
         return this.getSoundexMapping()[var2];
      } else {
         throw new IllegalArgumentException("The character is not mapped: " + var1);
      }
   }

   public int difference(String var1, String var2) throws EncoderException {
      return SoundexUtils.difference(this, var1, var2);
   }

   public Object encode(Object var1) throws EncoderException {
      if(!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
      } else {
         return this.soundex((String)var1);
      }
   }

   public String encode(String var1) {
      return this.soundex(var1);
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void setMaxLength(int var1) {
      this.maxLength = var1;
   }

   public String soundex(String var1) {
      String var2;
      if(var1 == null) {
         var2 = null;
      } else {
         var2 = SoundexUtils.clean(var1);
         if(var2.length() != 0) {
            char[] var3 = new char[]{'0', '0', '0', '0'};
            var3[0] = var2.charAt(0);
            char var4 = this.getMappingCode(var2, 0);
            int var5 = 1;
            char var6 = var4;
            int var7 = 1;

            while(var5 < var2.length() && var7 < var3.length) {
               int var8 = var5 + 1;
               char var9 = this.getMappingCode(var2, var5);
               if(var9 != 0) {
                  if(var9 != 48 && var9 != var6) {
                     int var10 = var7 + 1;
                     var3[var7] = var9;
                     var7 = var10;
                  }

                  var6 = var9;
                  var5 = var8;
               } else {
                  var5 = var8;
               }
            }

            var2 = new String(var3);
         }
      }

      return var2;
   }
}
