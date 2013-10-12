package com.mappn.gfan.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.util.ThemeManager;
import java.util.ArrayList;

public class NavigationTitle extends LinearLayout {

   private int mCurrentLevel = 0;
   private ArrayList<TextView> mNavigationTitles = new ArrayList();


   public NavigationTitle(Context var1) {
      super(var1);
   }

   public NavigationTitle(Context var1, AttributeSet var2) {
      super(var1, var2);
   }

   private TextView createTextView(Context var1, String var2, int var3) {
      TextView var4 = (TextView)LayoutInflater.from(var1).inflate(2130903128, this, false);
      var4.setIncludeFontPadding(false);
      var4.setLayoutParams(new LayoutParams(-2, -1));
      var4.setGravity(17);
      var4.setTextAppearance(var1, ThemeManager.getResource(Session.get(this.getContext()), 13));
      if(Session.get(this.getContext()).getTheme() == 2131361833) {
         var4.setShadowLayer(0.1F, 0.0F, -2.0F, -16777216);
      } else {
         var4.setShadowLayer(0.0F, 0.0F, 0.0F, -1);
      }

      var4.setSingleLine(true);
      var4.setText(var2);
      if(var3 != 0) {
         var4.setCompoundDrawablesWithIntrinsicBounds(ThemeManager.getResource(Session.get(this.getContext()), 34), 0, 0, 0);
      }

      return var4;
   }

   public void initSkin(int var1) {
      if(this.mNavigationTitles.size() > 0) {
         TextView var2 = (TextView)this.mNavigationTitles.get(this.mCurrentLevel - 1);
         var2.setTextAppearance(this.getContext(), ThemeManager.getResource(Session.get(this.getContext()), 13));
         if(var1 == 2131361833) {
            var2.setShadowLayer(0.1F, 0.0F, -2.0F, -16777216);
         } else {
            var2.setShadowLayer(0.0F, 0.0F, 0.0F, -1);
         }

         if(this.mCurrentLevel > 0) {
            int var3 = this.mNavigationTitles.size() - 1;

            for(int var4 = 0; var4 < var3; ++var4) {
               TextView var5 = (TextView)this.mNavigationTitles.get(var4);
               var5.setTextAppearance(this.getContext(), 2131361804);
               if(var1 == 2131361833) {
                  var5.setShadowLayer(0.1F, 0.0F, -2.0F, -16777216);
               } else {
                  var5.setShadowLayer(0.0F, 0.0F, 0.0F, -1);
               }
            }
         }
      }

   }

   public void popTitle() {
      if(this.mCurrentLevel > 1) {
         this.mNavigationTitles.remove(this.mCurrentLevel - 1);
         this.removeViewAt(this.mCurrentLevel - 1);
         ((TextView)this.mNavigationTitles.get(this.mCurrentLevel - 2)).setTextAppearance(this.getContext(), ThemeManager.getResource(Session.get(this.getContext()), 13));
         --this.mCurrentLevel;
      }

   }

   public void pushTitle(String var1) {
      TextView var2 = this.createTextView(this.getContext(), var1, this.mCurrentLevel);
      if(this.mCurrentLevel > 0) {
         int var5 = this.mNavigationTitles.size();

         for(int var6 = 0; var6 < var5; ++var6) {
            ((TextView)this.mNavigationTitles.get(var6)).setTextAppearance(this.getContext(), 2131361804);
         }
      }

      int var3 = this.mCurrentLevel;
      this.mCurrentLevel = var3 + 1;
      this.addView(var2, var3);
      this.mNavigationTitles.add(var2);
   }
}
