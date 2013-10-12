package com.mappn.gfan.common.codec.net;

import com.mappn.gfan.common.codec.BinaryDecoder;
import com.mappn.gfan.common.codec.BinaryEncoder;
import com.mappn.gfan.common.codec.DecoderException;
import com.mappn.gfan.common.codec.EncoderException;
import com.mappn.gfan.common.codec.StringDecoder;
import com.mappn.gfan.common.codec.StringEncoder;
import com.mappn.gfan.common.codec.binary.StringUtils;
import com.mappn.gfan.common.codec.net.Utils;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class URLCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder {

   protected static byte ESCAPE_CHAR = 37;
   static final int RADIX = 16;
   protected static final BitSet WWW_FORM_URL = new BitSet(256);
   protected String charset;


   static {
      for(int var0 = 97; var0 <= 122; ++var0) {
         WWW_FORM_URL.set(var0);
      }

      for(int var1 = 65; var1 <= 90; ++var1) {
         WWW_FORM_URL.set(var1);
      }

      for(int var2 = 48; var2 <= 57; ++var2) {
         WWW_FORM_URL.set(var2);
      }

      WWW_FORM_URL.set(45);
      WWW_FORM_URL.set(95);
      WWW_FORM_URL.set(46);
      WWW_FORM_URL.set(42);
      WWW_FORM_URL.set(32);
   }

   public URLCodec() {
      this("UTF-8");
   }

   public URLCodec(String var1) {
      this.charset = var1;
   }

   public static final byte[] decodeUrl(byte[] var0) throws DecoderException {
      byte[] var3;
      if(var0 == null) {
         var3 = null;
      } else {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            byte var4 = var0[var2];
            if(var4 == 43) {
               var1.write(32);
            } else if(var4 == ESCAPE_CHAR) {
               int var5 = var2 + 1;

               try {
                  int var7 = Utils.digit16(var0[var5]);
                  var2 = var5 + 1;
                  var1.write((char)(Utils.digit16(var0[var2]) + (var7 << 4)));
               } catch (ArrayIndexOutOfBoundsException var8) {
                  throw new DecoderException("Invalid URL encoding: ", var8);
               }
            } else {
               var1.write(var4);
            }
         }

         var3 = var1.toByteArray();
      }

      return var3;
   }

   public static final byte[] encodeUrl(BitSet var0, byte[] var1) {
      byte[] var5;
      if(var1 == null) {
         var5 = null;
      } else {
         BitSet var2;
         if(var0 == null) {
            var2 = WWW_FORM_URL;
         } else {
            var2 = var0;
         }

         ByteArrayOutputStream var3 = new ByteArrayOutputStream();

         for(int var4 = 0; var4 < var1.length; ++var4) {
            int var6 = var1[var4];
            if(var6 < 0) {
               var6 += 256;
            }

            if(var2.get(var6)) {
               if(var6 == 32) {
                  var6 = 43;
               }

               var3.write(var6);
            } else {
               var3.write(ESCAPE_CHAR);
               char var7 = Character.toUpperCase(Character.forDigit(15 & var6 >> 4, 16));
               char var8 = Character.toUpperCase(Character.forDigit(var6 & 15, 16));
               var3.write(var7);
               var3.write(var8);
            }
         }

         var5 = var3.toByteArray();
      }

      return var5;
   }

   public Object decode(Object var1) throws DecoderException {
      Object var2;
      if(var1 == null) {
         var2 = null;
      } else if(var1 instanceof byte[]) {
         var2 = this.decode((byte[])((byte[])var1));
      } else {
         if(!(var1 instanceof String)) {
            throw new DecoderException("Objects of type " + var1.getClass().getName() + " cannot be URL decoded");
         }

         var2 = this.decode((String)var1);
      }

      return var2;
   }

   public String decode(String var1) throws DecoderException {
      String var4;
      if(var1 == null) {
         var4 = null;
      } else {
         String var3;
         try {
            var3 = this.decode(var1, this.getDefaultCharset());
         } catch (UnsupportedEncodingException var5) {
            throw new DecoderException(var5.getMessage(), var5);
         }

         var4 = var3;
      }

      return var4;
   }

   public String decode(String var1, String var2) throws DecoderException, UnsupportedEncodingException {
      String var3;
      if(var1 == null) {
         var3 = null;
      } else {
         var3 = new String(this.decode(StringUtils.getBytesUsAscii(var1)), var2);
      }

      return var3;
   }

   public byte[] decode(byte[] var1) throws DecoderException {
      return decodeUrl(var1);
   }

   public Object encode(Object var1) throws EncoderException {
      Object var2;
      if(var1 == null) {
         var2 = null;
      } else if(var1 instanceof byte[]) {
         var2 = this.encode((byte[])((byte[])var1));
      } else {
         if(!(var1 instanceof String)) {
            throw new EncoderException("Objects of type " + var1.getClass().getName() + " cannot be URL encoded");
         }

         var2 = this.encode((String)var1);
      }

      return var2;
   }

   public String encode(String var1) throws EncoderException {
      String var4;
      if(var1 == null) {
         var4 = null;
      } else {
         String var3;
         try {
            var3 = this.encode(var1, this.getDefaultCharset());
         } catch (UnsupportedEncodingException var5) {
            throw new EncoderException(var5.getMessage(), var5);
         }

         var4 = var3;
      }

      return var4;
   }

   public String encode(String var1, String var2) throws UnsupportedEncodingException {
      String var3;
      if(var1 == null) {
         var3 = null;
      } else {
         var3 = StringUtils.newStringUsAscii(this.encode(var1.getBytes(var2)));
      }

      return var3;
   }

   public byte[] encode(byte[] var1) {
      return encodeUrl(WWW_FORM_URL, var1);
   }

   public String getDefaultCharset() {
      return this.charset;
   }

   public String getEncoding() {
      return this.charset;
   }
}
