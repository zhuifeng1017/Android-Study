package com.xxx.appstore.common;

import com.xxx.appstore.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.xxx.appstore.Session;
import org.apache.commons.codec.binary.Base64;
import com.xxx.appstore.common.util.Crypter;
import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.SecurityUtil;
import com.xxx.appstore.common.util.StringUtils;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.util.XmlElement;
import com.xxx.appstore.common.vo.BuyLog;
import com.xxx.appstore.common.vo.CardsVerification;
import com.xxx.appstore.common.vo.CardsVerifications;
import com.xxx.appstore.common.vo.DownloadItem;
import com.xxx.appstore.common.vo.PayAndChargeLog;
import com.xxx.appstore.common.vo.PayAndChargeLogs;
import com.xxx.appstore.common.vo.ProductDetail;
import com.xxx.appstore.common.vo.SplashInfo;
import com.xxx.appstore.common.vo.UpdateInfo;
import com.xxx.appstore.common.vo.UpgradeInfo;

public class ApiResponseFactory {

	private static void getPayAndChargeLog(List<XmlElement> var0,
			PayAndChargeLogs var1, String var2) {
		PayAndChargeLog var5;
		if (var0 != null && var0.size() > 0) {
			Iterator<XmlElement> var3 = var0.iterator();
			while (var3.hasNext()) {
				XmlElement var4 = (XmlElement) var3.next();
				var5 = new PayAndChargeLog();
				var5.name = var4.getAttribute("flag");
				var5.id = Utils.getInt(var4.getAttribute("order_id"));
				var5.desc = var4.getAttribute("description");
				var5.time = Utils.formatDate(Utils.getLong(var4
						.getAttribute("create_time")));
				var5.payment = (int) Utils.getFloat(var4.getAttribute("money"));
				if ("consume".equals(var2)) {
					var5.type = 1;
				} else if ("charge".equals(var2)) {
					var5.type = 3;
				} else if ("buy_app".equals(var2)) {
					var5.id = Utils.getInt(var4.getAttribute("p_id"));
					var5.name = var4.getAttribute("name");
					var5.iconUrl = var4.getAttribute("icon_url");
					var5.type = 2;
					var5.sourceType = Utils.getInt(var4
							.getAttribute("source_type"));
				}
				var1.payAndChargeLogList.add(var5);
			}
		}

	}

	public static Object getResponse(Context context, int action,
			HttpResponse httpresponse) {
		String strResponse = null;

		if (action >= MarketAPI.ACTION_STARTD
				|| MarketAPI.ACTION_GET_ALIPAY_ORDER_INFO == action
				|| MarketAPI.ACTION_QUERY_ALIPAY_RESULT == action) {
			strResponse = Utils.getStringResponse(httpresponse);
			if (TextUtils.isEmpty(strResponse))
				return null;
		}

		if (MarketAPI.ACTION_CHECK_LOG_LEVEL == action
				|| MarketAPI.ACTION_INSERT_LOG == action) {
			strResponse = Utils.getGzipStringResponse(httpresponse);
			if (TextUtils.isEmpty(strResponse))
				return null;
		}

		InputStream inputstream = null;
		if (action < MarketAPI.ACTION_STARTD) {// jfan需要unzip
			inputstream = Utils.getInputStreamResponse(httpresponse);
			if (null == inputstream) {
				return null;
			}
		}

		String s1 = "";
		Object obj = null;
		if (action >= MarketAPI.ACTION_STARTD) {	// uni action
			obj = parseUniJson(strResponse);
		} else {
			try {
				switch (action) {
				case MarketAPI.ACTION_LOGIN:
					s1 = "ACTION_LOGIN";
					obj = parseLoginOrRegisterResult(XmlElement
							.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_REGISTER:
					s1 = "ACTION_REGISTER";
					obj = parseLoginOrRegisterResult(XmlElement
							.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_COMMENTS:
					s1 = "ACTION_GET_COMMENTS";
					obj = parseComments(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_ADD_COMMENT:
					s1 = "ACTION_ADD_COMMENT";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_ADD_RATING:
					s1 = "ACTION_ADD_RATIONG";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_PURCHASE_PRODUCT:
					s1 = "ACTION_PURCHASE_PRODUCT";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_GET_CONSUMESUM:
					break;
				case MarketAPI.ACTION_SYNC_BUYLOG:
					s1 = "ACTION_SYNC_BUYLOG";
					obj = parseSyncBuyLog(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_MYRATING:
					s1 = "ACTION_GET_MYRATING";
					obj = parseMyRating(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_CONSUME_DETAIL:

					break;
				case MarketAPI.ACTION_GET_TOPIC:
					s1 = "ACTION_GET_TOPIC";
					obj = parseTopicList(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_SEARCH:
					s1 = "ACTION_SEARCH";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), false);
					break;
				case MarketAPI.ACTION_GET_PRODUCTS:
					s1 = "ACTION_GET_PRODUCTS";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), false);
					break;
				case MarketAPI.ACTION_GET_RECOMMEND_PRODUCTS:
					s1 = "ACTION_GET_RECOMMEND_PRODUCTS";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), false);
					break;
				case MarketAPI.ACTION_GET_PRODUCT_DETAIL:
					s1 = "ACTION_GET_PRODUCT_DETAIL";
					obj = parseProductDetail(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_DOWNLOAD_URL:
					s1 = "ACTION_GET_DOWNLOAD_URL";
					obj = parseDownloadInfo(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_HOME_RECOMMEND:
					s1 = "ACTION_GET_HOME_RECOMMEND";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), true);
					break;
				case MarketAPI.ACTION_CHECK_NEW_VERSION:
					s1 = "ACTION_CHECK_NEW_VERSION";
					obj = parseCheckNewVersion(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_CATEGORY:
					break;
				case MarketAPI.ACTION_CHECK_UPGRADE:
					s1 = "ACTION_CHECK_UPGRADE";
					obj = parseUpgrade(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_BIND_ACCOUNT:
					s1 = "ACTION_BIND_ACCOUNT";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_GET_BALANCE:
					s1 = "ACTION_GET_BALANCE";
					obj = parseGetBalance(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_PAY_LOG:
					s1 = "ACTION_GET_PAY_LOG";
					obj = parseGetPayLog(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_CHARGE:
					s1 = "ACTION_CHARGE";
					obj = parseChargeResult(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_SYNC_CARDINFO:
					s1 = "ACTION_SYNC_CARDINFO";
					obj = parseSyncCardinfo(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_QUERY_CHARGE_BY_ORDERID:
					s1 = "ACTION_QUERY_CHARGE_BY_ORDERID";
					obj = Integer
							.valueOf(parseQueryChargeResultByOderID(XmlElement
									.parseXml(inputstream)));
					break;
				case MarketAPI.ACTION_QUERY_CHARGE:
					break;
				case MarketAPI.ACTION_SYNC_APPS:
					s1 = "ACTION_SYNC_APPS";
					obj = parseSyncApps(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_CHECK_NEW_SPLASH:
					s1 = "ACTION_CHECK_NEW_SPLASH";
					obj = parseNewSplash(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_UNBIND:
					s1 = "ACTION_UNBIND";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_GET_DETAIL:
					s1 = "ACTION_GET_DETAIL";
					obj = parseProductDetail(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_ALIPAY_ORDER_INFO:
					s1 = "ACTION_GET_ALIPAY_ORDER_INFO";
					try {
						obj = parseGetAlipayOrderInfo(strResponse);
					} catch (JSONException e1) {
						Utils.D((new StringBuilder()).append(s1)
								.append(" has JSONException").toString(), e1);
					}
					break;
				case MarketAPI.ACTION_QUERY_ALIPAY_RESULT:
					s1 = "ACTION_QUERY_ALIPAY_RESULT";
					try {
						obj = parseGetAlipayOrderInfo(strResponse);
					} catch (JSONException e1) {
						Utils.D((new StringBuilder()).append(s1)
								.append(" has JSONException").toString(), e1);
					}
					break;
				case MarketAPI.ACTION_GET_SEARCH_KEYWORDS:
					s1 = "ACTION_GET_SEARCH_KEYWORDS";
					obj = parseSearchKeywords(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_TOP_RECOMMEND:
					s1 = "ACTION_GET_TOP_RECOMMEND";
					obj = parseTopRecommend(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_RANK_BY_CATEGORY:
					s1 = "ACTION_GET_RANK_BY_CATEGORY";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), false);
					break;
				case MarketAPI.ACTION_GET_GROW_FAST:
					s1 = "ACTION_GET_GROW_FAST";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), false);
					break;
				case MarketAPI.ACTION_GET_ALL_CATEGORY:
					s1 = "ACTION_GET_ALL_CATEGORY";
					obj = parseAllCategory(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_REQUIRED:
					s1 = "ACTION_GET_REQUIRED";
					obj = parseGetRequired(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_BBS_SEARCH:
					s1 = "ACTION_BBS_SEARCH";
					obj = parseBbsSearchResult((new BufferedReader(
							new InputStreamReader(inputstream))).readLine());
					break;
				case MarketAPI.ACTION_CHECK_LOG_LEVEL:
					s1 = "ACTION_CHECK_LOG_LEVEL";
					try {
						obj = parseLogLevel(strResponse);
					} catch (JSONException e1) {
						Utils.D((new StringBuilder()).append(s1)
								.append(" has JSONException").toString(), e1);
					}
					break;
				case MarketAPI.ACTION_INSERT_LOG:
					s1 = "ACTION_INSERT_LOG";
					try {
						obj = Boolean.valueOf(parseSubmitLog(strResponse));
					} catch (JSONException e1) {
						Utils.D((new StringBuilder()).append(s1)
								.append(" has JSONException").toString(), e1);
					}
					break;
				case MarketAPI.ACTION_GET_MASTER_RECOMMEND:
					s1 = "ACTION_GET_MASTER_RECOMMEND";
					obj = parseMasterRecommend(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_MASTER_RECOMMEND_APPS:
					s1 = "ACTION_GET_MASTER_RECOMMEND_APPS";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), false);
					break;
				case MarketAPI.ACTION_DOWN_REPORT:
					s1 = "ACTION_DOWN_REPORT";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_GET_DISCUSS:
					s1 = "ACTION_GET_DISCUSS";
					obj = parseGetDiscuss(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_ADD_DISCUSS:
					s1 = "ACTION_ADD_DISCUSS";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_ADD_RECOMMEND_RATING:
					s1 = "ACTION_ADD_RECOMMEND_RATING";
					obj = Boolean.valueOf(true);
					break;
				case MarketAPI.ACTION_GET_RECOMMEND_RATING:
					s1 = "ACTION_GET_RECOMMEND_RATING";
					obj = parseRecommendRating(XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_RECOMMEND_NOTIFICATION:
					s1 = "ACTION_GET_RECOMMEND_NOTIFICATION";
					obj = Boolean.valueOf(parseNotificationRecommend(context,
							XmlElement.parseXml(inputstream)));
					break;
				case MarketAPI.ACTION_GET_MASTER_CONTAINS:
					s1 = "ACTION_GET_MASTER_CONTAINS";
					obj = parseMasterContains(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_RECOMMEND_BY_APP:
					s1 = "ACTION_GET_RECOMMEND_BY_APP";
					obj = parseProductList(context,
							XmlElement.parseXml(inputstream), true);
					break;
				case MarketAPI.ACTION_FOLLOW_MASTER:
					s1 = "ACTION_FOLLOW_MASTER";
					obj = Boolean.valueOf(parseFllowResult(XmlElement
							.parseXml(inputstream)));
					break;
				case MarketAPI.ACTION_GET_FOLLOWED_RECOMMEND:
					s1 = "ACTION_GET_FOLLOWED_RECOMMEND";
					obj = parseFllowedRecommend(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_GET_HOME_MASTER_RECOMMEND:
					s1 = "ACTION_GET_HOME_MASTER_RECOMMEND";
					obj = parseHomeMasterRecommend(context,
							XmlElement.parseXml(inputstream));
					break;
				case MarketAPI.ACTION_MARK_READ:
					s1 = "ACTION_MARK_READ";
					obj = Boolean.valueOf(parseMarkAsRead(XmlElement
							.parseXml(inputstream)));
					break;
				case MarketAPI.ACTION_IFTTT_REPORT:
					s1 = "ACTION_IFTTT_REPORT";
					obj = Boolean.valueOf(true);
					break;
				default:
					break;
				}
			} catch (XmlPullParserException e) {
				Utils.D((new StringBuilder()).append(s1)
						.append(" has XmlPullParserException").toString(), e);
			} catch (IOException e) {
				Utils.D((new StringBuilder()).append(s1)
						.append(" has IOException").toString(), e);
			}
		}

		return obj;
	}

	private static ArrayList<HashMap<String, Object>> parseAllCategory(
			XmlElement var0) {
		ArrayList<HashMap<String, Object>> var5;
		if (var0 == null) {
			var5 = null;
		} else {
			List<XmlElement> var1 = var0.getChildren("category");
			if (var1 != null) {
				ArrayList<HashMap<String, Object>> var2 = new ArrayList<HashMap<String, Object>>();

				for (int var3 = 1; var3 < var1.size(); ++var3) {
					XmlElement var24 = (XmlElement) var1.get(var3);
					HashMap<String, Object> var25 = new HashMap<String, Object>();
					var25.put("category_name",
							var24.getAttribute("category_name"));
					var25.put("app_count", var24.getAttribute("app_count"));
					var25.put("icon_url", var24.getAttribute("icon_url"));
					String var29 = var24.getChild("sub_category", 0)
							.getAttribute("category_name") + ", ";
					XmlElement var30 = var24.getChild("sub_category", 1);
					if (var30 != null) {
						var29 = var29 + var30.getAttribute("category_name")
								+ ", ";
					}

					XmlElement var31 = var24.getChild("sub_category", 2);
					if (var31 != null) {
						var29 = var29 + var31.getAttribute("category_name")
								+ ", ";
					}

					if (var29.length() > 0) {
						var29 = var29.substring(0, var29.length() - 2);
					}

					var25.put("top_app", var29);
					List<XmlElement> var33 = var24.getChildren("sub_category");
					ArrayList<HashMap<String, String>> var34 = new ArrayList<HashMap<String, String>>();
					Iterator<XmlElement> var35 = var33.iterator();

					while (var35.hasNext()) {
						XmlElement var38 = (XmlElement) var35.next();
						HashMap<String, String> var39 = new HashMap<String, String>();
						var39.put("category_id",
								var38.getAttribute("category_id"));
						var39.put("category_name",
								var38.getAttribute("category_name"));
						var39.put("app_count", var38.getAttribute("app_count"));
						var39.put("icon_url", var38.getAttribute("icon_url"));
						String var44 = var38.getAttribute("app_1");
						String var45 = var38.getAttribute("app_2");
						String var46 = var38.getAttribute("app_3");
						StringBuilder var47 = new StringBuilder();
						String var48;
						if (TextUtils.isEmpty(var44)) {
							var48 = "";
						} else {
							var48 = var44 + ", ";
						}

						StringBuilder var49 = var47.append(var48);
						String var50;
						if (TextUtils.isEmpty(var45)) {
							var50 = "";
						} else {
							var50 = var45 + ", ";
						}

						StringBuilder var51 = var49.append(var50);
						String var52;
						if (TextUtils.isEmpty(var46)) {
							var52 = "";
						} else {
							var52 = var46 + ", ";
						}

						String var53 = var51.append(var52).toString();
						if (var53.length() > 0) {
							var53 = var53.substring(0, var53.length() - 2);
						}

						var39.put("top_app", var53);
						var34.add(var39);
					}

					var25.put("sub_category", var34);
					var2.add(var25);
				}

				Iterator<XmlElement> var4 = ((XmlElement) var1.get(0))
						.getChildren("sub_category").iterator();

				while (var4.hasNext()) {
					XmlElement var6 = (XmlElement) var4.next();
					HashMap<String, Object> var7 = new HashMap<String, Object>();
					var7.put("category_id", var6.getAttribute("category_id"));
					var7.put("category_name",
							var6.getAttribute("category_name"));
					var7.put("app_count", var6.getAttribute("app_count"));
					var7.put("icon_url", var6.getAttribute("icon_url"));
					String var12 = var6.getAttribute("app_1");
					String var13 = var6.getAttribute("app_2");
					String var14 = var6.getAttribute("app_3");
					StringBuilder var15 = new StringBuilder();
					String var16;
					if (TextUtils.isEmpty(var12)) {
						var16 = "";
					} else {
						var16 = var12 + ", ";
					}

					StringBuilder var17 = var15.append(var16);
					String var18;
					if (TextUtils.isEmpty(var13)) {
						var18 = "";
					} else {
						var18 = var13 + ", ";
					}

					StringBuilder var19 = var17.append(var18);
					String var20;
					if (TextUtils.isEmpty(var14)) {
						var20 = "";
					} else {
						var20 = var14 + ", ";
					}

					String var21 = var19.append(var20).toString();
					if (var21.length() > 0) {
						var21 = var21.substring(0, var21.length() - 2);
					}

					var7.put("top_app", var21);
					var2.add(var7);
				}

				var5 = var2;
			} else {
				var5 = null;
			}
		}

		return var5;
	}

	private static HashMap parseBbsSearchResult(String s) {
		if (s == null)
			return null;

		HashMap hashmap1 = null;
		JSONObject jsonobject;
		HashMap hashmap;
		try {
			jsonobject = new JSONObject(s);

			hashmap = new HashMap();
			JSONArray jsonarray;
			int i;
			ArrayList arraylist;
			hashmap.put("total_size",
					Integer.valueOf(jsonobject.getInt("totalSize")));
			hashmap.put("end_position",
					Integer.valueOf(jsonobject.getInt("endPosition")));
			jsonarray = jsonobject.getJSONArray("bbsAttJkVOList");
			i = jsonarray.length();
			if (i <= 0)
				return hashmap1;
			arraylist = new ArrayList();
			for (int j = 0; j < i; j++) {
				HashMap hashmap2 = new HashMap();
				JSONObject jsonobject1 = jsonarray.getJSONObject(j);
				hashmap2.put("search_result_title",
						String.valueOf(jsonobject1.get("subject")));
				hashmap2.put("place_holder", Boolean.valueOf(true));
				arraylist.add(hashmap2);
				JSONArray jsonarray1 = jsonobject1
						.getJSONArray("bbsAttJkFileVOList");
				int k = jsonarray1.length();
				for (int l = 0; l < k; l++) {
					HashMap hashmap3 = new HashMap();
					JSONObject jsonobject2 = jsonarray1.getJSONObject(l);
					hashmap3.put("place_holder", Boolean.valueOf(false));
					hashmap3.put("search_result_title",
							String.valueOf(jsonobject2.get("fileName")));
					hashmap3.put("downloadUrl", String.valueOf(jsonobject2
							.getString("downloadUrl")));
					arraylist.add(hashmap3);
				}
			}

			hashmap.put("bbsAttJkVOList", arraylist);
			// Utils.D("have json exception when parse search result from bbs",
			// jsonexception);
			hashmap1 = hashmap;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return hashmap1;
	}

	private static String parseChargeResult(XmlElement var0) {
		String var2;
		if (var0 == null) {
			var2 = null;
		} else {
			XmlElement var1 = var0.getChild("pay_result", 0);
			if (var1 != null) {
				var2 = var1.getAttribute("order_id");
			} else {
				var2 = null;
			}
		}

		return var2;
	}

	private static Object parseCheckNewVersion(Context var0, XmlElement var1) {
		UpdateInfo var3;
		if (var1 == null) {
			var3 = null;
		} else {
			int var2 = Utils.getInt(var1.getChild("update_level", 0).getText());
			if (var2 == 0) {
				var3 = null;
			} else {
				UpdateInfo var4 = new UpdateInfo();
				var4.setUpdageLevel(var2);
				XmlElement var5 = var1.getChild("version_code", 0);
				if (var5 != null) {
					var4.setVersionCode(Utils.getInt(var5.getText()));
				}

				XmlElement var6 = var1.getChild("version_name", 0);
				if (var6 != null) {
					var4.setVersionName(var6.getText());
				}

				XmlElement var7 = var1.getChild("description", 0);
				if (var7 != null) {
					var4.setDescription(var7.getText());
				}

				XmlElement var8 = var1.getChild("apk_url", 0);
				if (var8 != null) {
					var4.setApkUrl(var8.getText());
				}

				var3 = var4;
			}
		}

		return var3;
	}

	private static Object parseComments(XmlElement var0) {
		HashMap var8;
		if (var0 == null) {
			var8 = null;
		} else {
			XmlElement var1 = var0.getChild("comments", 0);
			if (var1 != null) {
				HashMap var2 = new HashMap();
				int var3 = Utils.getInt(var1.getAttribute("total_size"));
				var2.put("total_size", Integer.valueOf(var3));
				if (var3 > 0) {
					ArrayList var5 = new ArrayList();
					Iterator var6 = var1.getChildren("comment").iterator();

					while (var6.hasNext()) {
						XmlElement var9 = (XmlElement) var6.next();
						HashMap var10 = new HashMap();
						var10.put("comment_id", var9.getAttribute("comment_id"));
						var10.put("author", var9.getAttribute("author"));
						var10.put("comment", var9.getAttribute("comment"));
						var10.put("date", Utils.formatTime(Utils.getLong(var9
								.getAttribute("date"))));
						var5.add(var10);
					}

					var2.put("comment_list", var5);
				}

				var8 = var2;
			} else {
				var8 = null;
			}
		}

		return var8;
	}

	private static DownloadItem parseDownloadInfo(XmlElement var0) {
		DownloadItem var3;
		if (var0 == null) {
			var3 = null;
		} else {
			XmlElement var1 = var0.getChild("download_info", 0);
			if (var1 != null) {
				DownloadItem var2 = new DownloadItem();
				var2.pId = var1.getAttribute("p_id");
				var2.packageName = var1.getAttribute("packagename");
				var2.url = var1.getAttribute("url");
				var2.fileMD5 = var1.getAttribute("filemd5");
				var3 = var2;
			} else {
				var3 = null;
			}
		}

		return var3;
	}

	private static boolean parseFllowResult(XmlElement var0) {
		boolean var2;
		if (var0 == null) {
			var2 = false;
		} else {
			XmlElement var1 = var0.getChild("recommend", 0);
			if (var1 != null && "1".equals(var1.getAttribute("follow_flag"))) {
				var2 = true;
			} else {
				var2 = false;
			}
		}

		return var2;
	}

	private static HashMap<String, Object> parseFllowedRecommend(Context var0,
			XmlElement var1) {
		HashMap var8;
		if (var1 == null) {
			var8 = null;
		} else {
			HashMap var2 = new HashMap();
			var2.put("total_size", Integer.valueOf(Utils.getInt(var1
					.getAttribute("total_size"))));
			var2.put("total_unread", Integer.valueOf(Utils.getInt(var1
					.getAttribute("total_unread"))));
			ArrayList var5 = new ArrayList();
			List var6 = var1.getChildren("recommend");
			HashMap var11;
			if (var6 != null) {
				for (Iterator var9 = var6.iterator(); var9.hasNext(); var5
						.add(var11)) {
					XmlElement var10 = (XmlElement) var9.next();
					var11 = new HashMap();
					var11.put("id", var10.getAttribute("id"));
					var11.put("icon_url", var10.getAttribute("icon_url"));
					var11.put("name", var10.getAttribute("name"));
					var11.put("description", var10.getAttribute("description"));
					Object[] var16 = new Object[] { DateUtils
							.getRelativeTimeSpanString(Utils.getLong(var10
									.getAttribute("update_time")), System
									.currentTimeMillis(), 60000L, 262144) };
					var11.put("update_time", var0.getString(
							R.string.recommendation_update_time, var16));
					var11.put("like", var10.getAttribute("like"));
					var11.put("dislike", var10.getAttribute("dislike"));
					var11.put("user", var10.getAttribute("user"));
					var11.put("fans", var10.getAttribute("fans"));
					var11.put("experience", var10.getAttribute("experience"));
					int var23 = Utils.getInt(var10.getAttribute("unread"));
					var11.put("unread", Integer.valueOf(var23));
					if (var23 > 0) {
						parseProductListResult(var0, var10, var11);
					}
				}
			}

			var2.put("recommend_list", var5);
			var8 = var2;
		}

		return var8;
	}

	private static JSONObject parseGetAlipayOrderInfo(String var0)
			throws JSONException {
		byte[] var1 = Base64.decodeBase64(var0);
		return new JSONObject(new String((new Crypter()).decrypt(var1,
				SecurityUtil.SECRET_KEY_HTTP_CHARGE_ALIPAY)));
	}

	private static String parseGetBalance(XmlElement var0) {
		String var2;
		if (var0 == null) {
			var2 = null;
		} else {
			XmlElement var1 = var0.getChild("result", 0);
			if (var1 != null) {
				var2 = var1.getText();
			} else {
				var2 = null;
			}
		}

		return var2;
	}

	private static Object parseGetDiscuss(XmlElement var0) {
		HashMap var8;
		if (var0 == null) {
			var8 = null;
		} else {
			XmlElement var1 = var0.getChild("discusses", 0);
			if (var1 != null) {
				HashMap var2 = new HashMap();
				int var3 = Utils.getInt(var1.getAttribute("total_size"));
				var2.put("total_size", Integer.valueOf(var3));
				if (var3 > 0) {
					ArrayList var5 = new ArrayList();
					Iterator var6 = var1.getChildren("discuss").iterator();

					while (var6.hasNext()) {
						XmlElement var9 = (XmlElement) var6.next();
						HashMap var10 = new HashMap();
						var10.put("id", var9.getAttribute("id"));
						String var12;
						if ("1".equals(var9.getAttribute("is_owner"))) {
							var12 = var9.getAttribute("author") + " (楼主)";
						} else {
							var12 = var9.getAttribute("author");
						}

						var10.put("author", var12);
						var10.put("date", Utils.formatTime(Utils.getLong(var9
								.getAttribute("date"))));
						var10.put("content", var9.getAttribute("content"));
						var10.put("is_owner", var9.getAttribute("is_owner"));
						var5.add(var10);
					}

					var2.put("comment_list", var5);
				}

				var8 = var2;
			} else {
				var8 = null;
			}
		}

		return var8;
	}

	private static PayAndChargeLogs parseGetPayLog(Context var0, XmlElement var1) {
		PayAndChargeLogs var4;
		if (var1 == null) {
			var4 = null;
		} else {
			XmlElement var2 = var1.getChild("logs", 0);
			if (var2 != null) {
				PayAndChargeLogs var3 = new PayAndChargeLogs();
				var3.endPosition = Utils.getInt(var2
						.getAttribute("end_position"));
				var3.totalSize = Utils.getInt(var2.getAttribute("total_size"));
				getPayAndChargeLog(var2.getChildren("consume"), var3, "consume");
				getPayAndChargeLog(var2.getChildren("charge"), var3, "charge");
				getPayAndChargeLog(var2.getChildren("buy_app"), var3, "buy_app");
				var4 = var3;
			} else {
				var4 = null;
			}
		}

		return var4;
	}

	private static Object parseGetRequired(Context var0, XmlElement var1) {
		ArrayList var3;
		if (var1 == null) {
			var3 = null;
		} else {
			List var2 = var1.getChildren("req_category");
			if (var2 != null && var2.size() > 0) {
				ArrayList var4 = new ArrayList();
				ArrayList var5 = Session.get(var0).getInstalledApps();
				Iterator var6 = var2.iterator();

				while (var6.hasNext()) {
					XmlElement var7 = (XmlElement) var6.next();
					HashMap var8 = new HashMap();
					var8.put("place_holder", Boolean.valueOf(true));
					var8.put("app_title", var7.getAttribute("name"));
					List var11 = var7.getChildren("product");
					var4.add(var8);
					HashMap var15;
					if (var11 != null && var11.size() != 0) {
						for (Iterator var13 = var11.iterator(); var13.hasNext(); var4
								.add(var15)) {
							XmlElement var14 = (XmlElement) var13.next();
							var15 = new HashMap();
							var15.put("place_holder", Boolean.valueOf(false));
							var15.put("p_id", var14.getAttribute("p_id"));
							var15.put("logo", var14.getAttribute("icon_url"));
							var15.put("app_title", var14.getAttribute("name"));
							var15.put("app_detail",
									var14.getAttribute("short_description"));
							String var21 = var14.getAttribute("packagename");
							var15.put("packagename", var21);
							if (var5.contains(var21)) {
								var15.put("is_installed", Boolean.valueOf(true));
							} else {
								var15.put("is_checked", Boolean.valueOf(false));
							}
						}
					}
				}

				var3 = var4;
			} else {
				var3 = null;
			}
		}

		return var3;
	}

	private static Object parseHomeMasterRecommend(Context var0, XmlElement var1) {
		ArrayList var2;
		if (var1 == null) {
			var2 = null;
		} else if (Utils.getInt(var1.getAttribute("total_size")) <= 0) {
			var2 = null;
		} else {
			List var3 = var1.getChildren("recommend");
			if (var3 != null && var3.size() > 0) {
				ArrayList var4 = new ArrayList();
				Iterator var5 = var3.iterator();

				while (var5.hasNext()) {
					XmlElement var6 = (XmlElement) var5.next();
					HashMap var7 = new HashMap();
					var7.put("id", var6.getAttribute("id"));
					var7.put("icon_url", var6.getAttribute("icon_url"));
					var7.put("name", var6.getAttribute("name"));
					var7.put("description", var6.getAttribute("description"));
					Object[] var12 = new Object[] { DateUtils
							.getRelativeTimeSpanString(Utils.getLong(var6
									.getAttribute("update_time")), System
									.currentTimeMillis(), 60000L, 262144) };
					var7.put("update_time", var0.getString(
							R.string.recommendation_update_time, var12));
					var7.put("like", var6.getAttribute("like"));
					var7.put("dislike", var6.getAttribute("dislike"));
					var7.put("fans", var6.getAttribute("fans"));
					var7.put("user", var6.getAttribute("user"));
					var7.put("pic_url", var6.getAttribute("pic_url"));
					var4.add(var7);
				}

				var2 = var4;
			} else {
				var2 = null;
			}
		}

		return var2;
	}

	private static HashMap<String, String> parseLogLevel(String var0)
			throws JSONException {
		JSONObject var1 = new JSONObject(var0);
		Iterator var2 = var1.keys();
		HashMap var3 = new HashMap();

		while (var2.hasNext()) {
			String var4 = (String) var2.next();
			var3.put(var4, var1.getString(var4));
		}

		return var3;
	}

	private static HashMap<String, String> parseLoginOrRegisterResult(
			XmlElement var0) {
		HashMap var1;
		if (var0 == null) {
			var1 = null;
		} else {
			var1 = new HashMap();
			var1.put("uid", var0.getChild("uid", 0).getText());
			var1.put("name", var0.getChild("name", 0).getText());
			var1.put("email", var0.getChild("email", 0).getText());
		}

		return var1;
	}

	private static boolean parseMarkAsRead(XmlElement var0) {
		boolean var1;
		if (var0 == null) {
			var1 = false;
		} else if (var0.getChild("product", 0) != null) {
			var1 = true;
		} else {
			var1 = false;
		}

		return var1;
	}

	private static Object parseMasterContains(Context var0, XmlElement var1) {
		HashMap var2;
		if (var1 == null) {
			var2 = null;
		} else {
			var2 = new HashMap();
			ArrayList var3 = new ArrayList();
			List var4 = var1.getChildren("recommend");
			if (var4 != null) {
				Iterator var6 = var4.iterator();

				while (var6.hasNext()) {
					XmlElement var7 = (XmlElement) var6.next();
					HashMap var8 = new HashMap();
					var8.put("id", var7.getAttribute("id"));
					var8.put("icon_url", var7.getAttribute("icon_url"));
					var8.put("name", var7.getAttribute("name"));
					var8.put("description", var7.getAttribute("description"));
					var8.put("like", var7.getAttribute("like"));
					var8.put("dislike", var7.getAttribute("dislike"));
					var8.put("user", var7.getAttribute("user"));
					var8.put("fans", var7.getAttribute("fans"));
					var8.put("experience", var7.getAttribute("experience"));
					var3.add(var8);
				}
			}

			var2.put("master_contains_list", var3);
		}

		return var2;
	}

	private static Object parseMasterRecommend(Context var0, XmlElement var1) {
		HashMap var7;
		if (var1 == null) {
			var7 = null;
		} else {
			HashMap var2 = new HashMap();
			var2.put("total_size", Integer.valueOf(Utils.getInt(var1
					.getAttribute("total_size"))));
			ArrayList var4 = new ArrayList();
			List var5 = var1.getChildren("recommend");
			if (var5 != null) {
				Iterator var8 = var5.iterator();

				while (var8.hasNext()) {
					XmlElement var9 = (XmlElement) var8.next();
					HashMap var10 = new HashMap();
					var10.put("id", var9.getAttribute("id"));
					var10.put("icon_url", var9.getAttribute("icon_url"));
					var10.put("name", var9.getAttribute("name"));
					var10.put("description", var9.getAttribute("description"));
					Object[] var15 = new Object[] { DateUtils
							.getRelativeTimeSpanString(Utils.getLong(var9
									.getAttribute("update_time")), System
									.currentTimeMillis(), 60000L, 262144) };
					var10.put("update_time", var0.getString(
							R.string.recommendation_update_time, var15));
					var10.put("like", var9.getAttribute("like"));
					var10.put("dislike", var9.getAttribute("dislike"));
					var10.put("user", var9.getAttribute("user"));
					var10.put("fans", var9.getAttribute("fans"));
					var10.put("experience", var9.getAttribute("experience"));
					var4.add(var10);
				}
			}

			var2.put("recommend_list", var4);
			var7 = var2;
		}

		return var7;
	}

	private static Object parseMyRating(XmlElement var0) {
		String var2;
		if (var0 == null) {
			var2 = null;
		} else {
			XmlElement var1 = var0.getChild("rating", 0);
			if (var1 != null) {
				var2 = var1.getAttribute("value");
			} else {
				var2 = null;
			}
		}

		return var2;
	}

	private static SplashInfo parseNewSplash(XmlElement var0) {
		SplashInfo var1;
		if (var0 == null) {
			var1 = null;
		} else {
			var1 = new SplashInfo();
			XmlElement var2 = var0.getChild("url", 0);
			if (var2 != null) {
				var1.url = var2.getText();
			}

			XmlElement var3 = var0.getChild("time", 0);
			if (var3 != null) {
				var1.timestamp = Utils.getLong(var3.getText());
			}
		}

		return var1;
	}

	private static boolean parseNotificationRecommend(Context var0,
			XmlElement var1) {
		boolean var5;
		if (var1 == null) {
			var5 = false;
		} else {
			List var2 = var1.getChildren("product");
			if (var2 != null) {
				ArrayList var3 = new ArrayList();
				Iterator var4 = var2.iterator();

				while (var4.hasNext()) {
					XmlElement var6 = (XmlElement) var4.next();
					HashMap var7 = new HashMap();
					var7.put("id", var6.getAttribute("id"));
					var7.put("title", var6.getAttribute("title"));
					var7.put("description", var6.getAttribute("description"));
					var7.put("update_time", var6.getAttribute("update_time"));
					var7.put("nid", var6.getAttribute("nid"));
					var7.put("rule", var6.getAttribute("rule"));
					var3.add(var7);
				}

				DBUtils.insertPushItems(var0, var3);
			}

			var5 = true;
		}

		return var5;
	}

	private static Object parseProductDetail(XmlElement var0) {
		ProductDetail var5;
		if (var0 == null) {
			var5 = null;
		} else {
			XmlElement var1 = var0.getChild("product", 0);
			if (var1 != null) {
				ProductDetail var2 = new ProductDetail();
				var2.setPid(var1.getAttribute("p_id"));
				var2.setProductType(var1.getAttribute("product_type"));
				var2.setName(var1.getAttribute("name"));
				var2.setPrice(Utils.getInt(var1.getAttribute("price")));
				var2.setPayCategory(Utils.getInt(var1
						.getAttribute("pay_category")));
				var2.setRating((float) Utils.getInt(var1.getAttribute("rating")) / 10.0F);
				var2.setIconUrl(var1.getAttribute("icon_url"));
				var2.setShotDes(var1.getAttribute("short_description"));
				var2.setAppSize(Utils.getInt(var1.getAttribute("app_size")));
				var2.setSourceType(var1.getAttribute("source_type"));
				var2.setPackageName(var1.getAttribute("packagename"));
				var2.setVersionName(var1.getAttribute("version_name"));
				var2.setVersionCode(Utils.getInt(var1
						.getAttribute("version_code")));
				var2.setCommentsCount(Utils.getInt(var1
						.getAttribute("comments_count")));
				var2.setRatingCount(Utils.getInt(var1
						.getAttribute("ratings_count")));
				var2.setDownloadCount(Utils.getInt(var1
						.getAttribute("download_count")));
				var2.setLongDescription(var1.getAttribute("long_description"));
				var2.setAuthorName(var1.getAttribute("author_name"));
				var2.setPublishTime((long) Utils.getInt(var1
						.getAttribute("publish_time")));
				String[] var3 = new String[] {
						var1.getAttribute("screenshot_1"),
						var1.getAttribute("screenshot_2"),
						var1.getAttribute("screenshot_3"),
						var1.getAttribute("screenshot_4"),
						var1.getAttribute("screenshot_5") };
				var2.setScreenshot(var3);
				String[] var4 = new String[] {
						var1.getAttribute("screenshot_1"),
						var1.getAttribute("screenshot_2"),
						var1.getAttribute("screenshot_3"),
						var1.getAttribute("screenshot_4"),
						var1.getAttribute("screenshot_5") };
				var2.setScreenshotLdpi(var4);
				var2.setUpReason(var1.getAttribute("up_reason"));
				var2.setUpTime(Utils.getLong(var1.getAttribute("up_time")));
				var2.setPermission(var1.getAttribute("uses_permission"));
				var2.setRsaMd5(var1.getAttribute("rsa_md5"));
				var5 = var2;
			} else {
				var5 = null;
			}
		}

		return var5;
	}

	private static HashMap<String, Object> parseProductList(Context var0,
			XmlElement var1, boolean var2) {
		HashMap var4;
		if (var1 == null) {
			var4 = null;
		} else {
			XmlElement var3 = var1.getChild("products", 0);
			if (var3 != null) {
				ArrayList var5 = Session.get(var0).getInstalledApps();
				List var6 = var3.getChildren("product");
				if (var6 == null) {
					var4 = null;
				} else {
					HashMap var7 = new HashMap();
					var7.put("total_size", Integer.valueOf(Utils.getInt(var3
							.getAttribute("total_size"))));
					var7.put("end_position", Integer.valueOf(Utils.getInt(var3
							.getAttribute("end_position"))));
					ArrayList var10 = new ArrayList();
					Iterator var11 = var6.iterator();

					while (var11.hasNext()) {
						XmlElement var13 = (XmlElement) var11.next();
						HashMap var14 = new HashMap();
						var14.put("p_id", var13.getAttribute("p_id"));
						String var16 = var13.getAttribute("packagename");
						var14.put("packagename", var16);
						int var18 = Utils.getInt(var13.getAttribute("price"));
						String var20;
						if (var18 == 0) {
							var20 = var0.getString(R.string.free);
						} else {
							Object[] var19 = new Object[] { Integer
									.valueOf(var18) };
							var20 = var0.getString(R.string.duihuanquan_unit,
									var19);
						}

						var14.put("price", var20);
						boolean var22;
						if ("1".equals(var13.getAttribute("is_star"))) {
							var22 = true;
						} else {
							var22 = false;
						}

						if (var2) {
							var14.put("is_star", Boolean.valueOf(false));
						} else {
							var14.put("is_star", Boolean.valueOf(var22));
						}

						if (var5.contains(var16)) {
							if (var2 && !var22) {
								continue;
							}

							var14.put("product_download", Integer.valueOf(11));
						} else {
							var14.put("product_download", Integer.valueOf(0));
						}

						var14.put("name", var13.getAttribute("name"));
						var14.put("author_name",
								var13.getAttribute("author_name"));
						var14.put("sub_category",
								var13.getAttribute("product_type") + " > "
										+ var13.getAttribute("sub_category"));
						var14.put("pay_category", Integer.valueOf(Utils
								.getInt(var13.getAttribute("pay_category"))));
						var14.put("rating", Float.valueOf((float) Utils
								.getInt(var13.getAttribute("rating")) / 10.0F));
						var14.put("app_size", StringUtils.formatSize(var13
								.getAttribute("app_size")));
						var14.put("icon_url", var13.getAttribute("icon_url"));
						var14.put("short_description",
								var13.getAttribute("short_description"));
						if ("1".equals(var13.getAttribute("source_type"))) {
							var14.put("source_type",
									var0.getString(R.string.leble_google));
						}

						var10.add(var14);
					}

					var7.put("product_list", var10);
					var4 = var7;
				}
			} else {
				var4 = null;
			}
		}

		return var4;
	}

	private static void parseProductListResult(Context var0, XmlElement var1,
			HashMap<String, Object> var2) {
		List var3 = var1.getChildren("product");
		if (var3 != null) {
			ArrayList var4 = Session.get(var0).getInstalledApps();
			ArrayList var5 = new ArrayList();

			HashMap var9;
			for (Iterator var6 = var3.iterator(); var6.hasNext(); var5
					.add(var9)) {
				XmlElement var8 = (XmlElement) var6.next();
				var9 = new HashMap();
				var9.put("p_id", var8.getAttribute("p_id"));
				String var11 = var8.getAttribute("packagename");
				var9.put("packagename", var11);
				int var13 = Utils.getInt(var8.getAttribute("price"));
				String var15;
				if (var13 == 0) {
					var15 = var0.getString(R.string.free);
				} else {
					Object[] var14 = new Object[] { Integer.valueOf(var13) };
					var15 = var0.getString(R.string.duihuanquan_unit, var14);
				}

				var9.put("price", var15);
				if (var4.contains(var11)) {
					var9.put("product_download", Integer.valueOf(11));
				} else {
					var9.put("product_download", Integer.valueOf(0));
				}

				var9.put("name", var8.getAttribute("name"));
				var9.put("author_name", var8.getAttribute("author_name"));
				var9.put("sub_category", var8.getAttribute("product_type")
						+ " > " + var8.getAttribute("sub_category"));
				var9.put("pay_category", Integer.valueOf(Utils.getInt(var8
						.getAttribute("pay_category"))));
				var9.put("rating", Float.valueOf((float) Utils.getInt(var8
						.getAttribute("rating")) / 10.0F));
				var9.put("app_size",
						StringUtils.formatSize(var8.getAttribute("app_size")));
				var9.put("icon_url", var8.getAttribute("icon_url"));
				var9.put("short_description",
						var8.getAttribute("short_description"));
				if ("1".equals(var8.getAttribute("source_type"))) {
					var9.put("source_type",
							var0.getString(R.string.leble_google));
				}
			}

			var2.put("product_list", var5);
		}

	}

	private static int parseQueryChargeResultByOderID(XmlElement var0) {
		int var2;
		if (var0 == null) {
			var2 = 0;
		} else {
			XmlElement var1 = var0.getChild("pay_result", 0);
			if (var1 != null) {
				var2 = Utils.getInt(var1.getAttribute("status"));
			} else {
				var2 = 0;
			}
		}

		return var2;
	}

	private static String parseRecommendRating(XmlElement var0) {
		String var2;
		if (var0 == null) {
			var2 = null;
		} else {
			XmlElement var1 = var0.getChild("rating", 0);
			if (var1 != null) {
				var2 = var1.getAttribute("value");
			} else {
				var2 = null;
			}
		}

		return var2;
	}

	private static ArrayList<String> parseSearchKeywords(XmlElement var0) {
		ArrayList var4;
		if (var0 == null) {
			var4 = null;
		} else {
			XmlElement var1 = var0.getChild("keys", 0);
			if (var1 != null) {
				ArrayList var2 = new ArrayList();
				Iterator var3 = var1.getAllChildren().iterator();

				while (var3.hasNext()) {
					var2.add(((XmlElement) var3.next()).getAttribute("text"));
				}

				var4 = var2;
			} else {
				var4 = null;
			}
		}

		return var4;
	}

	private static boolean parseSubmitLog(String var0) throws JSONException {
		boolean var1;
		if ("success".equalsIgnoreCase((new JSONObject(var0))
				.getString("result"))) {
			var1 = true;
		} else {
			var1 = false;
		}

		return var1;
	}

	private static Object parseSyncApps(XmlElement var0) {
		UpdateInfo var1;
		if (var0 == null) {
			var1 = null;
		} else {
			var1 = new UpdateInfo();
			var1.setUpdageLevel(Integer.valueOf(
					var0.getChild("update_level", 0).getText()).intValue());
			var1.setVersionCode(Integer.valueOf(
					var0.getChild("version_code", 0).getText()).intValue());
			var1.setVersionName(var0.getChild("version_name", 0).getText());
			var1.setDescription(var0.getChild("description", 0).getText());
			var1.setApkUrl(var0.getChild("apk_url", 0).getText());
		}

		return var1;
	}

	private static Object parseSyncBuyLog(XmlElement var0) {
		int var1 = 0;
		ArrayList var3;
		if (var0 == null) {
			var3 = null;
		} else {
			XmlElement var2 = var0.getChild("products", 0);
			if (var2 == null) {
				var3 = null;
			} else {
				List var4 = var2.getChildren("product");
				if (var4 == null) {
					var3 = null;
				} else {
					ArrayList var5 = new ArrayList();

					for (int var6 = var4.size(); var1 < var6; ++var1) {
						XmlElement var7 = var2.getChild("product", var1);
						BuyLog var8 = new BuyLog();
						var8.pId = var7.getAttribute("p_id");
						var8.packageName = var7.getAttribute("package_name");
						var5.add(var8);
					}

					var3 = var5;
				}
			}
		}

		return var3;
	}

	private static CardsVerifications parseSyncCardinfo(Context var0,
			XmlElement var1) {
		CardsVerifications var2;
		if (var1 == null) {
			var2 = null;
		} else {
			var2 = new CardsVerifications();
			var2.version = Utils.getInt(var1.getAttribute("remote_version"));
			Iterator var3 = var1.getChildren("card").iterator();

			while (var3.hasNext()) {
				XmlElement var4 = (XmlElement) var3.next();
				CardsVerification var5 = new CardsVerification();
				var5.name = var4.getAttribute("name");
				var5.pay_type = var4.getAttribute("pay_type");
				var5.accountNum = Utils
						.getInt(var4.getAttribute("account_len"));
				var5.passwordNum = Utils.getInt(var4
						.getAttribute("password_len"));
				var5.credit = var4.getAttribute("credit");
				var2.cards.add(var5);
			}
		}

		return var2;
	}

	private static ArrayList<HashMap<String, Object>> parseTopRecommend(
			XmlElement var0) {
		ArrayList var4;
		if (var0 == null) {
			var4 = null;
		} else {
			List var1 = var0.getAllChildren();
			if (var1 != null) {
				ArrayList var2 = new ArrayList();
				Iterator var3 = var1.iterator();

				while (var3.hasNext()) {
					XmlElement var5 = (XmlElement) var3.next();
					HashMap var6 = new HashMap();
					if ("category".equals(var5.getName())) {
						var6.put("top_recommend_type", "category");
					} else if ("topic".equals(var5.getName())) {
						var6.put("top_recommend_type", "topic");
					} else if ("product".equals(var5.getName())) {
						var6.put("top_recommend_type", "product");
					} else {
						var6.put("top_recommend_type", Integer.valueOf(-1));
					}

					var6.put("id", var5.getAttribute("id"));
					var6.put("pic", var5.getAttribute("pic"));
					var6.put("reason", var5.getAttribute("reason"));
					var2.add(var6);
				}

				var4 = var2;
			} else {
				var4 = null;
			}
		}

		return var4;
	}

	private static ArrayList<HashMap<String, Object>> parseTopicList(
			Context var0, XmlElement var1) {
		ArrayList var3;
		if (var1 == null) {
			var3 = null;
		} else {
			XmlElement var2 = var1.getChild("topics", 0);
			if (var2 != null) {
				List var4 = var2.getChildren("topic");
				ArrayList var5 = new ArrayList();
				Iterator var6 = var4.iterator();

				while (var6.hasNext()) {
					XmlElement var7 = (XmlElement) var6.next();
					HashMap var8 = new HashMap();
					String var9 = var7.getAttribute("id");
					if (!"5".equals(var9)) {
						var8.put("id", var9);
						var8.put("category_name", var7.getAttribute("name"));
						var8.put("icon_url",
								var7.getAttribute("ldpi_app_icon_url"));
						String var13 = var7.getAttribute("app_1");
						String var14 = var7.getAttribute("app_2");
						String var15 = var7.getAttribute("app_3");
						String var16 = var13 + ", ";
						if (!TextUtils.isEmpty(var14)) {
							var16 = var16 + var14 + ", ";
						}

						if (!TextUtils.isEmpty(var15)) {
							var16 = var16 + var15 + ", ";
						}

						if (var16.length() > 1) {
							var16 = var16.substring(0, var16.length() - 2);
						}

						var8.put("top_app", var16);
						var8.put("app_count", var7.getAttribute("app_count"));
						var5.add(var8);
					}
				}

				var3 = var5;
			} else {
				var3 = null;
			}
		}

		return var3;
	}

	private static String parseUpgrade(Context context, XmlElement xmlEle) {
		String var3;
		if (xmlEle == null) {
			var3 = "";
		} else {
			XmlElement var2 = xmlEle.getChild("products", 0);
			if (var2 != null) {
				List var4 = var2.getChildren("product");
				if (var4 == null) {
					var3 = "";
				} else {
					ArrayList var5 = new ArrayList();
					Iterator var6 = var4.iterator();

					while (var6.hasNext()) {
						XmlElement var7 = (XmlElement) var6.next();
						UpgradeInfo var8 = new UpgradeInfo();
						var8.pid = var7.getAttribute("p_id");
						var8.pkgName = var7.getAttribute("packagename");
						var8.versionName = var7.getAttribute("version_name");
						var8.versionCode = Utils.getInt(var7
								.getAttribute("version_code"));
						var8.signature = var7.getAttribute("rsa_md5");
						var8.update = 0;
						var5.add(var8);
					}

					var3 = String.valueOf(DBUtils.addUpdateProduct(context, var5));
				}
			} else {
				var3 = "";
			}
		}

		return var3;
	}

	private static JSONObject parseUniJson(String strJson) {
		JSONObject obj = null;
		try {
			obj = new JSONObject(strJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			obj = null;
			e.printStackTrace();
		}
		return obj;
	}
}
