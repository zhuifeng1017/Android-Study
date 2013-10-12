package com.mappn.gfan.common.codec;

import com.mappn.gfan.common.codec.EncoderException;
import com.mappn.gfan.common.codec.StringEncoder;
import java.util.Comparator;

public class StringEncoderComparator implements Comparator {

   private final StringEncoder stringEncoder;


   public StringEncoderComparator() {
      this.stringEncoder = null;
   }

   public StringEncoderComparator(StringEncoder var1) {
      this.stringEncoder = var1;
   }

   public int compare(Object var1, Object var2) {
      int var4;
      int var5;
      try {
         var5 = ((Comparable)this.stringEncoder.encode(var1)).compareTo((Comparable)this.stringEncoder.encode(var2));
      } catch (EncoderException var6) {
         var4 = 0;
         return var4;
      }

      var4 = var5;
      return var4;
   }
}
