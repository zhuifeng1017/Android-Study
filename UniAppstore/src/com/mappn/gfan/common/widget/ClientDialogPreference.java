package com.mappn.gfan.common.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mappn.gfan.common.util.CacheManager;
import com.mappn.gfan.common.util.DBUtils;
import com.mappn.gfan.common.util.Utils;

public class ClientDialogPreference extends DialogPreference {

   private Context mContext;


   public ClientDialogPreference(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.mContext = var1;
      this.setLayoutResource(2130903069);
   }

   protected void onBindView(View var1) {
      super.onBindView(var1);
      ImageView var2 = (ImageView)var1.findViewById(2131492867);
      String var3 = this.getKey();
      if("manual_clear_cache".equals(var3)) {
         var2.setImageResource(2130837735);
      } else if("manual_clear_search_history".equals(var3)) {
         var2.setImageResource(2130837737);
      }

      ((TextView)var1.findViewById(2131492868)).setText(this.getTitle());
      ((TextView)var1.findViewById(16908304)).setText(this.getSummary());
   }

   public void onClick(DialogInterface var1, int var2) {
      String var3 = this.getKey();
      switch(var2) {
      case -1:
         if("manual_clear_cache".equals(var3)) {
            Utils.clearCache(this.getContext());
            CacheManager.getInstance().clearFromMemory();
            Utils.trackEvent(this.mContext, new String[]{"设置", "清除缓存"});
         } else if("manual_clear_search_history".equals(var3)) {
            DBUtils.clearSearchHistory(this.getContext());
            Utils.trackEvent(this.mContext, new String[]{"设置", "清除搜索历史"});
         }
      case -2:
      default:
      }
   }
}
