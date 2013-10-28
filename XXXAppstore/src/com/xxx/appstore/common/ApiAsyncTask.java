package com.xxx.appstore.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.text.TextUtils;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.AndroidHttpClient;
import com.xxx.appstore.common.HttpClientFactory;
import com.xxx.appstore.common.ResponseCacheManager;
import com.xxx.appstore.common.util.Utils;

public class ApiAsyncTask extends AsyncTask<Void, Void, Object> {

	// 网络错误相关状态码
	public static final int BUSSINESS_ERROR = 610;
	public static final int SC_DATA_NOT_EXIST = 225;
	public static final int SC_ENCODE_ERROR = 427;
	public static final int SC_ILLEGAL_COMMENT = 232;
	public static final int SC_ILLEGAL_USER_AGENT = 421;
	public static final int SC_SERVER_DB_ERROR = 520;
	public static final int SC_XML_ERROR = 422;
	public static final int SC_XML_PARAMS_ERROR = 423;
	public static final int TIMEOUT_ERROR = 600;
	
	private AndroidHttpClient mClient;
	private Context mContext;
	private ApiAsyncTask.ApiRequestListener mHandler;
	private Object mParameter;
	private ResponseCacheManager mResponseCache;
	private int mRequestAction;
	private Session mSession;

	public ApiAsyncTask(Context context, int requestAction,
			ApiAsyncTask.ApiRequestListener requestListener, Object parameter) {
		this.mContext = context;
		this.mSession = Session.get(context);
		this.mRequestAction = requestAction;
		this.mHandler = requestListener;
		this.mParameter = parameter;
		this.mResponseCache = ResponseCacheManager.getInstance(); // 缓存
		this.mClient = HttpClientFactory.get().getHttpClient();
	}

	private boolean handleCommonError(int var1) {
		boolean var2;
		if (var1 == 200) {
			var2 = true;
		} else {
			var2 = false;
		}

		return var2;
	}

	protected Object doInBackground(Void... avoid) {
		Object objResponse = null;
		if (!Utils.isNetworkAvailable(mContext)) {
			objResponse = Integer.valueOf(TIMEOUT_ERROR);
			return objResponse;
		}

		String url = null;
		HttpEntity httpentity = null;
		try {
			if (mRequestAction >= MarketAPI.ACTION_STARTD) { // uni action
				ArrayList arr = ApiRequestFactory.getUrlAndEntityEX(
						mRequestAction, mParameter);
				url = (String) arr.get(0);
				httpentity = (HttpEntity) arr.get(1);
			} else { // jfan action
				url = MarketAPI.API_URLS[mRequestAction];
				// 生成request body data (xml格式)
				httpentity = ApiRequestFactory.getRequestEntity(mContext,
						mRequestAction, mParameter);
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			httpentity = null;
			e1.printStackTrace();
			return null;
		}
		
		String s1 = "";
		// 如果该请求需要缓存
		if (false && ApiRequestFactory.API_CACHE_MAP.contains(Integer
				.valueOf(mRequestAction))) {
//			mResponseCache.putResponse(mContext, mRequestAction, s1, objResponse);
//			if (null == httpentity) {
//				s1 = Utils.getMD5(url);
//			} else {
//				if ((httpentity instanceof StringEntity)) {
//					try {
//						s1 = Utils.getMD5((new StringBuilder()).append(url)
//								.append(EntityUtils.toString(httpentity))
//								.toString());
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//
//			Object obj1 = mResponseCache.getResponse(mContext, s1);
//			if (obj1 == null) {
//				HttpUriRequest httpurirequest = null;
//				try {
//					httpurirequest = ApiRequestFactory.getRequest(url,
//							mRequestAction, httpentity, mSession);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				HttpResponse httpresponse1 = null;
//				try {
//					httpresponse1 = mClient.execute(httpurirequest);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Integer integer3;
//				int i = httpresponse1.getStatusLine().getStatusCode();
//				Utils.D((new StringBuilder()).append("requestUrl ").append(url)
//						.append(" statusCode: ").append(i).toString());
//				if (200 != i) {
//					integer3 = Integer.valueOf(i);
//					objResponse = integer3;
//					if (httpurirequest != null)
//						httpurirequest.abort();
//					if (httpresponse1 != null)
//						try {
//							HttpEntity httpentity5 = httpresponse1.getEntity();
//							if (httpentity5 != null)
//								httpentity5.consumeContent();
//						} catch (IOException ioexception5) {
//							Utils.D("release low-level resource error");
//						}
//				} else {
//					objResponse = ApiResponseFactory.getResponse(mContext,
//							mRequestAction, httpresponse1);
//					if (objResponse == null) {
//						objResponse = Integer.valueOf(BUSSINESS_ERROR);
//						if (httpurirequest != null)
//							httpurirequest.abort();
//						if (httpresponse1 != null) {
//							try {
//								HttpEntity httpentity4 = httpresponse1
//										.getEntity();
//								if (httpentity4 != null)
//									httpentity4.consumeContent();
//							} catch (IOException ioexception4) {
//								Utils.D("release low-level resource error");
//							}
//						}
//					} else {
//
//					}
//				}
//			} else {
//				Utils.V("retrieve response from the cache");
//				objResponse = obj1;
//			}
		} else { // 不缓存
			HttpUriRequest httpurirequest = null;
			try {
				// 组包
				httpurirequest = ApiRequestFactory.getRequest(url,
						mRequestAction, httpentity, mSession);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;	// 组包失败
			}

			HttpResponse httpresponse1 = null;
			try {
				httpresponse1 = mClient.execute(httpurirequest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			int statusCode = httpresponse1.getStatusLine().getStatusCode();
			Utils.D((new StringBuilder()).append("requestUrl ").append(url)
					.append(" statusCode: ").append(statusCode).toString());
			if (200 != statusCode) {// 请求错误
				objResponse = Integer.valueOf(statusCode);	// 将返回结果赋值为状态码
				if (httpurirequest != null)
					httpurirequest.abort();
				if (httpresponse1 != null)
					try {
						HttpEntity httpentity5 = httpresponse1.getEntity();
						if (httpentity5 != null)
							httpentity5.consumeContent();
					} catch (IOException ioexception5) {
						Utils.D("release low-level resource error");
					}
			} else {	// 请求成功
				objResponse = ApiResponseFactory.getResponse(mContext, mRequestAction,httpresponse1);
				if (objResponse == null) {	// 服务器返回数据为空或者数据解析失败（比如数据格式不正确）
					objResponse = Integer.valueOf(BUSSINESS_ERROR); // 返回结果赋值为整形
					if (httpurirequest != null)
						httpurirequest.abort();
					if (httpresponse1 != null) {
						try {
							HttpEntity httpentity4 = httpresponse1.getEntity();
							if (httpentity4 != null)
								httpentity4.consumeContent();
						} catch (IOException ioexception4) {
							Utils.D("release low-level resource error");
						}
					}
				}
			}
		}
		return objResponse;
	}

	protected void onPostExecute(Object obj) {
		if (this.mHandler != null
				&& (!(this.mContext instanceof Activity) || !((Activity) this.mContext)
						.isFinishing())) {
			if (obj == null) {
				this.mHandler.onError(this.mRequestAction, BUSSINESS_ERROR);
			} else if (obj instanceof Integer && !this.handleCommonError(((Integer) obj).intValue())) {
				// http 返回错误的状态码
				this.mHandler.onError(this.mRequestAction,
						((Integer) obj).intValue());
			} else {
				this.mHandler.onSuccess(this.mRequestAction, obj);
			}
		}
	}

	public interface ApiRequestListener {

		void onError(int var1, int var2);

		void onSuccess(int var1, Object var2);
	}
}
