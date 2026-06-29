package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class ElGamalDecryptorEngine implements PrivateKeyDecryptorEngine {
   private DHPrivateKey _localPrivateKey;
   private int _blocksize;
   private DHPublicKey _ephemeralkey;
   private boolean _available;

   public ElGamalDecryptorEngine(DHPrivateKey localPrivateKey, DHPublicKey remoteEphemeralKey) {
      if (localPrivateKey != null && remoteEphemeralKey != null) {
         if (!localPrivateKey.getDHCryptoSystem().equals(remoteEphemeralKey.getDHCryptoSystem())) {
            throw new IllegalArgumentException();
         }

         this._localPrivateKey = localPrivateKey;
         this._blocksize = localPrivateKey.getDHCryptoSystem().getP().length;
         this._ephemeralkey = remoteEphemeralKey;
         this._available = true;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void decrypt(byte[] param1, int param2, byte[] param3, int param4) throws CryptoTokenException {
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
      // 0b: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._blocksize I
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
      // 1f: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._blocksize I
      // 22: isub
      // 23: iload 4
      // 25: if_icmpge 30
      // 28: new java/lang/IllegalArgumentException
      // 2b: dup
      // 2c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 2f: athrow
      // 30: aload 0
      // 31: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._available Z
      // 34: ifne 3f
      // 37: new java/lang/IllegalStateException
      // 3a: dup
      // 3b: invokespecial java/lang/IllegalStateException.<init> ()V
      // 3e: athrow
      // 3f: aload 0
      // 40: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._localPrivateKey Lnet/rim/device/api/crypto/DHPrivateKey;
      // 43: invokevirtual net/rim/device/api/crypto/DHPrivateKey.getDHCryptoSystem ()Lnet/rim/device/api/crypto/DHCryptoSystem;
      // 46: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getP ()[B
      // 49: astore 5
      // 4b: aload 0
      // 4c: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._localPrivateKey Lnet/rim/device/api/crypto/DHPrivateKey;
      // 4f: aload 0
      // 50: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._ephemeralkey Lnet/rim/device/api/crypto/DHPublicKey;
      // 53: bipush 0
      // 54: invokestatic net/rim/device/api/crypto/DHKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/DHPrivateKey;Lnet/rim/device/api/crypto/DHPublicKey;Z)[B
      // 57: astore 6
      // 59: aload 6
      // 5b: aload 5
      // 5d: aload 6
      // 5f: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.invert ([B[B[B)V
      // 62: aload 6
      // 64: bipush 0
      // 65: aload 6
      // 67: arraylength
      // 68: aload 1
      // 69: iload 2
      // 6a: aload 0
      // 6b: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._blocksize I
      // 6e: aload 5
      // 70: bipush 0
      // 71: aload 0
      // 72: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._blocksize I
      // 75: aload 3
      // 76: iload 4
      // 78: aload 0
      // 79: getfield net/rim/device/api/crypto/ElGamalDecryptorEngine._blocksize I
      // 7c: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.multiply ([BII[BII[BII[BII)V
      // 7f: goto af
      // 82: astore 5
      // 84: new net/rim/device/api/crypto/CryptoTokenException
      // 87: dup
      // 88: aload 5
      // 8a: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 8d: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // 90: athrow
      // 91: astore 5
      // 93: new net/rim/device/api/crypto/CryptoTokenException
      // 96: dup
      // 97: aload 5
      // 99: invokevirtual net/rim/device/api/crypto/InvalidCryptoSystemException.toString ()Ljava/lang/String;
      // 9c: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // 9f: athrow
      // a0: astore 5
      // a2: new net/rim/device/api/crypto/CryptoTokenException
      // a5: dup
      // a6: aload 5
      // a8: invokevirtual net/rim/device/api/crypto/InvalidKeyException.toString ()Ljava/lang/String;
      // ab: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // ae: athrow
      // af: aload 0
      // b0: bipush 0
      // b1: putfield net/rim/device/api/crypto/ElGamalDecryptorEngine._available Z
      // b4: return
      // try (33 -> 66): 67 null
      // try (33 -> 66): 74 null
      // try (33 -> 66): 81 null
   }

   @Override
   public final String getAlgorithm() {
      return "ElGamal";
   }

   @Override
   public final int getBlockLength() {
      return this._blocksize;
   }

   public static final void selfTest() {
      try {
         DHCryptoSystem system = new DHCryptoSystem("SUN1024");
         DHPrivateKey key = new DHPrivateKey(system, Arrays.copy(SelfTestData_PK2.ELGAMAL_LOCAL_PRIVATE_KEY));
         ElGamalDecryptorEngine engine = new ElGamalDecryptorEngine(key, new DHPublicKey(system, SelfTestData_PK2.ELGAMAL_EPHEMERAL_PUBLIC_KEY));
         byte[] text = "testing ElGamal Encryptor Engine".getBytes();
         byte[] plaintext = new byte[engine.getBlockLength()];
         System.arraycopy(text, 0, plaintext, 1, text.length);
         byte[] result = new byte[plaintext.length];
         engine.decrypt(SelfTestData_PK2.ELGAMAL_CIPHERTEXT, 0, result, 0);
         if (!Arrays.equals(plaintext, result)) {
            throw new CryptoSelfTestError();
         }
      } finally {
         throw new CryptoSelfTestError();
      }
   }

   static {
      long ID_TEST_ELGAMAL_DECRYPTOR = -2247509177259734125L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_ELGAMAL_DECRYPTOR) == null) {
         selfTest();
         appRegistry.put(ID_TEST_ELGAMAL_DECRYPTOR, appRegistry);
      }
   }
}
