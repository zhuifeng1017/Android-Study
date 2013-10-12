package com.mappn.gfan.common.codec.binary;

import com.mappn.gfan.common.codec.binary.Base64;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64InputStream extends FilterInputStream {

   private final Base64 base64;
   private final boolean doEncode;
   private final byte[] singleByte;


   public Base64InputStream(InputStream var1) {
      this(var1, false);
   }

   public Base64InputStream(InputStream var1, boolean var2) {
      super(var1);
      this.singleByte = new byte[1];
      this.doEncode = var2;
      this.base64 = new Base64();
   }

   public Base64InputStream(InputStream var1, boolean var2, int var3, byte[] var4) {
      super(var1);
      this.singleByte = new byte[1];
      this.doEncode = var2;
      this.base64 = new Base64(var3, var4);
   }

   public boolean markSupported() {
      return false;
   }

   public int read() throws IOException {
      int var1;
      for(var1 = this.read(this.singleByte, 0, 1); var1 == 0; var1 = this.read(this.singleByte, 0, 1)) {
         ;
      }

      int var2;
      if(var1 > 0) {
         if(this.singleByte[0] < 0) {
            var2 = 256 + this.singleByte[0];
         } else {
            var2 = this.singleByte[0];
         }
      } else {
         var2 = -1;
      }

      return var2;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var2 >= 0 && var3 >= 0) {
         if(var2 <= var1.length && var2 + var3 <= var1.length) {
            int var4;
            if(var3 == 0) {
               var4 = 0;
            } else {
               if(!this.base64.hasData()) {
                  short var5;
                  if(this.doEncode) {
                     var5 = 4096;
                  } else {
                     var5 = 8192;
                  }

                  byte[] var6 = new byte[var5];
                  int var7 = this.in.read(var6);
                  if(var7 > 0 && var1.length == var3) {
                     this.base64.setInitialBuffer(var1, var2, var3);
                  }

                  if(this.doEncode) {
                     this.base64.encode(var6, 0, var7);
                  } else {
                     this.base64.decode(var6, 0, var7);
                  }
               }

               var4 = this.base64.readResults(var1, var2, var3);
            }

            return var4;
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
