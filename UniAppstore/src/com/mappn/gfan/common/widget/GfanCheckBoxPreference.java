package com.mappn.gfan.common.widget;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class GfanCheckBoxPreference extends CheckBoxPreference {

   public GfanCheckBoxPreference(Context var1) {
      super(var1);
   }

   public GfanCheckBoxPreference(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.setLayoutResource(2130903068);
   }

   public GfanCheckBoxPreference(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
   }

   protected void onBindView(View var1) {
      super.onBindView(var1);
      ((TextView)var1.findViewById(2131492868)).setText(this.getTitle());
      ((TextView)var1.findViewById(16908304)).setText(this.getSummary());
      CheckBox var2 = (CheckBox)var1.findViewById(16908289);
      var2.setClickable(false);
      var2.setChecked(this.isChecked());
      ImageView var3 = (ImageView)var1.findViewById(2131492867);
      String var4 = this.getKey();
      if("update_app_notification".equals(var4)) {
         var3.setImageResource(2130837740);
      } else if("pref.recommend.app".equals(var4)) {
         var3.setImageResource(2130837738);
      } else if("not_download_image".equals(var4)) {
         var3.setImageResource(2130837739);
      } else if("delete_after_installation".equals(var4)) {
         var3.setImageResource(2130837736);
      } else if("theme_dark".equals(var4)) {
         var2.setButtonDrawable(2130837769);
         var3.setImageResource(2130837741);
      } else if("theme_light".equals(var4)) {
         var2.setButtonDrawable(2130837769);
         var3.setImageResource(2130837742);
      }

   }
}
