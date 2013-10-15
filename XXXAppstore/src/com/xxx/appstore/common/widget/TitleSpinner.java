package com.xxx.appstore.common.widget;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import com.xxx.appstore.R;

public class TitleSpinner extends Button implements DialogInterface.OnClickListener {

   private SpinnerAdapter adapter;
   private int mNextSelectedPosition = -1;
   private DialogInterface.OnClickListener mOnClickListener;
   private CharSequence mPrompt;


   public TitleSpinner(Context var1) {
      super(var1);
      this.setGravity(19);
      this.setBackgroundResource(17301510);
   }

   public TitleSpinner(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.setGravity(19);
      this.setBackgroundResource(17301510);
      TypedArray var3 = var1.obtainStyledAttributes(var2, R.styleable.TitleSpinner);
      if(var3.hasValue(0)) {
         this.mPrompt = var3.getString(0);
      }

      var3.recycle();
   }

   public TitleSpinner(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.setGravity(19);
      this.setBackgroundResource(17301510);
      TypedArray var4 = var1.obtainStyledAttributes(var2, R.styleable.TitleSpinner);
      if(var4.hasValue(0)) {
         this.mPrompt = var4.getString(0);
      }

      var4.recycle();
   }

   public int getSelectedItemPosition() {
      return this.mNextSelectedPosition;
   }

   public void onClick(DialogInterface var1, int var2) {
      this.setSelection(var2);
      var1.dismiss();
      if(this.mOnClickListener != null) {
         this.mOnClickListener.onClick(var1, var2);
      }
   }

   public boolean performClick() {
      boolean var1 = super.performClick();
      if(!var1) {
         var1 = true;
         Builder var2 = new Builder(this.getContext());
         if(this.mPrompt != null) {
            var2.setTitle(this.mPrompt);
         }

         var2.setSingleChoiceItems(new TitleSpinner.DropDownAdapter(this.adapter), this.getSelectedItemPosition(), this).show();
      }

      return var1;
   }

   public void setAdapter(SpinnerAdapter var1) {
      this.adapter = var1;
   }

   public void setOnClickListener(DialogInterface.OnClickListener paramOnClickListener)
   {
     this.mOnClickListener = paramOnClickListener;
   }


   public void setPrompt(CharSequence var1) {
      this.mPrompt = var1;
   }

   public void setSelection(int var1) {
      this.mNextSelectedPosition = var1;
      this.setText(this.adapter.getItem(var1).toString());
   }

   private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {

      private SpinnerAdapter mAdapter;
      private ListAdapter mListAdapter;


      public DropDownAdapter(SpinnerAdapter var1) {
         this.mAdapter = var1;
         if(var1 instanceof ListAdapter) {
            this.mListAdapter = (ListAdapter)var1;
         }

      }

      public boolean areAllItemsEnabled() {
         ListAdapter var1 = this.mListAdapter;
         boolean var2;
         if(var1 != null) {
            var2 = var1.areAllItemsEnabled();
         } else {
            var2 = true;
         }

         return var2;
      }

      public int getCount() {
         int var1;
         if(this.mAdapter == null) {
            var1 = 0;
         } else {
            var1 = this.mAdapter.getCount();
         }

         return var1;
      }

      public View getDropDownView(int var1, View var2, ViewGroup var3) {
         View var4;
         if(this.mAdapter == null) {
            var4 = null;
         } else {
            var4 = this.mAdapter.getDropDownView(var1, var2, var3);
         }

         return var4;
      }

      public Object getItem(int var1) {
         Object var2;
         if(this.mAdapter == null) {
            var2 = null;
         } else {
            var2 = this.mAdapter.getItem(var1);
         }

         return var2;
      }

      public long getItemId(int var1) {
         long var2;
         if(this.mAdapter == null) {
            var2 = -1L;
         } else {
            var2 = this.mAdapter.getItemId(var1);
         }

         return var2;
      }

      public int getItemViewType(int var1) {
         return 0;
      }

      public View getView(int var1, View var2, ViewGroup var3) {
         return this.getDropDownView(var1, var2, var3);
      }

      public int getViewTypeCount() {
         return 1;
      }

      public boolean hasStableIds() {
         boolean var1;
         if(this.mAdapter != null && this.mAdapter.hasStableIds()) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public boolean isEmpty() {
         boolean var1;
         if(this.getCount() == 0) {
            var1 = true;
         } else {
            var1 = false;
         }

         return var1;
      }

      public boolean isEnabled(int var1) {
         ListAdapter var2 = this.mListAdapter;
         boolean var3;
         if(var2 != null) {
            var3 = var2.isEnabled(var1);
         } else {
            var3 = true;
         }

         return var3;
      }

      public void registerDataSetObserver(DataSetObserver var1) {
         if(this.mAdapter != null) {
            this.mAdapter.registerDataSetObserver(var1);
         }

      }

      public void unregisterDataSetObserver(DataSetObserver var1) {
         if(this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(var1);
         }

      }
   }
}
