package com.mappn.gfan.common.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.widget.NavigationTitle;
import com.mappn.gfan.ui.SearchActivity;

public class TopBar {

   public static void createTopBar(Session var0, final Activity var1, View[] var2, int[] var3, String var4) {
      var1.findViewById(2131492884).setBackgroundResource(ThemeManager.getResource(var0, 30));
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         View var7 = var2[var6];
         var7.setVisibility(var3[var6]);
         if(8 != var3[var6]) {
            switch(var7.getId()) {
            case 2131493033:
               ImageButton var8 = (ImageButton)var7;
               var8.setImageResource(ThemeManager.getResource(var0, 32));
               var8.setBackgroundResource(ThemeManager.getResource(var0, 42));
               var8.setOnClickListener(new OnClickListener() {
                  public void onClick(View var1x) {
                     Intent var2 = new Intent(var1, SearchActivity.class);
                     var1.startActivity(var2);
                  }
               });
               break;
            case 2131493034:
               ((ImageView)var1.findViewById(2131493034)).setImageResource(ThemeManager.getResource(var0, 33));
               break;
            case 2131493035:
               ((NavigationTitle)var7).pushTitle(var4);
            }
         }
      }

   }

   public static void initSkin(Session var0, Activity var1) {
      var1.findViewById(2131492884).setBackgroundResource(ThemeManager.getResource(var0, 30));
      ((ImageView)var1.findViewById(2131493034)).setImageResource(ThemeManager.getResource(var0, 33));
      ImageButton var2 = (ImageButton)var1.findViewById(2131493033);
      var2.setImageResource(ThemeManager.getResource(var0, 32));
      var2.setBackgroundResource(ThemeManager.getResource(var0, 42));
      ((NavigationTitle)var1.findViewById(2131493035)).initSkin(var0.getTheme());
   }
}
