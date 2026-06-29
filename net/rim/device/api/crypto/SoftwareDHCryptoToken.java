package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDHCryptoToken extends DHCryptoToken implements Persistable {
   private static SoftwareDHCryptoToken _instance = new SoftwareDHCryptoToken();
   private static final long ID_DH_TEST;

   static final SoftwareDHCryptoToken getInstance() {
      return _instance;
   }

   private SoftwareDHCryptoToken() {
   }

   @Override
   public final DHCryptoSystem[] getSuggestedDHCryptoSystems() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 1
      // 01: anewarray 37
      // 04: dup
      // 05: bipush 0
      // 06: new net/rim/device/api/crypto/DHCryptoSystem
      // 09: dup
      // 0a: getstatic net/rim/device/api/crypto/SoftwareDHCryptoToken._instance Lnet/rim/device/api/crypto/SoftwareDHCryptoToken;
      // 0d: ldc_w "SUN1024"
      // 10: bipush -1
      // 12: invokespecial net/rim/device/api/crypto/DHCryptoSystem.<init> (Lnet/rim/device/api/crypto/DHCryptoToken;Ljava/lang/String;I)V
      // 15: aastore
      // 16: areturn
      // 17: astore 1
      // 18: new java/lang/Object
      // 1b: dup
      // 1c: aload 1
      // 1d: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 20: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 23: athrow
      // 24: astore 1
      // 25: new java/lang/Object
      // 28: dup
      // 29: aload 1
      // 2a: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 2d: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 30: athrow
      // 31: astore 1
      // 32: new java/lang/Object
      // 35: dup
      // 36: aload 1
      // 37: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 3a: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 3d: athrow
      // try (0 -> 11): 12 null
      // try (0 -> 11): 19 null
      // try (0 -> 11): 26 net/rim/device/api/crypto/UnsupportedCryptoSystemException
   }

   @Override
   public final CryptoTokenCryptoSystemData getDHCryptoSystemData(byte[] p, byte[] q, byte[] g, int privateKeyMinRandomBits, String name) {
      return new SoftwareDHCryptoToken$DHCryptoSystemData(p, q, g, privateKeyMinRandomBits, name);
   }

   @Override
   public final int getDHCryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getBitLength();
   }

   @Override
   public final String getDHCryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getName();
   }

   @Override
   public final byte[] getDHCryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.copyP();
   }

   @Override
   public final byte[] getDHCryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.copyQ();
   }

   @Override
   public final byte[] getDHCryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.copyG();
   }

   @Override
   public final int getDHPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPublicKeyLength();
   }

   @Override
   public final int getDHPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPrivateKeyLength();
   }

   @Override
   public final int getDHPrivateKeyMinRandomBits(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      return cryptoSystem.getPrivateKeyMinRandomBits();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DHKeyPair createDHKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystemData = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenData;
      byte[] publicKeyBytes = new byte[cryptoSystemData.getPublicKeyLength()];
      byte[] privateKeyBytes = new byte[cryptoSystemData.getPrivateKeyMinRandomBits() + 7 >> 3];
      byte[] P = cryptoSystemData.getP();
      byte[] Q = cryptoSystemData.getQ();
      byte[] G = cryptoSystemData.getG();
      if (!NativeDL.isSupported(P, Q, G, cryptoSystemData.getPrivateKeyLength(), 1)) {
         throw new Object();
      }

      Certicom.assertAccessAllowed();
      NativeDL.generateKeyPair(P, Q, G, privateKeyBytes, publicKeyBytes);

      InvalidKeyPairException e;
      try {
         try {
            SoftwareDHCryptoToken$DHPublicKeyData publicKeyData = new SoftwareDHCryptoToken$DHPublicKeyData(cryptoSystemData, publicKeyBytes);
            SoftwareDHCryptoToken$DHPrivateKeyData privateKeyData = new SoftwareDHCryptoToken$DHPrivateKeyData(cryptoSystemData, privateKeyBytes);
            byte[] testPublicKeyBytes = new byte[cryptoSystemData.getPublicKeyLength()];
            byte[] testPrivateKeyBytes = new byte[cryptoSystemData.getPrivateKeyLength()];
            testPrivateKeyBytes[testPrivateKeyBytes.length - 1] = 2;
            CryptoByteArrayArithmetic.square(G, P, testPublicKeyBytes);
            SoftwareDHCryptoToken$DHPrivateKeyData testPrivateKeyData = new SoftwareDHCryptoToken$DHPrivateKeyData(cryptoSystemData, testPrivateKeyBytes);
            byte[] sharedSecret1 = this.generateDHSharedSecret(cryptoSystemData, privateKeyData, testPublicKeyBytes, false);
            byte[] sharedSecret2 = this.generateDHSharedSecret(cryptoSystemData, testPrivateKeyData, publicKeyBytes, false);
            if (!Arrays.equals(sharedSecret1, sharedSecret2)) {
               throw new Object();
            }

            DHCryptoSystem cryptoSystem = new DHCryptoSystem(this, cryptoSystemData);
            return new DHKeyPair(new DHPublicKey(cryptoSystem, publicKeyData), new DHPrivateKey(cryptoSystem, privateKeyData));
         } catch (InvalidKeyPairException var18) {
            e = var18;
         }
      } catch (Throwable var19) {
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
      // 02: instanceof net/rim/device/api/crypto/DHCryptoSystem
      // 05: ifne 0c
      // 08: pop
      // 09: goto 2b
      // 0c: checkcast net/rim/device/api/crypto/DHCryptoSystem
      // 0f: astore 3
      // 10: aload 3
      // 11: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getP ()[B
      // 14: aload 3
      // 15: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getQ ()[B
      // 18: aload 3
      // 19: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getG ()[B
      // 1c: aload 3
      // 1d: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getPrivateKeyLength ()I
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
   public final CryptoTokenPublicKeyData injectDHPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareDHCryptoToken$DHCryptoSystemData) {
         return new SoftwareDHCryptoToken$DHPublicKeyData((SoftwareDHCryptoToken$DHCryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final CryptoTokenPrivateKeyData injectDHPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      if (cryptoSystemData instanceof SoftwareDHCryptoToken$DHCryptoSystemData) {
         return new SoftwareDHCryptoToken$DHPrivateKeyData((SoftwareDHCryptoToken$DHCryptoSystemData)cryptoSystemData, data);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void deleteDHPublicKey(CryptoTokenPublicKeyData data) {
   }

   @Override
   public final void deleteDHPrivateKey(CryptoTokenPrivateKeyData data) {
   }

   @Override
   public final byte[] extractDHPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHPublicKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareDHCryptoToken$DHPublicKeyData)cryptoTokenData).copyPublicKeyData();
      }
   }

   @Override
   public final byte[] extractDHPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareDHCryptoToken$DHPrivateKeyData)cryptoTokenData).copyPublicKeyData();
      }
   }

   @Override
   public final byte[] extractDHPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareDHCryptoToken$DHPrivateKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareDHCryptoToken$DHPrivateKeyData)cryptoTokenData).copyPrivateKeyData();
      }
   }

   @Override
   public final byte[] generateDHSharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalPrivateKeyData,
      byte[] remotePublicKeyData,
      boolean useCofactor
   ) {
      if (useCofactor) {
         throw new Object();
      }

      if (!(cryptoTokenCryptoSystemData instanceof SoftwareDHCryptoToken$DHCryptoSystemData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystemData = (SoftwareDHCryptoToken$DHCryptoSystemData)cryptoTokenCryptoSystemData;
      if (!(cryptoTokenLocalPrivateKeyData instanceof SoftwareDHCryptoToken$DHPrivateKeyData)) {
         throw new Object();
      }

      SoftwareDHCryptoToken$DHPrivateKeyData localPrivateKeyData = (SoftwareDHCryptoToken$DHPrivateKeyData)cryptoTokenLocalPrivateKeyData;
      if (!NativeDL.isSupported(cryptoSystemData.getP(), cryptoSystemData.getQ(), cryptoSystemData.getG(), cryptoSystemData.getPrivateKeyLength(), 4)) {
         throw new Object();
      }

      byte[] sharedSecret = new byte[cryptoSystemData.getPublicKeyLength()];
      Certicom.assertAccessAllowed();
      NativeDL.generateDHSharedSecretNoCofactor(
         cryptoSystemData.getP(),
         cryptoSystemData.getQ(),
         cryptoSystemData.getG(),
         localPrivateKeyData.getPrivateKeyData(),
         remotePublicKeyData,
         sharedSecret,
         0
      );
      return sharedSecret;
   }

   @Override
   public final int hashCode() {
      return 783130379;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareDHCryptoToken;
   }

   public static final void selfTest() {
      byte[] SHARED_SECRET = new byte[]{
         -114,
         -101,
         -67,
         29,
         36,
         -24,
         -86,
         -55,
         -8,
         -126,
         -37,
         73,
         96,
         -63,
         86,
         3,
         99,
         14,
         39,
         -63,
         112,
         124,
         -32,
         112,
         80,
         2,
         -97,
         -106,
         -32,
         39,
         -39,
         -90,
         -110,
         104,
         -92,
         37,
         -93,
         35,
         -64,
         -84,
         -15,
         81,
         77,
         25,
         -120,
         -26,
         123,
         -69,
         -94,
         -60,
         -80,
         -31,
         125,
         -53,
         -105,
         -114,
         -87,
         -87,
         -47,
         33,
         70,
         83,
         125,
         -94
      };
      byte[] LOCAL_PRIVATE_KEY_DATA = new byte[]{
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
         -16,
         1,
         2,
         90,
         97,
         39,
         -42,
         -21,
         84,
         90,
         68,
         -112,
         -35,
         122,
         81,
         -38,
         44,
         102,
         82,
         21
      };
      byte[] REMOTE_PUBLIC_KEY_DATA = new byte[]{
         124,
         0,
         -67,
         -51,
         0,
         -65,
         -40,
         39,
         -76,
         101,
         -105,
         -81,
         36,
         -120,
         -96,
         -80,
         0,
         4,
         -90,
         60,
         -102,
         -20,
         96,
         -103,
         -63,
         120,
         33,
         105,
         17,
         119,
         38,
         -94,
         -91,
         63,
         16,
         -30,
         -46,
         76,
         109,
         123,
         93,
         -17,
         6,
         123,
         77,
         49,
         -6,
         -27,
         -116,
         -18,
         -50,
         34,
         21,
         69,
         89,
         77,
         19,
         -24,
         91,
         -70,
         25,
         86,
         -110,
         -38
      };
      byte[] target = new byte[SHARED_SECRET.length];

      try {
         DHCryptoSystem cryptoSystem = new DHCryptoSystem("SUN512");
         DHPrivateKey privateKey = new DHPrivateKey(cryptoSystem, LOCAL_PRIVATE_KEY_DATA);
         DHPublicKey publicKey = new DHPublicKey(cryptoSystem, REMOTE_PUBLIC_KEY_DATA);
         target = DHKeyAgreement.generateSharedSecret(privateKey, publicKey, false);
         if (Arrays.equals(target, SHARED_SECRET)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(4582469495395029045L) == null) {
         selfTest();
         appRegistry.put(4582469495395029045L, appRegistry);
      }
   }
}
