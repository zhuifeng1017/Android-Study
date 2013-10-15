package com.xxx.appstore.common.util;

import java.util.ArrayList;
import java.util.List;

public class BooleanLogicUtil {

   private static final int AND = 6;
   private static final int BIT_AND = 3;
   private static final int BIT_NOR = 4;
   private static final int BIT_OR = 5;
   private static final int LEFT = 0;
   private static final int NOT = 2;
   private static final int OR = 7;
   private static final int RIGHT = 1;
   private static final int SKIP = -1;
   private static final int TEXT = 8;
   private BooleanLogicUtil.LogicRunner g;
   private char[] operator;
   private String patten;


   public BooleanLogicUtil(String var1) {
      this.patten = var1;
      this.operator = var1.toCharArray();
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      int var4 = 0;

      int[] var6;
      for(int var5 = -1; var4 < this.operator.length; var4 += var6[2]) {
         var6 = this.parseOperator(this.operator, var4, this.operator.length);
         if(var6[0] != -1) {
            var2.add(var6);
            if(var6[0] == 0) {
               var3.add(var6);
            } else if(var6[0] == 1) {
               if(var3.size() == 0) {
                  throw new IllegalArgumentException(this.getMessage(var6, "missing \'(\'"));
               }

               var3.remove(var3.size() - 1);
            }

            if(var6[0] == 1 && var5 == 0) {
               throw new IllegalArgumentException(this.getMessage(var6, (String)null));
            }

            var5 = var6[0];
         }
      }

      if(var3.size() > 0) {
         throw new IllegalArgumentException(this.getMessage((int[])var3.get(var3.size() - 1), "missing \')\'"));
      } else {
         this.g = this.parseAll(var2, new int[]{0}, new BooleanLogicUtil.LogicRunner[]{null}, false);
         if(this.g == null || this.g.type == 8 && this.g.v == null || this.g.type != 8 && (this.g.r1 == null || this.g.type != 2 && this.g.r2 == null)) {
            throw new IllegalArgumentException("not completed\n\'" + var1 + "\'");
         }
      }
   }

   private void change(List<int[]> var1, int[] var2, BooleanLogicUtil.LogicRunner[] var3, int[] var4) {
      if(this.needSplit(var3[0].r2, var4[0])) {
         BooleanLogicUtil.LogicRunner[] var11 = new BooleanLogicUtil.LogicRunner[]{var3[0].r2};
         this.change(var1, var2, var11, var4);
      } else {
         BooleanLogicUtil.LogicRunner var5 = var3[0];
         int var6 = var4[0];
         BooleanLogicUtil.LogicRunner var7 = var3[0].r2;
         int[] var8 = new int[]{var4[1], var4[2]};
         var5.r2 = new BooleanLogicUtil.LogicRunner(var6, var7, (BooleanLogicUtil.LogicRunner)null, var8, this.operator);
         BooleanLogicUtil.LogicRunner[] var9 = new BooleanLogicUtil.LogicRunner[]{var3[0].r2};
         this.parseAll(var1, var2, var9, true);
      }

   }

   private String getMessage(int[] var1, String var2) {
      String var3;
      if(var2 == null) {
         var3 = "something wrong :";
      } else {
         var3 = var2;
      }

      StringBuilder var4 = new StringBuilder(var3);
      var4.append('\n');

      for(int var6 = 0; var6 < this.operator.length; var6 += 80) {
         char[] var7 = this.operator;
         int var8;
         if(this.operator.length - var6 < 80) {
            var8 = this.operator.length - var6;
         } else {
            var8 = 80;
         }

         var4.append(var7, var6, var8);
         var4.append('\n');
         if(var1[1] - var6 >= 0 && var1[1] - var6 < 80) {
            for(int var11 = var1[1] - var6; var11 > 0; --var11) {
               var4.append(' ');
            }

            var4.append("^\n");
         }
      }

      return var4.substring(0, var4.length() - 1);
   }

   private boolean needSplit(BooleanLogicUtil.LogicRunner var1, int var2) {
      boolean var3;
      if(var1.splitable && var2 > 2 && var2 < 7 && var1.type > 3 && var1.type < 8 && var1.type > var2) {
         var3 = true;
      } else {
         var3 = false;
      }

      return var3;
   }

   private BooleanLogicUtil.LogicRunner parse(List<int[]> var1, int[] var2) {
      int var3 = var2[0];
      var2[0] = var3 + 1;
      int[] var4 = (int[])var1.get(var3);
      BooleanLogicUtil.LogicRunner var5;
      if(var4[0] == 8) {
         int[] var8 = new int[]{var4[1], var4[2]};
         var5 = new BooleanLogicUtil.LogicRunner(8, (BooleanLogicUtil.LogicRunner)null, (BooleanLogicUtil.LogicRunner)null, var8, this.operator);
      } else if(var4[0] == 2) {
         BooleanLogicUtil.LogicRunner var6;
         do {
            var6 = this.parse(var1, var2);
         } while(var6 == null);

         int[] var7 = new int[]{var4[1], var4[2]};
         var5 = new BooleanLogicUtil.LogicRunner(2, var6, (BooleanLogicUtil.LogicRunner)null, var7, this.operator);
      } else {
         if(var4[0] != 0) {
            throw new IllegalArgumentException(this.getMessage(var4, (String)null));
         }

         var5 = this.parseAll(var1, var2, new BooleanLogicUtil.LogicRunner[]{null}, false);
      }

      return var5;
   }

   private BooleanLogicUtil.LogicRunner parseAll(List<int[]> var1, int[] var2, BooleanLogicUtil.LogicRunner[] var3, boolean var4) {
      if(var2[0] >= var1.size()) {
         throw new IllegalArgumentException("not completed\n\'" + this.patten + "\'");
      } else {
         while(true) {
            BooleanLogicUtil.LogicRunner var5;
            if(var2[0] < var1.size()) {
               int var6 = var2[0];
               var2[0] = var6 + 1;
               int[] var7 = (int[])var1.get(var6);
               switch(var7[0]) {
               case 0:
                  BooleanLogicUtil.LogicRunner var8;
                  do {
                     var8 = this.parseAll(var1, var2, new BooleanLogicUtil.LogicRunner[]{null}, false);
                  } while(var8 == null);

                  var8.splitable = false;
                  if(!this.tryAdd(var3, var8, var7, var4)) {
                     continue;
                  }

                  var5 = var3[0];
                  break;
               case 1:
                  if(var3[0] != null && (var3[0].type == 8 && var3[0].v == null || var3[0].type != 8 && (var3[0].r1 == null || var3[0].type != 2 && var3[0].r2 == null))) {
                     throw new IllegalArgumentException(this.getMessage(var7, (String)null));
                  }

                  var5 = var3[0];
                  break;
               case 2:
                  BooleanLogicUtil.LogicRunner var12;
                  do {
                     var12 = this.parse(var1, var2);
                  } while(var12 == null);

                  int[] var13 = new int[]{var7[1], var7[2]};
                  if(!this.tryAdd(var3, new BooleanLogicUtil.LogicRunner(2, var12, (BooleanLogicUtil.LogicRunner)null, var13, this.operator), var7, var4)) {
                     continue;
                  }

                  var5 = var3[0];
                  break;
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
                  if(var3[0] != null && (var3[0].type == 8 || var3[0].r1 != null && (var3[0].type == 2 || var3[0].r2 != null))) {
                     if(this.needSplit(var3[0], var7[0])) {
                        this.change(var1, var2, var3, var7);
                     } else {
                        int var9 = var7[0];
                        BooleanLogicUtil.LogicRunner var10 = var3[0];
                        int[] var11 = new int[]{var7[1], var7[2]};
                        var3[0] = new BooleanLogicUtil.LogicRunner(var9, var10, (BooleanLogicUtil.LogicRunner)null, var11, this.operator);
                     }
                     continue;
                  }

                  throw new IllegalArgumentException(this.getMessage(var7, (String)null));
               case 8:
                  int[] var14 = new int[]{var7[1], var7[2]};
                  if(!this.tryAdd(var3, new BooleanLogicUtil.LogicRunner(8, (BooleanLogicUtil.LogicRunner)null, (BooleanLogicUtil.LogicRunner)null, var14, this.operator), var7, var4)) {
                     continue;
                  }

                  var5 = var3[0];
                  break;
               default:
                  throw new IllegalArgumentException(this.getMessage(var7, (String)null));
               }
            } else {
               var5 = var3[0];
            }

            return var5;
         }
      }
   }

   private int[] parseOperator(char[] var1, int var2, int var3) {
      int[] var4 = new int[]{-1, var2, var3 - var2};

      for(int var5 = var2; var5 < var3; ++var5) {
         byte var6;
         if(var1[var5] == 40) {
            var6 = 0;
         } else if(var1[var5] == 41) {
            var6 = 1;
         } else if(var1[var5] == 33) {
            var6 = 2;
         } else if(var1[var5] == 94) {
            var6 = 4;
         } else if(var1[var5] == 38) {
            var6 = 6;
         } else if(var1[var5] == 124) {
            var6 = 7;
         } else if((var1[var5] < 48 || var1[var5] > 57) && (var1[var5] < 97 || var1[var5] > 122) && (var1[var5] < 65 || var1[var5] > 90) && var1[var5] != 95 && var1[var5] != 46 && var1[var5] != 45) {
            if(var1[var5] != 32) {
               throw new IllegalArgumentException("illegal character \'" + var1[var5] + "\' from \'" + this.patten + "\'");
            }

            var6 = -1;
         } else {
            var6 = 8;
         }

         if(var4[0] == -1) {
            var4[0] = var6;
            if(var6 == 2 || var6 == 4 || var6 == 0 || var6 == 1 || var6 == -1) {
               var4[2] += var5 + 1 - var3;
               break;
            }
         } else if(var4[0] != var6 || (var6 == 6 || var6 == 7) && var5 + var4[2] - var3 >= 2) {
            if(var5 + var4[2] - var3 == 1) {
               int var7;
               if(var4[0] == 6) {
                  var7 = 3;
               } else if(var4[0] == 7) {
                  var7 = 5;
               } else {
                  var7 = var4[0];
               }

               var4[0] = var7;
            }

            var4[2] += var5 - var3;
            break;
         }
      }

      return var4;
   }

   private boolean tryAdd(BooleanLogicUtil.LogicRunner[] var1, BooleanLogicUtil.LogicRunner var2, int[] var3, boolean var4) {
      boolean var5;
      if(var1[0] == null) {
         var1[0] = var2;
         var5 = false;
      } else {
         if(var1[0].type == 8 || var1[0].type == 2 || var1[0].r1 == null || var1[0].r2 != null) {
            throw new IllegalArgumentException(this.getMessage(var3, (String)null));
         }

         var1[0].r2 = var2;
         var5 = var4;
      }

      return var5;
   }

   public boolean excute(BooleanLogicUtil.CallBack<String, Boolean> var1) {
      return this.g.excute(var1);
   }

   public String toString() {
      return this.g.toString();
   }

   private static class LogicRunner {

      char[] operator;
      BooleanLogicUtil.LogicRunner r1;
      BooleanLogicUtil.LogicRunner r2;
      boolean splitable;
      int type;
      int[] v;


      public LogicRunner(int var1, BooleanLogicUtil.LogicRunner var2, BooleanLogicUtil.LogicRunner var3, int[] var4, char[] var5) {
         this.type = var1;
         this.r1 = var2;
         this.r2 = var3;
         this.v = var4;
         this.operator = var5;
         this.splitable = true;
      }

      boolean excute(BooleanLogicUtil.CallBack<String, Boolean> var1) {
         boolean var2;
         switch(this.type) {
         case 2:
            if(!this.r1.excute(var1)) {
               var2 = true;
            } else {
               var2 = false;
            }
            break;
         case 3:
            var2 = this.r1.excute(var1) & this.r2.excute(var1);
            break;
         case 4:
            var2 = this.r1.excute(var1) ^ this.r2.excute(var1);
            break;
         case 5:
            var2 = this.r1.excute(var1) | this.r2.excute(var1);
            break;
         case 6:
            if(this.r1.excute(var1) && this.r2.excute(var1)) {
               var2 = true;
            } else {
               var2 = false;
            }
            break;
         case 7:
            if(!this.r1.excute(var1) && !this.r2.excute(var1)) {
               var2 = false;
            } else {
               var2 = true;
            }
            break;
         case 8:
            var2 = ((Boolean)var1.run(new String(this.operator, this.v[0], this.v[1]))).booleanValue();
            break;
         default:
            throw new IllegalArgumentException("type : " + this.type);
         }

         return var2;
      }

      public String toString() {
         String var1;
         switch(this.type) {
         case 2:
            var1 = "(" + new String(this.operator, this.v[0], this.v[1]) + this.r1 + ")";
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            var1 = "(" + this.r1 + " " + new String(this.operator, this.v[0], this.v[1]) + " " + this.r2 + ")";
            break;
         case 8:
            var1 = new String(this.operator, this.v[0], this.v[1]);
            break;
         default:
            var1 = "";
         }

         return var1;
      }
   }

   public interface CallBack<K extends Object, V extends Object> {

      V run(K var1);
   }
}
