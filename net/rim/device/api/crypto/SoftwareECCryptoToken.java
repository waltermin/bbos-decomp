package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Persistable;

final class SoftwareECCryptoToken extends ECCryptoToken implements Persistable {
   private static SoftwareECCryptoToken _instance = new SoftwareECCryptoToken();
   private static final long ID_TEST_ECDSA = -4595467760303457643L;

   static final SoftwareECCryptoToken getInstance() {
      return _instance;
   }

   private SoftwareECCryptoToken() {
   }

   @Override
   public final ECCryptoSystem[] getSuggestedECCryptoSystems() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 18
      // 002: anewarray 40
      // 005: dup
      // 006: bipush 0
      // 007: new net/rim/device/api/crypto/ECCryptoSystem
      // 00a: dup
      // 00b: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 00e: ldc_w "EC163K1"
      // 011: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 014: aastore
      // 015: dup
      // 016: bipush 1
      // 017: new net/rim/device/api/crypto/ECCryptoSystem
      // 01a: dup
      // 01b: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 01e: ldc_w "EC163K2"
      // 021: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 024: aastore
      // 025: dup
      // 026: bipush 2
      // 028: new net/rim/device/api/crypto/ECCryptoSystem
      // 02b: dup
      // 02c: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 02f: ldc_w "EC163R2"
      // 032: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 035: aastore
      // 036: dup
      // 037: bipush 3
      // 039: new net/rim/device/api/crypto/ECCryptoSystem
      // 03c: dup
      // 03d: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 040: ldc_w "EC233K1"
      // 043: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 046: aastore
      // 047: dup
      // 048: bipush 4
      // 04a: new net/rim/device/api/crypto/ECCryptoSystem
      // 04d: dup
      // 04e: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 051: ldc_w "EC233R1"
      // 054: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 057: aastore
      // 058: dup
      // 059: bipush 5
      // 05b: new net/rim/device/api/crypto/ECCryptoSystem
      // 05e: dup
      // 05f: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 062: ldc_w "EC239K1"
      // 065: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 068: aastore
      // 069: dup
      // 06a: bipush 6
      // 06c: new net/rim/device/api/crypto/ECCryptoSystem
      // 06f: dup
      // 070: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 073: ldc_w "EC283K1"
      // 076: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 079: aastore
      // 07a: dup
      // 07b: bipush 7
      // 07d: new net/rim/device/api/crypto/ECCryptoSystem
      // 080: dup
      // 081: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 084: ldc_w "EC283R1"
      // 087: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 08a: aastore
      // 08b: dup
      // 08c: bipush 8
      // 08e: new net/rim/device/api/crypto/ECCryptoSystem
      // 091: dup
      // 092: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 095: ldc_w "EC409K1"
      // 098: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 09b: aastore
      // 09c: dup
      // 09d: bipush 9
      // 09f: new net/rim/device/api/crypto/ECCryptoSystem
      // 0a2: dup
      // 0a3: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 0a6: ldc_w "EC409R1"
      // 0a9: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 0ac: aastore
      // 0ad: dup
      // 0ae: bipush 10
      // 0b0: new net/rim/device/api/crypto/ECCryptoSystem
      // 0b3: dup
      // 0b4: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 0b7: ldc_w "EC571K1"
      // 0ba: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 0bd: aastore
      // 0be: dup
      // 0bf: bipush 11
      // 0c1: new net/rim/device/api/crypto/ECCryptoSystem
      // 0c4: dup
      // 0c5: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 0c8: ldc_w "EC571R1"
      // 0cb: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 0ce: aastore
      // 0cf: dup
      // 0d0: bipush 12
      // 0d2: new net/rim/device/api/crypto/ECCryptoSystem
      // 0d5: dup
      // 0d6: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 0d9: ldc_w "EC160R1"
      // 0dc: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 0df: aastore
      // 0e0: dup
      // 0e1: bipush 13
      // 0e3: new net/rim/device/api/crypto/ECCryptoSystem
      // 0e6: dup
      // 0e7: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 0ea: ldc_w "EC192R1"
      // 0ed: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 0f0: aastore
      // 0f1: dup
      // 0f2: bipush 14
      // 0f4: new net/rim/device/api/crypto/ECCryptoSystem
      // 0f7: dup
      // 0f8: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 0fb: ldc_w "EC224R1"
      // 0fe: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 101: aastore
      // 102: dup
      // 103: bipush 15
      // 105: new net/rim/device/api/crypto/ECCryptoSystem
      // 108: dup
      // 109: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 10c: ldc_w "EC256R1"
      // 10f: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 112: aastore
      // 113: dup
      // 114: bipush 16
      // 116: new net/rim/device/api/crypto/ECCryptoSystem
      // 119: dup
      // 11a: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 11d: ldc_w "EC384R1"
      // 120: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 123: aastore
      // 124: dup
      // 125: bipush 17
      // 127: new net/rim/device/api/crypto/ECCryptoSystem
      // 12a: dup
      // 12b: getstatic net/rim/device/api/crypto/SoftwareECCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 12e: ldc_w "EC521R1"
      // 131: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 134: aastore
      // 135: areturn
      // 136: astore 1
      // 137: new java/lang/Object
      // 13a: dup
      // 13b: aload 1
      // 13c: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 13f: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 142: athrow
      // 143: astore 1
      // 144: new java/lang/Object
      // 147: dup
      // 148: aload 1
      // 149: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 14c: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 14f: athrow
      // 150: astore 1
      // 151: new java/lang/Object
      // 154: dup
      // 155: aload 1
      // 156: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 159: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 15c: athrow
      // try (0 -> 146): 147 null
      // try (0 -> 146): 154 null
      // try (0 -> 146): 161 null
   }

   @Override
   public final CryptoTokenCryptoSystemData getECCryptoSystemData(String name) {
      return new SoftwareECCryptoToken$ECCryptoSystemData(name);
   }

   @Override
   public final String getECCryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getName();
   }

   @Override
   public final int getECCryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getBitLength();
   }

   @Override
   public final int getECCryptoSystemFieldLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getFieldLength();
   }

   @Override
   public final byte[] getECCryptoSystemBasePoint(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getBasePoint();
   }

   @Override
   public final byte[] getECCryptoSystemGroupOrder(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getGroupOrder();
   }

   @Override
   public final byte[] getECCryptoSystemA(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getA();
   }

   @Override
   public final byte[] getECCryptoSystemB(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getB();
   }

   @Override
   public final byte[] getECCryptoSystemCofactor(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getCofactor();
   }

   @Override
   public final byte[] getECCryptoSystemFieldReductor(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getFieldReductor();
   }

   @Override
   public final int getECPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData, boolean compress) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPublicKeyLength(compress);
   }

   @Override
   public final int getECPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPrivateKeyLength();
   }

   @Override
   public final ECKeyPair createECKeyPair(CryptoTokenCryptoSystemData param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: dup
      // 02: instanceof net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData
      // 05: ifne 0c
      // 08: pop
      // 09: goto e5
      // 0c: checkcast net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData
      // 0f: astore 2
      // 10: aload 2
      // 11: bipush 1
      // 12: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getPublicKeyLength (Z)I
      // 15: newarray 8
      // 17: astore 3
      // 18: aload 2
      // 19: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getPrivateKeyLength ()I
      // 1c: newarray 8
      // 1e: astore 4
      // 20: aload 2
      // 21: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getName ()Ljava/lang/String;
      // 24: bipush 1
      // 25: invokestatic net/rim/device/api/crypto/NativeEC.isSupported (Ljava/lang/String;I)Z
      // 28: ifne 33
      // 2b: new java/lang/Object
      // 2e: dup
      // 2f: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> ()V
      // 32: athrow
      // 33: invokestatic net/rim/device/api/crypto/Certicom.assertAccessAllowed ()V
      // 36: aload 2
      // 37: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getName ()Ljava/lang/String;
      // 3a: aload 4
      // 3c: aload 3
      // 3d: invokestatic net/rim/device/api/crypto/NativeEC.generateKeyPair (Ljava/lang/String;[B[B)V
      // 40: new net/rim/device/api/crypto/SoftwareECCryptoToken$ECPublicKeyData
      // 43: dup
      // 44: aload 2
      // 45: aload 3
      // 46: invokespecial net/rim/device/api/crypto/SoftwareECCryptoToken$ECPublicKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData;[B)V
      // 49: astore 5
      // 4b: new net/rim/device/api/crypto/SoftwareECCryptoToken$ECPrivateKeyData
      // 4e: dup
      // 4f: aload 2
      // 50: aload 4
      // 52: invokespecial net/rim/device/api/crypto/SoftwareECCryptoToken$ECPrivateKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData;[B)V
      // 55: astore 6
      // 57: bipush 20
      // 59: newarray 8
      // 5b: astore 7
      // 5d: aload 2
      // 5e: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getPrivateKeyLength ()I
      // 61: newarray 8
      // 63: astore 8
      // 65: aload 2
      // 66: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getPrivateKeyLength ()I
      // 69: newarray 8
      // 6b: astore 9
      // 6d: aload 0
      // 6e: aload 2
      // 6f: aload 6
      // 71: aload 7
      // 73: bipush 0
      // 74: aload 7
      // 76: arraylength
      // 77: aload 8
      // 79: bipush 0
      // 7a: aload 9
      // 7c: bipush 0
      // 7d: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken.signECDSA (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;[BII[BI[BI)V
      // 80: aload 0
      // 81: aload 2
      // 82: aload 5
      // 84: aload 7
      // 86: bipush 0
      // 87: aload 7
      // 89: arraylength
      // 8a: aload 8
      // 8c: bipush 0
      // 8d: aload 9
      // 8f: bipush 0
      // 90: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken.verifyECDSA (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;[BII[BI[BI)Z
      // 93: ifne 9e
      // 96: new java/lang/Object
      // 99: dup
      // 9a: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 9d: athrow
      // 9e: new net/rim/device/api/crypto/ECCryptoSystem
      // a1: dup
      // a2: aload 0
      // a3: aload 2
      // a4: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;)V
      // a7: astore 10
      // a9: new net/rim/device/api/crypto/ECKeyPair
      // ac: dup
      // ad: new net/rim/device/api/crypto/ECPublicKey
      // b0: dup
      // b1: aload 10
      // b3: aload 5
      // b5: invokespecial net/rim/device/api/crypto/ECPublicKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;)V
      // b8: new net/rim/device/api/crypto/ECPrivateKey
      // bb: dup
      // bc: aload 10
      // be: aload 6
      // c0: invokespecial net/rim/device/api/crypto/ECPrivateKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // c3: invokespecial net/rim/device/api/crypto/ECKeyPair.<init> (Lnet/rim/device/api/crypto/ECPublicKey;Lnet/rim/device/api/crypto/ECPrivateKey;)V
      // c6: areturn
      // c7: astore 5
      // c9: new java/lang/Object
      // cc: dup
      // cd: aload 5
      // cf: invokevirtual net/rim/device/api/crypto/InvalidKeyException.toString ()Ljava/lang/String;
      // d2: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // d5: athrow
      // d6: astore 5
      // d8: new java/lang/Object
      // db: dup
      // dc: aload 5
      // de: invokevirtual net/rim/device/api/crypto/InvalidKeyPairException.toString ()Ljava/lang/String;
      // e1: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // e4: athrow
      // e5: new java/lang/Object
      // e8: dup
      // e9: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // ec: athrow
      // try (32 -> 103): 104 null
      // try (32 -> 103): 111 null
   }

   @Override
   public final CryptoTokenPublicKeyData injectECPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData) {
         return new SoftwareECCryptoToken$ECPublicKeyData((SoftwareECCryptoToken$ECCryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final CryptoTokenPrivateKeyData injectECPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData) {
         return new SoftwareECCryptoToken$ECPrivateKeyData((SoftwareECCryptoToken$ECCryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void deleteECPublicKey(CryptoTokenPublicKeyData data) {
   }

   @Override
   public final void deleteECPrivateKey(CryptoTokenPrivateKeyData data) {
   }

   @Override
   public final byte[] extractECPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECPublicKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareECCryptoToken$ECPublicKeyData)cryptoTokenData).copyPublicKeyData();
      }
   }

   @Override
   public final byte[] extractECPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData, boolean compress) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECPublicKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareECCryptoToken$ECPublicKeyData)cryptoTokenData).copyPublicKeyData(compress);
      }
   }

   @Override
   public final byte[] extractECPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenData).copyPublicKeyData();
      }
   }

   @Override
   public final byte[] extractECPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData, boolean compress) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenData).copyPublicKeyData(compress);
      }
   }

   @Override
   public final byte[] extractECPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenData).copyPrivateKeyData();
      }
   }

   @Override
   public final byte[] generateECDHSharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalPrivateKeyData,
      byte[] remotePublicKeyData,
      boolean useCofactor
   ) {
      if (!(cryptoTokenCryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystemData = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenLocalPrivateKeyData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      }

      byte[] localPrivateKeyData = ((SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenLocalPrivateKeyData).getPrivateKeyData();
      if (!NativeEC.isSupported(cryptoSystemData.getName(), 4)) {
         throw new Object();
      }

      byte[] sharedSecret = new byte[cryptoSystemData.getFieldLength()];
      Certicom.assertAccessAllowed();
      if (useCofactor) {
         NativeEC.generateDHSharedSecretUseCofactor(cryptoSystemData.getName(), localPrivateKeyData, remotePublicKeyData, sharedSecret, 0);
      } else {
         NativeEC.generateDHSharedSecretNoCofactor(cryptoSystemData.getName(), localPrivateKeyData, remotePublicKeyData, sharedSecret, 0);
      }

      return sharedSecret;
   }

   @Override
   public final byte[] generateECMQVSharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalStaticPrivateKeyData,
      CryptoTokenPrivateKeyData cryptoTokenLocalEphemeralPrivateKeyData,
      CryptoTokenPublicKeyData cryptoTokenLocalEphemeralPublicKeyData,
      byte[] remoteStaticPublicKeyData,
      byte[] remoteEphemeralPublicKeyData,
      boolean useCofactor
   ) {
      if (!useCofactor) {
         throw new Object();
      }

      if (!(cryptoTokenCryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystemData = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenLocalStaticPrivateKeyData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      }

      byte[] localStaticPrivateKeyData = ((SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenLocalStaticPrivateKeyData).getPrivateKeyData();
      if (!(cryptoTokenLocalEphemeralPrivateKeyData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      }

      byte[] localEphemeralPrivateKeyData = ((SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenLocalEphemeralPrivateKeyData).getPrivateKeyData();
      if (!(cryptoTokenLocalEphemeralPublicKeyData instanceof SoftwareECCryptoToken$ECPublicKeyData)) {
         throw new Object();
      }

      byte[] localEphemeralPublicKeyData = ((SoftwareECCryptoToken$ECPublicKeyData)cryptoTokenLocalEphemeralPublicKeyData).getPublicKeyData();
      byte[] sharedSecret = new byte[cryptoSystemData.getFieldLength()];
      if (!NativeEC.isSupported(cryptoSystemData.getName(), 4)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      NativeEC.generateMQVSharedSecretUseCofactor(
         cryptoSystemData.getName(),
         localStaticPrivateKeyData,
         localEphemeralPrivateKeyData,
         localEphemeralPublicKeyData,
         remoteStaticPublicKeyData,
         remoteEphemeralPublicKeyData,
         sharedSecret,
         0
      );
      return sharedSecret;
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
      // 02: instanceof net/rim/device/api/crypto/ECCryptoSystem
      // 05: ifne 0c
      // 08: pop
      // 09: goto 1f
      // 0c: checkcast net/rim/device/api/crypto/ECCryptoSystem
      // 0f: astore 3
      // 10: aload 3
      // 11: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.getName ()Ljava/lang/String;
      // 14: iload 2
      // 15: invokestatic net/rim/device/api/crypto/NativeEC.isSupported (Ljava/lang/String;I)Z
      // 18: ireturn
      // 19: astore 4
      // 1b: bipush 0
      // 1c: ireturn
      // 1d: astore 4
      // 1f: bipush 0
      // 20: ireturn
      // try (8 -> 12): 13 null
      // try (8 -> 12): 16 null
   }

   @Override
   public final void signECDSA(
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
      if (!(cryptoTokenCryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystemData = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenPrivateKeyData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECPrivateKeyData privateKeyData = (SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenPrivateKeyData;
      if (!NativeEC.isSupported(cryptoSystemData.getName(), 4)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      NativeEC.signDSA(cryptoSystemData.getName(), privateKeyData.getPrivateKeyData(), digest, r, rOffset, s, sOffset);
   }

   @Override
   public final boolean verifyECDSA(
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
      if (!(cryptoTokenCryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystemData = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenPublicKeyData instanceof SoftwareECCryptoToken$ECPublicKeyData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECPublicKeyData publicKeyData = (SoftwareECCryptoToken$ECPublicKeyData)cryptoTokenPublicKeyData;
      if (!NativeEC.isSupported(cryptoSystemData.getName(), 2)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      return NativeEC.verifyDSA(cryptoSystemData.getName(), publicKeyData.getPublicKeyData(), digest, r, s);
   }

   @Override
   public final void signECNR(
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
      if (!(cryptoTokenCryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystemData = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenPrivateKeyData instanceof SoftwareECCryptoToken$ECPrivateKeyData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECPrivateKeyData privateKeyData = (SoftwareECCryptoToken$ECPrivateKeyData)cryptoTokenPrivateKeyData;
      if (!NativeEC.isSupported(cryptoSystemData.getName(), 4)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      NativeEC.signNR(cryptoSystemData.getName(), privateKeyData.getPrivateKeyData(), digest, r, rOffset, s, sOffset);
   }

   @Override
   public final boolean verifyECNR(
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
      if (!(cryptoTokenCryptoSystemData instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECCryptoSystemData cryptoSystemData = (SoftwareECCryptoToken$ECCryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenPublicKeyData instanceof SoftwareECCryptoToken$ECPublicKeyData)) {
         throw new Object();
      }

      SoftwareECCryptoToken$ECPublicKeyData publicKeyData = (SoftwareECCryptoToken$ECPublicKeyData)cryptoTokenPublicKeyData;
      if (!NativeEC.isSupported(cryptoSystemData.getName(), 2)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      return NativeEC.verifyNR(cryptoSystemData.getName(), publicKeyData.getPublicKeyData(), digest, r, s);
   }

   @Override
   public final int hashCode() {
      return -452745907;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareECCryptoToken;
   }

   public static final void selfTest() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 21
      // 002: newarray 8
      // 004: dup
      // 005: bipush 0
      // 006: bipush 0
      // 007: bastore
      // 008: dup
      // 009: bipush 1
      // 00a: bipush -86
      // 00c: bastore
      // 00d: dup
      // 00e: bipush 2
      // 00f: bipush 55
      // 011: bastore
      // 012: dup
      // 013: bipush 3
      // 014: bipush 79
      // 016: bastore
      // 017: dup
      // 018: bipush 4
      // 019: bipush -4
      // 01b: bastore
      // 01c: dup
      // 01d: bipush 5
      // 01e: bipush 60
      // 020: bastore
      // 021: dup
      // 022: bipush 6
      // 024: bipush -31
      // 026: bastore
      // 027: dup
      // 028: bipush 7
      // 02a: bipush 68
      // 02c: bastore
      // 02d: dup
      // 02e: bipush 8
      // 030: bipush -26
      // 032: bastore
      // 033: dup
      // 034: bipush 9
      // 036: bipush -80
      // 038: bastore
      // 039: dup
      // 03a: bipush 10
      // 03c: bipush 115
      // 03e: bastore
      // 03f: dup
      // 040: bipush 11
      // 042: bipush 48
      // 044: bastore
      // 045: dup
      // 046: bipush 12
      // 048: bipush 121
      // 04a: bastore
      // 04b: dup
      // 04c: bipush 13
      // 04e: bipush 114
      // 050: bastore
      // 051: dup
      // 052: bipush 14
      // 054: bipush -53
      // 056: bastore
      // 057: dup
      // 058: bipush 15
      // 05a: bipush 109
      // 05c: bastore
      // 05d: dup
      // 05e: bipush 16
      // 060: bipush 87
      // 062: bastore
      // 063: dup
      // 064: bipush 17
      // 066: bipush -78
      // 068: bastore
      // 069: dup
      // 06a: bipush 18
      // 06c: bipush -92
      // 06e: bastore
      // 06f: dup
      // 070: bipush 19
      // 072: bipush -23
      // 074: bastore
      // 075: dup
      // 076: bipush 20
      // 078: bipush -126
      // 07a: bastore
      // 07b: astore 0
      // 07c: invokestatic net/rim/device/api/crypto/SoftwareECCryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 07f: astore 1
      // 080: new net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData
      // 083: dup
      // 084: ldc_w "EC163K1"
      // 087: invokespecial net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.<init> (Ljava/lang/String;)V
      // 08a: astore 2
      // 08b: new net/rim/device/api/crypto/SoftwareECCryptoToken$ECPrivateKeyData
      // 08e: dup
      // 08f: aload 2
      // 090: aload 0
      // 091: invokespecial net/rim/device/api/crypto/SoftwareECCryptoToken$ECPrivateKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData;[B)V
      // 094: astore 3
      // 095: new net/rim/device/api/crypto/SoftwareECCryptoToken$ECPublicKeyData
      // 098: dup
      // 099: aload 2
      // 09a: aload 3
      // 09b: invokespecial net/rim/device/api/crypto/SoftwareECCryptoToken$ECPrivateKeyData.getPublicKeyData ()[B
      // 09e: invokespecial net/rim/device/api/crypto/SoftwareECCryptoToken$ECPublicKeyData.<init> (Lnet/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData;[B)V
      // 0a1: astore 4
      // 0a3: aload 2
      // 0a4: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getPrivateKeyLength ()I
      // 0a7: newarray 8
      // 0a9: astore 5
      // 0ab: aload 2
      // 0ac: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken$ECCryptoSystemData.getPrivateKeyLength ()I
      // 0af: newarray 8
      // 0b1: astore 6
      // 0b3: new java/lang/Object
      // 0b6: dup
      // 0b7: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 0ba: astore 7
      // 0bc: aload 7
      // 0be: getstatic net/rim/device/api/crypto/SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA [B
      // 0c1: bipush 0
      // 0c2: getstatic net/rim/device/api/crypto/SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA [B
      // 0c5: arraylength
      // 0c6: invokevirtual net/rim/device/api/crypto/SHA1Digest.update ([BII)V
      // 0c9: aload 7
      // 0cb: invokevirtual net/rim/device/api/crypto/SHA1Digest.getDigestLength ()I
      // 0ce: newarray 8
      // 0d0: astore 8
      // 0d2: aload 7
      // 0d4: aload 8
      // 0d6: bipush 0
      // 0d7: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 0da: pop
      // 0db: aload 1
      // 0dc: aload 2
      // 0dd: aload 3
      // 0de: aload 8
      // 0e0: bipush 0
      // 0e1: aload 7
      // 0e3: invokevirtual net/rim/device/api/crypto/SHA1Digest.getDigestLength ()I
      // 0e6: aload 5
      // 0e8: bipush 0
      // 0e9: aload 6
      // 0eb: bipush 0
      // 0ec: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken.signECDSA (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;[BII[BI[BI)V
      // 0ef: aload 1
      // 0f0: aload 2
      // 0f1: aload 4
      // 0f3: aload 8
      // 0f5: bipush 0
      // 0f6: aload 7
      // 0f8: invokevirtual net/rim/device/api/crypto/SHA1Digest.getDigestLength ()I
      // 0fb: aload 5
      // 0fd: bipush 0
      // 0fe: aload 6
      // 100: bipush 0
      // 101: invokevirtual net/rim/device/api/crypto/SoftwareECCryptoToken.verifyECDSA (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;[BII[BI[BI)Z
      // 104: ifeq 111
      // 107: return
      // 108: astore 1
      // 109: goto 111
      // 10c: astore 1
      // 10d: goto 111
      // 110: astore 1
      // 111: new java/lang/Object
      // 114: dup
      // 115: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 118: athrow
      // try (87 -> 159): 160 null
      // try (87 -> 159): 162 null
      // try (87 -> 159): 164 null
   }

   public static final void selfTest(String name) {
      SoftwareECCryptoToken$ECCryptoSystemData.selfTest(name, false);
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(-4595467760303457643L) == null) {
         selfTest();
         appRegistry.put(-4595467760303457643L, appRegistry);
      }
   }
}
