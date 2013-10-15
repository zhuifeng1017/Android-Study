package com.xxx.appstore.common.codec.net;

import com.xxx.appstore.common.codec.DecoderException;
import com.xxx.appstore.common.codec.EncoderException;
import com.xxx.appstore.common.codec.StringDecoder;
import com.xxx.appstore.common.codec.StringEncoder;
import com.xxx.appstore.common.codec.net.QuotedPrintableCodec;
import com.xxx.appstore.common.codec.net.RFC1522Codec;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class QCodec extends RFC1522Codec implements StringEncoder, StringDecoder {

   private static final byte BLANK = 32;
   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
   private static final byte UNDERSCORE = 95;
   private final String charset;
   private boolean encodeBlanks;


   static {
      PRINTABLE_CHARS.set(32);
      PRINTABLE_CHARS.set(33);
      PRINTABLE_CHARS.set(34);
      PRINTABLE_CHARS.set(35);
      PRINTABLE_CHARS.set(36);
      PRINTABLE_CHARS.set(37);
      PRINTABLE_CHARS.set(38);
      PRINTABLE_CHARS.set(39);
      PRINTABLE_CHARS.set(40);
      PRINTABLE_CHARS.set(41);
      PRINTABLE_CHARS.set(42);
      PRINTABLE_CHARS.set(43);
      PRINTABLE_CHARS.set(44);
      PRINTABLE_CHARS.set(45);
      PRINTABLE_CHARS.set(46);
      PRINTABLE_CHARS.set(47);

      for(int var0 = 48; var0 <= 57; ++var0) {
         PRINTABLE_CHARS.set(var0);
      }

      PRINTABLE_CHARS.set(58);
      PRINTABLE_CHARS.set(59);
      PRINTABLE_CHARS.set(60);
      PRINTABLE_CHARS.set(62);
      PRINTABLE_CHARS.set(64);

      for(int var1 = 65; var1 <= 90; ++var1) {
         PRINTABLE_CHARS.set(var1);
      }

      PRINTABLE_CHARS.set(91);
      PRINTABLE_CHARS.set(92);
      PRINTABLE_CHARS.set(93);
      PRINTABLE_CHARS.set(94);
      PRINTABLE_CHARS.set(96);

      for(int var2 = 97; var2 <= 122; ++var2) {
         PRINTABLE_CHARS.set(var2);
      }

      PRINTABLE_CHARS.set(123);
      PRINTABLE_CHARS.set(124);
      PRINTABLE_CHARS.set(125);
      PRINTABLE_CHARS.set(126);
   }

   public QCodec() {
      this("UTF-8");
   }

   public QCodec(String var1) {
      this.encodeBlanks = false;
      this.charset = var1;
   }

   public Object decode(Object var1) throws DecoderException {
      String var2;
      if(var1 == null) {
         var2 = null;
      } else {
         if(!(var1 instanceof String)) {
            throw new DecoderException("Objects of type " + var1.getClass().getName() + " cannot be decoded using Q codec");
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

   protected byte[] doDecoding(byte[] var1) throws DecoderException {
      byte[] var4;
      if(var1 == null) {
         var4 = null;
      } else {
         int var2 = 0;

         boolean var3;
         while(true) {
            if(var2 >= var1.length) {
               var3 = false;
               break;
            }

            if(var1[var2] == 95) {
               var3 = true;
               break;
            }

            ++var2;
         }

         if(var3) {
            byte[] var5 = new byte[var1.length];

            for(int var6 = 0; var6 < var1.length; ++var6) {
               byte var7 = var1[var6];
               if(var7 != 95) {
                  var5[var6] = var7;
               } else {
                  var5[var6] = 32;
               }
            }

            var4 = QuotedPrintableCodec.decodeQuotedPrintable(var5);
         } else {
            var4 = QuotedPrintableCodec.decodeQuotedPrintable(var1);
         }
      }

      return var4;
   }

   protected byte[] doEncoding(byte[] var1) {
      byte[] var2;
      if(var1 == null) {
         var2 = null;
      } else {
         var2 = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, var1);
         if(this.encodeBlanks) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if(var2[var3] == 32) {
                  var2[var3] = 95;
               }
            }
         }
      }

      return var2;
   }

   public Object encode(Object var1) throws EncoderException {
      String var2;
      if(var1 == null) {
         var2 = null;
      } else {
         if(!(var1 instanceof String)) {
            throw new EncoderException("Objects of type " + var1.getClass().getName() + " cannot be encoded using Q codec");
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
      return "Q";
   }

   public boolean isEncodeBlanks() {
      return this.encodeBlanks;
   }

   public void setEncodeBlanks(boolean var1) {
      this.encodeBlanks = var1;
   }
}
