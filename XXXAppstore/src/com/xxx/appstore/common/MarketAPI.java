package com.xxx.appstore.common;

import android.content.Context;
import android.text.TextUtils;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.codec.binary.Base64;
import com.xxx.appstore.common.codec.digest.DigestUtils;
import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.SecurityUtil;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.vo.CardInfo;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.message.BasicNameValuePair;

public class MarketAPI {

   public static final int ACTION_ADD_COMMENT = 3;
   public static final int ACTION_ADD_DISCUSS = 46;
   public static final int ACTION_ADD_RATING = 4;
   public static final int ACTION_ADD_RECOMMEND_RATING = 47;
   public static final int ACTION_BBS_SEARCH = 39;
   public static final int ACTION_BIND_ACCOUNT = 20;
   public static final int ACTION_CHARGE = 23;
   public static final int ACTION_CHECK_LOG_LEVEL = 40;
   public static final int ACTION_CHECK_NEW_SPLASH = 28;
   public static final int ACTION_CHECK_NEW_VERSION = 17;
   public static final int ACTION_CHECK_UPGRADE = 19;
   public static final int ACTION_DOWN_REPORT = 44;
   public static final int ACTION_FOLLOW_MASTER = 52;
   public static final int ACTION_GET_ALIPAY_ORDER_INFO = 31;
   public static final int ACTION_GET_ALL_CATEGORY = 37;
   public static final int ACTION_GET_BALANCE = 21;
   public static final int ACTION_GET_CATEGORY = 18;
   public static final int ACTION_GET_COMMENTS = 2;
   public static final int ACTION_GET_CONSUMESUM = 6;
   public static final int ACTION_GET_CONSUME_DETAIL = 9;
   public static final int ACTION_GET_DETAIL = 30;
   public static final int ACTION_GET_DISCUSS = 45;
   public static final int ACTION_GET_DOWNLOAD_URL = 15;
   public static final int ACTION_GET_FOLLOWED_RECOMMEND = 53;
   public static final int ACTION_GET_GROW_FAST = 36;
   public static final int ACTION_GET_HOME_MASTER_RECOMMEND = 54;
   public static final int ACTION_GET_HOME_RECOMMEND = 16;
   public static final int ACTION_GET_MASTER_CONTAINS = 50;
   public static final int ACTION_GET_MASTER_RECOMMEND = 42;
   public static final int ACTION_GET_MASTER_RECOMMEND_APPS = 43;
   public static final int ACTION_GET_MYRATING = 8;
   public static final int ACTION_GET_PAY_LOG = 22;
   public static final int ACTION_GET_PRODUCTS = 12;
   public static final int ACTION_GET_PRODUCT_DETAIL = 14;
   public static final int ACTION_GET_RANK_BY_CATEGORY = 35;
   public static final int ACTION_GET_RECOMMEND_BY_APP = 51;
   public static final int ACTION_GET_RECOMMEND_NOTIFICATION = 49;
   public static final int ACTION_GET_RECOMMEND_PRODUCTS = 13;
   public static final int ACTION_GET_RECOMMEND_RATING = 48;
   public static final int ACTION_GET_REQUIRED = 38;
   public static final int ACTION_GET_SEARCH_KEYWORDS = 33;
   public static final int ACTION_GET_TOPIC = 10;
   public static final int ACTION_GET_TOP_RECOMMEND = 34;
   public static final int ACTION_IFTTT_REPORT = 56;
   public static final int ACTION_INSERT_LOG = 41;
   public static final int ACTION_LOGIN = 0;
   public static final int ACTION_MARK_READ = 55;
   public static final int ACTION_PURCHASE_PRODUCT = 5;
   public static final int ACTION_QUERY_ALIPAY_RESULT = 32;
   public static final int ACTION_QUERY_CHARGE = 26;
   public static final int ACTION_QUERY_CHARGE_BY_ORDERID = 25;
   public static final int ACTION_REGISTER = 1;
   public static final int ACTION_SEARCH = 11;
   public static final int ACTION_SYNC_APPS = 27;
   public static final int ACTION_SYNC_BUYLOG = 7;
   public static final int ACTION_SYNC_CARDINFO = 24;
   public static final int ACTION_UNBIND = 29;
   public static final String API_BASE_URL = "http://api.gfan.com/";
   public static final String API_HOST_CLOUD = "http://passport.gfan.com/gfan_center/";
   public static final String API_HOST_JAVA = "http://api.gfan.com/market/api/";
   public static final String API_UCENTER_HOST = "http://api.gfan.com/uc1/common/";
   static final String[] API_URLS = new String[]{
	   "http://api.gfan.com/uc1/common/login", 	// 0
	   "http://api.gfan.com/uc1/common/register", 	// 1
	   "http://api.gfan.com/market/api/getComments",
	   "http://api.gfan.com/market/api/addComment",
	   "http://api.gfan.com/market/api/addRating",
	   "http://api.gfan.com/sdk/pay/purchaseProduct", 
	   "http://api.gfan.com/sdk/pay/getConsumeSum",
	   "http://api.gfan.com/market/api/syncBuyLog",
	   "http://api.gfan.com/market/api/getMyRating", 
	   "http://api.gfan.com/sdk/pay/getConsumeDetail", 
	   "http://api.gfan.com/market/api/getTopic", 
	   "http://api.gfan.com/market/api/search", 
	   "http://api.gfan.com/market/api/getProducts", 
	   "http://api.gfan.com/market/api/getRecommendProducts", 
	   "http://api.gfan.com/market/api/getProductDetail", 
	   "http://api.gfan.com/market/api/getDownloadUrl", 
	   "http://api.gfan.com/market/api/getHomeRecommend",
	   "http://api.gfan.com/market/api/checkNewVersion", 
	   "http://api.gfan.com/market/api/getCategory", 
	   "http://api.gfan.com/market/api/checkUpgrade", 
	   "http://passport.gfan.com/gfan_center/?mo=cloud_phone&do=addDev", 
	   "http://api.gfan.com/uc1/common/query_balance", 
	   "http://api.gfan.com/sdk/pay/chargeConsumeLog", 
	   "http://api.gfan.com/pay/szf/servlet/rechargeRequest", 
	   "http://api.gfan.com/pay/szf/getCardConfigServlet", 
	   "http://api.gfan.com/pay/szf/sdk/queryServlet", 
	   "http://api.gfan.com/uc1/common/query_charge_log_list",
	   "http://api.gfan.com/market/api/syncApps", 
	   "http://api.gfan.com/market/api/checkNewSplash", 
	   "http://passport.gfan.com/gfan_center/?mo=cloud_phone&do=delDev&uid=", 
	   "http://api.gfan.com/market/api/getDetail", 
	   "http://api.gfan.com/pay/szf/servlet/businessProcess.do", 
	   "http://api.gfan.com/pay/szf/servlet/businessProcess.do", 
	   "http://api.gfan.com/market/api/getKeywords",
	   "http://api.gfan.com/market/api/getTopRecommend",
	   "http://api.gfan.com/market/api/getRankByCategory", 
	   "http://api.gfan.com/market/api/getGrowFast", 
	   "http://api.gfan.com/market/api/getAllCategory", 
	   "http://api.gfan.com/market/api/getRequired", 
	   "http://search.gfan.com/search/search/luntanAttJk", 
	   "http://logcollect.gfan.com/marketExceptionReport/api/getModuleLogLevel", 
	   "http://logcollect.gfan.com/marketExceptionReport/api/insertModuleExceptionLog", 
	   "http://api.gfan.com/market/api/masterRecommend", 
	   "http://api.gfan.com/market/api/masterRecommendApps", 
	   "http://api.gfan.com/market/api/downReport", 
	   "http://api.gfan.com/market/api/masterRecommendDiscuss", 
	   "http://api.gfan.com/market/api/addMasterRecommendDiscuss", 
	   "http://api.gfan.com/market/api/addMasterRecommendRating", 
	   "http://api.gfan.com/market/api/masterRecommendRating",
	   "http://api.gfan.com/market/api/notification",
	   "http://api.gfan.com/market/api/masterContainsApp", 
	   "http://api.gfan.com/market/api/recommendByApp",
	   "http://api.gfan.com/market/api/followMasterRecommend", 
	   "http://api.gfan.com/market/api/followedMasterRecommend", 
	   "http://api.gfan.com/market/api/homeMasterRecommend", 
	   "http://api.gfan.com/market/api/masterRecommendNewApps", 
	   "http://api.gfan.com/market/api/notificationReport"};
   public static final String BBS_SEARCH_API = "http://search.gfan.com/search/search/luntanAttJk";
   private static final String LOG_HOST = "http://logcollect.gfan.com";


   public static void addComment(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3) {
      Session var4 = Session.get(var0);
      String var5 = SecurityUtil.encryptPassword(var4.getPassword(), var4.getUserName());
      String var6 = Utils.getUTF8String(Base64.encodeBase64(DigestUtils.md5(var4.getUserName() + var2 + var5)));
      HashMap var7 = new HashMap(3);
      var7.put("p_id", var2);
      var7.put("uid", var4.getUid());
      var7.put("comment", var3);
      var7.put("username", var4.getUserName());
      var7.put("password", var5);
      var7.put("verify_code", var6);
      (new ApiAsyncTask(var0, ACTION_ADD_COMMENT, var1, var7)).execute(new Void[0]);
   }

   public static void addMasterRecommendDiscuss(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3) {
      Session var4 = Session.get(var0);
      String var5 = SecurityUtil.encryptPassword(var4.getPassword(), var4.getUserName());
      String var6 = Utils.getUTF8String(Base64.encodeBase64(DigestUtils.md5(var4.getUserName() + var2 + var5)));
      HashMap var7 = new HashMap(6);
      var7.put("uid", var4.getUid());
      var7.put("content", var3);
      var7.put("promotion_id", var2);
      var7.put("username", var4.getUserName());
      var7.put("password", var5);
      var7.put("verify_code", var6);
      (new ApiAsyncTask(var0, ACTION_ADD_DISCUSS, var1, var7)).execute(new Void[0]);
   }

   public static void addMasterRecommendRating(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, String var3) {
      HashMap var4 = new HashMap(2);
      var4.put("promotion_id", var3);
      var4.put("rating", Integer.valueOf(var2));
      (new ApiAsyncTask(var0, ACTION_ADD_RECOMMEND_RATING, var1, var4)).execute(new Void[0]);
   }

   public static void addRating(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, int var3) {
      Session var4 = Session.get(var0);
      String var5 = SecurityUtil.encryptPassword(var4.getPassword(), var4.getUserName());
      String var6 = Utils.getUTF8String(Base64.encodeBase64(DigestUtils.md5(var4.getUserName() + var2 + var5)));
      HashMap var7 = new HashMap(6);
      var7.put("p_id", var2);
      var7.put("uid", var4.getUid());
      var7.put("rating", Integer.valueOf(var3));
      var7.put("username", var4.getUserName());
      var7.put("password", var5);
      var7.put("verify_code", var6);
      (new ApiAsyncTask(var0, ACTION_ADD_RATING, var1, var7)).execute(new Void[0]);
   }

   public static void bindAccount(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      Session var2 = Session.get(var0);
      HashMap var3 = new HashMap(4);
      var3.put("uid", var2.getUid());
      var3.put("devid", var2.getDeviceId());
      var3.put("imei", var2.getIMEI());
      var3.put("phonemodel", var2.getModel());
      var3.put("version", Integer.valueOf(2));
      (new ApiAsyncTask(var0, ACTION_BIND_ACCOUNT, var1, var3)).execute(new Void[0]);
   }

   public static void charge(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3, CardInfo var4) {
      Session var5 = Session.get(var0);
      HashMap var6 = new HashMap(7);
      var6.put("user_id", var5.getUid());
      var6.put("password", SecurityUtil.encryptPassword(var2, String.valueOf(var5.getUid())));
      var6.put("type", var3);
      var6.put("pay_type", var4.payType);
      var6.put("card_account", var4.cardAccount);
      var6.put("card_password", var4.cardPassword);
      var6.put("card_credit", Integer.valueOf(var4.cardCredit));
      (new ApiAsyncTask(var0, ACTION_CHARGE, var1, var6)).execute(new Void[0]);
   }

   public static void checkLogLevel(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      (new ApiAsyncTask(var0, ACTION_CHECK_LOG_LEVEL, var1, new String[]{"download", "crash_mobile"})).execute(new Void[0]);
   }

   public static void checkNewSplash(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      Session var2 = Session.get(var0);
      HashMap var3 = new HashMap(4);
      var3.put("package_name", var2.getPackageName());
      var3.put("version_code", Integer.valueOf(var2.getVersionCode()));
      var3.put("sdk_id", var2.getCpid());
      var3.put("time", Long.valueOf(var2.getSplashTime()));
      (new ApiAsyncTask(var0, ACTION_CHECK_NEW_SPLASH, var1, var3)).execute(new Void[0]);
   }

   public static void checkUpdate(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      Session var2 = Session.get(var0);
      HashMap var3 = new HashMap(4);
      var3.put("package_name", var2.getPackageName());
      var3.put("version_code", Integer.valueOf(var2.getVersionCode()));
      var3.put("sdk_id", var2.getCpid());
      var3.put("type", var2.getDebugType());
      (new ApiAsyncTask(var0, ACTION_CHECK_NEW_VERSION, var1, var3)).execute(new Void[0]);
   }

   public static void checkUpgrade(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(1);
      var2.put("upgradeList", Utils.getInstalledApps(var0));
      (new ApiAsyncTask(var0, ACTION_CHECK_UPGRADE, var1, var2)).execute(new Void[0]);
   }

   public static void getAliPayOrder(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, String var3, String var4) {
      Session var5 = Session.get(var0);
      HashMap var6 = new HashMap(4);
      var6.put("uid", Integer.valueOf(Utils.getInt(var5.getUid())));
      var6.put("money", Integer.valueOf(var2));
      var6.put("productName", var3);
      var6.put("productDesc", var4);
      (new ApiAsyncTask(var0, ACTION_GET_ALIPAY_ORDER_INFO, var1, var6)).execute(new Void[0]);
   }

   public static void getAllCategory(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(3);
      Session var3 = Session.get(var0);
      var2.put("platform", Integer.valueOf(var3.getOsVersion()));
      var2.put("screen_size", var3.getScreenSize());
      var2.put("match_type", Integer.valueOf(var3.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_ALL_CATEGORY, var1, var2)).execute(new Void[0]);
   }

   public static void getBalance(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      Session var2 = Session.get(var0);
      HashMap var3 = new HashMap(1);
      var3.put("uid", var2.getUid());
      (new ApiAsyncTask(var0, ACTION_GET_BALANCE, var1, var3)).execute(new Void[0]);
   }

   public static void getCategory(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      Session var3 = Session.get(var0);
      HashMap var4 = new HashMap(5);
      var4.put("local_version", Integer.valueOf(-1));
      var4.put("category_cord", var2);
      var4.put("platform", Integer.valueOf(var3.getOsVersion()));
      var4.put("screen_size", var3.getScreenSize());
      var4.put("match_type", Integer.valueOf(var3.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_CATEGORY, var1, var4)).execute(new Void[0]);
   }

   public static void getComments(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, int var3, int var4) {
      HashMap var5 = new HashMap(3);
      var5.put("p_id", var2);
      var5.put("size", Integer.valueOf(var3));
      var5.put("start_position", Integer.valueOf(var4));
      (new ApiAsyncTask(var0, ACTION_GET_COMMENTS, var1, var5)).execute(new Void[0]);
   }

   public static void getConsumeDetail(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3) {
      HashMap var4 = new HashMap(2);
      var4.put("uid", var2);
      var4.put("type", var3);
      (new ApiAsyncTask(var0, ACTION_GET_CONSUME_DETAIL, var1, var4)).execute(new Void[0]);
   }

   public static void getConsumeSum(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      HashMap var3 = new HashMap(1);
      var3.put("uid", var2);
      (new ApiAsyncTask(var0, ACTION_GET_CONSUMESUM, var1, var3)).execute(new Void[0]);
   }

   public static void getDownloadUrl(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3) {
      Session var4 = Session.get(var0);
      HashMap var5 = new HashMap(3);
      var5.put("p_id", var2);
      var5.put("uid", var4.getUid());
      var5.put("source_type", var3);
      (new ApiAsyncTask(var0, ACTION_GET_DOWNLOAD_URL, var1, var5)).execute(new Void[0]);
   }

   public static void getFollowedRecommend(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(1);
      Session var3 = Session.get(var0);
      var2.put("platform", Integer.valueOf(var3.getOsVersion()));
      var2.put("screen_size", var3.getScreenSize());
      var2.put("match_type", Integer.valueOf(var3.isFilterApps()));
      String var7;
      if(TextUtils.isEmpty(var3.getUid())) {
         var7 = "-1";
      } else {
         var7 = var3.getUid();
      }

      var2.put("uid", var7);
      (new ApiAsyncTask(var0, ACTION_GET_FOLLOWED_RECOMMEND, var1, var2)).execute(new Void[0]);
   }

   public static void getGrowFast(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3) {
      Session var4 = Session.get(var0);
      HashMap var5 = new HashMap(5);
      var5.put("size", Integer.valueOf(var3));
      var5.put("start_position", Integer.valueOf(var2));
      var5.put("platform", Integer.valueOf(var4.getOsVersion()));
      var5.put("screen_size", var4.getScreenSize());
      var5.put("match_type", Integer.valueOf(var4.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_GROW_FAST, var1, var5)).execute(new Void[0]);
   }

   public static void getHomeMasterRecommend(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(3);
      Session var3 = Session.get(var0);
      var2.put("platform", Integer.valueOf(var3.getOsVersion()));
      var2.put("screen_size", var3.getScreenSize());
      var2.put("match_type", Integer.valueOf(var3.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_HOME_MASTER_RECOMMEND, var1, var2)).execute(new Void[0]);
   }

   public static void getHomeRecommend(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3) {
      Session var4 = Session.get(var0);
      HashMap var5 = new HashMap(5);
      var5.put("size", Integer.valueOf(var3));
      var5.put("start_position", Integer.valueOf(var2));
      var5.put("platform", Integer.valueOf(var4.getOsVersion()));
      var5.put("screen_size", var4.getScreenSize());
      var5.put("match_type", Integer.valueOf(var4.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_HOME_RECOMMEND, var1, var5)).execute(new Void[0]);
   }

   public static void getMasterContains(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      HashMap var3 = new HashMap(4);
      Session var4 = Session.get(var0);
      var3.put("platform", Integer.valueOf(var4.getOsVersion()));
      var3.put("screen_size", var4.getScreenSize());
      var3.put("match_type", Integer.valueOf(var4.isFilterApps()));
      var3.put("p_id", var2);
      (new ApiAsyncTask(var0, ACTION_GET_MASTER_CONTAINS, var1, var3)).execute(new Void[0]);
   }

   public static void getMasterRecommend(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3) {
      HashMap var4 = new HashMap(3);
      Session var5 = Session.get(var0);
      var4.put("platform", Integer.valueOf(var5.getOsVersion()));
      var4.put("screen_size", var5.getScreenSize());
      var4.put("match_type", Integer.valueOf(var5.isFilterApps()));
      var4.put("size", Integer.valueOf(var2));
      var4.put("start_position", Integer.valueOf(var3));
      (new ApiAsyncTask(var0, ACTION_GET_MASTER_RECOMMEND, var1, var4)).execute(new Void[0]);
   }

   public static void getMasterRecommendApps(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3, String var4) {
      Session var5 = Session.get(var0);
      HashMap var6 = new HashMap(6);
      var6.put("size", Integer.valueOf(var2));
      var6.put("start_position", Integer.valueOf(var3));
      var6.put("platform", Integer.valueOf(var5.getOsVersion()));
      var6.put("screen_size", var5.getScreenSize());
      var6.put("promotion_id", var4);
      var6.put("match_type", Integer.valueOf(var5.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_MASTER_RECOMMEND_APPS, var1, var6)).execute(new Void[0]);
   }

   public static void getMasterRecommendDiscuss(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3, String var4) {
      HashMap var5 = new HashMap(3);
      var5.put("size", Integer.valueOf(var2));
      var5.put("start_position", Integer.valueOf(var3));
      var5.put("promotion_id", var4);
      (new ApiAsyncTask(var0, ACTION_GET_DISCUSS, var1, var5)).execute(new Void[0]);
   }

   public static void getMasterRecommendRating(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      HashMap var3 = new HashMap(1);
      var3.put("promotion_id", var2);
      (new ApiAsyncTask(var0, ACTION_GET_RECOMMEND_RATING, var1, var3)).execute(new Void[0]);
   }

   public static void getMyRating(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      Session var3 = Session.get(var0);
      HashMap var4 = new HashMap(2);
      var4.put("uid", var3.getUid());
      var4.put("p_id", var2);
      (new ApiAsyncTask(var0, ACTION_GET_MYRATING, var1, var4)).execute(new Void[0]);
   }

   public static void getNotificationRecommend(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(3);
      Session var3 = Session.get(var0);
      var2.put("platform", Integer.valueOf(var3.getOsVersion()));
      var2.put("screen_size", var3.getScreenSize());
      var2.put("match_type", Integer.valueOf(var3.isFilterApps()));
      var2.put("device_type", "1");
      var2.put("rule_support", "1");
      var2.put("last_update_time", var3.getLastNotificationTime());
      (new ApiAsyncTask(var0, ACTION_GET_RECOMMEND_NOTIFICATION, var1, var2)).execute(new Void[0]);
   }

   public static void getPayLog(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3) {
      Session var4 = Session.get(var0);
      HashMap var5 = new HashMap(3);
      var5.put("uid", var4.getUid());
      var5.put("start_position", Integer.valueOf(var2));
      var5.put("size", Integer.valueOf(var3));
      (new ApiAsyncTask(var0, ACTION_GET_PAY_LOG, var1, var5)).execute(new Void[0]);
   }

   public static void getProductDetailWithId(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, String var3, String var4) {
      HashMap var5 = new HashMap(3);
      var5.put("local_version", Integer.valueOf(var2));
      var5.put("p_id", var3);
      var5.put("source_type", var4);
      (new ApiAsyncTask(var0, ACTION_GET_PRODUCT_DETAIL, var1, var5)).execute(new Void[0]);
   }

   public static void getProductDetailWithPackageName(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, String var3) {
      HashMap var4 = new HashMap(3);
      var4.put("local_version", Integer.valueOf(var2));
      var4.put("packagename", var3);
      (new ApiAsyncTask(var0, ACTION_GET_DETAIL, var1, var4)).execute(new Void[0]);
   }

   public static void getProducts(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3, int var4, String var5) {
      Session var6 = Session.get(var0);
      HashMap var7 = new HashMap(6);
      var7.put("size", Integer.valueOf(var2));
      var7.put("start_position", Integer.valueOf(var3));
      var7.put("platform", Integer.valueOf(var6.getOsVersion()));
      var7.put("screen_size", var6.getScreenSize());
      var7.put("orderby", Integer.valueOf(var4));
      var7.put("category_id", var5);
      var7.put("match_type", Integer.valueOf(var6.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_PRODUCTS, var1, var7)).execute(new Void[0]);
   }

   public static void getRankByCategory(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3, String var4) {
      Session var5 = Session.get(var0);
      HashMap var6 = new HashMap(6);
      var6.put("size", Integer.valueOf(var3));
      var6.put("start_position", Integer.valueOf(var2));
      var6.put("category", var4);
      var6.put("platform", Integer.valueOf(var5.getOsVersion()));
      var6.put("screen_size", var5.getScreenSize());
      var6.put("match_type", Integer.valueOf(var5.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_RANK_BY_CATEGORY, var1, var6)).execute(new Void[0]);
   }

   public static void getRecommendByApp(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      HashMap var3 = new HashMap(4);
      Session var4 = Session.get(var0);
      var3.put("platform", Integer.valueOf(var4.getOsVersion()));
      var3.put("screen_size", var4.getScreenSize());
      var3.put("match_type", Integer.valueOf(var4.isFilterApps()));
      var3.put("p_id", var2);
      (new ApiAsyncTask(var0, ACTION_GET_RECOMMEND_BY_APP, var1, var3)).execute(new Void[0]);
   }

   public static void getRecommendProducts(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, int var3, int var4) {
      Session var5 = Session.get(var0);
      HashMap var6 = new HashMap(6);
      var6.put("type", var2);
      var6.put("size", Integer.valueOf(var3));
      var6.put("start_position", Integer.valueOf(var4));
      var6.put("platform", Integer.valueOf(var5.getOsVersion()));
      var6.put("screen_size", var5.getScreenSize());
      var6.put("match_type", Integer.valueOf(var5.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_RECOMMEND_PRODUCTS, var1, var6)).execute(new Void[0]);
   }

   public static void getRequired(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(3);
      Session var3 = Session.get(var0);
      var2.put("platform", Integer.valueOf(var3.getOsVersion()));
      var2.put("screen_size", var3.getScreenSize());
      var2.put("match_type", Integer.valueOf(var3.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_REQUIRED, var1, var2)).execute(new Void[0]);
   }

   public static void getSearchFromBBS(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, int var3, int var4) {
      ArrayList var5 = new ArrayList();
      var5.add(new BasicNameValuePair("searchWord", var2));
      var5.add(new BasicNameValuePair("startPosition", String.valueOf(var3)));
      var5.add(new BasicNameValuePair("size", String.valueOf(var4)));
      (new ApiAsyncTask(var0, ACTION_BBS_SEARCH, var1, var5)).execute(new Void[0]);
   }

   public static void getSearchKeywords(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(1);
      var2.put("size", Integer.valueOf(15));
      (new ApiAsyncTask(var0, ACTION_GET_SEARCH_KEYWORDS, var1, var2)).execute(new Void[0]);
   }

   public static void getTopRecommend(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      HashMap var2 = new HashMap(3);
      Session var3 = Session.get(var0);
      var2.put("platform", Integer.valueOf(var3.getOsVersion()));
      var2.put("screen_size", var3.getScreenSize());
      var2.put("match_type", Integer.valueOf(var3.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_TOP_RECOMMEND, var1, var2)).execute(new Void[0]);
   }

   public static void getTopic(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      Session var2 = Session.get(var0);
      HashMap var3 = new HashMap(4);
      var3.put("platform", Integer.valueOf(var2.getOsVersion()));
      var3.put("screen_size", var2.getScreenSize());
      var3.put("match_type", Integer.valueOf(var2.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_GET_TOPIC, var1, var3)).execute(new Void[0]);
   }

   public static void login(Context context, ApiAsyncTask.ApiRequestListener requestListener, String username, String password) {
      HashMap hmap = new HashMap(2);
      hmap.put("username", username);
      hmap.put("password", password);
      (new ApiAsyncTask(context, ACTION_LOGIN, requestListener, hmap)).execute(new Void[0]);
   }

   public static void markFollowedRead(Context var0, String var1, ApiAsyncTask.ApiRequestListener var2) {
      HashMap var3 = new HashMap(1);
      Session var4 = Session.get(var0);
      var3.put("platform", Integer.valueOf(var4.getOsVersion()));
      var3.put("screen_size", var4.getScreenSize());
      var3.put("match_type", Integer.valueOf(var4.isFilterApps()));
      var3.put("promotion_id", var1);
      String var9;
      if(TextUtils.isEmpty(var4.getUid())) {
         var9 = "-1";
      } else {
         var9 = var4.getUid();
      }

      var3.put("uid", var9);
      (new ApiAsyncTask(var0, ACTION_MARK_READ, var2, var3)).execute(new Void[0]);
   }

   public static void purchaseProduct(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3) {
      Session var4 = Session.get(var0);
      String var5 = SecurityUtil.encryptPassword(var3, var4.getUserName());
      String var6 = Utils.getUTF8String(Base64.encodeBase64(DigestUtils.md5(var4.getUserName() + var2 + var5)));
      HashMap var7 = new HashMap(4);
      var7.put("pid", var2);
      var7.put("username", var4.getUserName());
      var7.put("password", var5);
      var7.put("verify_code", var6);
      (new ApiAsyncTask(var0, ACTION_PURCHASE_PRODUCT, var1, var7)).execute(new Void[0]);
   }

   public static void queryAliPayResult(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      HashMap var3 = new HashMap(1);
      var3.put("orderNo", var2);
      (new ApiAsyncTask(var0, ACTION_QUERY_ALIPAY_RESULT, var1, var3)).execute(new Void[0]);
   }

   public static void queryChargeResult(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      HashMap var3 = new HashMap(1);
      var3.put("order_id", var2);
      (new ApiAsyncTask(var0, ACTION_QUERY_CHARGE_BY_ORDERID, var1, var3)).execute(new Void[0]);
   }

   public static void queryFoolowStatus(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2) {
      HashMap var3 = new HashMap(1);
      Session var4 = Session.get(var0);
      String var5;
      if(TextUtils.isEmpty(var4.getUid())) {
         var5 = "-1";
      } else {
         var5 = var4.getUid();
      }

      var3.put("uid", var5);
      var3.put("promotion_id", var2);
      (new ApiAsyncTask(var0, ACTION_FOLLOW_MASTER, var1, var3)).execute(new Void[0]);
   }

   public static void register(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3, String var4) {
      HashMap var5 = new HashMap(3);
      var5.put("username", var2);
      var5.put("password", var3);
      var5.put("email", var4);
      (new ApiAsyncTask(var0, ACTION_REGISTER, var1, var5)).execute(new Void[0]);
   }

   public static void reportIftttResult(Context var0, String var1, String var2, String var3, int var4) {
      HashMap var5 = new HashMap(3);
      Session var6 = Session.get(var0);
      String var7;
      if(TextUtils.isEmpty(var6.getUid())) {
         var7 = "-1";
      } else {
         var7 = var6.getUid();
      }

      var5.put("uid", var7);
      var5.put("p_id", var1);
      var5.put("nid", var2);
      var5.put("rule", var3);
      var5.put("cpid", var6.getCpid());
      var5.put("report_type", Integer.valueOf(var4));
      var5.put("package_name", var6.getPackageName());
      (new ApiAsyncTask(var0, ACTION_IFTTT_REPORT, (ApiAsyncTask.ApiRequestListener)null, var5)).execute(new Void[0]);
   }

   public static void requestFollowMaster(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3) {
      HashMap var4 = new HashMap(1);
      Session var5 = Session.get(var0);
      String var6;
      if(TextUtils.isEmpty(var5.getUid())) {
         var6 = "-1";
      } else {
         var6 = var5.getUid();
      }

      var4.put("uid", var6);
      var4.put("promotion_id", var2);
      var4.put("follow_flag", var3);
      (new ApiAsyncTask(var0, ACTION_FOLLOW_MASTER, var1, var4)).execute(new Void[0]);
   }

   public static void search(Context var0, ApiAsyncTask.ApiRequestListener var1, int var2, int var3, int var4, String var5) {
      Session var6 = Session.get(var0);
      HashMap var7 = new HashMap(7);
      var7.put("size", Integer.valueOf(var2));
      var7.put("start_position", Integer.valueOf(var3));
      var7.put("platform", Integer.valueOf(var6.getOsVersion()));
      var7.put("screen_size", var6.getScreenSize());
      var7.put("orderby", Integer.valueOf(var4));
      var7.put("keyword", var5);
      var7.put("match_type", Integer.valueOf(var6.isFilterApps()));
      (new ApiAsyncTask(var0, ACTION_SEARCH, var1, var7)).execute(new Void[0]);
   }

   public static void submitAllInstalledApps(Context var0) {
      HashMap var1 = new HashMap(1);
      var1.put("appList", "");
      (new ApiAsyncTask(var0, ACTION_SYNC_APPS, (ApiAsyncTask.ApiRequestListener)null, var1)).execute(new Void[0]);
   }

   public static void submitDownloadLog(Context var0, ApiAsyncTask.ApiRequestListener var1, String var2, String var3, String var4, String var5, int var6, String var7) {
      Session var8 = Session.get(var0);
      HashMap var9 = new HashMap(8);
      String var10 = var8.getUid();
      if(TextUtils.isEmpty(var10)) {
         var10 = "-1";
      }

      var9.put("uid", var10);
      var9.put("p_id", var2);
      var9.put("source_type", var3);
      var9.put("url", var4);
      var9.put("cpid", var8.getCpid());
      var9.put("net_context", var5);
      var9.put("report_type", Integer.valueOf(var6));
      var9.put("package_name", var7);
      (new ApiAsyncTask(var0, ACTION_DOWN_REPORT, var1, var9)).execute(new Void[0]);
   }

   public static void submitLogs(final Context var0, final int var1, final String var2) {
      HashMap var3 = new HashMap(2);
      var3.put("module", var2);
      var3.put("level", Integer.valueOf(var1));
      (new ApiAsyncTask(var0, ACTION_INSERT_LOG, new ApiAsyncTask.ApiRequestListener() {
         public void onError(int var1x, int var2x) {}
         public void onSuccess(int var1x, Object var2x) {
            DBUtils.delLogs(var0, var2, var1);
         }
      }, var3)).execute(new Void[0]);
   }

   public static void syncBuyLog(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      Session var2 = Session.get(var0);
      HashMap var3 = new HashMap(1);
      var3.put("uid", var2.getUid());
      (new ApiAsyncTask(var0, ACTION_SYNC_BUYLOG, var1, var3)).execute(new Void[0]);
   }

   public static void syncCardInfo(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      Session var2 = Session.get(var0);
      HashMap var3 = new HashMap(1);
      var3.put("local_version", Integer.valueOf(var2.getCreditCardVersion()));
      (new ApiAsyncTask(var0, ACTION_SYNC_CARDINFO, var1, var3)).execute(new Void[0]);
   }

   public static void unbindAccount(Context var0, ApiAsyncTask.ApiRequestListener var1) {
      (new ApiAsyncTask(var0, ACTION_UNBIND, var1, (Object)null)).execute(new Void[0]);
   }
}
