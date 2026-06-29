package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class SHA224Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeSHA224();
   private static final int MAX_UPDATE = 1024;
   public static final int DIGEST_LENGTH = 28;
   public static final int BLOCK_LENGTH = 64;

   @Override
   public final String getAlgorithm() {
      return "SHA224";
   }

   @Override
   public final void reset() {
      this._context.reset();
   }

   @Override
   public final void update(int data) {
      this._context.update(data);
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         while (length > 0) {
            int updated = Math.min(length, 1024);
            this._context.update(data, offset, updated);
            offset += updated;
            length -= updated;
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final int getDigestLength() {
      return 28;
   }

   @Override
   public final int getBlockLength() {
      return 64;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      this._context.getDigest(buffer, offset, resetDigest);
      return 28;
   }

   public static final void selfTest() {
      byte[] DIGEST_TEXT = new byte[]{
         69, -91, -9, 44, 57, -59, -49, -14, 82, 46, -77, 66, -105, -103, -28, -98, 95, 68, -77, 86, -17, -110, 107, -49, 57, 13, -52, -62
      };
      byte[] target = new byte[28];
      SHA224Digest digest = new SHA224Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 28)) {
         throw new Object();
      }
   }

   public static final void hmacSelfTest() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 28
      // 002: newarray 8
      // 004: dup
      // 005: bipush 0
      // 006: bipush -86
      // 008: bastore
      // 009: dup
      // 00a: bipush 1
      // 00b: bipush -86
      // 00d: bastore
      // 00e: dup
      // 00f: bipush 2
      // 010: bipush -86
      // 012: bastore
      // 013: dup
      // 014: bipush 3
      // 015: bipush -86
      // 017: bastore
      // 018: dup
      // 019: bipush 4
      // 01a: bipush -86
      // 01c: bastore
      // 01d: dup
      // 01e: bipush 5
      // 01f: bipush -86
      // 021: bastore
      // 022: dup
      // 023: bipush 6
      // 025: bipush -86
      // 027: bastore
      // 028: dup
      // 029: bipush 7
      // 02b: bipush -86
      // 02d: bastore
      // 02e: dup
      // 02f: bipush 8
      // 031: bipush -86
      // 033: bastore
      // 034: dup
      // 035: bipush 9
      // 037: bipush -86
      // 039: bastore
      // 03a: dup
      // 03b: bipush 10
      // 03d: bipush -86
      // 03f: bastore
      // 040: dup
      // 041: bipush 11
      // 043: bipush -86
      // 045: bastore
      // 046: dup
      // 047: bipush 12
      // 049: bipush -86
      // 04b: bastore
      // 04c: dup
      // 04d: bipush 13
      // 04f: bipush -86
      // 051: bastore
      // 052: dup
      // 053: bipush 14
      // 055: bipush -86
      // 057: bastore
      // 058: dup
      // 059: bipush 15
      // 05b: bipush -86
      // 05d: bastore
      // 05e: dup
      // 05f: bipush 16
      // 061: bipush -86
      // 063: bastore
      // 064: dup
      // 065: bipush 17
      // 067: bipush -86
      // 069: bastore
      // 06a: dup
      // 06b: bipush 18
      // 06d: bipush -86
      // 06f: bastore
      // 070: dup
      // 071: bipush 19
      // 073: bipush -86
      // 075: bastore
      // 076: dup
      // 077: bipush 20
      // 079: bipush -86
      // 07b: bastore
      // 07c: dup
      // 07d: bipush 21
      // 07f: bipush -86
      // 081: bastore
      // 082: dup
      // 083: bipush 22
      // 085: bipush -86
      // 087: bastore
      // 088: dup
      // 089: bipush 23
      // 08b: bipush -86
      // 08d: bastore
      // 08e: dup
      // 08f: bipush 24
      // 091: bipush -86
      // 093: bastore
      // 094: dup
      // 095: bipush 25
      // 097: bipush -86
      // 099: bastore
      // 09a: dup
      // 09b: bipush 26
      // 09d: bipush -86
      // 09f: bastore
      // 0a0: dup
      // 0a1: bipush 27
      // 0a3: bipush -86
      // 0a5: bastore
      // 0a6: astore 0
      // 0a7: bipush 28
      // 0a9: newarray 8
      // 0ab: dup
      // 0ac: bipush 0
      // 0ad: bipush -27
      // 0af: bastore
      // 0b0: dup
      // 0b1: bipush 1
      // 0b2: bipush -98
      // 0b4: bastore
      // 0b5: dup
      // 0b6: bipush 2
      // 0b7: bipush -11
      // 0b9: bastore
      // 0ba: dup
      // 0bb: bipush 3
      // 0bc: bipush -104
      // 0be: bastore
      // 0bf: dup
      // 0c0: bipush 4
      // 0c1: bipush -116
      // 0c3: bastore
      // 0c4: dup
      // 0c5: bipush 5
      // 0c6: bipush 62
      // 0c8: bastore
      // 0c9: dup
      // 0ca: bipush 6
      // 0cc: bipush 58
      // 0ce: bastore
      // 0cf: dup
      // 0d0: bipush 7
      // 0d2: bipush -69
      // 0d4: bastore
      // 0d5: dup
      // 0d6: bipush 8
      // 0d8: bipush -128
      // 0da: bastore
      // 0db: dup
      // 0dc: bipush 9
      // 0de: bipush -103
      // 0e0: bastore
      // 0e1: dup
      // 0e2: bipush 10
      // 0e4: bipush -86
      // 0e6: bastore
      // 0e7: dup
      // 0e8: bipush 11
      // 0ea: bipush -104
      // 0ec: bastore
      // 0ed: dup
      // 0ee: bipush 12
      // 0f0: bipush -114
      // 0f2: bastore
      // 0f3: dup
      // 0f4: bipush 13
      // 0f6: bipush 98
      // 0f8: bastore
      // 0f9: dup
      // 0fa: bipush 14
      // 0fc: bipush -60
      // 0fe: bastore
      // 0ff: dup
      // 100: bipush 15
      // 102: bipush 13
      // 104: bastore
      // 105: dup
      // 106: bipush 16
      // 108: bipush 18
      // 10a: bastore
      // 10b: dup
      // 10c: bipush 17
      // 10e: bipush -104
      // 110: bastore
      // 111: dup
      // 112: bipush 18
      // 114: bipush -90
      // 116: bastore
      // 117: dup
      // 118: bipush 19
      // 11a: bipush -65
      // 11c: bastore
      // 11d: dup
      // 11e: bipush 20
      // 120: bipush 75
      // 122: bastore
      // 123: dup
      // 124: bipush 21
      // 126: bipush 112
      // 128: bastore
      // 129: dup
      // 12a: bipush 22
      // 12c: bipush 14
      // 12e: bastore
      // 12f: dup
      // 130: bipush 23
      // 132: bipush 17
      // 134: bastore
      // 135: dup
      // 136: bipush 24
      // 138: bipush 26
      // 13a: bastore
      // 13b: dup
      // 13c: bipush 25
      // 13e: bipush 13
      // 140: bastore
      // 141: dup
      // 142: bipush 26
      // 144: bipush 119
      // 146: bastore
      // 147: dup
      // 148: bipush 27
      // 14a: bipush -106
      // 14c: bastore
      // 14d: astore 1
      // 14e: new java/lang/Object
      // 151: dup
      // 152: aload 0
      // 153: invokespecial net/rim/device/api/crypto/HMACKey.<init> ([B)V
      // 156: astore 2
      // 157: new java/lang/Object
      // 15a: dup
      // 15b: aload 2
      // 15c: new net/rim/device/api/crypto/SHA224Digest
      // 15f: dup
      // 160: invokespecial net/rim/device/api/crypto/SHA224Digest.<init> ()V
      // 163: invokespecial net/rim/device/api/crypto/HMAC.<init> (Lnet/rim/device/api/crypto/HMACKey;Lnet/rim/device/api/crypto/Digest;)V
      // 166: astore 3
      // 167: aload 3
      // 168: getstatic net/rim/device/api/crypto/SelfTestData.PLAIN_TEXT_DIGEST [B
      // 16b: invokevirtual net/rim/device/api/crypto/AbstractMAC.update ([B)V
      // 16e: aload 3
      // 16f: aload 1
      // 170: invokevirtual net/rim/device/api/crypto/AbstractMAC.checkMAC ([B)Z
      // 173: ifeq 17c
      // 176: return
      // 177: astore 2
      // 178: goto 17c
      // 17b: astore 2
      // 17c: new java/lang/Object
      // 17f: dup
      // 180: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 183: athrow
      // try (230 -> 250): 251 null
      // try (230 -> 250): 253 null
   }

   static {
      long ID_TEST_DIGEST_SHA224 = 5826112882055299302L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_SHA224) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_SHA224, appRegistry);
      }
   }
}
