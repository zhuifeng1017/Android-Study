package com.xxx.appstore.common.codec.net;

import com.xxx.appstore.common.codec.BinaryDecoder;
import com.xxx.appstore.common.codec.BinaryEncoder;
import com.xxx.appstore.common.codec.DecoderException;
import com.xxx.appstore.common.codec.EncoderException;
import com.xxx.appstore.common.codec.StringDecoder;
import com.xxx.appstore.common.codec.StringEncoder;
import com.xxx.appstore.common.codec.binary.StringUtils;
import com.xxx.appstore.common.codec.net.Utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class QuotedPrintableCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder {

   private static final byte ESCAPE_CHAR = 61;
   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
   private static final byte SPACE = 32;
   private static final byte TAB = 9;
   private final String charset;


   static {
      for(int var0 = 33; var0 <= 60; ++var0) {
         PRINTABLE_CHARS.set(var0);
      }

      for(int var1 = 62; var1 <= 126; ++var1) {
         PRINTABLE_CHARS.set(var1);
      }

      PRINTABLE_CHARS.set(9);
      PRINTABLE_CHARS.set(32);
   }

   public QuotedPrintableCodec() {
      this("UTF-8");
   }

   public QuotedPrintableCodec(String var1) {
      this.charset = var1;
   }

   public static final byte[] decodeQuotedPrintable(byte[] var0) throws DecoderException {
      byte[] var3;
      if(var0 == null) {
         var3 = null;
      } else {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            byte var4 = var0[var2];
            if(var4 == 61) {
               int var5 = var2 + 1;

               try {
                  int var7 = Utils.digit16(var0[var5]);
                  var2 = var5 + 1;
                  var1.write((char)(Utils.digit16(var0[var2]) + (var7 << 4)));
               } catch (ArrayIndexOutOfBoundsException var8) {
                  throw new DecoderException("Invalid quoted-printable encoding", var8);
               }
            } else {
               var1.write(var4);
            }
         }

         var3 = var1.toByteArray();
      }

      return var3;
   }

   private static final void encodeQuotedPrintable(int var0, ByteArrayOutputStream var1) {
      var1.write(61);
      char var2 = Character.toUpperCase(Character.forDigit(15 & var0 >> 4, 16));
      char var3 = Character.toUpperCase(Character.forDigit(var0 & 15, 16));
      var1.write(var2);
      var1.write(var3);
   }

   public static final byte[] encodeQuotedPrintable(BitSet var0, byte[] var1) {
      byte[] var5;
      if(var1 == null) {
         var5 = null;
      } else {
         BitSet var2;
         if(var0 == null) {
            var2 = PRINTABLE_CHARS;
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
               var3.write(var6);
            } else {
               encodeQuotedPrintable(var6, var3);
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
            throw new DecoderException("Objects of type " + var1.getClass().getName() + " cannot be quoted-printable decoded");
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
      return decodeQuotedPrintable(var1);
   }

   public Object encode(Object var1) throws EncoderException {
      Object var2;
      if(var1 == null) {
         var2 = null;
      } else if(var1 instanceof byte[]) {
         var2 = this.encode((byte[])((byte[])var1));
      } else {
         if(!(var1 instanceof String)) {
            throw new EncoderException("Objects of type " + var1.getClass().getName() + " cannot be quoted-printable encoded");
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
      return encodeQuotedPrintable(PRINTABLE_CHARS, var1);
   }

   public String getDefaultCharset() {
      return this.charset;
   }
}
