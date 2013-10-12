package com.mappn.gfan.common.util;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class Crypter {

   private static Random random = new Random();
   private ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
   private int contextStart;
   private int crypt;
   private boolean header = true;
   private byte[] key;
   private byte[] out;
   private int padding;
   private byte[] plain;
   private int pos;
   private int preCrypt;
   private byte[] prePlain;


   private byte[] decipher(byte[] var1) {
      return this.decipher(var1, 0);
   }

   private byte[] decipher(byte[] var1, int var2) {
      long var3 = getUnsignedInt(var1, var2, 4);
      long var5 = getUnsignedInt(var1, var2 + 4, 4);
      long var7 = getUnsignedInt(this.key, 0, 4);
      long var9 = getUnsignedInt(this.key, 4, 4);
      long var11 = getUnsignedInt(this.key, 8, 4);
      long var13 = getUnsignedInt(this.key, 12, 4);
      long var15 = -478700656L & 4294967295L;
      long var17 = -1640531527L & 4294967295L;
      long var19 = var3;
      long var21 = var5;
      int var23 = 16;
      long var24 = var15;

      while(true) {
         int var26 = var23 - 1;
         if(var23 <= 0) {
            this.baos.reset();
            this.writeInt((int)var19);
            this.writeInt((int)var21);
            return this.baos.toByteArray();
         }

         var21 = 4294967295L & var21 - (var11 + (var19 << 4) ^ var19 + var24 ^ var13 + (var19 >>> 5));
         var19 = 4294967295L & var19 - (var7 + (var21 << 4) ^ var21 + var24 ^ var9 + (var21 >>> 5));
         var24 = 4294967295L & var24 - var17;
         var23 = var26;
      }
   }

   private boolean decrypt8Bytes(byte[] var1, int var2, int var3) {
      this.pos = 0;

      boolean var4;
      while(true) {
         if(this.pos >= 8) {
            this.prePlain = this.decipher(this.prePlain);
            if(this.prePlain == null) {
               var4 = false;
            } else {
               this.contextStart += 8;
               this.crypt += 8;
               this.pos = 0;
               var4 = true;
            }
            break;
         }

         if(this.contextStart + this.pos >= var3) {
            var4 = true;
            break;
         }

         byte[] var5 = this.prePlain;
         int var6 = this.pos;
         var5[var6] ^= var1[var2 + this.crypt + this.pos];
         ++this.pos;
      }

      return var4;
   }

   private byte[] encipher(byte[] var1) {
      long var2 = getUnsignedInt(var1, 0, 4);
      long var4 = getUnsignedInt(var1, 4, 4);
      long var6 = getUnsignedInt(this.key, 0, 4);
      long var8 = getUnsignedInt(this.key, 4, 4);
      long var10 = getUnsignedInt(this.key, 8, 4);
      long var12 = getUnsignedInt(this.key, 12, 4);
      long var14 = -1640531527L & 4294967295L;
      long var16 = var2;
      long var18 = var4;
      int var20 = 16;
      long var21 = 0L;

      while(true) {
         int var23 = var20 - 1;
         if(var20 <= 0) {
            this.baos.reset();
            this.writeInt((int)var16);
            this.writeInt((int)var18);
            return this.baos.toByteArray();
         }

         var21 = 4294967295L & var21 + var14;
         var16 = 4294967295L & var16 + (var6 + (var18 << 4) ^ var18 + var21 ^ var8 + (var18 >>> 5));
         var18 = 4294967295L & var18 + (var10 + (var16 << 4) ^ var16 + var21 ^ var12 + (var16 >>> 5));
         var20 = var23;
      }
   }

   private void encrypt8Bytes() {
      for(this.pos = 0; this.pos < 8; ++this.pos) {
         if(this.header) {
            byte[] var5 = this.plain;
            int var6 = this.pos;
            var5[var6] ^= this.prePlain[this.pos];
         } else {
            byte[] var3 = this.plain;
            int var4 = this.pos;
            var3[var4] ^= this.out[this.preCrypt + this.pos];
         }
      }

      System.arraycopy(this.encipher(this.plain), 0, this.out, this.crypt, 8);

      for(this.pos = 0; this.pos < 8; ++this.pos) {
         byte[] var1 = this.out;
         int var2 = this.crypt + this.pos;
         var1[var2] ^= this.prePlain[this.pos];
      }

      System.arraycopy(this.plain, 0, this.prePlain, 0, 8);
      this.preCrypt = this.crypt;
      this.crypt += 8;
      this.pos = 0;
      this.header = false;
   }

   private static long getUnsignedInt(byte[] var0, int var1, int var2) {
      int var3;
      if(var2 > 8) {
         var3 = var1 + 8;
      } else {
         var3 = var1 + var2;
      }

      long var4 = 0L;

      for(int var6 = var1; var6 < var3; ++var6) {
         var4 = var4 << 8 | (long)(255 & var0[var6]);
      }

      return 4294967295L & var4 | var4 >>> 32;
   }

   private int rand() {
      return random.nextInt();
   }

   private void writeInt(int var1) {
      this.baos.write(var1 >>> 24);
      this.baos.write(var1 >>> 16);
      this.baos.write(var1 >>> 8);
      this.baos.write(var1);
   }

   public byte[] decrypt(byte[] var1, int var2, int var3, byte[] var4) {
      byte[] var6;
      if(var4 == null) {
         var6 = null;
      } else {
         this.preCrypt = 0;
         this.crypt = 0;
         this.key = var4;
         byte[] var5 = new byte[var2 + 8];
         if(var3 % 8 == 0 && var3 >= 16) {
            this.prePlain = this.decipher(var1, var2);
            this.pos = 7 & this.prePlain[0];
            int var7 = var3 - this.pos - 10;
            if(var7 < 0) {
               var6 = null;
            } else {
               for(int var8 = var2; var8 < var5.length; ++var8) {
                  var5[var8] = 0;
               }

               this.out = new byte[var7];
               this.preCrypt = 0;
               this.crypt = 8;
               this.contextStart = 8;
               ++this.pos;
               this.padding = 1;

               while(true) {
                  if(this.padding > 2) {
                     int var9 = var7;
                     byte[] var10 = var5;
                     int var11 = 0;

                     while(var9 != 0) {
                        if(this.pos < 8) {
                           this.out[var11] = (byte)(var10[var2 + this.preCrypt + this.pos] ^ this.prePlain[this.pos]);
                           ++var11;
                           --var9;
                           ++this.pos;
                        }

                        if(this.pos == 8) {
                           this.preCrypt = this.crypt - 8;
                           if(!this.decrypt8Bytes(var1, var2, var3)) {
                              var6 = null;
                              return var6;
                           }

                           var10 = var1;
                        }
                     }

                     this.padding = 1;

                     for(byte[] var12 = var10; this.padding < 8; ++this.padding) {
                        if(this.pos < 8) {
                           if((var12[var2 + this.preCrypt + this.pos] ^ this.prePlain[this.pos]) != 0) {
                              var6 = null;
                              return var6;
                           }

                           ++this.pos;
                        }

                        if(this.pos == 8) {
                           this.preCrypt = this.crypt;
                           if(!this.decrypt8Bytes(var1, var2, var3)) {
                              var6 = null;
                              return var6;
                           }

                           var12 = var1;
                        }
                     }

                     var6 = this.out;
                     break;
                  }

                  if(this.pos < 8) {
                     ++this.pos;
                     ++this.padding;
                  }

                  if(this.pos == 8) {
                     if(!this.decrypt8Bytes(var1, var2, var3)) {
                        var6 = null;
                        break;
                     }

                     var5 = var1;
                  }
               }
            }
         } else {
            var6 = null;
         }
      }

      return var6;
   }

   public byte[] decrypt(byte[] var1, byte[] var2) {
      return this.decrypt(var1, 0, var1.length, var2);
   }

   public byte[] encrypt(byte[] var1, int var2, int var3, byte[] var4) {
      byte[] var9;
      if(var4 == null) {
         var9 = var1;
      } else {
         this.plain = new byte[8];
         this.prePlain = new byte[8];
         this.pos = 1;
         this.padding = 0;
         this.preCrypt = 0;
         this.crypt = 0;
         this.key = var4;
         this.header = true;
         this.pos = (var3 + 10) % 8;
         if(this.pos != 0) {
            this.pos = 8 - this.pos;
         }

         this.out = new byte[10 + var3 + this.pos];
         this.plain[0] = (byte)(248 & this.rand() | this.pos);

         for(int var5 = 1; var5 <= this.pos; ++var5) {
            this.plain[var5] = (byte)(255 & this.rand());
         }

         ++this.pos;

         for(int var6 = 0; var6 < 8; ++var6) {
            this.prePlain[var6] = 0;
         }

         this.padding = 1;

         while(this.padding <= 2) {
            if(this.pos < 8) {
               byte[] var15 = this.plain;
               int var16 = this.pos;
               this.pos = var16 + 1;
               var15[var16] = (byte)(255 & this.rand());
               ++this.padding;
            }

            if(this.pos == 8) {
               this.encrypt8Bytes();
            }
         }

         int var7 = var2;
         int var8 = var3;

         while(var8 > 0) {
            if(this.pos < 8) {
               byte[] var12 = this.plain;
               int var13 = this.pos;
               this.pos = var13 + 1;
               int var14 = var7 + 1;
               var12[var13] = var1[var7];
               --var8;
               var7 = var14;
            }

            if(this.pos == 8) {
               this.encrypt8Bytes();
            }
         }

         this.padding = 1;

         while(this.padding <= 7) {
            if(this.pos < 8) {
               byte[] var10 = this.plain;
               int var11 = this.pos;
               this.pos = var11 + 1;
               var10[var11] = 0;
               ++this.padding;
            }

            if(this.pos == 8) {
               this.encrypt8Bytes();
            }
         }

         var9 = this.out;
      }

      return var9;
   }

   public byte[] encrypt(byte[] var1, byte[] var2) {
      return this.encrypt(var1, 0, var1.length, var2);
   }
}
