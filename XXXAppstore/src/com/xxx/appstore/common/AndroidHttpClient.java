package com.xxx.appstore.common;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

public final class AndroidHttpClient implements HttpClient {

   public static long DEFAULT_SYNC_MIN_GZIP_BYTES = 256L;
   private static final String TAG = "AndroidHttpClient";
   private static final HttpRequestInterceptor sThreadCheckInterceptor = new HttpRequestInterceptor() {
      public void process(HttpRequest var1, HttpContext var2) {
         if(Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("This thread forbids HTTP requests");
         }
      }
   };
   private volatile AndroidHttpClient.LoggingConfiguration curlConfiguration;
   private final DefaultHttpClient delegate;
   private boolean mIsLoadCookies = false;
   private RuntimeException mLeakedException = new IllegalStateException("AndroidHttpClient created and never closed");


   private AndroidHttpClient(final ClientConnectionManager var1, final HttpParams var2) {
      this.delegate = new DefaultHttpClient(var1, var2) {
         protected HttpContext createHttpContext() {
            BasicHttpContext var1 = new BasicHttpContext();
            var1.setAttribute("http.authscheme-registry", this.getAuthSchemes());
            var1.setAttribute("http.cookiespec-registry", this.getCookieSpecs());
            var1.setAttribute("http.auth.credentials-provider", this.getCredentialsProvider());
            var1.setAttribute("http.cookie-store", this.getCookieStore());
            return var1;
         }
         protected BasicHttpProcessor createHttpProcessor() {
            BasicHttpProcessor var1 = super.createHttpProcessor();
            var1.addRequestInterceptor(AndroidHttpClient.sThreadCheckInterceptor);
            var1.addRequestInterceptor(AndroidHttpClient.this.new CurlLogger(null));
            return var1;
         }
      };
   }

   public static AbstractHttpEntity getCompressedEntity(InputStream var0) throws IOException {
      byte[] var1 = new byte[4096];
      int var2 = var0.read(var1);
      ByteArrayEntity var5;
      if((long)var2 < getMinGzipSize()) {
         byte[] var6 = new byte[var2];
         System.arraycopy(var1, 0, var6, 0, var2);
         var5 = new ByteArrayEntity(var6);
         var0.close();
      } else {
         ByteArrayOutputStream var3 = new ByteArrayOutputStream();
         GZIPOutputStream var4 = new GZIPOutputStream(var3);

         do {
            var4.write(var1, 0, var2);
            var2 = var0.read(var1);
         } while(var2 != -1);

         var0.close();
         var4.close();
         var5 = new ByteArrayEntity(var3.toByteArray());
         var5.setContentEncoding("gzip");
      }

      return var5;
   }

   public static AbstractHttpEntity getCompressedEntity(byte[] var0) throws IOException {
      ByteArrayEntity var1;
      if((long)var0.length < getMinGzipSize()) {
         var1 = new ByteArrayEntity(var0);
      } else {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         GZIPOutputStream var3 = new GZIPOutputStream(var2);
         var3.write(var0);
         var3.close();
         ByteArrayEntity var4 = new ByteArrayEntity(var2.toByteArray());
         var4.setContentEncoding("gzip");
         var1 = var4;
      }

      return var1;
   }

   public static long getMinGzipSize() {
      return DEFAULT_SYNC_MIN_GZIP_BYTES;
   }

   public static InputStream getUngzippedContent(HttpEntity var0) throws IOException {
      Object var1 = var0.getContent();
      if(var1 != null) {
         Header var2 = var0.getContentEncoding();
         if(var2 != null) {
            String var3 = var2.getValue();
            if(var3 != null && var3.contains("gzip")) {
               var1 = new GZIPInputStream((InputStream)var1);
            }
         }
      }

      return (InputStream)var1;
   }

   public static void modifyRequestContentType(HttpRequest var0, String var1) {
      var0.addHeader("Content-Type", var1);
   }

   public static void modifyRequestToAcceptGzipResponse(HttpRequest var0) {
      var0.addHeader("Accept-Encoding", "gzip");
   }

   public static AndroidHttpClient newInstance(String var0) {
      return newInstance(var0, (Context)null);
   }

   public static AndroidHttpClient newInstance(String var0, Context var1) {
      BasicHttpParams var2 = new BasicHttpParams();
      HttpConnectionParams.setStaleCheckingEnabled(var2, false);
      HttpConnectionParams.setConnectionTimeout(var2, 20000);
      HttpConnectionParams.setSoTimeout(var2, 20000);
      HttpConnectionParams.setSocketBufferSize(var2, 8192);
      ConnManagerParams.setMaxTotalConnections(var2, 60);
      ConnPerRouteBean var3 = new ConnPerRouteBean(20);
      var3.setMaxForRoute(new HttpRoute(new HttpHost("locahost", 80)), 20);
      ConnManagerParams.setMaxConnectionsPerRoute(var2, var3);
      HttpClientParams.setRedirecting(var2, false);
      HttpProtocolParams.setUserAgent(var2, var0);
      SchemeRegistry var4 = new SchemeRegistry();
      var4.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
      var4.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
      return new AndroidHttpClient(new ThreadSafeClientConnManager(var2, var4), var2);
   }

   private static String toCurl(HttpUriRequest var0, boolean var1) throws IOException {
      StringBuilder var2 = new StringBuilder();
      var2.append("curl ");
      Header[] var4 = var0.getAllHeaders();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Header var18 = var4[var6];
         if(var1 || !var18.getName().equals("Authorization") && !var18.getName().equals("Cookie")) {
            var2.append("--header \"");
            var2.append(var18.toString().trim());
            var2.append("\" ");
         }
      }

      URI var8;
      label30: {
         URI var7 = var0.getURI();
         if(var0 instanceof RequestWrapper) {
            HttpRequest var17 = ((RequestWrapper)var0).getOriginal();
            if(var17 instanceof HttpUriRequest) {
               var8 = ((HttpUriRequest)var17).getURI();
               break label30;
            }
         }

         var8 = var7;
      }

      var2.append("\"");
      var2.append(var8);
      var2.append("\"");
      if(var0 instanceof HttpEntityEnclosingRequest) {
         HttpEntity var12 = ((HttpEntityEnclosingRequest)var0).getEntity();
         if(var12 != null && var12.isRepeatable()) {
            if(var12.getContentLength() < 1024L) {
               ByteArrayOutputStream var13 = new ByteArrayOutputStream();
               var12.writeTo(var13);
               String var14 = var13.toString();
               var2.append(" --data-ascii \"").append(var14).append("\"");
            } else {
               var2.append(" [TOO MUCH DATA TO INCLUDE]");
            }
         }
      }

      return var2.toString();
   }

   public void addRequestInterceptor(HttpRequestInterceptor var1) {
      if(var1 != null) {
         this.delegate.addRequestInterceptor(var1, this.delegate.getRequestInterceptorCount());
      }

   }

   public void addResponseInterceptor(HttpResponseInterceptor var1) {
      if(var1 != null) {
         this.delegate.addResponseInterceptor(var1, this.delegate.getResponseInterceptorCount());
      }

   }

   public void close() {
      if(this.mLeakedException != null) {
         this.getConnectionManager().shutdown();
         this.mLeakedException = null;
      }

   }

   public void disableCurlLogging() {
      this.curlConfiguration = null;
   }

   public void enableCurlLogging(String var1, int var2) {
      if(var1 == null) {
         throw new NullPointerException("name");
      } else if(var2 >= 2 && var2 <= 7) {
         this.curlConfiguration = new AndroidHttpClient.LoggingConfiguration(var1, var2, null);
      } else {
         throw new IllegalArgumentException("Level is out of range [2..7]");
      }
   }

   public <T extends Object> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3) throws IOException, ClientProtocolException {
      return this.delegate.execute(var1, var2, var3);
   }

   public <T extends Object> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3, HttpContext var4) throws IOException, ClientProtocolException {
      return this.delegate.execute(var1, var2, var3, var4);
   }

   public <T extends Object> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2) throws IOException, ClientProtocolException {
      return this.delegate.execute(var1, var2);
   }

   public <T extends Object> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2, HttpContext var3) throws IOException, ClientProtocolException {
      return this.delegate.execute(var1, var2, var3);
   }

   public HttpResponse execute(HttpHost var1, HttpRequest var2) throws IOException {
      return this.delegate.execute(var1, var2);
   }

   public HttpResponse execute(HttpHost var1, HttpRequest var2, HttpContext var3) throws IOException {
      return this.delegate.execute(var1, var2, var3);
   }

   public HttpResponse execute(HttpUriRequest var1) throws IOException {
      return this.delegate.execute(var1);
   }

   public HttpResponse execute(HttpUriRequest var1, HttpContext var2) throws IOException {
      return this.delegate.execute(var1, var2);
   }

   protected void finalize() throws Throwable {
      super.finalize();
      if(this.mLeakedException != null) {
         Log.e("AndroidHttpClient", "Leak found", this.mLeakedException);
         this.mLeakedException = null;
      }

   }

   public ClientConnectionManager getConnectionManager() {
      return this.delegate.getConnectionManager();
   }

   public HttpParams getParams() {
      return this.delegate.getParams();
   }

   public boolean isLoadOwnCookies() {
      return this.mIsLoadCookies;
   }

   public void loadCookies(CookieStore var1) {
      this.mIsLoadCookies = true;
      this.delegate.setCookieStore(var1);
   }

   public void removeRequestInterceptor(HttpRequestInterceptor var1) {
      if(var1 != null) {
         this.delegate.removeRequestInterceptorByClass(var1.getClass());
      }

   }

   public void removeResponseInterceptor(HttpResponseInterceptor var1) {
      if(var1 != null) {
         this.delegate.removeResponseInterceptorByClass(var1.getClass());
      }

   }

   public void useDefaultConnection() {
      this.delegate.getParams().removeParameter("http.route.default-proxy");
   }

   public void useProxyConnection(HttpHost var1) {
      this.delegate.getParams().setParameter("http.route.default-proxy", var1);
   }

   private static class LoggingConfiguration {

      private final int level;
      private final String tag;


      private LoggingConfiguration(String var1, int var2) {
         this.tag = var1;
         this.level = var2;
      }

      // $FF: synthetic method
      LoggingConfiguration(String var1, int var2, Object var3) {
         this(var1, var2);
      }

      private boolean isLoggable() {
         return Log.isLoggable(this.tag, this.level);
      }

      private void println(String var1) {
         Log.println(this.level, this.tag, var1);
      }
   }

   private class CurlLogger implements HttpRequestInterceptor {

      private CurlLogger() {}

      // $FF: synthetic method
      CurlLogger(Object var2) {
         this();
      }

      public void process(HttpRequest var1, HttpContext var2) throws HttpException, IOException {
         AndroidHttpClient.LoggingConfiguration var3 = AndroidHttpClient.this.curlConfiguration;
         if(var3 != null && var3.isLoggable() && var1 instanceof HttpUriRequest) {
            var3.println(AndroidHttpClient.toCurl((HttpUriRequest)var1, false));
         }

      }
   }
}
