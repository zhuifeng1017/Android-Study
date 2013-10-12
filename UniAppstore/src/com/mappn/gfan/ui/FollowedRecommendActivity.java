package com.mappn.gfan.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.mappn.gfan.common.ApiAsyncTask;
import com.mappn.gfan.common.MarketAPI;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.util.Utils;
import com.mappn.gfan.common.vo.RecommendTopic;
import com.mappn.gfan.common.widget.BaseActivity;
import com.mappn.gfan.common.widget.LoadingDrawable;
import com.mappn.gfan.ui.PreloadActivity;
import com.mappn.gfan.ui.RecommendActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class FollowedRecommendActivity extends BaseActivity implements OnGroupClickListener, OnChildClickListener, ApiAsyncTask.ApiRequestListener, OnClickListener {

   private FollowedRecommendActivity.ExpandableListAdapter mAdapter;
   private ExpandableListView mList;
   private FrameLayout mLoading;
   private TextView mNoData;
   private ProgressBar mProgress;


   public boolean onChildClick(ExpandableListView var1, View var2, int var3, int var4, long var5) {
      String var7 = (String)((HashMap)((ArrayList)((HashMap)this.mAdapter.getGroup(var3)).get("product_list")).get(var4)).get("packagename");
      Intent var8 = new Intent(this.getApplicationContext(), PreloadActivity.class);
      var8.putExtra("extra.key.package.name", var7);
      this.startActivity(var8);
      return true;
   }

   public void onClick(View var1) {
      if(var1.getId() == 2131492979) {
         this.mProgress.setVisibility(0);
         this.mNoData.setVisibility(8);
         MarketAPI.getFollowedRecommend(this.getApplicationContext(), this);
      }

   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2130903054);
      this.mList = (ExpandableListView)this.findViewById(2131492898);
      this.mList.setOnGroupClickListener(this);
      this.mList.setOnChildClickListener(this);
      this.mLoading = (FrameLayout)this.findViewById(2131492978);
      this.mProgress = (ProgressBar)this.mLoading.findViewById(2131492869);
      this.mProgress.setIndeterminateDrawable(new LoadingDrawable(this.getApplicationContext()));
      this.mProgress.setVisibility(0);
      this.mNoData = (TextView)this.mLoading.findViewById(2131492979);
      this.mNoData.setOnClickListener(this);
      this.mList.setEmptyView(this.mLoading);
      MarketAPI.getFollowedRecommend(this.getApplicationContext(), this);
   }

   public void onError(int var1, int var2) {
      if(var2 != 610) {
         this.mNoData.setVisibility(0);
         this.mProgress.setVisibility(8);
      }

   }

   public boolean onGroupClick(ExpandableListView var1, View var2, int var3, long var4) {
      HashMap var6 = (HashMap)this.mAdapter.getGroup(var3);
      RecommendTopic var7 = new RecommendTopic();
      var7.id = (String)var6.get("id");
      var7.icon = (String)var6.get("icon_url");
      var7.title = (String)var6.get("name");
      var7.description = (String)var6.get("description");
      var7.up = Utils.getInt((String)var6.get("like"));
      var7.down = Utils.getInt((String)var6.get("dislike"));
      var7.experience = (String)var6.get("experience");
      var7.user = (String)var6.get("user");
      var7.fans = Utils.getInt((String)var6.get("fans"));
      Intent var8 = new Intent(this.getApplicationContext(), RecommendActivity.class);
      var8.putExtra("extra.recommend.detail", var7);
      this.startActivityForResult(var8, 0);
      return true;
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      return this.getParent().onKeyDown(var1, var2);
   }

   public void onSuccess(int var1, Object var2) {
      if(var1 == 53) {
         HashMap var3 = (HashMap)var2;
         if(((Integer)var3.get("total_size")).intValue() > 0) {
            ArrayList var4 = (ArrayList)var3.get("recommend_list");
            this.mAdapter = new FollowedRecommendActivity.ExpandableListAdapter(this.getApplicationContext(), var4);
            this.mList.setAdapter(this.mAdapter);
         } else {
            this.mNoData.setCompoundDrawablesWithIntrinsicBounds(0, 2130837721, 0, 0);
            this.mNoData.setCompoundDrawablePadding(10);
            this.mNoData.setText(2131296647);
            this.mNoData.setVisibility(0);
            this.mProgress.setVisibility(8);
         }
      }

   }

   public class ExpandableListAdapter extends BaseExpandableListAdapter {

      private Context mContext;
      private ArrayList<HashMap<String, Object>> mDataSource;
      private LayoutInflater mInflater;
      private final int[] toChild = new int[]{2131492867, 2131492868, 2131493001, 2131492966, 2131492905};
      private final int[] toGroup = new int[]{2131492878, 2131492894, 2131492896, 2131492895, 2131492897, 2131492879};


      public ExpandableListAdapter(Context var2, ArrayList var3) {
         this.mContext = var2;
         this.mDataSource = var3;
         this.mInflater = (LayoutInflater)var2.getSystemService("layout_inflater");
      }

      private void bindChildView(int var1, int var2, View var3) {
         HashMap var4 = (HashMap)this.mDataSource.get(var1);
         if(var4 != null) {
            ArrayList var5 = (ArrayList)var4.get("product_list");
            if(var5 != null && var5.size() > 0) {
               HashMap var6 = (HashMap)var5.get(var2);
               View[] var7 = (View[])((View[])var3.getTag());
               int var8 = this.toChild.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  View var10 = var7[var9];
                  int var11 = var10.getId();
                  if(var11 == 2131492867) {
                     ImageUtils.download(this.mContext, (String)var6.get("icon_url"), (ImageView)var10);
                  } else if(var11 == 2131492868) {
                     ((TextView)var10).setText((String)var6.get("name"));
                  } else if(var11 == 2131493001) {
                     ((TextView)var10).setText((String)var6.get("sub_category"));
                  } else if(var11 == 2131492966) {
                     ((TextView)var10).setText((String)var6.get("app_size"));
                  } else if(var11 == 2131492905) {
                     ((TextView)var10).setText((String)var6.get("short_description"));
                  }
               }
            }
         }

      }

      private void bindGroupView(final int var1, final boolean var2, View var3) {
         final HashMap var4 = (HashMap)this.mDataSource.get(var1);
         if(var4 != null) {
            View[] var5 = (View[])((View[])var3.getTag());
            int var6 = this.toGroup.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               View var8 = var5[var7];
               int var9 = var8.getId();
               if(var9 == 2131492879) {
                  if(var2) {
                     var8.setVisibility(0);
                  } else {
                     var8.setVisibility(8);
                  }
               } else if(var9 == 2131492878) {
                  ImageUtils.download(this.mContext, (String)var4.get("icon_url"), (ImageView)var8, 2130837569, false);
               } else if(var9 == 2131492894) {
                  ((TextView)var8).setText((String)var4.get("name"));
               } else if(var9 == 2131492896) {
                  String var11 = (String)var4.get("user");
                  if(!TextUtils.isEmpty(var11)) {
                     SpannableStringBuilder var12 = new SpannableStringBuilder(var11);
                     TextAppearanceSpan var13 = new TextAppearanceSpan(this.mContext, 2131361797);
                     int var14 = var11.length();
                     var12.setSpan(var13, 0, var14, 17);
                     int var15 = ((Integer)var4.get("unread")).intValue();
                     if(var15 > 0) {
                        Context var17 = this.mContext;
                        Object[] var18 = new Object[]{String.valueOf(var15)};
                        var12.append(var17.getString(2131296645, var18));
                     } else {
                        var12.append(this.mContext.getString(2131296646));
                     }

                     var12.setSpan(new TextAppearanceSpan(this.mContext, 2131361794), var14, var12.length(), 17);
                     ((TextView)var8).setText(var12);
                  } else {
                     ((TextView)var8).setText((String)var4.get("update_time"));
                  }
               } else if(var9 == 2131492895) {
                  if(((Integer)var4.get("unread")).intValue() > 0) {
                     TextView var10 = (TextView)var8;
                     if(var2) {
                        var10.setBackgroundResource(2130837603);
                     } else {
                        var10.setBackgroundResource(2130837602);
                     }

                     var10.setText(2131296644);
                     var10.setVisibility(0);
                     var10.setClickable(true);
                     var10.setOnClickListener(new OnClickListener() {
                        public void onClick(View var1x) {
                           if(var2) {
                              FollowedRecommendActivity.this.mList.collapseGroup(var1);
                           } else {
                              Utils.trackEvent(FollowedRecommendActivity.this.getApplicationContext(), new String[]{"玩家推荐", "点击查看新品"});
                              String var2x = (String)var4.get("id");
                              MarketAPI.markFollowedRead(ExpandableListAdapter.this.mContext, var2x, FollowedRecommendActivity.this);
                              FollowedRecommendActivity.this.mList.expandGroup(var1);
                           }

                        }
                     });
                  } else {
                     ((TextView)var8).setVisibility(4);
                  }
               } else if(var9 == 2131492897) {
                  if(((Integer)var4.get("unread")).intValue() > 0) {
                     ((ImageView)var8).setVisibility(8);
                  } else {
                     ((ImageView)var8).setVisibility(0);
                  }
               }
            }
         }

      }

      private View newChildView(ViewGroup var1) {
         int var2 = 0;
         View var3 = this.mInflater.inflate(2130903114, var1, false);
         int var4 = this.toChild.length;

         View[] var5;
         for(var5 = new View[var4]; var2 < var4; ++var2) {
            var5[var2] = var3.findViewById(this.toChild[var2]);
         }

         var3.setTag(var5);
         return var3;
      }

      private View newGroupView(ViewGroup var1) {
         int var2 = 0;
         View var3 = this.mInflater.inflate(2130903053, var1, false);
         int var4 = this.toGroup.length;

         View[] var5;
         for(var5 = new View[var4]; var2 < var4; ++var2) {
            var5[var2] = var3.findViewById(this.toGroup[var2]);
         }

         var3.setTag(var5);
         return var3;
      }

      public Object getChild(int var1, int var2) {
         ArrayList var3 = (ArrayList)((HashMap)this.mDataSource.get(var1)).get("product_list");
         Object var4;
         if(var3 != null) {
            var4 = var3.get(var2);
         } else {
            var4 = null;
         }

         return var4;
      }

      public long getChildId(int var1, int var2) {
         return (long)var2;
      }

      public View getChildView(int var1, int var2, boolean var3, View var4, ViewGroup var5) {
         View var6;
         if(var4 == null) {
            var6 = this.newChildView(var5);
         } else {
            var6 = var4;
         }

         this.bindChildView(var1, var2, var6);
         return var6;
      }

      public int getChildrenCount(int var1) {
         ArrayList var2 = (ArrayList)((HashMap)this.mDataSource.get(var1)).get("product_list");
         int var3;
         if(var2 != null) {
            var3 = var2.size();
         } else {
            var3 = 0;
         }

         return var3;
      }

      public Object getGroup(int var1) {
         return this.mDataSource.get(var1);
      }

      public int getGroupCount() {
         return this.mDataSource.size();
      }

      public long getGroupId(int var1) {
         return (long)var1;
      }

      public View getGroupView(int var1, boolean var2, View var3, ViewGroup var4) {
         View var5;
         if(var3 == null) {
            var5 = this.newGroupView(var4);
         } else {
            var5 = var3;
         }

         this.bindGroupView(var1, var2, var5);
         return var5;
      }

      public boolean hasStableIds() {
         return true;
      }

      public boolean isChildSelectable(int var1, int var2) {
         return true;
      }
   }
}
