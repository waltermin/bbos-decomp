package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareKEACryptoToken extends KEACryptoToken implements Persistable {
   private static SoftwareKEACryptoToken _instance = new SoftwareKEACryptoToken();
   private static final byte[] KEA_PAD = new byte[]{114, -15, -88, 126, -110, -126, 65, -104, -85, 11};
   private static final long ID_TEST_KEA_KEY_AGREEMENT;

   static final SoftwareKEACryptoToken getInstance() {
      return _instance;
   }

   private SoftwareKEACryptoToken() {
   }

   public final KEACryptoSystem[] getSuggestedKEACryptoSystems() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 2
      // 02: anewarray 39
      // 05: dup
      // 06: bipush 0
      // 07: new net/rim/device/api/crypto/KEACryptoSystem
      // 0a: dup
      // 0b: getstatic net/rim/device/api/crypto/SoftwareKEACryptoToken._instance Lnet/rim/device/api/crypto/SoftwareKEACryptoToken;
      // 0e: ldc_w "FORTEZZA"
      // 11: invokespecial net/rim/device/api/crypto/KEACryptoSystem.<init> (Lnet/rim/device/api/crypto/KEACryptoToken;Ljava/lang/String;)V
      // 14: aastore
      // 15: dup
      // 16: bipush 1
      // 17: new net/rim/device/api/crypto/KEACryptoSystem
      // 1a: dup
      // 1b: getstatic net/rim/device/api/crypto/SoftwareKEACryptoToken._instance Lnet/rim/device/api/crypto/SoftwareKEACryptoToken;
      // 1e: ldc_w "SUN1024"
      // 21: invokespecial net/rim/device/api/crypto/KEACryptoSystem.<init> (Lnet/rim/device/api/crypto/KEACryptoToken;Ljava/lang/String;)V
      // 24: aastore
      // 25: areturn
      // 26: astore 1
      // 27: new java/lang/Object
      // 2a: dup
      // 2b: aload 1
      // 2c: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 2f: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 32: athrow
      // 33: astore 1
      // 34: new java/lang/Object
      // 37: dup
      // 38: aload 1
      // 39: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 3c: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 3f: athrow
      // 40: astore 1
      // 41: new java/lang/Object
      // 44: dup
      // 45: aload 1
      // 46: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 49: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 4c: athrow
      // try (0 -> 18): 19 null
      // try (0 -> 18): 26 null
      // try (0 -> 18): 33 null
   }

   @Override
   public final CryptoTokenCryptoSystemData getKEACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) {
      return new SoftwareKEACryptoToken$KEACryptoSystemData(p, q, g, name);
   }

   @Override
   public final int getKEACryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getBitLength();
   }

   @Override
   public final String getKEACryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getName();
   }

   @Override
   public final byte[] getKEACryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenData;
      return cryptoSystem.copyP();
   }

   @Override
   public final byte[] getKEACryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenData;
      return cryptoSystem.copyQ();
   }

   @Override
   public final byte[] getKEACryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenData;
      return cryptoSystem.copyG();
   }

   @Override
   public final int getKEAPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPublicKeyLength();
   }

   @Override
   public final int getKEAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystem = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPrivateKeyLength();
   }

   @Override
   public final byte[] extractKEAPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEAPublicKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareKEACryptoToken$KEAPublicKeyData)cryptoTokenData).getPublicKeyData();
      }
   }

   @Override
   public final byte[] extractKEAPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareKEACryptoToken$KEAPrivateKeyData)cryptoTokenData).getPublicKeyData();
      }
   }

   @Override
   public final byte[] extractKEAPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareKEACryptoToken$KEAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareKEACryptoToken$KEAPrivateKeyData)cryptoTokenData).getPrivateKeyData();
      }
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
      // 02: instanceof net/rim/device/api/crypto/KEACryptoSystem
      // 05: ifne 0c
      // 08: pop
      // 09: goto 2b
      // 0c: checkcast net/rim/device/api/crypto/KEACryptoSystem
      // 0f: astore 3
      // 10: aload 3
      // 11: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getP ()[B
      // 14: aload 3
      // 15: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getQ ()[B
      // 18: aload 3
      // 19: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getG ()[B
      // 1c: aload 3
      // 1d: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getPrivateKeyLength ()I
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
   public final KEAKeyPair createKEAKeyPair(CryptoTokenCryptoSystemData param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: dup
      // 002: instanceof net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData
      // 005: ifne 00c
      // 008: pop
      // 009: goto 124
      // 00c: checkcast net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData
      // 00f: astore 2
      // 010: aload 2
      // 011: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getPublicKeyLength ()I
      // 014: newarray 8
      // 016: astore 3
      // 017: aload 2
      // 018: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getPrivateKeyLength ()I
      // 01b: newarray 8
      // 01d: astore 4
      // 01f: aload 2
      // 020: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getP ()[B
      // 023: astore 5
      // 025: aload 2
      // 026: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getQ ()[B
      // 029: astore 6
      // 02b: aload 2
      // 02c: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getG ()[B
      // 02f: astore 7
      // 031: aload 5
      // 033: aload 6
      // 035: aload 7
      // 037: aload 2
      // 038: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getPrivateKeyLength ()I
      // 03b: bipush 1
      // 03c: invokestatic net/rim/device/api/crypto/NativeDL.isSupported ([B[B[BII)Z
      // 03f: ifne 04a
      // 042: new java/lang/Object
      // 045: dup
      // 046: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> ()V
      // 049: athrow
      // 04a: invokestatic net/rim/device/api/crypto/Certicom.assertAccessAllowed ()V
      // 04d: aload 5
      // 04f: aload 6
      // 051: aload 7
      // 053: aload 4
      // 055: aload 3
      // 056: invokestatic net/rim/device/api/crypto/NativeDL.generateKeyPair ([B[B[B[B[B)V
      // 059: new net/rim/device/api/crypto/SoftwareKEACryptoToken$KEAPublicKeyData
      // 05c: dup
      // 05d: aload 2
      // 05e: aload 3
      // 05f: invokespecial net/rim/device/api/crypto/SoftwareKEACryptoToken$KEAPublicKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData;[B)V
      // 062: astore 8
      // 064: new net/rim/device/api/crypto/SoftwareKEACryptoToken$KEAPrivateKeyData
      // 067: dup
      // 068: aload 2
      // 069: aload 4
      // 06b: invokespecial net/rim/device/api/crypto/SoftwareKEACryptoToken$KEAPrivateKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData;[B)V
      // 06e: astore 9
      // 070: aload 2
      // 071: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getPublicKeyLength ()I
      // 074: newarray 8
      // 076: astore 10
      // 078: aload 2
      // 079: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData.getPrivateKeyLength ()I
      // 07c: newarray 8
      // 07e: astore 11
      // 080: aload 11
      // 082: aload 11
      // 084: arraylength
      // 085: bipush 1
      // 086: isub
      // 087: bipush 2
      // 089: bastore
      // 08a: aload 7
      // 08c: aload 5
      // 08e: aload 10
      // 090: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.square ([B[B[B)V
      // 093: new net/rim/device/api/crypto/SoftwareKEACryptoToken$KEAPrivateKeyData
      // 096: dup
      // 097: aload 2
      // 098: aload 11
      // 09a: invokespecial net/rim/device/api/crypto/SoftwareKEACryptoToken$KEAPrivateKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareKEACryptoToken$KEACryptoSystemData;[B)V
      // 09d: astore 12
      // 09f: aload 0
      // 0a0: aload 2
      // 0a1: aload 9
      // 0a3: aload 12
      // 0a5: aload 10
      // 0a7: aload 10
      // 0a9: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken.generateKEASharedSecret (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;[B[B)[B
      // 0ac: astore 13
      // 0ae: aload 0
      // 0af: aload 2
      // 0b0: aload 12
      // 0b2: aload 12
      // 0b4: aload 3
      // 0b5: aload 10
      // 0b7: invokevirtual net/rim/device/api/crypto/SoftwareKEACryptoToken.generateKEASharedSecret (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;[B[B)[B
      // 0ba: astore 14
      // 0bc: aload 13
      // 0be: aload 14
      // 0c0: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 0c3: ifne 0ce
      // 0c6: new java/lang/Object
      // 0c9: dup
      // 0ca: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 0cd: athrow
      // 0ce: new net/rim/device/api/crypto/KEACryptoSystem
      // 0d1: dup
      // 0d2: aload 0
      // 0d3: aload 2
      // 0d4: invokespecial net/rim/device/api/crypto/KEACryptoSystem.<init> (Lnet/rim/device/api/crypto/KEACryptoToken;Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;)V
      // 0d7: astore 15
      // 0d9: new net/rim/device/api/crypto/KEAKeyPair
      // 0dc: dup
      // 0dd: new net/rim/device/api/crypto/KEAPublicKey
      // 0e0: dup
      // 0e1: aload 15
      // 0e3: aload 8
      // 0e5: invokespecial net/rim/device/api/crypto/KEAPublicKey.<init> (Lnet/rim/device/api/crypto/KEACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;)V
      // 0e8: new net/rim/device/api/crypto/KEAPrivateKey
      // 0eb: dup
      // 0ec: aload 15
      // 0ee: aload 9
      // 0f0: invokespecial net/rim/device/api/crypto/KEAPrivateKey.<init> (Lnet/rim/device/api/crypto/KEACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 0f3: invokespecial net/rim/device/api/crypto/KEAKeyPair.<init> (Lnet/rim/device/api/crypto/KEAPublicKey;Lnet/rim/device/api/crypto/KEAPrivateKey;)V
      // 0f6: areturn
      // 0f7: astore 8
      // 0f9: new java/lang/Object
      // 0fc: dup
      // 0fd: aload 8
      // 0ff: invokevirtual net/rim/device/api/crypto/InvalidKeyException.toString ()Ljava/lang/String;
      // 102: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 105: athrow
      // 106: astore 8
      // 108: new java/lang/Object
      // 10b: dup
      // 10c: aload 8
      // 10e: invokevirtual net/rim/device/api/crypto/InvalidKeyPairException.toString ()Ljava/lang/String;
      // 111: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 114: athrow
      // 115: astore 8
      // 117: new java/lang/Object
      // 11a: dup
      // 11b: aload 8
      // 11d: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 120: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 123: athrow
      // 124: new java/lang/Object
      // 127: dup
      // 128: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 12b: athrow
      // try (44 -> 124): 125 null
      // try (44 -> 124): 132 null
      // try (44 -> 124): 139 null
   }

   @Override
   public final CryptoTokenPublicKeyData injectKEAPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareKEACryptoToken$KEACryptoSystemData) {
         return new SoftwareKEACryptoToken$KEAPublicKeyData((SoftwareKEACryptoToken$KEACryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final CryptoTokenPrivateKeyData injectKEAPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareKEACryptoToken$KEACryptoSystemData) {
         return new SoftwareKEACryptoToken$KEAPrivateKeyData((SoftwareKEACryptoToken$KEACryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void deleteKEAPublicKey(CryptoTokenPublicKeyData data) {
   }

   @Override
   public final void deleteKEAPrivateKey(CryptoTokenPrivateKeyData data) {
   }

   @Override
   public final byte[] generateKEASharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalStaticPrivateKeyData,
      CryptoTokenPrivateKeyData cryptoTokenLocalEphemeralPrivateKeyData,
      byte[] remoteStaticPublicKeyData,
      byte[] remoteEphemeralPublicKeyData
   ) {
      if (cryptoTokenCryptoSystemData == null
         || cryptoTokenLocalStaticPrivateKeyData == null
         || cryptoTokenLocalEphemeralPrivateKeyData == null
         || remoteStaticPublicKeyData == null
         || remoteEphemeralPublicKeyData == null) {
         throw new Object();
      }

      if (!(cryptoTokenCryptoSystemData instanceof SoftwareKEACryptoToken$KEACryptoSystemData)) {
         throw new Object();
      }

      SoftwareKEACryptoToken$KEACryptoSystemData cryptoSystemData = (SoftwareKEACryptoToken$KEACryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenLocalStaticPrivateKeyData instanceof SoftwareKEACryptoToken$KEAPrivateKeyData)) {
         throw new Object();
      }

      byte[] localStaticPrivateKeyData = ((SoftwareKEACryptoToken$KEAPrivateKeyData)cryptoTokenLocalStaticPrivateKeyData).getPrivateKeyData();
      if (!(cryptoTokenLocalEphemeralPrivateKeyData instanceof SoftwareKEACryptoToken$KEAPrivateKeyData)) {
         throw new Object();
      }

      byte[] localEphemeralPrivateKeyData = ((SoftwareKEACryptoToken$KEAPrivateKeyData)cryptoTokenLocalEphemeralPrivateKeyData).getPrivateKeyData();
      int publicKeyLength = cryptoSystemData.getPublicKeyLength();
      byte[] p = cryptoSystemData.getP();
      byte[] q = cryptoSystemData.getQ();
      byte[] g = cryptoSystemData.getG();
      if (!NativeDL.isSupported(p, q, g, cryptoSystemData.getPrivateKeyLength(), 4)) {
         throw new Object();
      }

      byte[] t = new byte[publicKeyLength];
      Certicom.assertAccessAllowed();
      NativeDL.generateDHSharedSecretNoCofactor(p, q, g, localStaticPrivateKeyData, remoteEphemeralPublicKeyData, t, 0);
      byte[] u = new byte[publicKeyLength];
      NativeDL.generateDHSharedSecretNoCofactor(p, q, g, localEphemeralPrivateKeyData, remoteStaticPublicKeyData, u, 0);
      byte[] w = new byte[publicKeyLength];
      CryptoByteArrayArithmetic.add(t, u, p, w);
      if (CryptoByteArrayArithmetic.isZero(w)) {
         throw new Object();
      }

      for (int i = 0; i < 10; i++) {
         w[i] ^= KEA_PAD[i];
      }

      SkipjackEncryptorEngine skipjackEncryptorEngine = new SkipjackEncryptorEngine(new SkipjackKey(w, 0));
      skipjackEncryptorEngine.encrypt(w, 10, w, 0);
      w[18] ^= w[0];
      w[19] ^= w[1];
      skipjackEncryptorEngine.encrypt(w, 0, w, 10);
      return Arrays.copy(w, 10, 10);
   }

   @Override
   public final int hashCode() {
      return 484054014;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareKEACryptoToken;
   }

   public static final void selfTest() {
      byte[] target = new byte[SelfTestData_PK2.KEA_SHARED_SECRET_2_WAY.length];

      try {
         KEACryptoSystem cryptoSystem = new KEACryptoSystem(SelfTestData_PK2.KEA_P, SelfTestData_PK2.KEA_Q, SelfTestData_PK2.KEA_G);
         KEAPrivateKey localStaticPrivateKey = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_STATIC_PRIVATE_KEY_A));
         KEAPrivateKey localEphemeralPrivateKey = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_EPHEMERAL_PRIVATE_KEY_A));
         KEAPrivateKey remoteStaticPrivateKey = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_STATIC_PRIVATE_KEY_B));
         KEAPrivateKey remoteEphemeralPrivateKey = new KEAPrivateKey(cryptoSystem, Arrays.copy(SelfTestData_PK2.KEA_EPHEMERAL_PRIVATE_KEY_B));
         KEAPublicKey remoteStaticPublicKey = new KEAPublicKey(cryptoSystem, remoteStaticPrivateKey.getPublicKeyData());
         KEAPublicKey remoteEphemeralPublicKey = new KEAPublicKey(cryptoSystem, remoteEphemeralPrivateKey.getPublicKeyData());
         target = KEAKeyAgreement.generateSharedSecret(localStaticPrivateKey, localEphemeralPrivateKey, remoteStaticPublicKey, remoteEphemeralPublicKey);
         if (Arrays.equals(target, SelfTestData_PK2.KEA_SHARED_SECRET_2_WAY)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(1905910533143246511L) == null) {
         selfTest();
         appRegistry.put(1905910533143246511L, appRegistry);
      }
   }
}
