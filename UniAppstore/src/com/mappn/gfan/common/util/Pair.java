package com.mappn.gfan.common.util;


public class Pair<F extends Object, S extends Object> {

   public final F first;
   public final S second;


   public Pair(F var1, S var2) {
      this.first = var1;
      this.second = var2;
   }

   public static <A extends Object, B extends Object> Pair<A, B> create(A var0, B var1) {
      return new Pair(var0, var1);
   }

   public boolean equals(Object var1) {
      boolean var2;
      if(var1 == this) {
         var2 = true;
      } else if(!(var1 instanceof Pair)) {
         var2 = false;
      } else {
         Pair var4;
         try {
            var4 = (Pair)var1;
         } catch (ClassCastException var5) {
            var2 = false;
            return var2;
         }

         if(this.first.equals(var4.first) && this.second.equals(var4.second)) {
            var2 = true;
         } else {
            var2 = false;
         }
      }

      return var2;
   }

   public int hashCode() {
      int var10000 = 17 * 31;
      var10000 = 31 * (527 + this.first.hashCode()) + this.second.hashCode();
      return this.first.hashCode();
   }
}
