package com.mappn.gfan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.util.ThemeManager;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.RecommendTopic;
import com.mappn.gfan.common.widget.BaseTabActivity;
import com.mappn.gfan.common.widget.MarqueeTextView;
import com.mappn.gfan.ui.RecommendAppsActivity;
import com.mappn.gfan.ui.RecommendDiscussActivity;

public class RecommendActivity extends BaseTabActivity implements OnClickListener, ApiAsyncTask.ApiRequestListener {

   private static final String TAB_ALL = "all";
   private static final String TAB_FOLLOWED = "followed";
   private boolean isFirstIn = true;
   private CheckBox mFavorite;
   private TabHost mTabHost;
   private RecommendTopic mTopic;


   private void initTab() {
      this.mTabHost = this.getTabHost();
      this.mTabHost.getTabWidget().setPadding(this.mSession.mTabMargin110, 0, this.mSession.mTabMargin9, 0);
      this.mTabHost.setup();
      ((FrameLayout)this.mTabHost.findViewById(2131492901)).setBackgroundResource(ThemeManager.getResource(this.mSession, 14));
      TabSpec var1 = this.mTabHost.newTabSpec("all");
      var1.setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296639), -1, (TextView)null));
      Intent var3 = new Intent(this, RecommendAppsActivity.class);
      var3.putExtra("extra.recommend.detail", this.mTopic);
      var1.setContent(var3);
      this.mTabHost.addTab(var1);
      TabSpec var6 = this.mTabHost.newTabSpec("followed");
      var6.setIndicator(Utils.createTabView(this.getApplicationContext(), this.mSession, this.getString(2131296640), 1, (TextView)null));
      Intent var8 = new Intent(this, RecommendDiscussActivity.class);
      var8.putExtra("extra.recommend.detail", this.mTopic);
      var6.setContent(var8);
      this.mTabHost.addTab(var6);
      this.mTabHost.setCurrentTab(0);
   }

   private void initTopBar(RecommendTopic var1) {
      ((TextView)this.findViewById(2131492913)).setBackgroundResource(ThemeManager.getResource(this.mSession, 30));
      ImageView var2 = (ImageView)this.findViewById(2131492878);
      ImageUtils.download(this.getApplicationContext(), var1.icon, var2, 2130837569, true);
      MarqueeTextView var3 = (MarqueeTextView)this.findViewById(2131492868);
      var3.setTextAppearance(this.getApplicationContext(), ThemeManager.getResource(this.mSession, 13));
      if(this.mSession.getTheme() == 2131361833) {
         var3.setShadowLayer(0.1F, 0.0F, -2.0F, -16777216);
      }

      var3.setText(var1.title);
      this.mFavorite = (CheckBox)this.findViewById(2131492915);
      this.mFavorite.setButtonDrawable(ThemeManager.getResource(this.mSession, 38));
      this.mFavorite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         public void onCheckedChanged(CompoundButton var1, boolean var2) {
            if(!RecommendActivity.this.isFirstIn) {
               if(var2) {
                  Utils.trackEvent(RecommendActivity.this.getApplicationContext(), new String[]{"玩家推荐", "关注"});
                  MarketAPI.requestFollowMaster(RecommendActivity.this.getApplicationContext(), RecommendActivity.this, RecommendActivity.this.mTopic.id, "1");
               } else {
                  Utils.trackEvent(RecommendActivity.this.getApplicationContext(), new String[]{"玩家推荐", "取消关注"});
                  MarketAPI.requestFollowMaster(RecommendActivity.this.getApplicationContext(), RecommendActivity.this, RecommendActivity.this.mTopic.id, "0");
               }

               var1.setEnabled(false);
            }

         }
      });
   }

   public boolean dispatchKeyEvent(KeyEvent var1) {
      if(var1.getKeyCode() == 4) {
         Intent var2 = new Intent();
         var2.putExtra("extra.recommend.detail", this.mTopic);
         this.setResult(-1, var2);
      }

      return super.dispatchKeyEvent(var1);
   }

   public void onClick(View var1) {
      switch(var1.getId()) {
      case 2131492915:
         MarketAPI.requestFollowMaster(this.getApplicationContext(), this, this.mTopic.id, "1");
      default:
      }
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903061);
      this.mTopic = (RecommendTopic)this.getIntent().getSerializableExtra("extra.recommend.detail");
      this.initTopBar(this.mTopic);
      this.initTab();
      MarketAPI.getMasterRecommendRating(this.getApplicationContext(), this, this.mTopic.id);
      MarketAPI.queryFoolowStatus(this.getApplicationContext(), this, this.mTopic.id);
   }

   protected void onDestroy() {
      super.onDestroy();
      this.getLocalActivityManager().removeAllActivities();
   }

   public void onError(int var1, int var2) {
      if(var1 == 52) {
         this.mFavorite.setEnabled(true);
      }

      Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296415), false);
   }

   public void onSuccess(int var1, Object var2) {
      if(var1 == 52) {
         this.mFavorite.setEnabled(true);
         Boolean var3 = (Boolean)var2;
         this.mFavorite.setChecked(var3.booleanValue());
         if(this.isFirstIn) {
            this.isFirstIn = false;
         } else if(var3.booleanValue()) {
            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296648), false);
         } else {
            Utils.makeEventToast(this.getApplicationContext(), this.getString(2131296649), false);
         }
      }

   }
}
