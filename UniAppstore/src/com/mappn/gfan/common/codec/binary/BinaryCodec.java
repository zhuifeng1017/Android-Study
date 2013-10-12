package com.mappn.gfan.common.codec.binary;

import com.mappn.gfan.common.codec.BinaryDecoder;
import com.mappn.gfan.common.codec.BinaryEncoder;
import com.mappn.gfan.common.codec.DecoderException;
import com.mappn.gfan.common.codec.EncoderException;

public class BinaryCodec implements BinaryDecoder, BinaryEncoder {

   private static final int[] BITS = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
   private static final int BIT_0 = 1;
   private static final int BIT_1 = 2;
   private static final int BIT_2 = 4;
   private static final int BIT_3 = 8;
   private static final int BIT_4 = 16;
   private static final int BIT_5 = 32;
   private static final int BIT_6 = 64;
   private static final int BIT_7 = 128;
   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   private static final char[] EMPTY_CHAR_ARRAY = new char[0];


   public static byte[] fromAscii(byte[] var0) {
      byte[] var1;
      if(isEmpty(var0)) {
         var1 = EMPTY_BYTE_ARRAY;
      } else {
         var1 = new byte[var0.length >> 3];
         int var2 = var0.length - 1;

         for(int var3 = 0; var3 < var1.length; var2 -= 8) {
            for(int var4 = 0; var4 < BITS.length; ++var4) {
               if(var0[var2 - var4] == 49) {
                  var1[var3] = (byte)(var1[var3] | BITS[var4]);
               }
            }

            ++var3;
         }
      }

      return var1;
   }

   public static byte[] fromAscii(char[] var0) {
      byte[] var1;
      if(var0 != null && var0.length != 0) {
         var1 = new byte[var0.length >> 3];
         int var2 = var0.length - 1;

         for(int var3 = 0; var3 < var1.length; var2 -= 8) {
            for(int var4 = 0; var4 < BITS.length; ++var4) {
               if(var0[var2 - var4] == 49) {
                  var1[var3] = (byte)(var1[var3] | BITS[var4]);
               }
            }

            ++var3;
         }
      } else {
         var1 = EMPTY_BYTE_ARRAY;
      }

      return var1;
   }

   private static boolean isEmpty(byte[] var0) {
      boolean var1;
      if(var0 != null && var0.length != 0) {
         var1 = false;
      } else {
         var1 = true;
      }

      return var1;
   }

   public static byte[] toAsciiBytes(byte[] var0) {
      byte[] var1;
      if(isEmpty(var0)) {
         var1 = EMPTY_BYTE_ARRAY;
      } else {
         var1 = new byte[var0.length << 3];
         int var2 = var1.length - 1;

         for(int var3 = 0; var3 < var0.length; var2 -= 8) {
            for(int var4 = 0; var4 < BITS.length; ++var4) {
               if((var0[var3] & BITS[var4]) == 0) {
                  var1[var2 - var4] = 48;
               } else {
                  var1[var2 - var4] = 49;
               }
            }

            ++var3;
         }
      }

      return var1;
   }

   public static char[] toAsciiChars(byte[] var0) {
      char[] var1;
      if(isEmpty(var0)) {
         var1 = EMPTY_CHAR_ARRAY;
      } else {
         var1 = new char[var0.length << 3];
         int var2 = var1.length - 1;

         for(int var3 = 0; var3 < var0.length; var2 -= 8) {
            for(int var4 = 0; var4 < BITS.length; ++var4) {
               if((var0[var3] & BITS[var4]) == 0) {
                  var1[var2 - var4] = 48;
               } else {
                  var1[var2 - var4] = 49;
               }
            }

            ++var3;
         }
      }

      return var1;
   }

   public static String toAsciiString(byte[] var0) {
      return new String(toAsciiChars(var0));
   }

   public Object decode(Object var1) throws DecoderException {
      byte[] var2;
      if(var1 == null) {
         var2 = EMPTY_BYTE_ARRAY;
      } else if(var1 instanceof byte[]) {
         var2 = fromAscii((byte[])((byte[])var1));
      } else if(var1 instanceof char[]) {
         var2 = fromAscii((char[])((char[])var1));
      } else {
         if(!(var1 instanceof String)) {
            throw new DecoderException("argument not a byte array");
         }

         var2 = fromAscii(((String)var1).toCharArray());
      }

      return var2;
   }

   public byte[] decode(byte[] var1) {
      return fromAscii(var1);
   }

   public Object encode(Object var1) throws EncoderException {
      if(!(var1 instanceof byte[])) {
         throw new EncoderException("argument not a byte array");
      } else {
         return toAsciiChars((byte[])((byte[])var1));
      }
   }

   public byte[] encode(byte[] var1) {
      return toAsciiBytes(var1);
   }

   public byte[] toByteArray(String var1) {
      byte[] var2;
      if(var1 == null) {
         var2 = EMPTY_BYTE_ARRAY;
      } else {
         var2 = fromAscii(var1.toCharArray());
      }

      return var2;
   }
}
