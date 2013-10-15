package com.xxx.appstore.common.codec.language;

import com.xxx.appstore.common.codec.EncoderException;
import com.xxx.appstore.common.codec.StringEncoder;

import java.util.Locale;

public class Metaphone implements StringEncoder {

   private static final String FRONTV = "EIY";
   private static final String VARSON = "CSPTG";
   private static final String VOWELS = "AEIOU";
   private int maxCodeLen = 4;


   private boolean isLastChar(int var1, int var2) {
      boolean var3;
      if(var2 + 1 == var1) {
         var3 = true;
      } else {
         var3 = false;
      }

      return var3;
   }

   private boolean isNextChar(StringBuffer var1, int var2, char var3) {
      boolean var4;
      if(var2 >= 0 && var2 < var1.length() - 1) {
         if(var1.charAt(var2 + 1) == var3) {
            var4 = true;
         } else {
            var4 = false;
         }
      } else {
         var4 = false;
      }

      return var4;
   }

   private boolean isPreviousChar(StringBuffer var1, int var2, char var3) {
      boolean var4;
      if(var2 > 0 && var2 < var1.length()) {
         if(var1.charAt(var2 - 1) == var3) {
            var4 = true;
         } else {
            var4 = false;
         }
      } else {
         var4 = false;
      }

      return var4;
   }

   private boolean isVowel(StringBuffer var1, int var2) {
      boolean var3;
      if("AEIOU".indexOf(var1.charAt(var2)) >= 0) {
         var3 = true;
      } else {
         var3 = false;
      }

      return var3;
   }

   private boolean regionMatch(StringBuffer var1, int var2, String var3) {
      boolean var4 = false;
      if(var2 >= 0) {
         int var5 = var2 + var3.length() - 1;
         int var6 = var1.length();
         var4 = false;
         if(var5 < var6) {
            var4 = var1.substring(var2, var2 + var3.length()).equals(var3);
         }
      }

      return var4;
   }

   public Object encode(Object var1) throws EncoderException {
      if(!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to Metaphone encode is not of type java.lang.String");
      } else {
         return this.metaphone((String)var1);
      }
   }

   public String encode(String var1) {
      return this.metaphone(var1);
   }

   public int getMaxCodeLen() {
      return this.maxCodeLen;
   }

   public boolean isMetaphoneEqual(String var1, String var2) {
      return this.metaphone(var1).equals(this.metaphone(var2));
   }

   public String metaphone(String var1) {
      String var2;
      if(var1 != null && var1.length() != 0) {
         if(var1.length() == 1) {
            var2 = var1.toUpperCase(Locale.ENGLISH);
         } else {
            char[] var3 = var1.toUpperCase(Locale.ENGLISH).toCharArray();
            StringBuffer var4 = new StringBuffer(40);
            StringBuffer var5 = new StringBuffer(10);
            switch(var3[0]) {
            case 65:
               if(var3[1] == 69) {
                  var4.append(var3, 1, var3.length - 1);
               } else {
                  var4.append(var3);
               }
               break;
            case 71:
            case 75:
            case 80:
               if(var3[1] == 78) {
                  var4.append(var3, 1, var3.length - 1);
               } else {
                  var4.append(var3);
               }
               break;
            case 87:
               if(var3[1] == 82) {
                  var4.append(var3, 1, var3.length - 1);
               } else if(var3[1] == 72) {
                  var4.append(var3, 1, var3.length - 1);
                  var4.setCharAt(0, 'W');
               } else {
                  var4.append(var3);
               }
               break;
            case 88:
               var3[0] = 83;
               var4.append(var3);
               break;
            default:
               var4.append(var3);
            }

            int var7 = var4.length();
            int var8 = 0;

            while(var5.length() < this.getMaxCodeLen() && var8 < var7) {
               char var9 = var4.charAt(var8);
               if(var9 != 67 && this.isPreviousChar(var4, var8, var9)) {
                  ++var8;
               } else {
                  switch(var9) {
                  case 65:
                  case 69:
                  case 73:
                  case 79:
                  case 85:
                     if(var8 == 0) {
                        var5.append(var9);
                     }
                     break;
                  case 66:
                     if(!this.isPreviousChar(var4, var8, 'M') || !this.isLastChar(var7, var8)) {
                        var5.append(var9);
                     }
                     break;
                  case 67:
                     if(!this.isPreviousChar(var4, var8, 'S') || this.isLastChar(var7, var8) || "EIY".indexOf(var4.charAt(var8 + 1)) < 0) {
                        if(this.regionMatch(var4, var8, "CIA")) {
                           var5.append('X');
                        } else if(!this.isLastChar(var7, var8) && "EIY".indexOf(var4.charAt(var8 + 1)) >= 0) {
                           var5.append('S');
                        } else if(this.isPreviousChar(var4, var8, 'S') && this.isNextChar(var4, var8, 'H')) {
                           var5.append('K');
                        } else if(this.isNextChar(var4, var8, 'H')) {
                           if(var8 == 0 && var7 >= 3 && this.isVowel(var4, 2)) {
                              var5.append('K');
                           } else {
                              var5.append('X');
                           }
                        } else {
                           var5.append('K');
                        }
                     }
                     break;
                  case 68:
                     if(!this.isLastChar(var7, var8 + 1) && this.isNextChar(var4, var8, 'G') && "EIY".indexOf(var4.charAt(var8 + 2)) >= 0) {
                        var5.append('J');
                        var8 += 2;
                     } else {
                        var5.append('T');
                     }
                     break;
                  case 70:
                  case 74:
                  case 76:
                  case 77:
                  case 78:
                  case 82:
                     var5.append(var9);
                     break;
                  case 71:
                     if((!this.isLastChar(var7, var8 + 1) || !this.isNextChar(var4, var8, 'H')) && (this.isLastChar(var7, var8 + 1) || !this.isNextChar(var4, var8, 'H') || this.isVowel(var4, var8 + 2)) && (var8 <= 0 || !this.regionMatch(var4, var8, "GN") && !this.regionMatch(var4, var8, "GNED"))) {
                        boolean var27;
                        if(this.isPreviousChar(var4, var8, 'G')) {
                           var27 = true;
                        } else {
                           var27 = false;
                        }

                        if(!this.isLastChar(var7, var8) && "EIY".indexOf(var4.charAt(var8 + 1)) >= 0 && !var27) {
                           var5.append('J');
                        } else {
                           var5.append('K');
                        }
                     }
                     break;
                  case 72:
                     if(!this.isLastChar(var7, var8) && (var8 <= 0 || "CSPTG".indexOf(var4.charAt(var8 - 1)) < 0) && this.isVowel(var4, var8 + 1)) {
                        var5.append('H');
                     }
                     break;
                  case 75:
                     if(var8 > 0) {
                        if(!this.isPreviousChar(var4, var8, 'C')) {
                           var5.append(var9);
                        }
                     } else {
                        var5.append(var9);
                     }
                     break;
                  case 80:
                     if(this.isNextChar(var4, var8, 'H')) {
                        var5.append('F');
                     } else {
                        var5.append(var9);
                     }
                     break;
                  case 81:
                     var5.append('K');
                     break;
                  case 83:
                     if(!this.regionMatch(var4, var8, "SH") && !this.regionMatch(var4, var8, "SIO") && !this.regionMatch(var4, var8, "SIA")) {
                        var5.append('S');
                     } else {
                        var5.append('X');
                     }
                     break;
                  case 84:
                     if(!this.regionMatch(var4, var8, "TIA") && !this.regionMatch(var4, var8, "TIO")) {
                        if(!this.regionMatch(var4, var8, "TCH")) {
                           if(this.regionMatch(var4, var8, "TH")) {
                              var5.append('0');
                           } else {
                              var5.append('T');
                           }
                        }
                     } else {
                        var5.append('X');
                     }
                     break;
                  case 86:
                     var5.append('F');
                     break;
                  case 87:
                  case 89:
                     if(!this.isLastChar(var7, var8) && this.isVowel(var4, var8 + 1)) {
                        var5.append(var9);
                     }
                     break;
                  case 88:
                     var5.append('K');
                     var5.append('S');
                     break;
                  case 90:
                     var5.append('S');
                  }

                  ++var8;
               }

               if(var5.length() > this.getMaxCodeLen()) {
                  var5.setLength(this.getMaxCodeLen());
               }
            }

            var2 = var5.toString();
         }
      } else {
         var2 = "";
      }

      return var2;
   }

   public void setMaxCodeLen(int var1) {
      this.maxCodeLen = var1;
   }
}
