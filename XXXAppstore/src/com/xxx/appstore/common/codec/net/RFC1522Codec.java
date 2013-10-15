package com.xxx.appstore.common.codec.net;

import com.xxx.appstore.common.codec.DecoderException;
import com.xxx.appstore.common.codec.EncoderException;
import com.xxx.appstore.common.codec.binary.StringUtils;

import java.io.UnsupportedEncodingException;

abstract class RFC1522Codec {

   protected static final String POSTFIX = "?=";
   protected static final String PREFIX = "=?";
   protected static final char SEP = '?';


   protected String decodeText(String var1) throws DecoderException, UnsupportedEncodingException {
      String var9;
      if(var1 == null) {
         var9 = null;
      } else {
         if(!var1.startsWith("=?") || !var1.endsWith("?=")) {
            throw new DecoderException("RFC 1522 violation: malformed encoded content");
         }

         int var2 = var1.length() - 2;
         int var3 = var1.indexOf(63, 2);
         if(var3 == var2) {
            throw new DecoderException("RFC 1522 violation: charset token not found");
         }

         String var4 = var1.substring(2, var3);
         if(var4.equals("")) {
            throw new DecoderException("RFC 1522 violation: charset not specified");
         }

         int var5 = var3 + 1;
         int var6 = var1.indexOf(63, var5);
         if(var6 == var2) {
            throw new DecoderException("RFC 1522 violation: encoding token not found");
         }

         String var7 = var1.substring(var5, var6);
         if(!this.getEncoding().equalsIgnoreCase(var7)) {
            throw new DecoderException("This codec cannot decode " + var7 + " encoded content");
         }

         int var8 = var6 + 1;
         var9 = new String(this.doDecoding(StringUtils.getBytesUsAscii(var1.substring(var8, var1.indexOf(63, var8)))), var4);
      }

      return var9;
   }

   protected abstract byte[] doDecoding(byte[] var1) throws DecoderException;

   protected abstract byte[] doEncoding(byte[] var1) throws EncoderException;

   protected String encodeText(String var1, String var2) throws EncoderException, UnsupportedEncodingException {
      String var11;
      if(var1 == null) {
         var11 = null;
      } else {
         StringBuffer var3 = new StringBuffer();
         var3.append("=?");
         var3.append(var2);
         var3.append('?');
         var3.append(this.getEncoding());
         var3.append('?');
         var3.append(StringUtils.newStringUsAscii(this.doEncoding(var1.getBytes(var2))));
         var3.append("?=");
         var11 = var3.toString();
      }

      return var11;
   }

   protected abstract String getEncoding();
}
