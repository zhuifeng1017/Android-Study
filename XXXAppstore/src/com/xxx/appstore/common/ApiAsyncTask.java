package com.xxx.appstore.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

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

	public ApiAsyncTask(Context var1, int var2,
			ApiAsyncTask.ApiRequestListener var3, Object var4) {
		this.mContext = var1;
		this.mSession = Session.get(var1);
		this.mRequestAction = var2;
		this.mHandler = var3;
		this.mParameter = var4;
		this.mResponseCache = ResponseCacheManager.getInstance();
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
		Object obj = null;
		if (!Utils.isNetworkAvailable(mContext)) {
			obj = Integer.valueOf(TIMEOUT_ERROR);
			return obj;
		}

		String s = MarketAPI.API_URLS[mRequestAction];
		HttpEntity httpentity = null;
		try {
			httpentity = ApiRequestFactory.getRequestEntity(mContext,
					mRequestAction, mParameter);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String s1 = "";
		if (ApiRequestFactory.API_CACHE_MAP.contains(Integer
				.valueOf(mRequestAction))) {
			 mResponseCache.putResponse(mContext, mRequestAction, s1, obj);
			if (null == httpentity) {
				s1 = Utils.getMD5(s);
			} else {
				if ((httpentity instanceof StringEntity)) {
					try {
						s1 = Utils.getMD5((new StringBuilder()).append(s)
								.append(EntityUtils.toString(httpentity))
								.toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			Object obj1 = mResponseCache.getResponse(mContext, s1);
			if (obj1 == null) {
				HttpUriRequest httpurirequest = null;
				try {
					httpurirequest = ApiRequestFactory.getRequest(s,
							mRequestAction, httpentity, mSession);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HttpResponse httpresponse1 = null;
				try {
					httpresponse1 = mClient.execute(httpurirequest);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Integer integer3;
				int i = httpresponse1.getStatusLine().getStatusCode();
				Utils.D((new StringBuilder()).append("requestUrl ").append(s)
						.append(" statusCode: ").append(i).toString());
				if (200 != i) {
					integer3 = Integer.valueOf(i);
					obj = integer3;
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
				} else {
					obj = ApiResponseFactory.getResponse(mContext,
							mRequestAction, httpresponse1);
					if (obj == null) {
						obj = Integer.valueOf(BUSSINESS_ERROR);
						if (httpurirequest != null)
							httpurirequest.abort();
						if (httpresponse1 != null) {
							try {
								HttpEntity httpentity4 = httpresponse1
										.getEntity();
								if (httpentity4 != null)
									httpentity4.consumeContent();
							} catch (IOException ioexception4) {
								Utils.D("release low-level resource error");
							}
						}
					} else {
						
					}
				}
			} else {
				Utils.V("retrieve response from the cache");
				obj = obj1;
			}
		}
		else {
				HttpUriRequest httpurirequest = null;
				try {
					httpurirequest = ApiRequestFactory.getRequest(s,
							mRequestAction, httpentity, mSession);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HttpResponse httpresponse1 = null;
				try {
					httpresponse1 = mClient.execute(httpurirequest);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Integer integer3;
				int i = httpresponse1.getStatusLine().getStatusCode();
				Utils.D((new StringBuilder()).append("requestUrl ").append(s)
						.append(" statusCode: ").append(i).toString());
				if (200 != i) {
					integer3 = Integer.valueOf(i);
					obj = integer3;
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
				} else {
					obj = ApiResponseFactory.getResponse(mContext,
							mRequestAction, httpresponse1);
					if (obj == null) {
						obj = Integer.valueOf(BUSSINESS_ERROR);
						if (httpurirequest != null)
							httpurirequest.abort();
						if (httpresponse1 != null) {
							try {
								HttpEntity httpentity4 = httpresponse1
										.getEntity();
								if (httpentity4 != null)
									httpentity4.consumeContent();
							} catch (IOException ioexception4) {
								Utils.D("release low-level resource error");
							}
						}
					} else {
						
					}
				}
			}
		return obj;
	}

	protected void onPostExecute(Object var1) {
		if (this.mHandler != null
				&& (!(this.mContext instanceof Activity) || !((Activity) this.mContext)
						.isFinishing())) {
			if (var1 == null) {
				this.mHandler.onError(this.mRequestAction, BUSSINESS_ERROR);
			} else if (var1 instanceof Integer
					&& !this.handleCommonError(((Integer) var1).intValue())) {
				this.mHandler.onError(this.mRequestAction,
						((Integer) var1).intValue());
			} else {
				this.mHandler.onSuccess(this.mRequestAction, var1);
			}
		}

	}

	public interface ApiRequestListener {

		void onError(int var1, int var2);

		void onSuccess(int var1, Object var2);
	}
}
