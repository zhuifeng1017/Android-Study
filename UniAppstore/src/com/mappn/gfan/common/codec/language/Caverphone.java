package com.mappn.gfan.common.codec.language;

import com.mappn.gfan.common.codec.EncoderException;
import com.mappn.gfan.common.codec.StringEncoder;
import java.util.Locale;

public class Caverphone implements StringEncoder {

   public String caverphone(String var1) {
      String var2;
      if(var1 != null && var1.length() != 0) {
         String var3 = var1.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z]", "").replaceAll("e$", "").replaceAll("^cough", "cou2f").replaceAll("^rough", "rou2f").replaceAll("^tough", "tou2f").replaceAll("^enough", "enou2f").replaceAll("^trough", "trou2f").replaceAll("^gn", "2n").replaceAll("^mb", "m2").replaceAll("cq", "2q").replaceAll("ci", "si").replaceAll("ce", "se").replaceAll("cy", "sy").replaceAll("tch", "2ch").replaceAll("c", "k").replaceAll("q", "k").replaceAll("x", "k").replaceAll("v", "f").replaceAll("dg", "2g").replaceAll("tio", "sio").replaceAll("tia", "sia").replaceAll("d", "t").replaceAll("ph", "fh").replaceAll("b", "p").replaceAll("sh", "s2").replaceAll("z", "s").replaceAll("^[aeiou]", "A").replaceAll("[aeiou]", "3").replaceAll("j", "y").replaceAll("^y3", "Y3").replaceAll("^y", "A").replaceAll("y", "3").replaceAll("3gh3", "3kh3").replaceAll("gh", "22").replaceAll("g", "k").replaceAll("s+", "S").replaceAll("t+", "T").replaceAll("p+", "P").replaceAll("k+", "K").replaceAll("f+", "F").replaceAll("m+", "M").replaceAll("n+", "N").replaceAll("w3", "W3").replaceAll("wh3", "Wh3").replaceAll("w$", "3").replaceAll("w", "2").replaceAll("^h", "A").replaceAll("h", "2").replaceAll("r3", "R3").replaceAll("r$", "3").replaceAll("r", "2").replaceAll("l3", "L3").replaceAll("l$", "3").replaceAll("l", "2").replaceAll("2", "").replaceAll("3$", "A").replaceAll("3", "");
         var2 = (var3 + "111111" + "1111").substring(0, 10);
      } else {
         var2 = "1111111111";
      }

      return var2;
   }

   public Object encode(Object var1) throws EncoderException {
      if(!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to Caverphone encode is not of type java.lang.String");
      } else {
         return this.caverphone((String)var1);
      }
   }

   public String encode(String var1) {
      return this.caverphone(var1);
   }

   public boolean isCaverphoneEqual(String var1, String var2) {
      return this.caverphone(var1).equals(this.caverphone(var2));
   }
}
