package com.mappn.gfan.common.vo;

import com.mappn.gfan.common.vo.CardsVerification;
import java.util.ArrayList;
import java.util.List;

public class CardsVerifications {

   public List<CardsVerification> cards = new ArrayList();
   public int version;


   public String[] getCardNames() {
      List var1 = this.cards;
      String[] var2 = null;
      if(var1 != null) {
         int var3 = this.cards.size();
         var2 = null;
         if(var3 > 0) {
            int var4 = this.cards.size();
            String[] var5 = new String[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = ((CardsVerification)this.cards.get(var6)).name;
            }

            var2 = var5;
         }
      }

      return var2;
   }
}
