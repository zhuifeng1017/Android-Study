package com.mappn.gfan.common.codec.net;

import com.mappn.gfan.common.codec.DecoderException;
import com.mappn.gfan.common.codec.EncoderException;
import com.mappn.gfan.common.codec.StringDecoder;
import com.mappn.gfan.common.codec.StringEncoder;
import com.mappn.gfan.common.codec.binary.Base64;
import com.mappn.gfan.common.codec.net.RFC1522Codec;
import java.io.UnsupportedEncodingException;

public class BCodec extends RFC1522Codec implements StringEncoder, StringDecoder {

   private final String charset;


   public BCodec() {
      this("UTF-8");
   }

   public BCodec(String var1) {
      this.charset = var1;
   }

   public Object decode(Object var1) throws DecoderException {
      String var2;
      if(var1 == null) {
         var2 = null;
      } else {
         if(!(var1 instanceof String)) {
            throw new DecoderException("Objects of type " + var1.getClass().getName() + " cannot be decoded using BCodec");
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
            var3 = this.decodeText(var1);
         } catch (UnsupportedEncodingException var5) {
            throw new DecoderException(var5.getMessage(), var5);
         }

         var4 = var3;
      }

      return var4;
   }

   protected byte[] doDecoding(byte[] var1) {
      byte[] var2;
      if(var1 == null) {
         var2 = null;
      } else {
         var2 = Base64.decodeBase64(var1);
      }

      return var2;
   }

   protected byte[] doEncoding(byte[] var1) {
      byte[] var2;
      if(var1 == null) {
         var2 = null;
      } else {
         var2 = Base64.encodeBase64(var1);
      }

      return var2;
   }

   public Object encode(Object var1) throws EncoderException {
      String var2;
      if(var1 == null) {
         var2 = null;
      } else {
         if(!(var1 instanceof String)) {
            throw new EncoderException("Objects of type " + var1.getClass().getName() + " cannot be encoded using BCodec");
         }

         var2 = this.encode((String)var1);
      }

      return var2;
   }

   public String encode(String var1) throws EncoderException {
      String var2;
      if(var1 == null) {
         var2 = null;
      } else {
         var2 = this.encode(var1, this.getDefaultCharset());
      }

      return var2;
   }

   public String encode(String var1, String var2) throws EncoderException {
      String var5;
      if(var1 == null) {
         var5 = null;
      } else {
         String var4;
         try {
            var4 = this.encodeText(var1, var2);
         } catch (UnsupportedEncodingException var6) {
            throw new EncoderException(var6.getMessage(), var6);
         }

         var5 = var4;
      }

      return var5;
   }

   public String getDefaultCharset() {
      return this.charset;
   }

   protected String getEncoding() {
      return "B";
   }
}
