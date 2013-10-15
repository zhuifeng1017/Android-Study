package com.xxx.appstore.common.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.ui.ProductDetailActivity;

public class DialogUtil {

   private static int[] mItemIds;
   private static int mWhich;


   public static Dialog createBigInputDialog(final Context var0, final int var1, int var2, final DialogUtil.InputDialogListener var3) {
      View var4 = LayoutInflater.from(var0).inflate(2130903081, (ViewGroup)null);
      final EditText var5 = (EditText)var4.findViewById(2131492939);
      return (new Builder(var0)).setTitle(var2).setView(var4).setPositiveButton(2131296376, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            String var3x = var5.getText().toString();
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onInputDialogOK(var1, var3x);
            }

         }
      }).setNegativeButton(2131296375, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onInputDialogCancel(var1);
            }

         }
      }).create();
   }

   public static Dialog createComfirmDownloadDialog(final Context var0, boolean var1, final DialogUtil.WarningDialogListener var2) {
      AlertDialog var3;
      if(var1) {
         var3 = (new Builder(var0)).setTitle(2131296377).setMessage(var0.getString(2131296388)).setPositiveButton(2131296378, new OnClickListener() {
            public void onClick(DialogInterface var1, int var2x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(100);
               }

               if(var2 != null) {
                  var2.onWarningDialogOK(100);
               }

            }
         }).setNegativeButton(2131296379, new OnClickListener() {
            public void onClick(DialogInterface var1, int var2) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(100);
               }

            }
         }).create();
      } else {
         var3 = (new Builder(var0)).setTitle(2131296377).setMessage(var0.getString(2131296389)).setPositiveButton(2131296378, new OnClickListener() {
            public void onClick(DialogInterface var1, int var2x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(100);
               }

               if(var2 != null) {
                  var2.onWarningDialogOK(100);
               }

            }
         }).setNegativeButton(2131296379, new OnClickListener() {
            public void onClick(DialogInterface var1, int var2) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(100);
               }

            }
         }).create();
      }

      return var3;
   }

   public static ProgressDialog createDeterminateProgressDialog(final Context var0, final int var1, String var2, boolean var3, final DialogUtil.ProgressDialogListener var4) {
      ProgressDialog var5 = new ProgressDialog(var0);
      var5.setIcon(17301659);
      var5.setTitle(var2);
      var5.setProgressStyle(1);
      if(var3) {
         var5.setButton(var0.getString(2131296375), new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var4 != null) {
                  var4.onProgressDialogCancel(var1);
               }

            }
         });
         var5.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface var1x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var4 != null) {
                  var4.onProgressDialogCancel(var1);
               }

            }
         });
      }

      if(!var3) {
         var5.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface var1x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

            }
         });
      }

      return var5;
   }

   public static Dialog createIndeterminateProgressDialog(final Context var0, final int var1, String var2, boolean var3, final DialogUtil.ProgressDialogListener var4) {
      View var5 = LayoutInflater.from(var0).inflate(2130903082, (ViewGroup)null);
      if(var2 == null) {
         throw new RuntimeException("Must provide a hint string for input dialog");
      } else {
         ((TextView)var5.findViewById(2131492937)).setText(var2);
         Builder var6 = (new Builder(var0)).setCancelable(var3).setView(var5);
         if(var3) {
            var6.setNegativeButton(2131296375, new OnClickListener() {
               public void onClick(DialogInterface var1x, int var2) {
                  if(var0 instanceof Activity) {
                     ((Activity)var0).removeDialog(var1);
                  }

                  if(var4 != null) {
                     var4.onProgressDialogCancel(var1);
                  }

               }
            });
            var6.setOnCancelListener(new OnCancelListener() {
               public void onCancel(DialogInterface var1x) {
                  if(var0 instanceof Activity) {
                     ((Activity)var0).removeDialog(var1);
                  }

                  if(var4 != null) {
                     var4.onProgressDialogCancel(var1);
                  }

               }
            });
         }

         AlertDialog var7 = var6.create();
         if(!var3) {
            var7.setOnDismissListener(new OnDismissListener() {
               public void onDismiss(DialogInterface var1x) {
                  if(var0 instanceof Activity) {
                     ((Activity)var0).removeDialog(var1);
                  }

               }
            });
         }

         return var7;
      }
   }

   public static Dialog createIndeterminateProgressWhiteTextDialog(Context var0, int var1, String var2, boolean var3) {
      View var4 = LayoutInflater.from(var0).inflate(2130903082, (ViewGroup)null);
      if(var2 == null) {
         throw new RuntimeException("Must provide a hint string for input dialog");
      } else {
         ((TextView)var4.findViewById(2131492937)).setText(var2);
         return (new Builder(var0)).setCancelable(var3).setView(var4).create();
      }
   }

   public static Dialog createIndeterminateProgressWhiteTextDialog(final Context var0, final int var1, String var2, boolean var3, final DialogUtil.ProgressDialogListener var4) {
      View var5 = LayoutInflater.from(var0).inflate(2130903082, (ViewGroup)null);
      if(var2 == null) {
         throw new RuntimeException("Must provide a hint string for input dialog");
      } else {
         ((TextView)var5.findViewById(2131492937)).setText(var2);
         Builder var6 = (new Builder(var0)).setCancelable(var3).setView(var5);
         if(var3) {
            var6.setNegativeButton(2131296375, new OnClickListener() {
               public void onClick(DialogInterface var1x, int var2) {
                  if(var0 instanceof Activity) {
                     ((Activity)var0).removeDialog(var1);
                  }

                  if(var4 != null) {
                     var4.onProgressDialogCancel(var1);
                  }

               }
            });
            var6.setOnCancelListener(new OnCancelListener() {
               public void onCancel(DialogInterface var1x) {
                  if(var0 instanceof Activity) {
                     ((Activity)var0).removeDialog(var1);
                  }

                  if(var4 != null) {
                     var4.onProgressDialogCancel(var1);
                  }

               }
            });
         }

         AlertDialog var7 = var6.create();
         if(!var3) {
            var7.setOnDismissListener(new OnDismissListener() {
               public void onDismiss(DialogInterface var1x) {
                  if(var0 instanceof Activity) {
                     ((Activity)var0).removeDialog(var1);
                  }

               }
            });
         }

         return var7;
      }
   }

   public static Dialog createInfoDialog(final Context var0, final int var1, String var2, final DialogUtil.InfoDialogListener var3) {
      return (new Builder(var0)).setIcon(17301659).setTitle(var0.getString(2131296380)).setMessage(var2).setPositiveButton(2131296376, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onInfoDialogOK(var1);
            }

         }
      }).setOnCancelListener(new OnCancelListener() {
         public void onCancel(DialogInterface var1x) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onInfoDialogOK(var1);
            }

         }
      }).create();
   }

   public static Dialog createInputDialog(final Context var0, final int var1, String var2, String var3, String var4, String var5, boolean var6, final DialogUtil.InputDialogListener var7) {
      View var8 = LayoutInflater.from(var0).inflate(2130903083, (ViewGroup)null);
      if(var2 == null) {
         throw new RuntimeException("Must provide a hint string for input dialog");
      } else {
         TextView var9 = (TextView)var8.findViewById(2131492937);
         var9.setText(var2);
         var9.setVisibility(8);
         final EditText var10 = (EditText)var8.findViewById(2131492939);
         if(var3 != null) {
            var10.setText(var3);
         }

         if(var5 != null) {
            var10.setHint(var5);
         }

         if(var6) {
            var10.setTransformationMethod(PasswordTransformationMethod.getInstance());
         }

         return (new Builder(var0)).setIcon(17301543).setTitle(var4).setView(var8).setPositiveButton(2131296376, new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2) {
               String var3 = var10.getText().toString();
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var7 != null) {
                  var7.onInputDialogOK(var1, var3);
               }

            }
         }).setNegativeButton(2131296375, new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var7 != null) {
                  var7.onInputDialogCancel(var1);
               }

            }
         }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface var1x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var7 != null) {
                  var7.onInputDialogCancel(var1);
               }

            }
         }).create();
      }
   }

   public static Dialog createInputDialog(final Context var0, final int var1, String var2, String var3, boolean var4, final DialogUtil.InputDialogListener var5) {
      View var6 = LayoutInflater.from(var0).inflate(2130903083, (ViewGroup)null);
      if(var2 == null) {
         throw new RuntimeException("Must provide a hint string for input dialog");
      } else {
         ((TextView)var6.findViewById(2131492937)).setText(var2);
         final EditText var7 = (EditText)var6.findViewById(2131492939);
         if(var3 != null) {
            var7.setText(var3);
         }

         if(var4) {
            var7.setTransformationMethod(PasswordTransformationMethod.getInstance());
         }

         return (new Builder(var0)).setIcon(17301543).setTitle(2131296384).setView(var6).setPositiveButton(2131296376, new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2) {
               String var3 = var7.getText().toString();
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var5 != null) {
                  var5.onInputDialogOK(var1, var3);
               }

            }
         }).setNegativeButton(2131296375, new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var5 != null) {
                  var5.onInputDialogCancel(var1);
               }

            }
         }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface var1x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var5 != null) {
                  var5.onInputDialogCancel(var1);
               }

            }
         }).create();
      }
   }

   public static Dialog createListCheckboxDialog(Context var0, int var1, CharSequence[] var2, int var3, DialogUtil.ListCheckboxDialogListener var4) {
      return createListCheckboxDialog(var0, var1, var2, (int[])null, var3, var4);
   }

   public static Dialog createListCheckboxDialog(final Context var0, final int var1, final CharSequence[] var2, int[] var3, int var4, final DialogUtil.ListCheckboxDialogListener var5) {
      mWhich = var4;
      if(var3 != null && var3.length >= var2.length) {
         mItemIds = var3;
      } else {
         mItemIds = null;
      }

      Builder var6 = (new Builder(var0)).setOnCancelListener(new OnCancelListener() {
         public void onCancel(DialogInterface var1x) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var5 != null) {
               var5.onListDialogCancel(var1, var2);
            }

         }
      });
      if(var4 == -1) {
         var6.setAdapter(new ArrayAdapter(var0, 2130903111, var2), new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var5 != null) {
                  DialogUtil.ListCheckboxDialogListener var3 = var5;
                  int var4 = var1;
                  CharSequence[] var5x = var2;
                  int var6;
                  if(DialogUtil.mItemIds == null) {
                     var6 = -1;
                  } else {
                     var6 = DialogUtil.mItemIds[var2x];
                  }

                  var3.onListDialogOK(var4, var5x, var6, var2x);
               }

            }
         });
      } else {
         var6.setSingleChoiceItems(new ArrayAdapter(var0, 2130903110, 2131492999, var2), var4, new OnClickListener() {
            public void onClick(DialogInterface var1, int var2) {
               DialogUtil.mWhich = var2;
            }
         }).setPositiveButton(2131296376, new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var5 != null) {
                  DialogUtil.ListCheckboxDialogListener var3 = var5;
                  int var4 = var1;
                  CharSequence[] var5x = var2;
                  int var6;
                  if(DialogUtil.mItemIds == null) {
                     var6 = -1;
                  } else {
                     var6 = DialogUtil.mItemIds[var2x];
                  }

                  var3.onListDialogOK(var4, var5x, var6, DialogUtil.mWhich);
               }

            }
         }).setNegativeButton(2131296375, new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2x) {
               if(var0 instanceof Activity) {
                  ((Activity)var0).removeDialog(var1);
               }

               if(var5 != null) {
                  var5.onListDialogCancel(var1, var2);
               }

            }
         });
      }

      return var6.create();
   }

   public static Dialog createListDialog(final Context var0, final int var1, int var2, final DialogUtil.ListDIalogListener var3) {
      return (new Builder(var0)).setItems(var2, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onListDialogOK(var1, var2);
            }

         }
      }).create();
   }

   public static Dialog createOKWarningDialog(final Context var0, final int var1, String var2, final DialogUtil.WarningDialogListener var3) {
      return (new Builder(var0)).setCancelable(false).setTitle(2131296377).setMessage(var2).setPositiveButton(2131296376, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onWarningDialogOK(var1);
            }

         }
      }).setOnCancelListener(new OnCancelListener() {
         public void onCancel(DialogInterface var1x) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onWarningDialogOK(var1);
            }

         }
      }).create();
   }

   public static Dialog createShowHintOKDialog(final Context var0, final int var1, String var2, String var3) {
      return (new Builder(var0)).setTitle(var2).setMessage(var3).setPositiveButton(2131296376, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

         }
      }).create();
   }

   public static Dialog createYesNo2TVDialog(final Context var0, final int var1, String var2, String var3, final DialogUtil.WarningDialogListener var4) {
      new LinearLayout(var0);
      View var6 = LayoutInflater.from(var0).inflate(2130903080, (ViewGroup)null, false);
      TextView var7 = (TextView)var6.findViewById(2131492937);
      TextView var8 = (TextView)var6.findViewById(2131492938);
      var7.setTextColor(-1);
      var7.setText(var2);
      var8.setTextColor(-65536);
      var8.setText(var3);
      return (new Builder(var0)).setView(var6).setTitle(2131296377).setPositiveButton(2131296378, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var4 != null) {
               var4.onWarningDialogOK(var1);
            }

         }
      }).setNegativeButton(2131296379, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
               if(var4 != null) {
                  var4.onWarningDialogCancel(var1);
               }
            }

         }
      }).create();
   }

   public static Dialog createYesNoDialog(final Context var0, final int var1, String var2, final DialogUtil.YesNoDialogListener var3) {
      return (new Builder(var0)).setIcon(2130837546).setTitle(var2).setPositiveButton(2131296378, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onYesDialog(var1);
            }

         }
      }).setNegativeButton(2131296379, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onNoDialog(var1);
            }

         }
      }).create();
   }

   public static Dialog createYesNoWarningDialog(final Context var0, final int var1, String var2, final DialogUtil.WarningDialogListener var3) {
      return (new Builder(var0)).setTitle(2131296377).setMessage(var2).setPositiveButton(2131296378, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

            if(var3 != null) {
               var3.onWarningDialogOK(var1);
            }

         }
      }).setNegativeButton(2131296379, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
               if(var3 != null) {
                  var3.onWarningDialogCancel(var1);
               }
            }

         }
      }).setOnCancelListener(new OnCancelListener() {
         public void onCancel(DialogInterface var1x) {
            if(var0 instanceof Activity) {
               ((Activity)var0).removeDialog(var1);
            }

         }
      }).create();
   }

   public static Dialog newEnsurePurchaseDialog(final ProductDetailActivity var0, final int var1, String var2) {
      View var3 = LayoutInflater.from(var0).inflate(2130903083, (ViewGroup)null);
      if(var2 == null) {
         throw new RuntimeException("Must provide a hint string for input dialog");
      } else {
         ((TextView)var3.findViewById(2131492937)).setText(var2);
         final EditText var4 = (EditText)var3.findViewById(2131492939);
         var4.setTransformationMethod(PasswordTransformationMethod.getInstance());
         Builder var5 = new Builder(var0);
         var5.setIcon(17301543).setTitle(2131296384).setView(var3).setPositiveButton(2131296376, new OnClickListener() {
            public void onClick(DialogInterface var1x, int var2) {
               String var3 = var4.getText().toString();
               if(TextUtils.isEmpty(var3)) {
                  Utils.makeEventToast(var0, var0.getString(2131296286), false);
               } else {
                  var0.removeDialog(var1);
                  var0.purchaseProduct(var3);
               }

            }
         }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface var1x) {
               var0.removeDialog(var1);
            }
         });
         return var5.create();
      }
   }

   public static Dialog newInsufficientBalanceDialog(final ProductDetailActivity var0, final int var1, String var2) {
      return (new Builder(var0)).setTitle(2131296377).setMessage(var2).setPositiveButton(2131296378, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            var0.removeDialog(var1);
            var0.gotoDepositPage();
         }
      }).setNegativeButton(2131296379, new OnClickListener() {
         public void onClick(DialogInterface var1x, int var2) {
            var0.removeDialog(var1);
         }
      }).setOnCancelListener(new OnCancelListener() {
         public void onCancel(DialogInterface var1x) {
            var0.removeDialog(var1);
         }
      }).create();
   }

   public interface WarningDialogListener {

      void onWarningDialogCancel(int var1);

      void onWarningDialogOK(int var1);
   }

   public interface ListDIalogListener {

      void onListDialogOK(int var1, int var2);
   }

   public interface InfoDialogListener {

      void onInfoDialogOK(int var1);
   }

   public interface RatingDialogListener {

      void onRatingDialogCancel();

      void onRatingDialogOK(int var1, float var2);
   }

   public interface ListCheckboxDialogListener {

      void onListDialogCancel(int var1, CharSequence[] var2);

      void onListDialogOK(int var1, CharSequence[] var2, int var3, int var4);
   }

   public interface ListDialogListener2 {

      void onListDialogCancel2(int var1, Object[] var2);

      void onListDialogOK2(int var1, Object[] var2, int var3);
   }

   public interface YesNoDialogListener {

      void onNoDialog(int var1);

      void onYesDialog(int var1);
   }

   public interface ProgressDialogListener {

      void onProgressDialogCancel(int var1);
   }

   public interface CheckBoxWarningDialogListener {

      void onWarningDialogCancel(int var1);

      void onWarningDialogOK(int var1, boolean var2);
   }

   public interface UserPwdDialogListener {

      void onUserPwdDialogCancel(int var1);

      void onUserPwdDialogOK(int var1, String var2, String var3, boolean var4);

      void onUserPwdDialogRegister(int var1);
   }

   public interface EditTextDialogListener {

      void onEditTextDialogCancel(int var1);

      void onEditTextDialogOK(int var1, String var2);
   }

   public interface InputDialogListener {

      void onInputDialogCancel(int var1);

      void onInputDialogOK(int var1, String var2);
   }

   public interface RegisterDialogListener {

      void onRegisterDialogCancel(int var1);

      void onRegisterDialogOK(int var1, String var2, String var3, String var4);
   }
}
