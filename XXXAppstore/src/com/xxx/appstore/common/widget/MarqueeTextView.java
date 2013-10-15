package com.xxx.appstore.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

   public MarqueeTextView(Context var1) {
      super(var1);
   }

   public MarqueeTextView(Context var1, AttributeSet var2) {
      super(var1, var2);
   }

   public MarqueeTextView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
   }

   public boolean isFocused() {
      return true;
   }

   protected void onFocusChanged(boolean var1, int var2, Rect var3) {}
}
