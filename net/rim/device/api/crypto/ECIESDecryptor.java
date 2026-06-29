package net.rim.device.api.crypto;

import java.io.InputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class ECIESDecryptor extends StreamDecryptor {
   private MAC _mac;
   private boolean _verified;

   public ECIESDecryptor(InputStream input, ECPrivateKey recipientPrivateKey) {
      this(input, recipientPrivateKey, "HMAC/SHA1", -1, null, -1, null, null, true);
   }

   public ECIESDecryptor(
      InputStream input,
      ECPrivateKey recipientPrivateKey,
      String macAlgorithm,
      int macKeyLength,
      byte[] additionalMACInfo,
      int macLength,
      byte[] additionalKDFInfo,
      Digest kdfDigest,
      boolean useCofactor
   ) {
      super(input);
      if (recipientPrivateKey == null) {
         throw new Object();
      }

      int fieldLength = recipientPrivateKey.getECCryptoSystem().getFieldLength();
      byte[] data = readStreamCompletely(input);
      int offset = 0;
      if (data[0] != 2 && data[0] != 3) {
         offset = 2 * fieldLength + 1;
      } else {
         offset = fieldLength + 1;
      }

      byte[] ephemeralKey = Arrays.copy(data, 0, offset);
      ECPublicKey ephemeral = new ECPublicKey(recipientPrivateKey.getECCryptoSystem(), ephemeralKey);
      byte[] sharedSecret = ECDHKeyAgreement.generateSharedSecret(recipientPrivateKey, ephemeral, useCofactor);
      if (kdfDigest == null) {
         kdfDigest = (Digest)(new Object());
      }

      X963KDFPseudoRandomSource source = new X963KDFPseudoRandomSource(sharedSecret, additionalKDFInfo, kdfDigest);
      if (macKeyLength == -1) {
         if (macAlgorithm.startsWith("HMAC")) {
            String digestName = RIMFactoryUtilities.stripLeftMostSubAlgorithm(macAlgorithm);
            if (digestName == null) {
               digestName = "SHA1";
            }

            Digest digest = DigestFactory.getInstance(digestName);
            macKeyLength = digest.getDigestLength();
            if (macLength == -1) {
               macLength = macKeyLength;
            }
         } else {
            SymmetricKey key = SymmetricKeyFactory.getInstance(macAlgorithm, new byte[100], 0);
            macKeyLength = key.getLength();
            if (macLength == -1) {
               macLength = macKeyLength;
            }
         }
      }

      if (macLength == -1) {
         SymmetricKey key = SymmetricKeyFactory.getInstance(macAlgorithm, new byte[macKeyLength], 0, macKeyLength);
         MAC mac = MACFactory.getInstance(macAlgorithm, key);
         macLength = mac.getLength();
      }

      int encryptedDataLength = data.length - offset - macLength;
      byte[] keyData = source.getBytes(encryptedDataLength);
      if (macAlgorithm == null) {
         macAlgorithm = "HMAC/SHA1";
      }

      byte[] macKeyData = source.getBytes(macKeyLength);
      SymmetricKey macKey = SymmetricKeyFactory.getInstance(macAlgorithm, macKeyData, 0, macKeyLength);
      this._mac = MACFactory.getInstance(macAlgorithm, macKey);
      this._mac.update(data, offset, encryptedDataLength);
      if (additionalMACInfo != null) {
         this._mac.update(additionalMACInfo);
      }

      for (int i = 0; i < encryptedDataLength; i++) {
         data[i + offset] = (byte)(data[i + offset] ^ keyData[i]);
      }

      super._inputStream = (InputStream)(new Object(data, offset, encryptedDataLength));
      this._verified = Arrays.equals(this._mac.getMAC(), 0, data, offset + encryptedDataLength, macLength);
   }

   private static final byte[] readStreamCompletely(InputStream in) {
      int blockSize = 1024;
      byte[] textBytes = new byte[blockSize];
      int numTextBytes = 0;

      while (true) {
         int bytesRead = in.read(textBytes, numTextBytes, blockSize);
         if (bytesRead < blockSize) {
            if (bytesRead == -1) {
               Array.resize(textBytes, numTextBytes);
               return textBytes;
            } else {
               Array.resize(textBytes, numTextBytes + bytesRead);
               return textBytes;
            }
         }

         numTextBytes += bytesRead;
         Array.resize(textBytes, numTextBytes + blockSize);
      }
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object("ECIES/"))).append(this._mac.getAlgorithm()).toString();
   }

   @Override
   protected final void decrypt(byte[] data, int dataOffset, int dataLength) {
   }

   public final boolean verify() {
      return this._verified;
   }

   public static final void selfTest() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: ldc_w "abcdefghijklmnopqrst"
      // 003: invokevirtual java/lang/String.getBytes ()[B
      // 006: astore 0
      // 007: bipush 20
      // 009: newarray 8
      // 00b: dup
      // 00c: bipush 0
      // 00d: bipush 69
      // 00f: bastore
      // 010: dup
      // 011: bipush 1
      // 012: bipush -5
      // 014: bastore
      // 015: dup
      // 016: bipush 2
      // 017: bipush 88
      // 019: bastore
      // 01a: dup
      // 01b: bipush 3
      // 01c: bipush -87
      // 01e: bastore
      // 01f: dup
      // 020: bipush 4
      // 021: bipush 42
      // 023: bastore
      // 024: dup
      // 025: bipush 5
      // 026: bipush 23
      // 028: bastore
      // 029: dup
      // 02a: bipush 6
      // 02c: bipush -83
      // 02e: bastore
      // 02f: dup
      // 030: bipush 7
      // 032: bipush 75
      // 034: bastore
      // 035: dup
      // 036: bipush 8
      // 038: bipush 21
      // 03a: bastore
      // 03b: dup
      // 03c: bipush 9
      // 03e: bipush 16
      // 040: bastore
      // 041: dup
      // 042: bipush 10
      // 044: bipush 28
      // 046: bastore
      // 047: dup
      // 048: bipush 11
      // 04a: bipush 102
      // 04c: bastore
      // 04d: dup
      // 04e: bipush 12
      // 050: bipush -25
      // 052: bastore
      // 053: dup
      // 054: bipush 13
      // 056: bipush 79
      // 058: bastore
      // 059: dup
      // 05a: bipush 14
      // 05c: bipush 39
      // 05e: bastore
      // 05f: dup
      // 060: bipush 15
      // 062: bipush 126
      // 064: bastore
      // 065: dup
      // 066: bipush 16
      // 068: bipush 43
      // 06a: bastore
      // 06b: dup
      // 06c: bipush 17
      // 06e: bipush 70
      // 070: bastore
      // 071: dup
      // 072: bipush 18
      // 074: bipush 8
      // 076: bastore
      // 077: dup
      // 078: bipush 19
      // 07a: bipush 102
      // 07c: bastore
      // 07d: astore 1
      // 07e: new net/rim/device/api/crypto/ECPrivateKey
      // 081: dup
      // 082: new net/rim/device/api/crypto/ECCryptoSystem
      // 085: dup
      // 086: ldc_w "EC160R1"
      // 089: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Ljava/lang/String;)V
      // 08c: aload 1
      // 08d: invokespecial net/rim/device/api/crypto/ECPrivateKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)V
      // 090: astore 2
      // 091: bipush 5
      // 092: newarray 8
      // 094: dup
      // 095: bipush 0
      // 096: bipush 17
      // 098: bastore
      // 099: dup
      // 09a: bipush 1
      // 09b: bipush 34
      // 09d: bastore
      // 09e: dup
      // 09f: bipush 2
      // 0a0: bipush 51
      // 0a2: bastore
      // 0a3: dup
      // 0a4: bipush 3
      // 0a5: bipush 68
      // 0a7: bastore
      // 0a8: dup
      // 0a9: bipush 4
      // 0aa: bipush 85
      // 0ac: bastore
      // 0ad: astore 3
      // 0ae: bipush 5
      // 0af: newarray 8
      // 0b1: dup
      // 0b2: bipush 0
      // 0b3: bipush 81
      // 0b5: bastore
      // 0b6: dup
      // 0b7: bipush 1
      // 0b8: bipush 66
      // 0ba: bastore
      // 0bb: dup
      // 0bc: bipush 2
      // 0bd: bipush 51
      // 0bf: bastore
      // 0c0: dup
      // 0c1: bipush 3
      // 0c2: bipush 36
      // 0c4: bastore
      // 0c5: dup
      // 0c6: bipush 4
      // 0c7: bipush 21
      // 0c9: bastore
      // 0ca: astore 4
      // 0cc: bipush 61
      // 0ce: newarray 8
      // 0d0: dup
      // 0d1: bipush 0
      // 0d2: bipush 2
      // 0d3: bastore
      // 0d4: dup
      // 0d5: bipush 1
      // 0d6: bipush -68
      // 0d8: bastore
      // 0d9: dup
      // 0da: bipush 2
      // 0db: bipush 7
      // 0dd: bastore
      // 0de: dup
      // 0df: bipush 3
      // 0e0: bipush 31
      // 0e2: bastore
      // 0e3: dup
      // 0e4: bipush 4
      // 0e5: bipush -128
      // 0e7: bastore
      // 0e8: dup
      // 0e9: bipush 5
      // 0ea: bipush 38
      // 0ec: bastore
      // 0ed: dup
      // 0ee: bipush 6
      // 0f0: bipush -97
      // 0f2: bastore
      // 0f3: dup
      // 0f4: bipush 7
      // 0f6: bipush 12
      // 0f8: bastore
      // 0f9: dup
      // 0fa: bipush 8
      // 0fc: bipush -43
      // 0fe: bastore
      // 0ff: dup
      // 100: bipush 9
      // 102: bipush -63
      // 104: bastore
      // 105: dup
      // 106: bipush 10
      // 108: bipush -45
      // 10a: bastore
      // 10b: dup
      // 10c: bipush 11
      // 10e: bipush 85
      // 110: bastore
      // 111: dup
      // 112: bipush 12
      // 114: bipush 122
      // 116: bastore
      // 117: dup
      // 118: bipush 13
      // 11a: bipush -41
      // 11c: bastore
      // 11d: dup
      // 11e: bipush 14
      // 120: bipush 83
      // 122: bastore
      // 123: dup
      // 124: bipush 15
      // 126: bipush -74
      // 128: bastore
      // 129: dup
      // 12a: bipush 16
      // 12c: bipush -59
      // 12e: bastore
      // 12f: dup
      // 130: bipush 17
      // 132: bipush -53
      // 134: bastore
      // 135: dup
      // 136: bipush 18
      // 138: bipush -17
      // 13a: bastore
      // 13b: dup
      // 13c: bipush 19
      // 13e: bipush 52
      // 140: bastore
      // 141: dup
      // 142: bipush 20
      // 144: bipush 8
      // 146: bastore
      // 147: dup
      // 148: bipush 21
      // 14a: bipush -43
      // 14c: bastore
      // 14d: dup
      // 14e: bipush 22
      // 150: bipush -11
      // 152: bastore
      // 153: dup
      // 154: bipush 23
      // 156: bipush 59
      // 158: bastore
      // 159: dup
      // 15a: bipush 24
      // 15c: bipush 30
      // 15e: bastore
      // 15f: dup
      // 160: bipush 25
      // 162: bipush 116
      // 164: bastore
      // 165: dup
      // 166: bipush 26
      // 168: bipush -86
      // 16a: bastore
      // 16b: dup
      // 16c: bipush 27
      // 16e: bipush 115
      // 170: bastore
      // 171: dup
      // 172: bipush 28
      // 174: bipush 74
      // 176: bastore
      // 177: dup
      // 178: bipush 29
      // 17a: bipush 85
      // 17c: bastore
      // 17d: dup
      // 17e: bipush 30
      // 180: bipush 100
      // 182: bastore
      // 183: dup
      // 184: bipush 31
      // 186: bipush -102
      // 188: bastore
      // 189: dup
      // 18a: bipush 32
      // 18c: bipush -43
      // 18e: bastore
      // 18f: dup
      // 190: bipush 33
      // 192: bipush -60
      // 194: bastore
      // 195: dup
      // 196: bipush 34
      // 198: bipush 43
      // 19a: bastore
      // 19b: dup
      // 19c: bipush 35
      // 19e: bipush -47
      // 1a0: bastore
      // 1a1: dup
      // 1a2: bipush 36
      // 1a4: bipush -54
      // 1a6: bastore
      // 1a7: dup
      // 1a8: bipush 37
      // 1aa: bipush 9
      // 1ac: bastore
      // 1ad: dup
      // 1ae: bipush 38
      // 1b0: bipush -66
      // 1b2: bastore
      // 1b3: dup
      // 1b4: bipush 39
      // 1b6: bipush -98
      // 1b8: bastore
      // 1b9: dup
      // 1ba: bipush 40
      // 1bc: bipush -45
      // 1be: bastore
      // 1bf: dup
      // 1c0: bipush 41
      // 1c2: bipush -22
      // 1c4: bastore
      // 1c5: dup
      // 1c6: bipush 42
      // 1c8: bipush 87
      // 1ca: bastore
      // 1cb: dup
      // 1cc: bipush 43
      // 1ce: bipush -55
      // 1d0: bastore
      // 1d1: dup
      // 1d2: bipush 44
      // 1d4: bipush 62
      // 1d6: bastore
      // 1d7: dup
      // 1d8: bipush 45
      // 1da: bipush 20
      // 1dc: bastore
      // 1dd: dup
      // 1de: bipush 46
      // 1e0: bipush -11
      // 1e2: bastore
      // 1e3: dup
      // 1e4: bipush 47
      // 1e6: bipush 77
      // 1e8: bastore
      // 1e9: dup
      // 1ea: bipush 48
      // 1ec: bipush -50
      // 1ee: bastore
      // 1ef: dup
      // 1f0: bipush 49
      // 1f2: bipush 11
      // 1f4: bastore
      // 1f5: dup
      // 1f6: bipush 50
      // 1f8: bipush -2
      // 1fa: bastore
      // 1fb: dup
      // 1fc: bipush 51
      // 1fe: bipush -75
      // 200: bastore
      // 201: dup
      // 202: bipush 52
      // 204: bipush 15
      // 206: bastore
      // 207: dup
      // 208: bipush 53
      // 20a: bipush 88
      // 20c: bastore
      // 20d: dup
      // 20e: bipush 54
      // 210: bipush 21
      // 212: bastore
      // 213: dup
      // 214: bipush 55
      // 216: bipush -13
      // 218: bastore
      // 219: dup
      // 21a: bipush 56
      // 21c: bipush -84
      // 21e: bastore
      // 21f: dup
      // 220: bipush 57
      // 222: bipush -87
      // 224: bastore
      // 225: dup
      // 226: bipush 58
      // 228: bipush 79
      // 22a: bastore
      // 22b: dup
      // 22c: bipush 59
      // 22e: bipush -17
      // 230: bastore
      // 231: dup
      // 232: bipush 60
      // 234: bipush -22
      // 236: bastore
      // 237: astore 5
      // 239: new java/lang/Object
      // 23c: dup
      // 23d: aload 5
      // 23f: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 242: astore 6
      // 244: new net/rim/device/api/crypto/ECIESDecryptor
      // 247: dup
      // 248: aload 6
      // 24a: aload 2
      // 24b: ldc_w "HMAC/SHA1"
      // 24e: bipush 20
      // 250: aload 3
      // 251: bipush 20
      // 253: aload 4
      // 255: aconst_null
      // 256: bipush 1
      // 257: invokespecial net/rim/device/api/crypto/ECIESDecryptor.<init> (Ljava/io/InputStream;Lnet/rim/device/api/crypto/ECPrivateKey;Ljava/lang/String;I[BI[BLnet/rim/device/api/crypto/Digest;Z)V
      // 25a: astore 7
      // 25c: bipush 32
      // 25e: newarray 8
      // 260: astore 8
      // 262: aload 7
      // 264: aload 8
      // 266: invokevirtual net/rim/device/api/crypto/CryptoInputStream.read ([B)I
      // 269: istore 9
      // 26b: aload 7
      // 26d: invokevirtual net/rim/device/api/crypto/ECIESDecryptor.verify ()Z
      // 270: ifeq 296
      // 273: aload 8
      // 275: bipush 0
      // 276: aload 0
      // 277: bipush 0
      // 278: iload 9
      // 27a: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 27d: ifeq 296
      // 280: return
      // 281: astore 1
      // 282: goto 296
      // 285: astore 1
      // 286: goto 296
      // 289: astore 1
      // 28a: goto 296
      // 28d: astore 1
      // 28e: goto 296
      // 291: astore 1
      // 292: goto 296
      // 295: astore 1
      // 296: new java/lang/Object
      // 299: dup
      // 29a: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 29d: athrow
      // try (3 -> 423): 424 null
      // try (3 -> 423): 426 null
      // try (3 -> 423): 428 null
      // try (3 -> 423): 430 null
      // try (3 -> 423): 432 null
      // try (3 -> 423): 434 null
   }

   static {
      long ID_TEST_ECIESDECRYPTOR = 3242660902010942984L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_ECIESDECRYPTOR) == null) {
         selfTest();
         appRegistry.put(ID_TEST_ECIESDECRYPTOR, appRegistry);
      }
   }
}
