package com.xxx.appstore.common.widget;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class GfanListPreference extends ListPreference {

   public GfanListPreference(Context var1) {
      super(var1);
   }

   public GfanListPreference(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.setLayoutResource(2130903069);
   }

   protected void onBindView(View var1) {
      super.onBindView(var1);
      ((TextView)var1.findViewById(2131492868)).setText(this.getTitle());
      ((TextView)var1.findViewById(16908304)).setText(this.getSummary());
   }
}
