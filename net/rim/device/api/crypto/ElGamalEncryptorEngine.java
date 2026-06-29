package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class ElGamalEncryptorEngine implements PublicKeyEncryptorEngine {
   private DHPublicKey _publickey;
   private DHKeyPair _ephemeralKeyPair;
   private int _blocksize;
   private boolean _available;

   public final DHPublicKey getEphemeralKey() {
      return this._ephemeralKeyPair.getDHPublicKey();
   }

   @Override
   public final void encrypt(byte[] param1, int param2, byte[] param3, int param4) throws CryptoTokenException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 28
      // 04: iload 2
      // 05: iflt 28
      // 08: aload 1
      // 09: arraylength
      // 0a: aload 0
      // 0b: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._blocksize I
      // 0e: isub
      // 0f: iload 2
      // 10: if_icmplt 28
      // 13: aload 3
      // 14: ifnull 28
      // 17: iload 4
      // 19: iflt 28
      // 1c: aload 3
      // 1d: arraylength
      // 1e: aload 0
      // 1f: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._blocksize I
      // 22: isub
      // 23: iload 4
      // 25: if_icmpge 30
      // 28: new java/lang/IllegalArgumentException
      // 2b: dup
      // 2c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 2f: athrow
      // 30: aload 0
      // 31: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._available Z
      // 34: ifne 3f
      // 37: new java/lang/IllegalStateException
      // 3a: dup
      // 3b: invokespecial java/lang/IllegalStateException.<init> ()V
      // 3e: athrow
      // 3f: aload 0
      // 40: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._ephemeralKeyPair Lnet/rim/device/api/crypto/DHKeyPair;
      // 43: invokevirtual net/rim/device/api/crypto/DHKeyPair.getDHCryptoSystem ()Lnet/rim/device/api/crypto/DHCryptoSystem;
      // 46: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getP ()[B
      // 49: astore 5
      // 4b: aload 0
      // 4c: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._ephemeralKeyPair Lnet/rim/device/api/crypto/DHKeyPair;
      // 4f: invokevirtual net/rim/device/api/crypto/DHKeyPair.getDHPrivateKey ()Lnet/rim/device/api/crypto/DHPrivateKey;
      // 52: aload 0
      // 53: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._publickey Lnet/rim/device/api/crypto/DHPublicKey;
      // 56: bipush 0
      // 57: invokestatic net/rim/device/api/crypto/DHKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/DHPrivateKey;Lnet/rim/device/api/crypto/DHPublicKey;Z)[B
      // 5a: astore 6
      // 5c: aload 6
      // 5e: bipush 0
      // 5f: aload 6
      // 61: arraylength
      // 62: aload 1
      // 63: iload 2
      // 64: aload 0
      // 65: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._blocksize I
      // 68: aload 5
      // 6a: bipush 0
      // 6b: aload 5
      // 6d: arraylength
      // 6e: aload 3
      // 6f: iload 4
      // 71: aload 0
      // 72: getfield net/rim/device/api/crypto/ElGamalEncryptorEngine._blocksize I
      // 75: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.multiply ([BII[BII[BII[BII)V
      // 78: goto a8
      // 7b: astore 5
      // 7d: new net/rim/device/api/crypto/CryptoTokenException
      // 80: dup
      // 81: aload 5
      // 83: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 86: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // 89: athrow
      // 8a: astore 5
      // 8c: new net/rim/device/api/crypto/CryptoTokenException
      // 8f: dup
      // 90: aload 5
      // 92: invokevirtual net/rim/device/api/crypto/InvalidCryptoSystemException.toString ()Ljava/lang/String;
      // 95: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // 98: athrow
      // 99: astore 5
      // 9b: new net/rim/device/api/crypto/CryptoTokenException
      // 9e: dup
      // 9f: aload 5
      // a1: invokevirtual net/rim/device/api/crypto/InvalidKeyException.toString ()Ljava/lang/String;
      // a4: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // a7: athrow
      // a8: aload 0
      // a9: bipush 0
      // aa: putfield net/rim/device/api/crypto/ElGamalEncryptorEngine._available Z
      // ad: return
      // try (33 -> 63): 64 null
      // try (33 -> 63): 71 null
      // try (33 -> 63): 78 null
   }

   @Override
   public final String getAlgorithm() {
      return "ElGamal";
   }

   @Override
   public final int getBlockLength() {
      return this._blocksize;
   }

   public ElGamalEncryptorEngine(DHPublicKey remoteKey) {
      this(remoteKey, null);
   }

   public ElGamalEncryptorEngine(DHPublicKey remoteKey, DHKeyPair localEphemeralKeyPairPair) {
      if (remoteKey == null) {
         throw new IllegalArgumentException();
      }

      if (localEphemeralKeyPairPair == null) {
         localEphemeralKeyPairPair = new DHKeyPair(remoteKey.getDHCryptoSystem());
      } else if (!remoteKey.getDHCryptoSystem().equals(localEphemeralKeyPairPair.getDHCryptoSystem())) {
         throw new IllegalArgumentException();
      }

      this._publickey = remoteKey;
      this._ephemeralKeyPair = localEphemeralKeyPairPair;
      this._blocksize = remoteKey.getDHCryptoSystem().getP().length;
      this._available = true;
   }

   public static final void selfTest() {
      try {
         DHCryptoSystem system = new DHCryptoSystem("SUN1024");
         DHPublicKey key = new DHPublicKey(system, SelfTestData_PK2.ELGAMAL_LOCAL_PUBLIC_KEY);
         ElGamalEncryptorEngine engine = new ElGamalEncryptorEngine(
            key,
            new DHKeyPair(
               new DHPublicKey(system, SelfTestData_PK2.ELGAMAL_EPHEMERAL_PUBLIC_KEY),
               new DHPrivateKey(system, Arrays.copy(SelfTestData_PK2.ELGAMAL_EPHEMERAL_PRIVATE_KEY))
            )
         );
         byte[] ciphertext = new byte[engine.getBlockLength()];
         byte[] text = "testing ElGamal Encryptor Engine".getBytes();
         byte[] plaintext = new byte[engine.getBlockLength()];
         System.arraycopy(text, 0, plaintext, 1, text.length);
         engine.encrypt(plaintext, 0, ciphertext, 0);
         if (!Arrays.equals(ciphertext, SelfTestData_PK2.ELGAMAL_CIPHERTEXT)) {
            throw new CryptoSelfTestError();
         }
      } finally {
         throw new CryptoSelfTestError();
      }
   }

   static {
      long ID_TEST_ELGAMAL_ENCRYPTOR = -4912329971300133728L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_ELGAMAL_ENCRYPTOR) == null) {
         selfTest();
         appRegistry.put(ID_TEST_ELGAMAL_ENCRYPTOR, appRegistry);
      }
   }
}
