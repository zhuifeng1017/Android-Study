package com.mappn.gfan.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.ui.PersonalAccountActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

public class PersonalAccountAdapter extends BaseAdapter implements OnCheckedChangeListener {

   // $FF: synthetic field
   static final boolean $assertionsDisabled;
   private Context mContext;
   private ArrayList<HashMap<String, Object>> mDataSource;
   private String[] mFrom;
   private Handler mHandler;
   private final WeakHashMap<View, View[]> mHolders = new WeakHashMap();
   private LayoutInflater mInflater;
   private int mResource;
   private Session mSession;
   private int[] mTo;


   static {
      boolean var0;
      if(!PersonalAccountAdapter.class.desiredAssertionStatus()) {
         var0 = true;
      } else {
         var0 = false;
      }

      $assertionsDisabled = var0;
   }

   PersonalAccountAdapter(Context var1, ArrayList<HashMap<String, Object>> var2, int var3, String[] var4, int[] var5, Handler var6) {
      if(var2 == null) {
         this.mDataSource = new ArrayList();
      } else {
         this.mDataSource = var2;
      }

      this.mContext = var1;
      this.mResource = var3;
      this.mFrom = var4;
      this.mTo = var5;
      this.mHandler = var6;
      this.mSession = Session.get(var1);
      this.mInflater = (LayoutInflater)var1.getSystemService("layout_inflater");
   }

   private void bindView(int var1, View var2) {
      HashMap var3 = (HashMap)this.mDataSource.get(var1);
      if(var3 != null) {
         View[] var4 = (View[])this.mHolders.get(var2);
         String[] var5 = this.mFrom;
         int var6 = this.mTo.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            View var8 = var4[var7];
            if(var8 != null) {
               Object var9 = var3.get(var5[var7]);
               if(var8 instanceof CheckBox) {
                  if(var9 != null && var9 instanceof Integer) {
                     if(this.mSession.isLogin()) {
                        if(this.mSession.isDeviceBinded()) {
                           ((CheckBox)((CheckBox)var2.findViewById(2131492916))).setButtonDrawable(2130837567);
                        } else {
                           ((CheckBox)var8).setButtonDrawable(((Integer)var9).intValue());
                        }
                     } else {
                        ((CheckBox)((CheckBox)var2.findViewById(2131492916))).setButtonDrawable(2130837566);
                     }

                     ((CheckBox)var8).setOnCheckedChangeListener(this);
                  }
               } else if(var8 instanceof TextView) {
                  this.setTextViewValue((TextView)var8, var9, var1);
               } else if(var8 instanceof ImageView) {
                  ImageView var10 = (ImageView)var8;
                  if(var9 instanceof Integer) {
                     int var12;
                     if(var9 == null) {
                        var12 = 2130837575;
                     } else {
                        var12 = ((Integer)var9).intValue();
                     }

                     var10.setImageResource(var12);
                  } else if(var9 instanceof Drawable) {
                     Drawable var11;
                     if(var9 == null) {
                        var11 = this.mContext.getResources().getDrawable(2130837579);
                     } else {
                        var11 = (Drawable)var9;
                     }

                     var10.setImageDrawable(var11);
                  } else if(var9 instanceof String) {
                     ImageUtils.download(this.mContext, (String)var9, var10);
                  } else if(var9 instanceof Boolean) {
                     if(((Boolean)var9).booleanValue() && var1 == 0) {
                        var10.setVisibility(0);
                     } else {
                        var10.setVisibility(8);
                     }
                  }
               }
            }
         }
      }

   }

   private boolean isGroupOrHeader(int var1, String var2) {
      boolean var4;
      if(this.mDataSource == null) {
         var4 = false;
      } else {
         HashMap var3 = (HashMap)this.mDataSource.get(var1);
         if(var3 == null) {
            var4 = false;
         } else {
            Object var5 = var3.get(var2);
            if(var5 == null) {
               var4 = false;
            } else if(var5 instanceof String) {
               var4 = Boolean.valueOf((String)var5).booleanValue();
            } else if(var5 instanceof Boolean) {
               var4 = ((Boolean)var5).booleanValue();
            } else {
               var4 = false;
            }
         }
      }

      return var4;
   }

   private View newView(int var1, ViewGroup var2, int var3) {
      View var4;
      switch(var1) {
      case 1:
      case 3:
         var4 = this.mInflater.inflate(2130903066, var2, false);
         break;
      case 2:
         var4 = this.mInflater.inflate(2130903065, var2, false);
         break;
      case 4:
      case 5:
      case 6:
      case 7:
      default:
         var4 = this.mInflater.inflate(this.mResource, var2, false);
         break;
      case 8:
         var4 = this.mInflater.inflate(2130903059, var2, false);
         break;
      case 9:
         var4 = this.mInflater.inflate(this.mResource, var2, false);
         break;
      case 10:
         var4 = this.mInflater.inflate(2130903067, var2, false);
      }

      int[] var5 = this.mTo;
      int var6 = var5.length;
      View[] var7 = new View[var6];

      for(int var8 = 0; var8 < var6; ++var8) {
         var7[var8] = var4.findViewById(var5[var8]);
      }

      if(this.mSession.isLogin()) {
         if(var3 == 1) {
            var4.findViewById(2131492916).setVisibility(0);
            var4.findViewById(2131492917).setVisibility(8);
         } else if(var3 == 0 || var3 == 2) {
            var4.findViewById(2131492917).setVisibility(0);
            var4.findViewById(2131492916).setVisibility(8);
         }
      } else {
         if(var3 == 0) {
            var4.findViewById(2131492917).setVisibility(0);
         }

         if(var3 == 1) {
            var4.findViewById(2131492916).setVisibility(0);
            var4.findViewById(2131492917).setVisibility(8);
         }

         if(var3 == 2) {
            var4.findViewById(2131492917).setVisibility(0);
            var4.findViewById(2131492916).setVisibility(8);
         }
      }

      this.mHolders.put(var4, var7);
      var4.setTag(Integer.valueOf(var1));
      return var4;
   }

   private void setTextViewValue(TextView var1, Object var2, int var3) {
      if(this.mSession.isLogin() && var3 == 0) {
         if(var1.getId() == 2131492868) {
            var1.setText(this.mSession.getUserName());
         }

         if(var1.getId() == 2131492905) {
            var1.setText(this.mContext.getString(2131296330));
         }
      } else {
         var1.setText((CharSequence)var2);
      }

   }

   public void addData(HashMap<String, Object> var1) {
      if(var1 != null) {
         this.mDataSource.add(this.getCount(), var1);
         this.notifyDataSetChanged();
      }

   }

   public void addData(List<HashMap<String, Object>> var1) {
      if(var1 != null && var1.size() > 0) {
         this.mDataSource.addAll(this.getCount(), var1);
         this.notifyDataSetChanged();
      }

   }

   public void changeDataSource(List<HashMap<String, Object>> var1) {
      if(var1 != null && var1.size() > 0) {
         this.mDataSource.clear();
         this.mDataSource.addAll(var1);
         this.notifyDataSetChanged();
      }

   }

   public void clearData() {
      this.mDataSource.clear();
      this.notifyDataSetChanged();
   }

   public int getCount() {
      int var1;
      if(this.mDataSource == null) {
         var1 = 0;
      } else {
         var1 = this.mDataSource.size();
      }

      return var1;
   }

   public ArrayList<HashMap<String, Object>> getDataSource() {
      return this.mDataSource;
   }

   public Object getItem(int var1) {
      Object var2;
      if(this.mDataSource != null && var1 < this.getCount()) {
         var2 = this.mDataSource.get(var1);
      } else {
         var2 = null;
      }

      return var2;
   }

   public long getItemId(int var1) {
      return (long)var1;
   }

   public int getItemViewType(int var1) {
      byte var2;
      if(!this.isGroupOrHeader(var1, "place_holder")) {
         var2 = 0;
      } else if(this.isGroupOrHeader(var1, "header")) {
         var2 = 2;
      } else {
         var2 = 1;
      }

      return var2;
   }

   public View getView(int var1, View var2, ViewGroup var3) {
      if(!$assertionsDisabled && var1 >= this.getCount()) {
         throw new AssertionError();
      } else {
         int var4 = ((Integer)((HashMap)this.mDataSource.get(var1)).get("account_type")).intValue();
         View var5;
         if(var2 == null) {
            var5 = this.newView(var4, var3, var1);
         } else if(((Integer)var2.getTag()).intValue() != var4) {
            var5 = this.newView(var4, var3, var1);
         } else {
            var5 = var2;
         }

         this.bindView(var1, var5);
         return var5;
      }
   }

   public boolean isEnabled(int var1) {
      boolean var2;
      switch(((Integer)((HashMap)this.mDataSource.get(var1)).get("account_type")).intValue()) {
      case 8:
         var2 = false;
         break;
      case 9:
         if(this.mSession.isLogin()) {
            var2 = true;
         } else if(!this.mSession.isLogin() && var1 == 0) {
            var2 = true;
         } else {
            var2 = false;
         }
         break;
      default:
         var2 = true;
      }

      return var2;
   }

   public void onCheckedChanged(CompoundButton var1, boolean var2) {
      PersonalAccountActivity var3 = (PersonalAccountActivity)this.mContext;
      if(this.mSession.isLogin()) {
         if(!this.mSession.isDeviceBinded()) {
            if(!var3.getCurrentBindStatue()) {
               this.mHandler.sendEmptyMessage(2);
            }
         } else {
            this.mHandler.sendEmptyMessage(3);
         }
      }

   }
}
