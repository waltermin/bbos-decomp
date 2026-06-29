package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareRSACryptoToken extends RSACryptoToken implements Persistable {
   private static SoftwareRSACryptoToken _instance = new SoftwareRSACryptoToken();
   private static final long ID_TEST_RSA;
   private static final int RSA_MODULUS_BIT_LENGTH;

   static final SoftwareRSACryptoToken getInstance() {
      return _instance;
   }

   private SoftwareRSACryptoToken() {
   }

   @Override
   public final byte[] extractRSAPublicKeyE(CryptoTokenPublicKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPublicKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPublicKeyData)cryptoTokenData).copyE();
      }
   }

   @Override
   public final byte[] extractRSAPublicKeyN(CryptoTokenPublicKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPublicKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPublicKeyData)cryptoTokenData).copyN();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyE(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyE();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyN(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyN();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyD(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyD();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyP(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyP();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyQ(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyQ();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyDModPm1(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyDModPm1();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyDModQm1(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyDModQm1();
      }
   }

   @Override
   public final byte[] extractRSAPrivateKeyQInvModP(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenData).copyQInvModP();
      }
   }

   @Override
   public final boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      if (!(cryptoSystem instanceof RSACryptoSystem)) {
         return false;
      }

      RSACryptoSystem rsaCryptoSystem = (RSACryptoSystem)cryptoSystem;
      return NativeRSA.isSupported(rsaCryptoSystem.getBitLength(), operation);
   }

   @Override
   public final RSACryptoSystem[] getSuggestedRSACryptoSystems() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 1
      // 01: anewarray 316
      // 04: dup
      // 05: bipush 0
      // 06: new net/rim/device/api/crypto/RSACryptoSystem
      // 09: dup
      // 0a: getstatic net/rim/device/api/crypto/SoftwareRSACryptoToken._instance Lnet/rim/device/api/crypto/SoftwareRSACryptoToken;
      // 0d: sipush 1024
      // 10: invokespecial net/rim/device/api/crypto/RSACryptoSystem.<init> (Lnet/rim/device/api/crypto/RSACryptoToken;I)V
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
   public final RSAKeyPair createRSAKeyPair(RSACryptoSystem param1, byte[] param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnull 008
      // 004: aload 2
      // 005: ifnonnull 010
      // 008: new java/lang/Object
      // 00b: dup
      // 00c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 00f: athrow
      // 010: aload 1
      // 011: invokevirtual net/rim/device/api/crypto/RSACryptoSystem.getBitLength ()I
      // 014: bipush 1
      // 015: invokestatic net/rim/device/api/crypto/NativeRSA.isSupported (II)Z
      // 018: ifne 023
      // 01b: new java/lang/Object
      // 01e: dup
      // 01f: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> ()V
      // 022: athrow
      // 023: aload 1
      // 024: invokevirtual net/rim/device/api/crypto/RSACryptoSystem.getModulusLength ()I
      // 027: istore 3
      // 028: iload 3
      // 029: bipush 3
      // 02b: iand
      // 02c: ifeq 037
      // 02f: new java/lang/Object
      // 032: dup
      // 033: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 036: athrow
      // 037: iload 3
      // 038: newarray 8
      // 03a: astore 4
      // 03c: iload 3
      // 03d: newarray 8
      // 03f: astore 5
      // 041: iload 3
      // 042: bipush 2
      // 044: idiv
      // 045: istore 6
      // 047: iload 6
      // 049: newarray 8
      // 04b: astore 7
      // 04d: iload 6
      // 04f: newarray 8
      // 051: astore 8
      // 053: iload 6
      // 055: newarray 8
      // 057: astore 9
      // 059: iload 6
      // 05b: newarray 8
      // 05d: astore 10
      // 05f: iload 6
      // 061: newarray 8
      // 063: astore 11
      // 065: iload 3
      // 066: bipush 3
      // 068: ishl
      // 069: istore 12
      // 06b: invokestatic net/rim/device/api/crypto/Certicom.assertAccessAllowed ()V
      // 06e: iload 12
      // 070: aload 2
      // 071: aload 4
      // 073: aload 5
      // 075: aload 7
      // 077: aload 8
      // 079: aload 9
      // 07b: aload 10
      // 07d: aload 11
      // 07f: invokestatic net/rim/device/api/crypto/NativeRSA.generateKeyPair (I[B[B[B[B[B[B[B[B)V
      // 082: new net/rim/device/api/crypto/SoftwareRSACryptoToken$RSAPublicKeyData
      // 085: dup
      // 086: aload 1
      // 087: aload 2
      // 088: aload 5
      // 08a: invokespecial net/rim/device/api/crypto/SoftwareRSACryptoToken$RSAPublicKeyData.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;[B[B)V
      // 08d: astore 13
      // 08f: new net/rim/device/api/crypto/SoftwareRSACryptoToken$RSAPrivateKeyData
      // 092: dup
      // 093: aload 1
      // 094: aload 2
      // 095: aload 4
      // 097: aload 5
      // 099: aload 7
      // 09b: aload 8
      // 09d: aload 9
      // 09f: aload 10
      // 0a1: aload 11
      // 0a3: invokespecial net/rim/device/api/crypto/SoftwareRSACryptoToken$RSAPrivateKeyData.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;[B[B[B[B[B[B[B[B)V
      // 0a6: astore 14
      // 0a8: iload 3
      // 0a9: newarray 8
      // 0ab: astore 15
      // 0ad: iload 3
      // 0ae: newarray 8
      // 0b0: astore 16
      // 0b2: iload 3
      // 0b3: newarray 8
      // 0b5: astore 17
      // 0b7: aload 15
      // 0b9: bipush 1
      // 0ba: iload 3
      // 0bb: bipush 1
      // 0bc: isub
      // 0bd: invokestatic net/rim/device/api/crypto/RandomSource.getBytes ([BII)V
      // 0c0: aload 15
      // 0c2: bipush 1
      // 0c3: dup2
      // 0c4: baload
      // 0c5: bipush 2
      // 0c7: ior
      // 0c8: i2b
      // 0c9: bastore
      // 0ca: aload 1
      // 0cb: aload 13
      // 0cd: aload 15
      // 0cf: bipush 0
      // 0d0: aload 16
      // 0d2: bipush 0
      // 0d3: invokestatic net/rim/device/api/crypto/SoftwareRSACryptoToken.publicKeyOperation (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;[BI[BI)V
      // 0d6: aload 15
      // 0d8: aload 16
      // 0da: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 0dd: ifeq 0e8
      // 0e0: new java/lang/Object
      // 0e3: dup
      // 0e4: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 0e7: athrow
      // 0e8: aload 1
      // 0e9: aload 14
      // 0eb: aload 16
      // 0ed: bipush 0
      // 0ee: aload 17
      // 0f0: bipush 0
      // 0f1: invokestatic net/rim/device/api/crypto/SoftwareRSACryptoToken.privateKeyOperation (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;[BI[BI)V
      // 0f4: aload 15
      // 0f6: aload 17
      // 0f8: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 0fb: ifne 106
      // 0fe: new java/lang/Object
      // 101: dup
      // 102: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 105: athrow
      // 106: new net/rim/device/api/crypto/RSAKeyPair
      // 109: dup
      // 10a: new net/rim/device/api/crypto/RSAPublicKey
      // 10d: dup
      // 10e: aload 1
      // 10f: aload 13
      // 111: invokespecial net/rim/device/api/crypto/RSAPublicKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;)V
      // 114: new net/rim/device/api/crypto/RSAPrivateKey
      // 117: dup
      // 118: aload 1
      // 119: aload 14
      // 11b: invokespecial net/rim/device/api/crypto/RSAPrivateKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 11e: invokespecial net/rim/device/api/crypto/RSAKeyPair.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/RSAPrivateKey;)V
      // 121: areturn
      // 122: astore 13
      // 124: new java/lang/Object
      // 127: dup
      // 128: aload 13
      // 12a: invokevirtual net/rim/device/api/crypto/InvalidKeyException.toString ()Ljava/lang/String;
      // 12d: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 130: athrow
      // 131: astore 13
      // 133: new java/lang/Object
      // 136: dup
      // 137: aload 13
      // 139: invokevirtual net/rim/device/api/crypto/InvalidKeyPairException.toString ()Ljava/lang/String;
      // 13c: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 13f: athrow
      // 140: astore 13
      // 142: new java/lang/Object
      // 145: dup
      // 146: aload 13
      // 148: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 14b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 14e: athrow
      // try (68 -> 154): 155 null
      // try (68 -> 154): 162 net/rim/device/api/crypto/InvalidKeyPairException
      // try (68 -> 154): 169 null
   }

   @Override
   public final CryptoTokenPublicKeyData injectRSAPublicKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] n) {
      return new SoftwareRSACryptoToken$RSAPublicKeyData(cryptoSystem, e, n);
   }

   @Override
   public final CryptoTokenPrivateKeyData injectRSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n) {
      return new SoftwareRSACryptoToken$RSAPrivateKeyData(cryptoSystem, e, d, n);
   }

   @Override
   public final CryptoTokenPrivateKeyData injectRSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] p, byte[] q) {
      return new SoftwareRSACryptoToken$RSAPrivateKeyData(cryptoSystem, e, d, p, q);
   }

   @Override
   public final CryptoTokenPrivateKeyData injectRSAPrivateKey(
      RSACryptoSystem cryptoSystem, byte[] e, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP
   ) {
      return new SoftwareRSACryptoToken$RSAPrivateKeyData(cryptoSystem, e, p, q, dModPm1, dModQm1, qInvModP);
   }

   @Override
   public final CryptoTokenPrivateKeyData injectRSAPrivateKey(
      RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP
   ) {
      return new SoftwareRSACryptoToken$RSAPrivateKeyData(cryptoSystem, e, d, n, p, q, dModPm1, dModQm1, qInvModP);
   }

   @Override
   public final void deleteRSAPublicKey(CryptoTokenPublicKeyData data) {
   }

   @Override
   public final void deleteRSAPrivateKey(CryptoTokenPrivateKeyData data) {
   }

   @Override
   public final boolean isSupportedEncryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData) {
      return NativeRSA.isSupported(cryptoSystem.getBitLength(), 2);
   }

   @Override
   public final void encryptRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      publicKeyOperation(cryptoSystem, publicKeyData, input, inputOffset, output, outputOffset);
   }

   @Override
   public final boolean isSupportedDecryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData) {
      return NativeRSA.isSupported(cryptoSystem.getBitLength(), 4);
   }

   @Override
   public final void decryptRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      privateKeyOperation(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset);
   }

   @Override
   public final void signRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      if (!NativeRSA.isSupported(cryptoSystem.getBitLength(), 4)) {
         throw new Object();
      }

      privateKeyOperation(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset);
   }

   @Override
   public final void verifyRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      if (!NativeRSA.isSupported(cryptoSystem.getBitLength(), 2)) {
         throw new Object();
      }

      publicKeyOperation(cryptoSystem, publicKeyData, input, inputOffset, output, outputOffset);
   }

   private static final void publicKeyOperation(
      RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData cryptoTokenPublicKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      if (!(cryptoTokenPublicKeyData instanceof SoftwareRSACryptoToken$RSAPublicKeyData)) {
         throw new Object();
      }

      SoftwareRSACryptoToken$RSAPublicKeyData publicKeyData = (SoftwareRSACryptoToken$RSAPublicKeyData)cryptoTokenPublicKeyData;
      Certicom.assertAccessAllowed();
      NativeRSA.publicKeyOperation(cryptoSystem.getBitLength(), publicKeyData.getE(), publicKeyData.getN(), input, inputOffset, output, outputOffset);
   }

   private static final void privateKeyOperation(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      if (!(cryptoTokenPrivateKeyData instanceof SoftwareRSACryptoToken$RSAPrivateKeyData)) {
         throw new Object();
      }

      SoftwareRSACryptoToken$RSAPrivateKeyData privateKeyData = (SoftwareRSACryptoToken$RSAPrivateKeyData)cryptoTokenPrivateKeyData;
      Certicom.assertAccessAllowed();
      if (privateKeyData.getP() == null) {
         NativeRSA.privateKeyOperation(
            cryptoSystem.getBitLength(), privateKeyData.getE(), privateKeyData.getD(), privateKeyData.getN(), input, inputOffset, output, outputOffset
         );
      } else if (privateKeyData.getDModPm1() == null) {
         NativeRSA.privateKeyOperation(
            cryptoSystem.getBitLength(),
            privateKeyData.getE(),
            privateKeyData.getD(),
            privateKeyData.getP(),
            privateKeyData.getQ(),
            input,
            inputOffset,
            output,
            outputOffset
         );
      } else {
         NativeRSA.privateKeyOperation(
            cryptoSystem.getBitLength(),
            privateKeyData.getE(),
            privateKeyData.getP(),
            privateKeyData.getQ(),
            privateKeyData.getDModPm1(),
            privateKeyData.getDModQm1(),
            privateKeyData.getQInvModP(),
            input,
            inputOffset,
            output,
            outputOffset
         );
      }
   }

   @Override
   public final int hashCode() {
      return 902688471;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareRSACryptoToken;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT_RSA = new byte[]{
         67,
         33,
         -55,
         -26,
         118,
         103,
         32,
         -122,
         -42,
         64,
         12,
         -31,
         -2,
         28,
         89,
         43,
         37,
         26,
         117,
         -31,
         54,
         11,
         -100,
         -68,
         102,
         -45,
         111,
         81,
         -1,
         34,
         -109,
         -89,
         -20,
         15,
         88,
         -78,
         -38,
         32,
         29,
         92,
         -110,
         116,
         117,
         -125,
         -30,
         -77,
         68,
         76,
         -84,
         23,
         108,
         -96,
         -99,
         80,
         68,
         -127,
         -46,
         71,
         -108,
         -20,
         26,
         -77,
         -107,
         78
      };
      byte[] CIPHER_TEXT_RSA = new byte[]{
         86,
         -120,
         79,
         18,
         80,
         27,
         62,
         -19,
         37,
         28,
         94,
         -39,
         9,
         -119,
         -98,
         31,
         -125,
         55,
         105,
         12,
         -34,
         -63,
         115,
         -125,
         73,
         -48,
         -31,
         60,
         -46,
         102,
         -25,
         -113,
         -42,
         59,
         58,
         115,
         47,
         -106,
         97,
         127,
         8,
         -123,
         -14,
         -85,
         101,
         -28,
         115,
         40,
         126,
         -32,
         -52,
         80,
         -67,
         76,
         35,
         -57,
         114,
         -15,
         125,
         72,
         58,
         45,
         101,
         50
      };
      byte[] RSA_E = new byte[]{1, 0, 1};
      byte[] RSA_N = new byte[]{
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -3,
         -34,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         0,
         1,
         7,
         -111
      };
      int length = 64;
      byte[] target = new byte[length];

      try {
         SoftwareRSACryptoToken token = new SoftwareRSACryptoToken();
         RSACryptoSystem cryptoSystemRSA = new RSACryptoSystem(512);
         SoftwareRSACryptoToken$RSAPublicKeyData publicKeyData = new SoftwareRSACryptoToken$RSAPublicKeyData(cryptoSystemRSA, RSA_E, RSA_N);
         token.encryptRSA(cryptoSystemRSA, publicKeyData, PLAIN_TEXT_RSA, 0, target, 0);
         if (Arrays.equals(target, CIPHER_TEXT_RSA)) {
            return;
         }
      } catch (UnsupportedCryptoSystemException var11) {
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(542271390387802543L) == null) {
         selfTest();
         appRegistry.put(542271390387802543L, appRegistry);
      }
   }
}
