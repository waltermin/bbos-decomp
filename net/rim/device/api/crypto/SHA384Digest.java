package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class SHA384Digest extends AbstractDigest implements Digest {
   private NativeDigest _context = NativeDigest.initializeSHA384();
   private static final int MAX_UPDATE = 1024;
   public static final int DIGEST_LENGTH = 48;
   public static final int BLOCK_LENGTH = 128;

   @Override
   public final String getAlgorithm() {
      return "SHA384";
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
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getDigestLength() {
      return 48;
   }

   @Override
   public final int getBlockLength() {
      return 128;
   }

   @Override
   public final int getDigest(byte[] buffer, int offset, boolean resetDigest) {
      this._context.getDigest(buffer, offset, resetDigest);
      return 48;
   }

   public static final void selfTest() {
      byte[] DIGEST_TEXT = new byte[]{
         -2,
         -74,
         115,
         73,
         -33,
         61,
         -74,
         -11,
         -110,
         72,
         21,
         -42,
         -61,
         -36,
         19,
         63,
         9,
         24,
         9,
         33,
         55,
         49,
         -2,
         92,
         123,
         95,
         73,
         -103,
         -28,
         99,
         71,
         -97,
         -14,
         -121,
         127,
         95,
         41,
         54,
         -6,
         99,
         -69,
         67,
         120,
         75,
         18,
         -13,
         -21,
         -76
      };
      byte[] target = new byte[48];
      SHA384Digest digest = new SHA384Digest();
      digest.update(SelfTestData.PLAIN_TEXT_DIGEST, 0, SelfTestData.PLAIN_TEXT_DIGEST.length);
      digest.getDigest(target, 0);
      if (!Arrays.equals(target, 0, DIGEST_TEXT, 0, 48)) {
         throw new CryptoSelfTestError();
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
      // 000: bipush 48
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
      // 0a6: dup
      // 0a7: bipush 28
      // 0a9: bipush -86
      // 0ab: bastore
      // 0ac: dup
      // 0ad: bipush 29
      // 0af: bipush -86
      // 0b1: bastore
      // 0b2: dup
      // 0b3: bipush 30
      // 0b5: bipush -86
      // 0b7: bastore
      // 0b8: dup
      // 0b9: bipush 31
      // 0bb: bipush -86
      // 0bd: bastore
      // 0be: dup
      // 0bf: bipush 32
      // 0c1: bipush -86
      // 0c3: bastore
      // 0c4: dup
      // 0c5: bipush 33
      // 0c7: bipush -86
      // 0c9: bastore
      // 0ca: dup
      // 0cb: bipush 34
      // 0cd: bipush -86
      // 0cf: bastore
      // 0d0: dup
      // 0d1: bipush 35
      // 0d3: bipush -86
      // 0d5: bastore
      // 0d6: dup
      // 0d7: bipush 36
      // 0d9: bipush -86
      // 0db: bastore
      // 0dc: dup
      // 0dd: bipush 37
      // 0df: bipush -86
      // 0e1: bastore
      // 0e2: dup
      // 0e3: bipush 38
      // 0e5: bipush -86
      // 0e7: bastore
      // 0e8: dup
      // 0e9: bipush 39
      // 0eb: bipush -86
      // 0ed: bastore
      // 0ee: dup
      // 0ef: bipush 40
      // 0f1: bipush -86
      // 0f3: bastore
      // 0f4: dup
      // 0f5: bipush 41
      // 0f7: bipush -86
      // 0f9: bastore
      // 0fa: dup
      // 0fb: bipush 42
      // 0fd: bipush -86
      // 0ff: bastore
      // 100: dup
      // 101: bipush 43
      // 103: bipush -86
      // 105: bastore
      // 106: dup
      // 107: bipush 44
      // 109: bipush -86
      // 10b: bastore
      // 10c: dup
      // 10d: bipush 45
      // 10f: bipush -86
      // 111: bastore
      // 112: dup
      // 113: bipush 46
      // 115: bipush -86
      // 117: bastore
      // 118: dup
      // 119: bipush 47
      // 11b: bipush -86
      // 11d: bastore
      // 11e: astore 0
      // 11f: bipush 48
      // 121: newarray 8
      // 123: dup
      // 124: bipush 0
      // 125: bipush 45
      // 127: bastore
      // 128: dup
      // 129: bipush 1
      // 12a: bipush 121
      // 12c: bastore
      // 12d: dup
      // 12e: bipush 2
      // 12f: bipush 103
      // 131: bastore
      // 132: dup
      // 133: bipush 3
      // 134: bipush 82
      // 136: bastore
      // 137: dup
      // 138: bipush 4
      // 139: bipush 0
      // 13a: bastore
      // 13b: dup
      // 13c: bipush 5
      // 13d: bipush -70
      // 13f: bastore
      // 140: dup
      // 141: bipush 6
      // 143: bipush 86
      // 145: bastore
      // 146: dup
      // 147: bipush 7
      // 149: bipush -52
      // 14b: bastore
      // 14c: dup
      // 14d: bipush 8
      // 14f: bipush 12
      // 151: bastore
      // 152: dup
      // 153: bipush 9
      // 155: bipush -118
      // 157: bastore
      // 158: dup
      // 159: bipush 10
      // 15b: bipush -10
      // 15d: bastore
      // 15e: dup
      // 15f: bipush 11
      // 161: bipush -85
      // 163: bastore
      // 164: dup
      // 165: bipush 12
      // 167: bipush 44
      // 169: bastore
      // 16a: dup
      // 16b: bipush 13
      // 16d: bipush -91
      // 16f: bastore
      // 170: dup
      // 171: bipush 14
      // 173: bipush -23
      // 175: bastore
      // 176: dup
      // 177: bipush 15
      // 179: bipush -113
      // 17b: bastore
      // 17c: dup
      // 17d: bipush 16
      // 17f: bipush -11
      // 181: bastore
      // 182: dup
      // 183: bipush 17
      // 185: bipush 71
      // 187: bastore
      // 188: dup
      // 189: bipush 18
      // 18b: bipush 125
      // 18d: bastore
      // 18e: dup
      // 18f: bipush 19
      // 191: bipush 77
      // 193: bastore
      // 194: dup
      // 195: bipush 20
      // 197: bipush 110
      // 199: bastore
      // 19a: dup
      // 19b: bipush 21
      // 19d: bipush 29
      // 19f: bastore
      // 1a0: dup
      // 1a1: bipush 22
      // 1a3: bipush 51
      // 1a5: bastore
      // 1a6: dup
      // 1a7: bipush 23
      // 1a9: bipush -73
      // 1ab: bastore
      // 1ac: dup
      // 1ad: bipush 24
      // 1af: bipush 65
      // 1b1: bastore
      // 1b2: dup
      // 1b3: bipush 25
      // 1b5: bipush -79
      // 1b7: bastore
      // 1b8: dup
      // 1b9: bipush 26
      // 1bb: bipush -122
      // 1bd: bastore
      // 1be: dup
      // 1bf: bipush 27
      // 1c1: bipush -86
      // 1c3: bastore
      // 1c4: dup
      // 1c5: bipush 28
      // 1c7: bipush 43
      // 1c9: bastore
      // 1ca: dup
      // 1cb: bipush 29
      // 1cd: bipush -98
      // 1cf: bastore
      // 1d0: dup
      // 1d1: bipush 30
      // 1d3: bipush -5
      // 1d5: bastore
      // 1d6: dup
      // 1d7: bipush 31
      // 1d9: bipush 15
      // 1db: bastore
      // 1dc: dup
      // 1dd: bipush 32
      // 1df: bipush 31
      // 1e1: bastore
      // 1e2: dup
      // 1e3: bipush 33
      // 1e5: bipush -106
      // 1e7: bastore
      // 1e8: dup
      // 1e9: bipush 34
      // 1eb: bipush -23
      // 1ed: bastore
      // 1ee: dup
      // 1ef: bipush 35
      // 1f1: bipush 58
      // 1f3: bastore
      // 1f4: dup
      // 1f5: bipush 36
      // 1f7: bipush -95
      // 1f9: bastore
      // 1fa: dup
      // 1fb: bipush 37
      // 1fd: bipush -42
      // 1ff: bastore
      // 200: dup
      // 201: bipush 38
      // 203: bipush -69
      // 205: bastore
      // 206: dup
      // 207: bipush 39
      // 209: bipush -11
      // 20b: bastore
      // 20c: dup
      // 20d: bipush 40
      // 20f: bipush 119
      // 211: bastore
      // 212: dup
      // 213: bipush 41
      // 215: bipush -30
      // 217: bastore
      // 218: dup
      // 219: bipush 42
      // 21b: bipush -112
      // 21d: bastore
      // 21e: dup
      // 21f: bipush 43
      // 221: bipush -71
      // 223: bastore
      // 224: dup
      // 225: bipush 44
      // 227: bipush -82
      // 229: bastore
      // 22a: dup
      // 22b: bipush 45
      // 22d: bipush -63
      // 22f: bastore
      // 230: dup
      // 231: bipush 46
      // 233: bipush -7
      // 235: bastore
      // 236: dup
      // 237: bipush 47
      // 239: bipush 101
      // 23b: bastore
      // 23c: astore 1
      // 23d: new net/rim/device/api/crypto/HMACKey
      // 240: dup
      // 241: aload 0
      // 242: invokespecial net/rim/device/api/crypto/HMACKey.<init> ([B)V
      // 245: astore 2
      // 246: new net/rim/device/api/crypto/HMAC
      // 249: dup
      // 24a: aload 2
      // 24b: new net/rim/device/api/crypto/SHA384Digest
      // 24e: dup
      // 24f: invokespecial net/rim/device/api/crypto/SHA384Digest.<init> ()V
      // 252: invokespecial net/rim/device/api/crypto/HMAC.<init> (Lnet/rim/device/api/crypto/HMACKey;Lnet/rim/device/api/crypto/Digest;)V
      // 255: astore 3
      // 256: aload 3
      // 257: getstatic net/rim/device/api/crypto/SelfTestData.PLAIN_TEXT_DIGEST [B
      // 25a: invokevirtual net/rim/device/api/crypto/AbstractMAC.update ([B)V
      // 25d: aload 3
      // 25e: aload 1
      // 25f: invokevirtual net/rim/device/api/crypto/AbstractMAC.checkMAC ([B)Z
      // 262: ifeq 26b
      // 265: return
      // 266: astore 2
      // 267: goto 26b
      // 26a: astore 2
      // 26b: new net/rim/device/api/crypto/CryptoSelfTestError
      // 26e: dup
      // 26f: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 272: athrow
      // try (390 -> 410): 411 null
      // try (390 -> 410): 413 null
   }

   static {
      long ID_TEST_DIGEST_SHA384 = 25928557022040727L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DIGEST_SHA384) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DIGEST_SHA384, appRegistry);
      }
   }
}
