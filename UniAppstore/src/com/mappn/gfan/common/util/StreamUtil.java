package com.mappn.gfan.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StreamUtil {

   StreamUtil.Processhandler handler;
   private long length;
   private MessageDigest md;
   private byte[] md5;
   private boolean withMd5;


   public StreamUtil(boolean var1) {
      this.length = 0L;
      this.md = null;
      this.md5 = null;
      this.withMd5 = false;
      this.handler = null;
      this.length = 0L;
      this.withMd5 = var1;
      if(var1) {
         try {
            this.md = MessageDigest.getInstance("MD5");
         } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
         }
      }

   }

   public StreamUtil(boolean var1, StreamUtil.Processhandler var2) {
      this(var1);
      this.handler = var2;
   }

   public static long copyStream(InputStream var0, OutputStream var1) throws IOException {
      StreamUtil var2 = new StreamUtil(false);
      var2.copyStreamInner(var0, var1);
      return var2.length;
   }

   public void copyStreamInner(InputStream var1, OutputStream var2) throws IOException {
      byte[] var3 = new byte[4096];

      int var4;
      do {
         var4 = var1.read(var3);
         if(var4 < 0) {
            break;
         }

         if(var2 != null) {
            var2.write(var3, 0, var4);
            var2.flush();
         }

         this.length += (long)var4;
         if(this.withMd5) {
            this.md.update(var3, 0, var4);
         }
      } while(this.handler == null || this.handler.onProcess(this.length, var3, 0, var4));

   }

   public long getLength() {
      return this.length;
   }

   public byte[] getMD5() {
      byte[] var1;
      if(!this.withMd5) {
         var1 = null;
      } else {
         if(this.md5 == null) {
            this.md5 = this.md.digest();
         }

         var1 = this.md5;
      }

      return var1;
   }

   public interface Processhandler {

      boolean onProcess(long var1, byte[] var3, int var4, int var5);
   }
}
