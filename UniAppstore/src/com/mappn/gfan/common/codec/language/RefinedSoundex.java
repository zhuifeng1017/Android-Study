package com.mappn.gfan.common.codec.language;

import com.mappn.gfan.common.codec.EncoderException;
import com.mappn.gfan.common.codec.StringEncoder;
import com.mappn.gfan.common.codec.language.SoundexUtils;

public class RefinedSoundex implements StringEncoder {

   public static final RefinedSoundex US_ENGLISH = new RefinedSoundex();
   public static final char[] US_ENGLISH_MAPPING = "01360240043788015936020505".toCharArray();
   public static final String US_ENGLISH_MAPPING_STRING = "01360240043788015936020505";
   private final char[] soundexMapping;


   public RefinedSoundex() {
      this.soundexMapping = US_ENGLISH_MAPPING;
   }

   public RefinedSoundex(String var1) {
      this.soundexMapping = var1.toCharArray();
   }

   public RefinedSoundex(char[] var1) {
      this.soundexMapping = new char[var1.length];
      System.arraycopy(var1, 0, this.soundexMapping, 0, var1.length);
   }

   public int difference(String var1, String var2) throws EncoderException {
      return SoundexUtils.difference(this, var1, var2);
   }

   public Object encode(Object var1) throws EncoderException {
      if(!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to RefinedSoundex encode is not of type java.lang.String");
      } else {
         return this.soundex((String)var1);
      }
   }

   public String encode(String var1) {
      return this.soundex(var1);
   }

   char getMappingCode(char var1) {
      char var2;
      if(!Character.isLetter(var1)) {
         var2 = 0;
      } else {
         var2 = this.soundexMapping[Character.toUpperCase(var1) - 65];
      }

      return var2;
   }

   public String soundex(String var1) {
      String var2;
      if(var1 == null) {
         var2 = null;
      } else {
         var2 = SoundexUtils.clean(var1);
         if(var2.length() != 0) {
            StringBuffer var3 = new StringBuffer();
            var3.append(var2.charAt(0));
            char var5 = 42;

            for(int var6 = 0; var6 < var2.length(); ++var6) {
               char var7 = this.getMappingCode(var2.charAt(var6));
               if(var7 != var5) {
                  if(var7 != 0) {
                     var3.append(var7);
                  }

                  var5 = var7;
               }
            }

            var2 = var3.toString();
         }
      }

      return var2;
   }
}
