package com.xxx.appstore.common.widget;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class GfanPreferenceCategory extends PreferenceCategory {

   public GfanPreferenceCategory(Context var1) {
      super(var1);
   }

   public GfanPreferenceCategory(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.setLayoutResource(2130903045);
   }

   public GfanPreferenceCategory(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
   }

   protected void onBindView(View var1) {
      super.onBindView(var1);
      ((TextView)var1.findViewById(2131492875)).setText(this.getTitle());
   }
}
