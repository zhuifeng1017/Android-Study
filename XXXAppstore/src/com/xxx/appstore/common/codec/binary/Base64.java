package com.xxx.appstore.common.codec.binary;

import com.xxx.appstore.common.codec.BinaryDecoder;
import com.xxx.appstore.common.codec.BinaryEncoder;
import com.xxx.appstore.common.codec.DecoderException;
import com.xxx.appstore.common.codec.EncoderException;
import com.xxx.appstore.common.codec.binary.StringUtils;

import java.math.BigInteger;

public class Base64 implements BinaryEncoder, BinaryDecoder {

   static final byte[] CHUNK_SEPARATOR = new byte[]{(byte)13, (byte)10};
   static final int CHUNK_SIZE = 76;
   private static final byte[] DECODE_TABLE = new byte[]{(byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)62, (byte)-1, (byte)62, (byte)-1, (byte)63, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)58, (byte)59, (byte)60, (byte)61, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)0, (byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6, (byte)7, (byte)8, (byte)9, (byte)10, (byte)11, (byte)12, (byte)13, (byte)14, (byte)15, (byte)16, (byte)17, (byte)18, (byte)19, (byte)20, (byte)21, (byte)22, (byte)23, (byte)24, (byte)25, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)63, (byte)-1, (byte)26, (byte)27, (byte)28, (byte)29, (byte)30, (byte)31, (byte)32, (byte)33, (byte)34, (byte)35, (byte)36, (byte)37, (byte)38, (byte)39, (byte)40, (byte)41, (byte)42, (byte)43, (byte)44, (byte)45, (byte)46, (byte)47, (byte)48, (byte)49, (byte)50, (byte)51};
   private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   private static final int MASK_6BITS = 63;
   private static final int MASK_8BITS = 255;
   private static final byte PAD = 61;
   private static final byte[] STANDARD_ENCODE_TABLE = new byte[]{(byte)65, (byte)66, (byte)67, (byte)68, (byte)69, (byte)70, (byte)71, (byte)72, (byte)73, (byte)74, (byte)75, (byte)76, (byte)77, (byte)78, (byte)79, (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87, (byte)88, (byte)89, (byte)90, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106, (byte)107, (byte)108, (byte)109, (byte)110, (byte)111, (byte)112, (byte)113, (byte)114, (byte)115, (byte)116, (byte)117, (byte)118, (byte)119, (byte)120, (byte)121, (byte)122, (byte)48, (byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)43, (byte)47};
   private static final byte[] URL_SAFE_ENCODE_TABLE = new byte[]{(byte)65, (byte)66, (byte)67, (byte)68, (byte)69, (byte)70, (byte)71, (byte)72, (byte)73, (byte)74, (byte)75, (byte)76, (byte)77, (byte)78, (byte)79, (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87, (byte)88, (byte)89, (byte)90, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106, (byte)107, (byte)108, (byte)109, (byte)110, (byte)111, (byte)112, (byte)113, (byte)114, (byte)115, (byte)116, (byte)117, (byte)118, (byte)119, (byte)120, (byte)121, (byte)122, (byte)48, (byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)45, (byte)95};
   private byte[] buffer;
   private int currentLinePos;
   private final int decodeSize;
   private final int encodeSize;
   private final byte[] encodeTable;
   private boolean eof;
   private final int lineLength;
   private final byte[] lineSeparator;
   private int modulus;
   private int pos;
   private int readPos;
   private int x;


   public Base64() {
      this(false);
   }

   public Base64(int var1) {
      this(var1, CHUNK_SEPARATOR);
   }

   public Base64(int var1, byte[] var2) {
      this(var1, var2, false);
   }

   public Base64(int var1, byte[] var2, boolean var3) {
      byte[] var4;
      int var5;
      if(var2 == null) {
         var4 = CHUNK_SEPARATOR;
         var5 = 0;
      } else {
         var4 = var2;
         var5 = var1;
      }

      int var6;
      if(var5 > 0) {
         var6 = 4 * (var5 / 4);
      } else {
         var6 = 0;
      }

      this.lineLength = var6;
      this.lineSeparator = new byte[var4.length];
      System.arraycopy(var4, 0, this.lineSeparator, 0, var4.length);
      if(var5 > 0) {
         this.encodeSize = 4 + var4.length;
      } else {
         this.encodeSize = 4;
      }

      this.decodeSize = this.encodeSize - 1;
      if(containsBase64Byte(var4)) {
         String var8 = StringUtils.newStringUtf8(var4);
         throw new IllegalArgumentException("lineSeperator must not contain base64 characters: [" + var8 + "]");
      } else {
         byte[] var7;
         if(var3) {
            var7 = URL_SAFE_ENCODE_TABLE;
         } else {
            var7 = STANDARD_ENCODE_TABLE;
         }

         this.encodeTable = var7;
      }
   }

   public Base64(boolean var1) {
      this(76, CHUNK_SEPARATOR, var1);
   }

   private static boolean containsBase64Byte(byte[] var0) {
      int var1 = 0;

      boolean var2;
      while(true) {
         if(var1 >= var0.length) {
            var2 = false;
            break;
         }

         if(isBase64(var0[var1])) {
            var2 = true;
            break;
         }

         ++var1;
      }

      return var2;
   }

   public static byte[] decodeBase64(String var0) {
      return (new Base64()).decode(var0);
   }

   public static byte[] decodeBase64(byte[] var0) {
      return (new Base64()).decode(var0);
   }

   public static BigInteger decodeInteger(byte[] var0) {
      return new BigInteger(1, decodeBase64(var0));
   }

   static byte[] discardWhitespace(byte[] var0) {
      byte[] var1 = new byte[var0.length];
      int var2 = 0;
      int var3 = 0;

      while(var2 < var0.length) {
         switch(var0[var2]) {
         default:
            int var5 = var3 + 1;
            var1[var3] = var0[var2];
            var3 = var5;
         case 9:
         case 10:
         case 13:
         case 32:
            ++var2;
         }
      }

      byte[] var4 = new byte[var3];
      System.arraycopy(var1, 0, var4, 0, var3);
      return var4;
   }

   public static byte[] encodeBase64(byte[] var0) {
      return encodeBase64(var0, false);
   }

   public static byte[] encodeBase64(byte[] var0, boolean var1) {
      return encodeBase64(var0, var1, false);
   }

   public static byte[] encodeBase64(byte[] var0, boolean var1, boolean var2) {
      return encodeBase64(var0, var1, var2, Integer.MAX_VALUE);
   }

   public static byte[] encodeBase64(byte[] var0, boolean var1, boolean var2, int var3) {
      byte[] var4;
      if(var0 != null && var0.length != 0) {
         long var5 = getEncodeLength(var0, 76, CHUNK_SEPARATOR);
         if(var5 > (long)var3) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + var5 + ") than the specified maxium size of " + var3);
         }

         Base64 var7;
         if(var1) {
            var7 = new Base64(var2);
         } else {
            var7 = new Base64(0, CHUNK_SEPARATOR, var2);
         }

         var4 = var7.encode(var0);
      } else {
         var4 = var0;
      }

      return var4;
   }

   public static byte[] encodeBase64Chunked(byte[] var0) {
      return encodeBase64(var0, true);
   }

   public static String encodeBase64String(byte[] var0) {
      return StringUtils.newStringUtf8(encodeBase64(var0, true));
   }

   public static byte[] encodeBase64URLSafe(byte[] var0) {
      return encodeBase64(var0, false, true);
   }

   public static String encodeBase64URLSafeString(byte[] var0) {
      return StringUtils.newStringUtf8(encodeBase64(var0, false, true));
   }

   public static byte[] encodeInteger(BigInteger var0) {
      if(var0 == null) {
         throw new NullPointerException("encodeInteger called with null parameter");
      } else {
         return encodeBase64(toIntegerBytes(var0), false);
      }
   }

   private static long getEncodeLength(byte[] var0, int var1, byte[] var2) {
      int var3 = 4 * (var1 / 4);
      long var4 = (long)(4 * var0.length / 3);
      long var6 = var4 % 4L;
      if(var6 != 0L) {
         var4 += 4L - var6;
      }

      long var8;
      if(var3 > 0) {
         boolean var10;
         if(var4 % (long)var3 == 0L) {
            var10 = true;
         } else {
            var10 = false;
         }

         var8 = var4 + var4 / (long)var3 * (long)var2.length;
         if(!var10) {
            var8 += (long)var2.length;
         }
      } else {
         var8 = var4;
      }

      return var8;
   }

   public static boolean isArrayByteBase64(byte[] var0) {
      int var1 = 0;

      boolean var2;
      while(true) {
         if(var1 >= var0.length) {
            var2 = true;
            break;
         }

         if(!isBase64(var0[var1]) && !isWhiteSpace(var0[var1])) {
            var2 = false;
            break;
         }

         ++var1;
      }

      return var2;
   }

   public static boolean isBase64(byte var0) {
      boolean var1;
      if(var0 != 61 && (var0 < 0 || var0 >= DECODE_TABLE.length || DECODE_TABLE[var0] == -1)) {
         var1 = false;
      } else {
         var1 = true;
      }

      return var1;
   }

   private static boolean isWhiteSpace(byte var0) {
      boolean var1;
      switch(var0) {
      case 9:
      case 10:
      case 13:
      case 32:
         var1 = true;
         break;
      default:
         var1 = false;
      }

      return var1;
   }

   private void reset() {
      this.buffer = null;
      this.pos = 0;
      this.readPos = 0;
      this.currentLinePos = 0;
      this.modulus = 0;
      this.eof = false;
   }

   private void resizeBuffer() {
      if(this.buffer == null) {
         this.buffer = new byte[8192];
         this.pos = 0;
         this.readPos = 0;
      } else {
         byte[] var1 = new byte[2 * this.buffer.length];
         System.arraycopy(this.buffer, 0, var1, 0, this.buffer.length);
         this.buffer = var1;
      }

   }

   static byte[] toIntegerBytes(BigInteger var0) {
      int var1 = 7 + var0.bitLength() >> 3 << 3;
      byte[] var2 = var0.toByteArray();
      byte[] var7;
      if(var0.bitLength() % 8 != 0 && 1 + var0.bitLength() / 8 == var1 / 8) {
         var7 = var2;
      } else {
         int var3 = var2.length;
         int var4;
         byte var5;
         if(var0.bitLength() % 8 == 0) {
            int var8 = var3 - 1;
            var5 = 1;
            var4 = var8;
         } else {
            var4 = var3;
            var5 = 0;
         }

         int var6 = var1 / 8 - var4;
         var7 = new byte[var1 / 8];
         System.arraycopy(var2, var5, var7, var6, var4);
      }

      return var7;
   }

   int avail() {
      int var1;
      if(this.buffer != null) {
         var1 = this.pos - this.readPos;
      } else {
         var1 = 0;
      }

      return var1;
   }

   public Object decode(Object var1) throws DecoderException {
      byte[] var2;
      if(var1 instanceof byte[]) {
         var2 = this.decode((byte[])((byte[])var1));
      } else {
         if(!(var1 instanceof String)) {
            throw new DecoderException("Parameter supplied to Base64 decode is not a byte[] or a String");
         }

         var2 = this.decode((String)var1);
      }

      return var2;
   }

   void decode(byte[] var1, int var2, int var3) {
      if(!this.eof) {
         if(var3 < 0) {
            this.eof = true;
         }

         int var4 = 0;

         int var12;
         for(int var5 = var2; var4 < var3; var5 = var12) {
            if(this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
               this.resizeBuffer();
            }

            var12 = var5 + 1;
            byte var13 = var1[var5];
            if(var13 == 61) {
               this.eof = true;
               break;
            }

            if(var13 >= 0 && var13 < DECODE_TABLE.length) {
               byte var14 = DECODE_TABLE[var13];
               if(var14 >= 0) {
                  int var15 = 1 + this.modulus;
                  this.modulus = var15;
                  this.modulus = var15 % 4;
                  this.x = var14 + (this.x << 6);
                  if(this.modulus == 0) {
                     byte[] var16 = this.buffer;
                     int var17 = this.pos;
                     this.pos = var17 + 1;
                     var16[var17] = (byte)(255 & this.x >> 16);
                     byte[] var18 = this.buffer;
                     int var19 = this.pos;
                     this.pos = var19 + 1;
                     var18[var19] = (byte)(255 & this.x >> 8);
                     byte[] var20 = this.buffer;
                     int var21 = this.pos;
                     this.pos = var21 + 1;
                     var20[var21] = (byte)(255 & this.x);
                  }
               }
            }

            ++var4;
         }

         if(this.eof && this.modulus != 0) {
            this.x <<= 6;
            switch(this.modulus) {
            case 2:
               this.x <<= 6;
               byte[] var10 = this.buffer;
               int var11 = this.pos;
               this.pos = var11 + 1;
               var10[var11] = (byte)(255 & this.x >> 16);
               break;
            case 3:
               byte[] var6 = this.buffer;
               int var7 = this.pos;
               this.pos = var7 + 1;
               var6[var7] = (byte)(255 & this.x >> 16);
               byte[] var8 = this.buffer;
               int var9 = this.pos;
               this.pos = var9 + 1;
               var8[var9] = (byte)(255 & this.x >> 8);
            }
         }
      }

   }

   public byte[] decode(String var1) {
      return this.decode(StringUtils.getBytesUtf8(var1));
   }

   public byte[] decode(byte[] var1) {
      this.reset();
      byte[] var2;
      if(var1 != null && var1.length != 0) {
         byte[] var3 = new byte[(int)((long)(3 * var1.length / 4))];
         this.setInitialBuffer(var3, 0, var3.length);
         this.decode(var1, 0, var1.length);
         this.decode(var1, 0, -1);
         var2 = new byte[this.pos];
         this.readResults(var2, 0, var2.length);
      } else {
         var2 = var1;
      }

      return var2;
   }

   public Object encode(Object var1) throws EncoderException {
      if(!(var1 instanceof byte[])) {
         throw new EncoderException("Parameter supplied to Base64 encode is not a byte[]");
      } else {
         return this.encode((byte[])((byte[])var1));
      }
   }

   void encode(byte[] var1, int var2, int var3) {
      if(!this.eof) {
         if(var3 < 0) {
            this.eof = true;
            if(this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
               this.resizeBuffer();
            }

            switch(this.modulus) {
            case 1:
               byte[] var25 = this.buffer;
               int var26 = this.pos;
               this.pos = var26 + 1;
               var25[var26] = this.encodeTable[63 & this.x >> 2];
               byte[] var27 = this.buffer;
               int var28 = this.pos;
               this.pos = var28 + 1;
               var27[var28] = this.encodeTable[63 & this.x << 4];
               if(this.encodeTable == STANDARD_ENCODE_TABLE) {
                  byte[] var29 = this.buffer;
                  int var30 = this.pos;
                  this.pos = var30 + 1;
                  var29[var30] = 61;
                  byte[] var31 = this.buffer;
                  int var32 = this.pos;
                  this.pos = var32 + 1;
                  var31[var32] = 61;
               }
               break;
            case 2:
               byte[] var17 = this.buffer;
               int var18 = this.pos;
               this.pos = var18 + 1;
               var17[var18] = this.encodeTable[63 & this.x >> 10];
               byte[] var19 = this.buffer;
               int var20 = this.pos;
               this.pos = var20 + 1;
               var19[var20] = this.encodeTable[63 & this.x >> 4];
               byte[] var21 = this.buffer;
               int var22 = this.pos;
               this.pos = var22 + 1;
               var21[var22] = this.encodeTable[63 & this.x << 2];
               if(this.encodeTable == STANDARD_ENCODE_TABLE) {
                  byte[] var23 = this.buffer;
                  int var24 = this.pos;
                  this.pos = var24 + 1;
                  var23[var24] = 61;
               }
            }

            if(this.lineLength > 0 && this.pos > 0) {
               System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
               this.pos += this.lineSeparator.length;
            }
         } else {
            int var4 = 0;

            int var7;
            for(int var5 = var2; var4 < var3; var5 = var7) {
               if(this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
                  this.resizeBuffer();
               }

               int var6 = 1 + this.modulus;
               this.modulus = var6;
               this.modulus = var6 % 3;
               var7 = var5 + 1;
               int var8 = var1[var5];
               if(var8 < 0) {
                  var8 += 256;
               }

               this.x = var8 + (this.x << 8);
               if(this.modulus == 0) {
                  byte[] var9 = this.buffer;
                  int var10 = this.pos;
                  this.pos = var10 + 1;
                  var9[var10] = this.encodeTable[63 & this.x >> 18];
                  byte[] var11 = this.buffer;
                  int var12 = this.pos;
                  this.pos = var12 + 1;
                  var11[var12] = this.encodeTable[63 & this.x >> 12];
                  byte[] var13 = this.buffer;
                  int var14 = this.pos;
                  this.pos = var14 + 1;
                  var13[var14] = this.encodeTable[63 & this.x >> 6];
                  byte[] var15 = this.buffer;
                  int var16 = this.pos;
                  this.pos = var16 + 1;
                  var15[var16] = this.encodeTable[63 & this.x];
                  this.currentLinePos += 4;
                  if(this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
                     System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                     this.pos += this.lineSeparator.length;
                     this.currentLinePos = 0;
                  }
               }

               ++var4;
            }
         }
      }

   }

   public byte[] encode(byte[] var1) {
      this.reset();
      byte[] var2;
      if(var1 != null && var1.length != 0) {
         var2 = new byte[(int)getEncodeLength(var1, this.lineLength, this.lineSeparator)];
         this.setInitialBuffer(var2, 0, var2.length);
         this.encode(var1, 0, var1.length);
         this.encode(var1, 0, -1);
         if(this.buffer != var2) {
            this.readResults(var2, 0, var2.length);
         }

         if(this.isUrlSafe() && this.pos < var2.length) {
            byte[] var3 = new byte[this.pos];
            System.arraycopy(var2, 0, var3, 0, this.pos);
            var2 = var3;
         }
      } else {
         var2 = var1;
      }

      return var2;
   }

   public String encodeToString(byte[] var1) {
      return StringUtils.newStringUtf8(this.encode(var1));
   }

   boolean hasData() {
      boolean var1;
      if(this.buffer != null) {
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   public boolean isUrlSafe() {
      boolean var1;
      if(this.encodeTable == URL_SAFE_ENCODE_TABLE) {
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   int readResults(byte[] var1, int var2, int var3) {
      int var4;
      if(this.buffer != null) {
         var4 = Math.min(this.avail(), var3);
         if(this.buffer != var1) {
            System.arraycopy(this.buffer, this.readPos, var1, var2, var4);
            this.readPos += var4;
            if(this.readPos >= this.pos) {
               this.buffer = null;
            }
         } else {
            this.buffer = null;
         }
      } else if(this.eof) {
         var4 = -1;
      } else {
         var4 = 0;
      }

      return var4;
   }

   void setInitialBuffer(byte[] var1, int var2, int var3) {
      if(var1 != null && var1.length == var3) {
         this.buffer = var1;
         this.pos = var2;
         this.readPos = var2;
      }

   }
}
