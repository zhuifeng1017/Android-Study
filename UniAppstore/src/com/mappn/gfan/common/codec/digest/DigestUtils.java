package com.mappn.gfan.common.codec.digest;

import com.mappn.gfan.common.codec.binary.Hex;
import com.mappn.gfan.common.codec.binary.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {

   private static final int STREAM_BUFFER_LENGTH = 1024;


   private static byte[] digest(MessageDigest var0, InputStream var1) throws IOException {
      byte[] var2 = new byte[1024];

      for(int var3 = var1.read(var2, 0, 1024); var3 > -1; var3 = var1.read(var2, 0, 1024)) {
         var0.update(var2, 0, var3);
      }

      return var0.digest();
   }

   private static byte[] getBytesUtf8(String var0) {
      return StringUtils.getBytesUtf8(var0);
   }

   static MessageDigest getDigest(String var0) {
      try {
         MessageDigest var2 = MessageDigest.getInstance(var0);
         return var2;
      } catch (NoSuchAlgorithmException var3) {
         throw new RuntimeException(var3.getMessage());
      }
   }

   private static MessageDigest getMd5Digest() {
      return getDigest("MD5");
   }

   private static MessageDigest getSha256Digest() {
      return getDigest("SHA-256");
   }

   private static MessageDigest getSha384Digest() {
      return getDigest("SHA-384");
   }

   private static MessageDigest getSha512Digest() {
      return getDigest("SHA-512");
   }

   private static MessageDigest getShaDigest() {
      return getDigest("SHA");
   }

   public static byte[] md5(InputStream var0) throws IOException {
      return digest(getMd5Digest(), var0);
   }

   public static byte[] md5(String var0) {
      return md5(getBytesUtf8(var0));
   }

   public static byte[] md5(byte[] var0) {
      return getMd5Digest().digest(var0);
   }

   public static String md5Hex(InputStream var0) throws IOException {
      return Hex.encodeHexString(md5(var0));
   }

   public static String md5Hex(String var0) {
      return Hex.encodeHexString(md5(var0));
   }

   public static String md5Hex(byte[] var0) {
      return Hex.encodeHexString(md5(var0));
   }

   public static byte[] sha(InputStream var0) throws IOException {
      return digest(getShaDigest(), var0);
   }

   public static byte[] sha(String var0) {
      return sha(getBytesUtf8(var0));
   }

   public static byte[] sha(byte[] var0) {
      return getShaDigest().digest(var0);
   }

   public static byte[] sha256(InputStream var0) throws IOException {
      return digest(getSha256Digest(), var0);
   }

   public static byte[] sha256(String var0) {
      return sha256(getBytesUtf8(var0));
   }

   public static byte[] sha256(byte[] var0) {
      return getSha256Digest().digest(var0);
   }

   public static String sha256Hex(InputStream var0) throws IOException {
      return Hex.encodeHexString(sha256(var0));
   }

   public static String sha256Hex(String var0) {
      return Hex.encodeHexString(sha256(var0));
   }

   public static String sha256Hex(byte[] var0) {
      return Hex.encodeHexString(sha256(var0));
   }

   public static byte[] sha384(InputStream var0) throws IOException {
      return digest(getSha384Digest(), var0);
   }

   public static byte[] sha384(String var0) {
      return sha384(getBytesUtf8(var0));
   }

   public static byte[] sha384(byte[] var0) {
      return getSha384Digest().digest(var0);
   }

   public static String sha384Hex(InputStream var0) throws IOException {
      return Hex.encodeHexString(sha384(var0));
   }

   public static String sha384Hex(String var0) {
      return Hex.encodeHexString(sha384(var0));
   }

   public static String sha384Hex(byte[] var0) {
      return Hex.encodeHexString(sha384(var0));
   }

   public static byte[] sha512(InputStream var0) throws IOException {
      return digest(getSha512Digest(), var0);
   }

   public static byte[] sha512(String var0) {
      return sha512(getBytesUtf8(var0));
   }

   public static byte[] sha512(byte[] var0) {
      return getSha512Digest().digest(var0);
   }

   public static String sha512Hex(InputStream var0) throws IOException {
      return Hex.encodeHexString(sha512(var0));
   }

   public static String sha512Hex(String var0) {
      return Hex.encodeHexString(sha512(var0));
   }

   public static String sha512Hex(byte[] var0) {
      return Hex.encodeHexString(sha512(var0));
   }

   public static String shaHex(InputStream var0) throws IOException {
      return Hex.encodeHexString(sha(var0));
   }

   public static String shaHex(String var0) {
      return Hex.encodeHexString(sha(var0));
   }

   public static String shaHex(byte[] var0) {
      return Hex.encodeHexString(sha(var0));
   }
}
