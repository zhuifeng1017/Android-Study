package com.mappn.gfan.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

// Referenced classes of package com.mappn.gfan.common.util:
//            BaseHelper

public class NetworkManager
{
	static final String TAG = "NetworkManager";
    private int connectTimeout;
    Context mContext;
    java.net.Proxy mProxy;
    private int readTimeout;
    
    public NetworkManager(Context context)
    {
        connectTimeout = 30000;
        readTimeout = 30000;
        mProxy = null;
        mContext = context;
        setDefaultHostnameVerifier();
    }

    private void setDefaultHostnameVerifier()
    {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
        	public boolean verify(String paramString, SSLSession paramSSLSession)
        	  {
        	    return true;
        	  }
        });
    }

    public String SendAndWaitResponse(String s, String s1)
    {
    	return null;
//        ArrayList arraylist;
//        detectProxy();
//        arraylist = new ArrayList();
//        arraylist.add(new BasicNameValuePair("requestData", s));
//        UrlEncodedFormEntity urlencodedformentity;
//        URL url;
//        urlencodedformentity = new UrlEncodedFormEntity(arraylist, "utf-8");
//        url = new URL(s1);
//        if(mProxy == null) goto _L2; else goto _L1
//_L1:
//        HttpURLConnection httpurlconnection1 = (HttpURLConnection)url.openConnection(mProxy);
//_L3:
//        String s4;
//        httpurlconnection1.setConnectTimeout(connectTimeout);
//        httpurlconnection1.setReadTimeout(readTimeout);
//        httpurlconnection1.setDoOutput(true);
//        httpurlconnection1.addRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        httpurlconnection1.connect();
//        OutputStream outputstream = httpurlconnection1.getOutputStream();
//        urlencodedformentity.writeTo(outputstream);
//        outputstream.flush();
//        s4 = BaseHelper.convertStreamToString(httpurlconnection1.getInputStream());
//        BaseHelper.log("NetworkManager", (new StringBuilder()).append("response ").append(s4).toString());
//        String s3;
//        httpurlconnection1.disconnect();
//        s3 = s4;
//_L4:
//        return s3;
//_L2:
//        httpurlconnection1 = (HttpURLConnection)url.openConnection();
//          goto _L3
//        IOException ioexception;
//        ioexception;
//        HttpURLConnection httpurlconnection;
//        String s2;
//        httpurlconnection = null;
//        s2 = null;
//_L7:
//        ioexception.printStackTrace();
//        httpurlconnection.disconnect();
//        s3 = s2;
//          goto _L4
//        Exception exception;
//        exception;
//        httpurlconnection = null;
//_L6:
//        httpurlconnection.disconnect();
//        throw exception;
//        Exception exception1;
//        exception1;
//        httpurlconnection = httpurlconnection1;
//        exception = exception1;
//        continue; /* Loop/switch isn't completed */
//        exception;
//        if(true) goto _L6; else goto _L5
//_L5:
//        IOException ioexception1;
//        ioexception1;
//        s2 = null;
//        HttpURLConnection httpurlconnection2 = httpurlconnection1;
//        ioexception = ioexception1;
//        httpurlconnection = httpurlconnection2;
//          goto _L7
//        IOException ioexception2;
//        ioexception2;
//        s2 = s4;
//        httpurlconnection = httpurlconnection1;
//        ioexception = ioexception2;
//          goto _L7
    }

    public void detectProxy()
    {
        NetworkInfo networkinfo = ((ConnectivityManager)mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if(networkinfo != null && networkinfo.isAvailable() && networkinfo.getType() == 0)
        {
            String s = Proxy.getDefaultHost();
            int i = Proxy.getDefaultPort();
            if(s != null)
            {
                InetSocketAddress inetsocketaddress = new InetSocketAddress(s, i);
                mProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, inetsocketaddress);
            }
        }
    }

    public boolean urlDownloadToFile(Context context, String s, String s1)
    {
    	return true;
//        detectProxy();
//        URL url = new URL(s);
//        if(mProxy == null) goto _L2; else goto _L1
//_L1:
//        HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(mProxy);
//_L4:
//        InputStream inputstream;
//        FileOutputStream fileoutputstream;
//        httpurlconnection.setConnectTimeout(connectTimeout);
//        httpurlconnection.setReadTimeout(readTimeout);
//        httpurlconnection.setDoInput(true);
//        httpurlconnection.connect();
//        inputstream = httpurlconnection.getInputStream();
//        File file = new File(s1);
//        file.createNewFile();
//        fileoutputstream = new FileOutputStream(file);
//        byte abyte0[] = new byte[1024];
//        do
//        {
//            int i = inputstream.read(abyte0);
//            if(i <= 0)
//                break;
//            fileoutputstream.write(abyte0, 0, i);
//        } while(true);
//          goto _L3
//        IOException ioexception;
//        ioexception;
//        boolean flag;
//        ioexception.printStackTrace();
//        flag = false;
//_L5:
//        return flag;
//_L2:
//        httpurlconnection = (HttpURLConnection)url.openConnection();
//          goto _L4
//_L3:
//        fileoutputstream.close();
//        inputstream.close();
//        flag = true;
//          goto _L5
    }
}