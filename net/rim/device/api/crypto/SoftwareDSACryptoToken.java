package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Persistable;

final class SoftwareDSACryptoToken extends DSACryptoToken implements Persistable {
   private static SoftwareDSACryptoToken _instance = new SoftwareDSACryptoToken();
   private static final long ID_DSA_SIGN_VERIFY = 4859317825184477194L;

   static final SoftwareDSACryptoToken getInstance() {
      return _instance;
   }

   private SoftwareDSACryptoToken() {
   }

   @Override
   public final DSACryptoSystem[] getSuggestedDSACryptoSystems() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 1
      // 01: anewarray 40
      // 04: dup
      // 05: bipush 0
      // 06: new net/rim/device/api/crypto/DSACryptoSystem
      // 09: dup
      // 0a: getstatic net/rim/device/api/crypto/SoftwareDSACryptoToken._instance Lnet/rim/device/api/crypto/SoftwareDSACryptoToken;
      // 0d: ldc_w "SUN1024"
      // 10: invokespecial net/rim/device/api/crypto/DSACryptoSystem.<init> (Lnet/rim/device/api/crypto/DSACryptoToken;Ljava/lang/String;)V
      // 13: aastore
      // 14: areturn
      // 15: astore 1
      // 16: new java/lang/Object
      // 19: dup
      // 1a: aload 1
      // 1b: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 1e: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 21: athrow
      // 22: astore 1
      // 23: new java/lang/Object
      // 26: dup
      // 27: aload 1
      // 28: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 2b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2e: athrow
      // 2f: astore 1
      // 30: new java/lang/Object
      // 33: dup
      // 34: aload 1
      // 35: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 38: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 3b: athrow
      // try (0 -> 10): 11 null
      // try (0 -> 10): 18 null
      // try (0 -> 10): 25 net/rim/device/api/crypto/UnsupportedCryptoSystemException
   }

   @Override
   public final CryptoTokenCryptoSystemData getDSACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) {
      return new SoftwareDSACryptoToken$DSACryptoSystemData(p, q, g, name);
   }

   @Override
   public final int getDSACryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getBitLength();
   }

   @Override
   public final String getDSACryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getName();
   }

   @Override
   public final byte[] getDSACryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getP();
   }

   @Override
   public final byte[] getDSACryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getQ();
   }

   @Override
   public final byte[] getDSACryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getG();
   }

   @Override
   public final int getDSAPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPublicKeyLength();
   }

   @Override
   public final int getDSAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPrivateKeyLength();
   }

   @Override
   public final byte[] extractDSAPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSAPublicKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareDSACryptoToken$DSAPublicKeyData)cryptoTokenData).copyPublicKeyData();
      }
   }

   @Override
   public final byte[] extractDSAPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareDSACryptoToken$DSAPrivateKeyData)cryptoTokenData).copyPublicKeyData();
      }
   }

   @Override
   public final byte[] extractDSAPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareDSACryptoToken$DSAPrivateKeyData)cryptoTokenData).copyPrivateKeyData();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DSAKeyPair createDSAKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystemData = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenData;
      if (!NativeDL.isSupported(cryptoSystemData.getP(), cryptoSystemData.getQ(), cryptoSystemData.getG(), cryptoSystemData.getPrivateKeyLength(), 1)) {
         throw new Object();
      }

      byte[] publicKeyBytes = new byte[cryptoSystemData.getPublicKeyLength()];
      byte[] privateKeyBytes = new byte[cryptoSystemData.getPrivateKeyLength()];
      Certicom.assertAccessAllowed();
      NativeDL.generateKeyPair(cryptoSystemData.getP(), cryptoSystemData.getQ(), cryptoSystemData.getG(), privateKeyBytes, publicKeyBytes);

      InvalidKeyPairException e;
      try {
         try {
            SoftwareDSACryptoToken$DSAPublicKeyData publicKeyData = new SoftwareDSACryptoToken$DSAPublicKeyData(cryptoSystemData, publicKeyBytes);
            SoftwareDSACryptoToken$DSAPrivateKeyData privateKeyData = new SoftwareDSACryptoToken$DSAPrivateKeyData(cryptoSystemData, privateKeyBytes);
            byte[] digest = new byte[20];
            byte[] r = new byte[cryptoSystemData.getPrivateKeyLength()];
            byte[] s = new byte[cryptoSystemData.getPrivateKeyLength()];
            this.signDSA(cryptoSystemData, privateKeyData, digest, 0, digest.length, r, 0, s, 0);
            if (!this.verifyDSA(cryptoSystemData, publicKeyData, digest, 0, digest.length, r, 0, s, 0)) {
               throw new Object();
            }

            DSACryptoSystem cryptoSystem = new DSACryptoSystem(this, cryptoSystemData);
            return new DSAKeyPair(new DSAPublicKey(cryptoSystem, publicKeyData), new DSAPrivateKey(cryptoSystem, privateKeyData));
         } catch (InvalidKeyPairException var13) {
            e = var13;
         }
      } catch (Throwable var14) {
         throw new Object(e.toString());
      }

      throw new Object(e.toString());
   }

   @Override
   public final boolean isSupported(CryptoSystem param1, int param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: dup
      // 02: instanceof net/rim/device/api/crypto/DSACryptoSystem
      // 05: ifne 0c
      // 08: pop
      // 09: goto 2b
      // 0c: checkcast net/rim/device/api/crypto/DSACryptoSystem
      // 0f: astore 3
      // 10: aload 3
      // 11: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getP ()[B
      // 14: aload 3
      // 15: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getQ ()[B
      // 18: aload 3
      // 19: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getG ()[B
      // 1c: aload 3
      // 1d: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getPrivateKeyLength ()I
      // 20: iload 2
      // 21: invokestatic net/rim/device/api/crypto/NativeDL.isSupported ([B[B[BII)Z
      // 24: ireturn
      // 25: astore 4
      // 27: bipush 0
      // 28: ireturn
      // 29: astore 4
      // 2b: bipush 0
      // 2c: ireturn
      // try (8 -> 18): 19 null
      // try (8 -> 18): 22 null
   }

   @Override
   public final CryptoTokenPublicKeyData injectDSAPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareDSACryptoToken$DSACryptoSystemData) {
         return new SoftwareDSACryptoToken$DSAPublicKeyData((SoftwareDSACryptoToken$DSACryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final CryptoTokenPrivateKeyData injectDSAPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareDSACryptoToken$DSACryptoSystemData) {
         return new SoftwareDSACryptoToken$DSAPrivateKeyData((SoftwareDSACryptoToken$DSACryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void deleteDSAPublicKey(CryptoTokenPublicKeyData data) {
   }

   @Override
   public final void deleteDSAPrivateKey(CryptoTokenPrivateKeyData data) {
   }

   @Override
   public final void signDSA(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      if (!(cryptoTokenCryptoSystemData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystemData = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenPrivateKeyData instanceof SoftwareDSACryptoToken$DSAPrivateKeyData)) {
         throw new Object();
      }

      byte[] privateKeyData = ((SoftwareDSACryptoToken$DSAPrivateKeyData)cryptoTokenPrivateKeyData).getPrivateKeyData();
      if (!NativeDL.isSupported(cryptoSystemData.getP(), cryptoSystemData.getQ(), cryptoSystemData.getG(), cryptoSystemData.getPrivateKeyLength(), 4)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      NativeDL.signDSA(cryptoSystemData.getP(), cryptoSystemData.getQ(), cryptoSystemData.getG(), privateKeyData, digest, r, rOffset, s, sOffset);
   }

   @Override
   public final boolean verifyDSA(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPublicKeyData cryptoTokenPublicKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      if (!(cryptoTokenCryptoSystemData instanceof SoftwareDSACryptoToken$DSACryptoSystemData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystemData = (SoftwareDSACryptoToken$DSACryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenPublicKeyData instanceof SoftwareDSACryptoToken$DSAPublicKeyData)) {
         throw new Object();
      }

      SoftwareDSACryptoToken$DSAPublicKeyData publicKeyData = (SoftwareDSACryptoToken$DSAPublicKeyData)cryptoTokenPublicKeyData;
      byte[] q = cryptoSystemData.getQ();
      if (!NativeDL.isSupported(cryptoSystemData.getP(), q, cryptoSystemData.getG(), cryptoSystemData.getPrivateKeyLength(), 2)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      return NativeDL.verifyDSA(cryptoSystemData.getP(), cryptoSystemData.getQ(), cryptoSystemData.getG(), publicKeyData.getPublicKeyData(), digest, r, s);
   }

   @Override
   public final int hashCode() {
      return 394246161;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareDSACryptoToken;
   }

   public static final void selfTest() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 20
      // 002: newarray 8
      // 004: dup
      // 005: bipush 0
      // 006: bipush 107
      // 008: bastore
      // 009: dup
      // 00a: bipush 1
      // 00b: bipush 44
      // 00d: bastore
      // 00e: dup
      // 00f: bipush 2
      // 010: bipush -39
      // 012: bastore
      // 013: dup
      // 014: bipush 3
      // 015: bipush 53
      // 017: bastore
      // 018: dup
      // 019: bipush 4
      // 01a: bipush -48
      // 01c: bastore
      // 01d: dup
      // 01e: bipush 5
      // 01f: bipush 25
      // 021: bastore
      // 022: dup
      // 023: bipush 6
      // 025: bipush 45
      // 027: bastore
      // 028: dup
      // 029: bipush 7
      // 02b: bipush 84
      // 02d: bastore
      // 02e: dup
      // 02f: bipush 8
      // 031: bipush -30
      // 033: bastore
      // 034: dup
      // 035: bipush 9
      // 037: bipush -55
      // 039: bastore
      // 03a: dup
      // 03b: bipush 10
      // 03d: bipush 66
      // 03f: bastore
      // 040: dup
      // 041: bipush 11
      // 043: bipush -75
      // 045: bastore
      // 046: dup
      // 047: bipush 12
      // 049: bipush 116
      // 04b: bastore
      // 04c: dup
      // 04d: bipush 13
      // 04f: bipush -56
      // 051: bastore
      // 052: dup
      // 053: bipush 14
      // 055: bipush 1
      // 056: bastore
      // 057: dup
      // 058: bipush 15
      // 05a: bipush 2
      // 05b: bastore
      // 05c: dup
      // 05d: bipush 16
      // 05f: bipush -56
      // 061: bastore
      // 062: dup
      // 063: bipush 17
      // 065: bipush -8
      // 067: bastore
      // 068: dup
      // 069: bipush 18
      // 06b: bipush -17
      // 06d: bastore
      // 06e: dup
      // 06f: bipush 19
      // 071: bipush 103
      // 073: bastore
      // 074: astore 0
      // 075: sipush 128
      // 078: newarray 8
      // 07a: dup
      // 07b: bipush 0
      // 07c: bipush 126
      // 07e: bastore
      // 07f: dup
      // 080: bipush 1
      // 081: bipush 39
      // 083: bastore
      // 084: dup
      // 085: bipush 2
      // 086: bipush 36
      // 088: bastore
      // 089: dup
      // 08a: bipush 3
      // 08b: bipush 30
      // 08d: bastore
      // 08e: dup
      // 08f: bipush 4
      // 090: bipush -117
      // 092: bastore
      // 093: dup
      // 094: bipush 5
      // 095: bipush -42
      // 097: bastore
      // 098: dup
      // 099: bipush 6
      // 09b: bipush 86
      // 09d: bastore
      // 09e: dup
      // 09f: bipush 7
      // 0a1: bipush 125
      // 0a3: bastore
      // 0a4: dup
      // 0a5: bipush 8
      // 0a7: bipush 123
      // 0a9: bastore
      // 0aa: dup
      // 0ab: bipush 9
      // 0ad: bipush 126
      // 0af: bastore
      // 0b0: dup
      // 0b1: bipush 10
      // 0b3: bipush 78
      // 0b5: bastore
      // 0b6: dup
      // 0b7: bipush 11
      // 0b9: bipush -31
      // 0bb: bastore
      // 0bc: dup
      // 0bd: bipush 12
      // 0bf: bipush 21
      // 0c1: bastore
      // 0c2: dup
      // 0c3: bipush 13
      // 0c5: bipush 82
      // 0c7: bastore
      // 0c8: dup
      // 0c9: bipush 14
      // 0cb: bipush -33
      // 0cd: bastore
      // 0ce: dup
      // 0cf: bipush 15
      // 0d1: bipush -72
      // 0d3: bastore
      // 0d4: dup
      // 0d5: bipush 16
      // 0d7: bipush 26
      // 0d9: bastore
      // 0da: dup
      // 0db: bipush 17
      // 0dd: bipush -18
      // 0df: bastore
      // 0e0: dup
      // 0e1: bipush 18
      // 0e3: bipush -110
      // 0e5: bastore
      // 0e6: dup
      // 0e7: bipush 19
      // 0e9: bipush -109
      // 0eb: bastore
      // 0ec: dup
      // 0ed: bipush 20
      // 0ef: bipush -8
      // 0f1: bastore
      // 0f2: dup
      // 0f3: bipush 21
      // 0f5: bipush 6
      // 0f7: bastore
      // 0f8: dup
      // 0f9: bipush 22
      // 0fb: bipush -6
      // 0fd: bastore
      // 0fe: dup
      // 0ff: bipush 23
      // 101: bipush 50
      // 103: bastore
      // 104: dup
      // 105: bipush 24
      // 107: bipush 38
      // 109: bastore
      // 10a: dup
      // 10b: bipush 25
      // 10d: bipush 120
      // 10f: bastore
      // 110: dup
      // 111: bipush 26
      // 113: bipush 117
      // 115: bastore
      // 116: dup
      // 117: bipush 27
      // 119: bipush -17
      // 11b: bastore
      // 11c: dup
      // 11d: bipush 28
      // 11f: bipush 18
      // 121: bastore
      // 122: dup
      // 123: bipush 29
      // 125: bipush 20
      // 127: bastore
      // 128: dup
      // 129: bipush 30
      // 12b: bipush 117
      // 12d: bastore
      // 12e: dup
      // 12f: bipush 31
      // 131: bipush -66
      // 133: bastore
      // 134: dup
      // 135: bipush 32
      // 137: bipush -9
      // 139: bastore
      // 13a: dup
      // 13b: bipush 33
      // 13d: bipush 104
      // 13f: bastore
      // 140: dup
      // 141: bipush 34
      // 143: bipush 33
      // 145: bastore
      // 146: dup
      // 147: bipush 35
      // 149: bipush -34
      // 14b: bastore
      // 14c: dup
      // 14d: bipush 36
      // 14f: bipush 32
      // 151: bastore
      // 152: dup
      // 153: bipush 37
      // 155: bipush 34
      // 157: bastore
      // 158: dup
      // 159: bipush 38
      // 15b: bipush 35
      // 15d: bastore
      // 15e: dup
      // 15f: bipush 39
      // 161: bipush 119
      // 163: bastore
      // 164: dup
      // 165: bipush 40
      // 167: bipush 117
      // 169: bastore
      // 16a: dup
      // 16b: bipush 41
      // 16d: bipush 40
      // 16f: bastore
      // 170: dup
      // 171: bipush 42
      // 173: bipush -104
      // 175: bastore
      // 176: dup
      // 177: bipush 43
      // 179: bipush -56
      // 17b: bastore
      // 17c: dup
      // 17d: bipush 44
      // 17f: bipush -9
      // 181: bastore
      // 182: dup
      // 183: bipush 45
      // 185: bipush 95
      // 187: bastore
      // 188: dup
      // 189: bipush 46
      // 18b: bipush -20
      // 18d: bastore
      // 18e: dup
      // 18f: bipush 47
      // 191: bipush 63
      // 193: bastore
      // 194: dup
      // 195: bipush 48
      // 197: bipush 123
      // 199: bastore
      // 19a: dup
      // 19b: bipush 49
      // 19d: bipush -97
      // 19f: bastore
      // 1a0: dup
      // 1a1: bipush 50
      // 1a3: bipush -97
      // 1a5: bastore
      // 1a6: dup
      // 1a7: bipush 51
      // 1a9: bipush -57
      // 1ab: bastore
      // 1ac: dup
      // 1ad: bipush 52
      // 1af: bipush 110
      // 1b1: bastore
      // 1b2: dup
      // 1b3: bipush 53
      // 1b5: bipush -59
      // 1b7: bastore
      // 1b8: dup
      // 1b9: bipush 54
      // 1bb: bipush 91
      // 1bd: bastore
      // 1be: dup
      // 1bf: bipush 55
      // 1c1: bipush -62
      // 1c3: bastore
      // 1c4: dup
      // 1c5: bipush 56
      // 1c7: bipush 74
      // 1c9: bastore
      // 1ca: dup
      // 1cb: bipush 57
      // 1cd: bipush -76
      // 1cf: bastore
      // 1d0: dup
      // 1d1: bipush 58
      // 1d3: bipush -111
      // 1d5: bastore
      // 1d6: dup
      // 1d7: bipush 59
      // 1d9: bipush 38
      // 1db: bastore
      // 1dc: dup
      // 1dd: bipush 60
      // 1df: bipush 28
      // 1e1: bastore
      // 1e2: dup
      // 1e3: bipush 61
      // 1e5: bipush -66
      // 1e7: bastore
      // 1e8: dup
      // 1e9: bipush 62
      // 1eb: bipush -42
      // 1ed: bastore
      // 1ee: dup
      // 1ef: bipush 63
      // 1f1: bipush 121
      // 1f3: bastore
      // 1f4: dup
      // 1f5: bipush 64
      // 1f7: bipush -1
      // 1f9: bastore
      // 1fa: dup
      // 1fb: bipush 65
      // 1fd: bipush 61
      // 1ff: bastore
      // 200: dup
      // 201: bipush 66
      // 203: bipush 70
      // 205: bastore
      // 206: dup
      // 207: bipush 67
      // 209: bipush -64
      // 20b: bastore
      // 20c: dup
      // 20d: bipush 68
      // 20f: bipush 124
      // 211: bastore
      // 212: dup
      // 213: bipush 69
      // 215: bipush -79
      // 217: bastore
      // 218: dup
      // 219: bipush 70
      // 21b: bipush 47
      // 21d: bastore
      // 21e: dup
      // 21f: bipush 71
      // 221: bipush 122
      // 223: bastore
      // 224: dup
      // 225: bipush 72
      // 227: bipush 84
      // 229: bastore
      // 22a: dup
      // 22b: bipush 73
      // 22d: bipush 11
      // 22f: bastore
      // 230: dup
      // 231: bipush 74
      // 233: bipush 112
      // 235: bastore
      // 236: dup
      // 237: bipush 75
      // 239: bipush -16
      // 23b: bastore
      // 23c: dup
      // 23d: bipush 76
      // 23f: bipush -103
      // 241: bastore
      // 242: dup
      // 243: bipush 77
      // 245: bipush 57
      // 247: bastore
      // 248: dup
      // 249: bipush 78
      // 24b: bipush -88
      // 24d: bastore
      // 24e: dup
      // 24f: bipush 79
      // 251: bipush 40
      // 253: bastore
      // 254: dup
      // 255: bipush 80
      // 257: bipush -48
      // 259: bastore
      // 25a: dup
      // 25b: bipush 81
      // 25d: bipush 109
      // 25f: bastore
      // 260: dup
      // 261: bipush 82
      // 263: bipush 31
      // 265: bastore
      // 266: dup
      // 267: bipush 83
      // 269: bipush -52
      // 26b: bastore
      // 26c: dup
      // 26d: bipush 84
      // 26f: bipush -71
      // 271: bastore
      // 272: dup
      // 273: bipush 85
      // 275: bipush 107
      // 277: bastore
      // 278: dup
      // 279: bipush 86
      // 27b: bipush -90
      // 27d: bastore
      // 27e: dup
      // 27f: bipush 87
      // 281: bipush 104
      // 283: bastore
      // 284: dup
      // 285: bipush 88
      // 287: bipush -20
      // 289: bastore
      // 28a: dup
      // 28b: bipush 89
      // 28d: bipush 37
      // 28f: bastore
      // 290: dup
      // 291: bipush 90
      // 293: bipush -60
      // 295: bastore
      // 296: dup
      // 297: bipush 91
      // 299: bipush -75
      // 29b: bastore
      // 29c: dup
      // 29d: bipush 92
      // 29f: bipush 122
      // 2a1: bastore
      // 2a2: dup
      // 2a3: bipush 93
      // 2a5: bipush 62
      // 2a7: bastore
      // 2a8: dup
      // 2a9: bipush 94
      // 2ab: bipush 127
      // 2ad: bastore
      // 2ae: dup
      // 2af: bipush 95
      // 2b1: bipush -108
      // 2b3: bastore
      // 2b4: dup
      // 2b5: bipush 96
      // 2b7: bipush -67
      // 2b9: bastore
      // 2ba: dup
      // 2bb: bipush 97
      // 2bd: bipush -95
      // 2bf: bastore
      // 2c0: dup
      // 2c1: bipush 98
      // 2c3: bipush -15
      // 2c5: bastore
      // 2c6: dup
      // 2c7: bipush 99
      // 2c9: bipush 123
      // 2cb: bastore
      // 2cc: dup
      // 2cd: bipush 100
      // 2cf: bipush 91
      // 2d1: bastore
      // 2d2: dup
      // 2d3: bipush 101
      // 2d5: bipush 94
      // 2d7: bastore
      // 2d8: dup
      // 2d9: bipush 102
      // 2db: bipush 107
      // 2dd: bastore
      // 2de: dup
      // 2df: bipush 103
      // 2e1: bipush 89
      // 2e3: bastore
      // 2e4: dup
      // 2e5: bipush 104
      // 2e7: bipush -8
      // 2e9: bastore
      // 2ea: dup
      // 2eb: bipush 105
      // 2ed: bipush 15
      // 2ef: bastore
      // 2f0: dup
      // 2f1: bipush 106
      // 2f3: bipush 114
      // 2f5: bastore
      // 2f6: dup
      // 2f7: bipush 107
      // 2f9: bipush 28
      // 2fb: bastore
      // 2fc: dup
      // 2fd: bipush 108
      // 2ff: bipush 45
      // 301: bastore
      // 302: dup
      // 303: bipush 109
      // 305: bipush 67
      // 307: bastore
      // 308: dup
      // 309: bipush 110
      // 30b: bipush 35
      // 30d: bastore
      // 30e: dup
      // 30f: bipush 111
      // 311: bipush 59
      // 313: bastore
      // 314: dup
      // 315: bipush 112
      // 317: bipush -110
      // 319: bastore
      // 31a: dup
      // 31b: bipush 113
      // 31d: bipush -8
      // 31f: bastore
      // 320: dup
      // 321: bipush 114
      // 323: bipush 108
      // 325: bastore
      // 326: dup
      // 327: bipush 115
      // 329: bipush 44
      // 32b: bastore
      // 32c: dup
      // 32d: bipush 116
      // 32f: bipush -127
      // 331: bastore
      // 332: dup
      // 333: bipush 117
      // 335: bipush 90
      // 337: bastore
      // 338: dup
      // 339: bipush 118
      // 33b: bipush 47
      // 33d: bastore
      // 33e: dup
      // 33f: bipush 119
      // 341: bipush -125
      // 343: bastore
      // 344: dup
      // 345: bipush 120
      // 347: bipush -106
      // 349: bastore
      // 34a: dup
      // 34b: bipush 121
      // 34d: bipush -91
      // 34f: bastore
      // 350: dup
      // 351: bipush 122
      // 353: bipush -10
      // 355: bastore
      // 356: dup
      // 357: bipush 123
      // 359: bipush -115
      // 35b: bastore
      // 35c: dup
      // 35d: bipush 124
      // 35f: bipush -80
      // 361: bastore
      // 362: dup
      // 363: bipush 125
      // 365: bipush -30
      // 367: bastore
      // 368: dup
      // 369: bipush 126
      // 36b: bipush 27
      // 36d: bastore
      // 36e: dup
      // 36f: bipush 127
      // 371: bipush 76
      // 373: bastore
      // 374: astore 1
      // 375: new net/rim/device/api/crypto/SoftwareDSACryptoToken
      // 378: dup
      // 379: invokespecial net/rim/device/api/crypto/SoftwareDSACryptoToken.<init> ()V
      // 37c: astore 2
      // 37d: new net/rim/device/api/crypto/SoftwareDSACryptoToken$DSACryptoSystemData
      // 380: dup
      // 381: getstatic net/rim/device/api/crypto/DHCryptoSystem.SUN1024_P [B
      // 384: getstatic net/rim/device/api/crypto/DHCryptoSystem.SUN1024_Q [B
      // 387: getstatic net/rim/device/api/crypto/DHCryptoSystem.SUN1024_G [B
      // 38a: ldc_w "SUN1024"
      // 38d: invokespecial net/rim/device/api/crypto/SoftwareDSACryptoToken$DSACryptoSystemData.<init> ([B[B[BLjava/lang/String;)V
      // 390: astore 3
      // 391: new net/rim/device/api/crypto/SoftwareDSACryptoToken$DSAPrivateKeyData
      // 394: dup
      // 395: aload 3
      // 396: aload 0
      // 397: invokespecial net/rim/device/api/crypto/SoftwareDSACryptoToken$DSAPrivateKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareDSACryptoToken$DSACryptoSystemData;[B)V
      // 39a: astore 4
      // 39c: new net/rim/device/api/crypto/SoftwareDSACryptoToken$DSAPublicKeyData
      // 39f: dup
      // 3a0: aload 3
      // 3a1: aload 1
      // 3a2: invokespecial net/rim/device/api/crypto/SoftwareDSACryptoToken$DSAPublicKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareDSACryptoToken$DSACryptoSystemData;[B)V
      // 3a5: astore 5
      // 3a7: new java/lang/Object
      // 3aa: dup
      // 3ab: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 3ae: astore 6
      // 3b0: aload 6
      // 3b2: getstatic net/rim/device/api/crypto/SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA [B
      // 3b5: bipush 0
      // 3b6: getstatic net/rim/device/api/crypto/SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA [B
      // 3b9: arraylength
      // 3ba: invokevirtual net/rim/device/api/crypto/SHA1Digest.update ([BII)V
      // 3bd: aload 6
      // 3bf: invokevirtual net/rim/device/api/crypto/SHA1Digest.getDigestLength ()I
      // 3c2: newarray 8
      // 3c4: astore 7
      // 3c6: aload 6
      // 3c8: aload 7
      // 3ca: bipush 0
      // 3cb: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 3ce: pop
      // 3cf: aload 3
      // 3d0: invokevirtual net/rim/device/api/crypto/SoftwareDSACryptoToken$DSACryptoSystemData.getPrivateKeyLength ()I
      // 3d3: newarray 8
      // 3d5: astore 8
      // 3d7: aload 3
      // 3d8: invokevirtual net/rim/device/api/crypto/SoftwareDSACryptoToken$DSACryptoSystemData.getPrivateKeyLength ()I
      // 3db: newarray 8
      // 3dd: astore 9
      // 3df: aload 2
      // 3e0: aload 3
      // 3e1: aload 4
      // 3e3: aload 7
      // 3e5: bipush 0
      // 3e6: aload 6
      // 3e8: invokevirtual net/rim/device/api/crypto/SHA1Digest.getDigestLength ()I
      // 3eb: aload 8
      // 3ed: bipush 0
      // 3ee: aload 9
      // 3f0: bipush 0
      // 3f1: invokevirtual net/rim/device/api/crypto/SoftwareDSACryptoToken.signDSA (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;[BII[BI[BI)V
      // 3f4: aload 2
      // 3f5: aload 3
      // 3f6: aload 5
      // 3f8: aload 7
      // 3fa: bipush 0
      // 3fb: aload 6
      // 3fd: invokevirtual net/rim/device/api/crypto/SHA1Digest.getDigestLength ()I
      // 400: aload 8
      // 402: bipush 0
      // 403: aload 9
      // 405: bipush 0
      // 406: invokevirtual net/rim/device/api/crypto/SoftwareDSACryptoToken.verifyDSA (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;[BII[BI[BI)Z
      // 409: ifeq 41a
      // 40c: return
      // 40d: astore 2
      // 40e: goto 41a
      // 411: astore 2
      // 412: goto 41a
      // 415: astore 2
      // 416: goto 41a
      // 419: astore 2
      // 41a: new java/lang/Object
      // 41d: dup
      // 41e: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 421: athrow
      // try (598 -> 674): 675 net/rim/device/api/crypto/UnsupportedCryptoSystemException
      // try (598 -> 674): 677 null
      // try (598 -> 674): 679 null
      // try (598 -> 674): 681 net/rim/device/api/crypto/InvalidCryptoSystemException
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(4859317825184477194L) == null) {
         selfTest();
         appRegistry.put(4859317825184477194L, appRegistry);
      }
   }
}
