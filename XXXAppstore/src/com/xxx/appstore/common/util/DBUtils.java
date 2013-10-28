package com.xxx.appstore.common.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.xxx.appstore.Session;
import com.xxx.appstore.common.util.MarketProvider;
import com.xxx.appstore.common.vo.BuyLog;
import com.xxx.appstore.common.vo.CardsVerification;
import com.xxx.appstore.common.vo.CardsVerifications;
import com.xxx.appstore.common.vo.LogEntity;
import com.xxx.appstore.common.vo.UpgradeInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DBUtils {

   public static void addSearchItem(Context var0, String var1) {
      ContentValues var2 = new ContentValues();
      var2.put("keyword", var1);
      (new AsyncQueryHandler(var0.getContentResolver()) {
      }).startInsert(0, (Object)null, MarketProvider.SEARCH_CONTENT_URI, var2);
   }

   public static int addUpdateProduct(Context var0, ArrayList<UpgradeInfo> var1) {
      Cursor var2 = var0.getContentResolver().query(MarketProvider.UPDATE_CONTENT_URI, (String[])null, (String)null, (String[])null, (String)null);
      HashMap var8;
      if(var2 != null) {
         HashMap var3 = new HashMap();
         var2.moveToFirst();

         while(!var2.isAfterLast()) {
            UpgradeInfo var5 = new UpgradeInfo();
            var5.pkgName = var2.getString(var2.getColumnIndex("p_package_name"));
            var5.versionCode = var2.getInt(var2.getColumnIndex("p_new_version_code"));
            var5.update = var2.getInt(var2.getColumnIndex("p_update_ingore"));
            var3.put(var5.pkgName, var5);
            var2.moveToNext();
         }

         var2.close();
         var8 = var3;
      } else {
         var8 = null;
      }

      int var9;
      if(var8 != null) {
         Iterator var15 = var1.iterator();

         int var16;
         int var18;
         for(var16 = 0; var15.hasNext(); var16 = var18) {
            UpgradeInfo var17 = (UpgradeInfo)var15.next();
            if(var8.containsKey(var17.pkgName)) {
               UpgradeInfo var19 = (UpgradeInfo)var8.get(var17.pkgName);
               if(var17.versionCode <= var19.versionCode && var19.update == 1) {
                  var17.update = 1;
                  var18 = var16;
               } else {
                  var18 = var16 + 1;
               }
            } else {
               var18 = var16 + 1;
            }
         }

         var9 = var16;
      } else {
         var9 = 0;
      }

      ContentValues[] var10 = new ContentValues[var1.size()];

      for(int var11 = 0; var11 < var10.length; ++var11) {
         var10[var11] = ((UpgradeInfo)var1.get(var11)).getContentValues();
      }

      var0.getContentResolver().delete(MarketProvider.UPDATE_CONTENT_URI, (String)null, (String[])null);
      var0.getContentResolver().bulkInsert(MarketProvider.UPDATE_CONTENT_URI, var10);
      if(var9 > 0) {
         Session var14 = Session.get(var0);
         var14.setUpdateList();
         var14.setUpgradeNumber(var9);
      }

      return var9;
   }

   public static void clearSearchHistory(Context var0) {
      (new AsyncQueryHandler(var0.getContentResolver()) {
      }).startDelete(0, (Object)null, MarketProvider.SEARCH_CONTENT_URI, (String)null, (String[])null);
   }

   public static int clearUpdateProduct(Context var0) {
      return var0.getContentResolver().delete(MarketProvider.UPDATE_CONTENT_URI, (String)null, (String[])null);
   }

   public static void delLogs(Context var0, String var1, int var2) {
      String var3 = "level >= \'" + var2 + "\' AND " + "module" + " == \'" + var1 + "\'";
      var0.getContentResolver().delete(MarketProvider.LOG_CONTENT_URI, var3, (String[])null);
   }

   public static void delPushItem(Context var0, String var1) {
      (new AsyncQueryHandler(var0.getContentResolver()) {
      }).startDelete(0, (Object)null, MarketProvider.PUSH_CONTENT_URI, "nid = \'" + var1 + "\'", (String[])null);
   }

   public static CardsVerifications getAllCardsVerification(Context var0) {
      CardsVerifications var1 = new CardsVerifications();
      Cursor var2 = var0.getContentResolver().query(MarketProvider.CARD_CONTENT_URI, (String[])null, (String)null, (String[])null, (String)null);
      if(var2 != null) {
         if(var2.getCount() > 0) {
            var2.moveToFirst();

            do {
               CardsVerification var4 = new CardsVerification();
               var4.name = var2.getString(var2.getColumnIndex("card_name"));
               var4.pay_type = var2.getString(var2.getColumnIndex("card_pay_type"));
               var4.accountNum = var2.getInt(var2.getColumnIndex("card_account_num"));
               var4.passwordNum = var2.getInt(var2.getColumnIndex("card_password_num"));
               var4.credit = var2.getString(var2.getColumnIndex("card_credit"));
               var1.cards.add(var4);
            } while(var2.moveToNext());
         }

         var2.close();
      }

      return var1;
   }

   public static List<BuyLog> getUpdateBuyedList(Context var0) {
      ArrayList var1 = new ArrayList();
      Cursor var2 = var0.getContentResolver().query(MarketProvider.BUY_CONTENT_URI, (String[])null, (String)null, (String[])null, (String)null);
      if(var2 != null && var2.getCount() > 0 && var2.moveToFirst()) {
         do {
            BuyLog var3 = new BuyLog();
            var3.pId = var2.getString(var2.getColumnIndex("p_id"));
            var3.packageName = var2.getString(var2.getColumnIndex("p_package_name"));
            var1.add(var3);
         } while(var2.moveToNext());

         var2.close();
      }

      return var1;
   }

   public static String getUpdateRsaMd5(Context var0, String var1) {
      ContentResolver var2 = var0.getContentResolver();
      String[] var3 = new String[]{"p_signature"};
      String[] var4 = new String[]{var1};
      Cursor var5 = var2.query(MarketProvider.UPDATE_CONTENT_URI, var3, "p_package_name = ? ", var4, (String)null);
      String var6;
      if(var5 != null) {
         var6 = var5.getString(0);
      } else {
         var6 = "";
      }

      return var6;
   }

   public static void ignoreUpdate(final Context var0, final String var1) {
      ContentValues var2 = new ContentValues();
      String[] var3 = new String[]{var1};
      var2.put("p_update_ingore", Integer.valueOf(1));
      (new AsyncQueryHandler(var0.getContentResolver()) {
         protected void onUpdateComplete(int var1x, Object var2, int var3) {
            Session.get(var0).removeUpdateItem(var1);
         }
      }).startUpdate(0, (Object)null, MarketProvider.UPDATE_CONTENT_URI, var2, "p_package_name = ? ", var3);
   }

   public static void insertBuyLog(Context var0, BuyLog var1) {
      ContentValues var2 = new ContentValues();
      var1.onAddToDatabase(var2);
      (new AsyncQueryHandler(var0.getContentResolver()) {
      }).startInsert(0, (Object)null, MarketProvider.BUY_CONTENT_URI, var2);
   }

   public static Uri insertLog(Context var0, LogEntity var1) {
      ContentValues var2 = new ContentValues();
      var2.put("content", var1.getLogContent());
      var2.put("module", var1.module);
      var2.put("level", Integer.valueOf(var1.level));
      var2.put("network", var1.network);
      var2.put("create_time", Long.valueOf(var1.createTime));
      return var0.getContentResolver().insert(MarketProvider.LOG_CONTENT_URI, var2);
   }

   public static void insertPushItems(Context var0, ArrayList<HashMap<String, Object>> var1) {
      ContentValues[] var2 = new ContentValues[var1.size()];
      Iterator var3 = var1.iterator();

      int var8;
      for(int var4 = 0; var3.hasNext(); var4 = var8) {
         HashMap var6 = (HashMap)var3.next();
         ContentValues var7 = new ContentValues();
         var7.put("p_id", (String)var6.get("id"));
         var7.put("title", (String)var6.get("title"));
         var7.put("description", (String)var6.get("description"));
         var7.put("update_time", (String)var6.get("update_time"));
         var7.put("nid", (String)var6.get("nid"));
         var7.put("rule", (String)var6.get("rule"));
         var7.put("checked", Integer.valueOf(0));
         var8 = var4 + 1;
         var2[var4] = var7;
      }

      Session.get(var0).setNotificationTime(var2[var2.length - 1].getAsString("update_time"));
      var0.getContentResolver().bulkInsert(MarketProvider.PUSH_CONTENT_URI, var2);
   }

   public static void isBought(Context var0, String var1, final DBUtils.DbOperationResultListener<Boolean> var2) {
      (new AsyncQueryHandler(var0.getContentResolver()) {
         protected void onQueryComplete(int var1, Object var2x, Cursor var3) {
            if(var3 != null && var3.getCount() > 0) {
               var2.onQueryResult(Boolean.valueOf(true));
            } else {
               var2.onQueryResult(Boolean.valueOf(false));
            }

            var3.close();
         }
      }).startQuery(0, (Object)null, MarketProvider.BUY_CONTENT_URI, new String[]{"p_id", "p_package_name"}, "p_id = \'" + var1 + "\'", (String[])null, (String)null);
   }

   public static void markItemChecked(Context var0, String var1) {
      ContentValues var2 = new ContentValues();
      var2.put("checked", Integer.valueOf(1));
      (new AsyncQueryHandler(var0.getContentResolver()) {
      }).startUpdate(0, (Object)null, MarketProvider.PUSH_CONTENT_URI, var2, "nid = \'" + var1 + "\'", (String[])null);
   }

   public static void queryPushItems(Context var0, final DBUtils.DbOperationResultListener<ArrayList<HashMap<String, Object>>> var1) {
      (new AsyncQueryHandler(var0.getContentResolver()) {
         protected void onQueryComplete(int var1x, Object var2, Cursor var3) {
            if(var3 != null) {
               int var4 = 0;

               ArrayList var5;
               for(var5 = new ArrayList(); var3.moveToNext(); ++var4) {
                  HashMap var6 = new HashMap();
                  var6.put("id", var3.getString(var3.getColumnIndex("p_id")));
                  var6.put("title", var3.getString(var3.getColumnIndex("title")));
                  var6.put("description", var3.getString(var3.getColumnIndex("description")));
                  var6.put("update_time", var3.getString(var3.getColumnIndex("update_time")));
                  var6.put("nid", var3.getString(var3.getColumnIndex("nid")));
                  var6.put("rule", var3.getString(var3.getColumnIndex("rule")));
                  var5.add(var6);
               }

               var3.close();
               var1.onQueryResult(var5);
            }

         }
      }).startQuery(0, (Object)null, MarketProvider.PUSH_CONTENT_URI, (String[])null, "checked = \'0\'", (String[])null, "nid ASC");
   }

   public static void querySearchHistory(Context var0, final DBUtils.DbOperationResultListener<ArrayList<String>> var1) {
      final ArrayList var2 = new ArrayList();
      (new AsyncQueryHandler(var0.getContentResolver()) {
         protected void onQueryComplete(int var1x, Object var2x, Cursor var3) {
            if(var3 != null) {
               int var4 = var3.getColumnIndex("keyword");

               for(int var5 = 0; var3.moveToNext() && var5 <= 19; ++var5) {
                  var2.add(var3.getString(var4));
               }

               var3.close();
               var1.onQueryResult(var2);
            }

         }
      }).startQuery(0, (Object)null, MarketProvider.SEARCH_CONTENT_URI, (String[])null, (String)null, (String[])null, "_id DESC");
   }

   public static ConcurrentHashMap<String, UpgradeInfo> queryUpdateProduct(Context var0) {
      ConcurrentHashMap var1 = new ConcurrentHashMap();
      String[] var2 = new String[]{"0"};
      Cursor var3 = var0.getContentResolver().query(MarketProvider.UPDATE_CONTENT_URI, (String[])null, "p_update_ingore = ? ", var2, (String)null);
      if(var3 != null) {
    	  if(var3.getCount() > 0) {
	         while(var3.moveToNext()) {
	            UpgradeInfo var4 = new UpgradeInfo();
	            var4.pid = var3.getString(var3.getColumnIndex("p_id"));
	            var4.pkgName = var3.getString(var3.getColumnIndex("p_package_name"));
	            var4.versionName = var3.getString(var3.getColumnIndex("p_new_version_name"));
	            var4.versionCode = var3.getInt(var3.getColumnIndex("p_new_version_code"));
	            var4.signature = var3.getString(var3.getColumnIndex("p_signature"));
	            var1.put(var4.pkgName, var4);
	         }
    	  }
         var3.close();
      }

      return var1;
   }

   public static void removeUpgradable(Context var0, final Session var1, final String var2) {
      String[] var3 = new String[]{var2};
      (new AsyncQueryHandler(var0.getContentResolver()) {
         protected void onDeleteComplete(int var1x, Object var2x, int var3) {
            if(var3 > 0) {
               var1.getUpdateList().remove(var2);
               var1.setUpgradeNumber(var1.getUpgradeNumber() - 1);
            }

         }
      }).startDelete(0, (Object)null, MarketProvider.UPDATE_CONTENT_URI, "p_package_name = ? ", var3);
   }

   public static ArrayList<LogEntity> submitLogs(Context var0, String var1, int var2) {
      String var3 = "level >= \'" + var2 + "\' AND " + "module" + " == \'" + var1 + "\'";
      Cursor var4 = var0.getContentResolver().query(MarketProvider.LOG_CONTENT_URI, (String[])null, var3, (String[])null, (String)null);
      ArrayList var10;
      if(var4 != null) {
         ArrayList var5 = new ArrayList();
         var4.moveToFirst();

         while(!var4.isAfterLast()) {
            LogEntity var7 = new LogEntity(var0, var1, var2);
            var7.module = var1;
            var7.level = var2;
            var7.network = var4.getString(var4.getColumnIndex("network"));
            var7.setLogContent(var4.getString(var4.getColumnIndex("content")));
            var7.createTime = (long)var4.getInt(var4.getColumnIndex("create_time"));
            var5.add(var7);
            var4.moveToNext();
         }

         var4.close();
         var10 = var5;
      } else {
         var10 = null;
      }

      return var10;
   }

   public static void updataCardsVerification(Context var0, List<CardsVerification> var1) {
      ContentResolver var2 = var0.getContentResolver();
      var2.delete(MarketProvider.CARD_CONTENT_URI, (String)null, (String[])null);
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         CardsVerification var5 = (CardsVerification)var4.next();
         ContentValues var6 = new ContentValues();
         var5.onAddToDatabase(var6);
         var2.insert(MarketProvider.CARD_CONTENT_URI, var6);
      }

   }

   public static void updateBuyedList(Context var0, List<BuyLog> var1) {
      ContentResolver var2 = var0.getContentResolver();
      var2.delete(MarketProvider.BUY_CONTENT_URI, (String)null, (String[])null);
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         BuyLog var5 = (BuyLog)var4.next();
         ContentValues var6 = new ContentValues();
         var5.onAddToDatabase(var6);
         var2.insert(MarketProvider.BUY_CONTENT_URI, var6);
      }

   }

   public static class DbOperationResultListener<T extends Object> {

      protected void onInsertResult(T var1) {}

      protected void onQueryResult(T var1) {}
   }
}
