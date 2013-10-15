package com.xxx.appstore.common.codec.binary;

import com.xxx.appstore.common.codec.binary.Base64;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Base64OutputStream extends FilterOutputStream {

   private final Base64 base64;
   private final boolean doEncode;
   private final byte[] singleByte;


   public Base64OutputStream(OutputStream var1) {
      this(var1, true);
   }

   public Base64OutputStream(OutputStream var1, boolean var2) {
      super(var1);
      this.singleByte = new byte[1];
      this.doEncode = var2;
      this.base64 = new Base64();
   }

   public Base64OutputStream(OutputStream var1, boolean var2, int var3, byte[] var4) {
      super(var1);
      this.singleByte = new byte[1];
      this.doEncode = var2;
      this.base64 = new Base64(var3, var4);
   }

   private void flush(boolean var1) throws IOException {
      int var2 = this.base64.avail();
      if(var2 > 0) {
         byte[] var3 = new byte[var2];
         int var4 = this.base64.readResults(var3, 0, var2);
         if(var4 > 0) {
            this.out.write(var3, 0, var4);
         }
      }

      if(var1) {
         this.out.flush();
      }

   }

   public void close() throws IOException {
      if(this.doEncode) {
         this.base64.encode(this.singleByte, 0, -1);
      } else {
         this.base64.decode(this.singleByte, 0, -1);
      }

      this.flush();
      this.out.close();
   }

   public void flush() throws IOException {
      this.flush(true);
   }

   public void write(int var1) throws IOException {
      this.singleByte[0] = (byte)var1;
      this.write(this.singleByte, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var2 >= 0 && var3 >= 0) {
         if(var2 <= var1.length && var2 + var3 <= var1.length) {
            if(var3 > 0) {
               if(this.doEncode) {
                  this.base64.encode(var1, var2, var3);
               } else {
                  this.base64.decode(var1, var2, var3);
               }

               this.flush(false);
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
