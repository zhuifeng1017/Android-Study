package com.xxx.appstore.common.codec.binary;

import com.xxx.appstore.common.codec.BinaryDecoder;
import com.xxx.appstore.common.codec.BinaryEncoder;
import com.xxx.appstore.common.codec.DecoderException;
import com.xxx.appstore.common.codec.EncoderException;
import com.xxx.appstore.common.codec.binary.StringUtils;

import java.io.UnsupportedEncodingException;

public class Hex implements BinaryEncoder, BinaryDecoder {

   public static final String DEFAULT_CHARSET_NAME = "UTF-8";
   private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   private final String charsetName;


   public Hex() {
      this.charsetName = "UTF-8";
   }

   public Hex(String var1) {
      this.charsetName = var1;
   }

   public static byte[] decodeHex(char[] var0) throws DecoderException {
      int var1 = 0;
      int var2 = var0.length;
      if((var2 & 1) != 0) {
         throw new DecoderException("Odd number of characters.");
      } else {
         byte[] var3 = new byte[var2 >> 1];

         for(int var4 = 0; var1 < var2; ++var4) {
            int var5 = toDigit(var0[var1], var1) << 4;
            int var6 = var1 + 1;
            int var7 = var5 | toDigit(var0[var6], var6);
            var1 = var6 + 1;
            var3[var4] = (byte)(var7 & 255);
         }

         return var3;
      }
   }

   public static char[] encodeHex(byte[] var0) {
      return encodeHex(var0, true);
   }

   public static char[] encodeHex(byte[] var0, boolean var1) {
      char[] var2;
      if(var1) {
         var2 = DIGITS_LOWER;
      } else {
         var2 = DIGITS_UPPER;
      }

      return encodeHex(var0, var2);
   }

   protected static char[] encodeHex(byte[] var0, char[] var1) {
      int var2 = 0;
      int var3 = var0.length;
      char[] var4 = new char[var3 << 1];

      for(int var5 = 0; var5 < var3; ++var5) {
         int var6 = var2 + 1;
         var4[var2] = var1[(240 & var0[var5]) >>> 4];
         var2 = var6 + 1;
         var4[var6] = var1[15 & var0[var5]];
      }

      return var4;
   }

   public static String encodeHexString(byte[] var0) {
      return new String(encodeHex(var0));
   }

   protected static int toDigit(char var0, int var1) throws DecoderException {
      int var2 = Character.digit(var0, 16);
      if(var2 == -1) {
         throw new DecoderException("Illegal hexadecimal charcter " + var0 + " at index " + var1);
      } else {
         return var2;
      }
   }

	public Object decode(Object obj) throws DecoderException {
		if ((obj instanceof String)) {
			char ac1[] = ((String) obj).toCharArray();
			return decodeHex(ac1);
		} else {
			char ac[] = (char[]) (char[]) obj;
			return decodeHex(ac);
		}
	}

	public byte[] decode(byte abyte0[]) throws DecoderException {
		byte abyte1[];
		try {
			abyte1 = decodeHex((new String(abyte0, getCharsetName()))
					.toCharArray());
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw new DecoderException(
					unsupportedencodingexception.getMessage(),
					unsupportedencodingexception);
		}
		return abyte1;
	}

	public Object encode(Object obj) throws EncoderException {
		byte abyte[];
		if ((obj instanceof String)) {
			try {
				abyte = ((String) obj).getBytes(getCharsetName());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new EncoderException(e.getMessage(), e);
			}

		} else {
			abyte = (byte[]) (byte[]) obj;
		}
		return encodeHex(abyte);
	}

   public byte[] encode(byte[] var1) {
      return StringUtils.getBytesUnchecked(encodeHexString(var1), this.getCharsetName());
   }

   public String getCharsetName() {
      return this.charsetName;
   }

   public String toString() {
      return super.toString() + "[charsetName=" + this.charsetName + "]";
   }
}
