package com.xxx.appstore.common.util;

import android.text.TextUtils;

import com.xxx.appstore.common.codec.binary.Base64;
import com.xxx.appstore.common.codec.digest.DigestUtils;
import com.xxx.appstore.common.util.Crypter;
import com.xxx.appstore.common.util.Utils;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class SecurityUtil {

   public static final String KEY_HTTP_CHARGE_ALIPAY = "h9sEVED84X81u9ev";
   private static final byte[] SECRET_KEY_HTTP = "sdk_mappn_201008".getBytes();
   private static final byte[] SECRET_KEY_HTTP_CHARGE = "MAPPN-ANDY-XIAN-".getBytes();
   public static final byte[] SECRET_KEY_HTTP_CHARGE_ALIPAY = "h9sEVED84X81u9ev".getBytes();
   private static final byte[] SECRET_KEY_NORMAL = DigestUtils.md5(DigestUtils.md5("7U727ALEWH8".getBytes()));


   public static String decrypt(String var0) {
      String var3;
      if(TextUtils.isEmpty(var0)) {
         var3 = "";
      } else {
         byte[] var1 = Base64.decodeBase64(Utils.getUTF8Bytes(var0));
         if(var1 == null) {
            var3 = "";
         } else {
            byte[] var2 = (new Crypter()).decrypt(var1, SECRET_KEY_NORMAL);
            if(var2 == null) {
               var3 = "";
            } else {
               var3 = Utils.getUTF8String(var2);
            }
         }
      }

      return var3;
   }

   public static byte[] decryptHttpEntity(HttpEntity var0) {
      byte[] var2;
      label16: {
         byte[] var3;
         try {
            var3 = EntityUtils.toByteArray(var0);
         } catch (IOException var4) {
            var4.printStackTrace();
            var2 = null;
            break label16;
         }

         var2 = var3;
      }

      if(var2 != null) {
         var2 = (new Crypter()).decrypt(var2, SECRET_KEY_HTTP);
      }

      return var2;
   }

   public static String encrypt(String var0) {
      String var2;
      if(var0 == null) {
         var2 = null;
      } else {
         byte[] var1 = Utils.getUTF8Bytes(var0);
         var2 = Utils.getUTF8String(Base64.encodeBase64((new Crypter()).encrypt(var1, SECRET_KEY_NORMAL)));
      }

      return var2;
   }

   public static byte[] encryptHttpBody(String var0) {
      return Base64.encodeBase64((new Crypter()).encrypt(Utils.getUTF8Bytes(var0), SECRET_KEY_HTTP));
   }

   public static byte[] encryptHttpChargeBody(String var0) {
      return (new Crypter()).encrypt(Utils.getUTF8Bytes(var0), SECRET_KEY_HTTP_CHARGE);
   }

   public static byte[] encryptHttpChargePalipayBody(String var0) {
      return Base64.encodeBase64((new Crypter()).encrypt(Utils.getUTF8Bytes(var0), SECRET_KEY_HTTP_CHARGE_ALIPAY));
   }

   public static String encryptPassword(String var0, String var1) {
      byte[] var2 = DigestUtils.md5(Utils.getUTF8Bytes(var1));
      swapBytes(var2);
      reverseBits(var2);
      return Utils.getUTF8String(Base64.encodeBase64((new Crypter()).encrypt(Utils.getUTF8Bytes(var0), var2)));
   }

   private static void reverseBits(byte[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1] = (byte)(255 ^ var0[var1]);
      }

   }

   private static void swapBytes(byte[] var0) {
      for(int var1 = 0; var1 < var0.length; var1 += 2) {
         byte var2 = var0[var1];
         var0[var1] = var0[var1 + 1];
         var0[var1 + 1] = var2;
      }

   }
}
