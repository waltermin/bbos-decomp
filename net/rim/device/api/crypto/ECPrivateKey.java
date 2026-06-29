package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class ECPrivateKey implements PrivateKey, ECKey, Persistable {
   private ECCryptoSystem _cryptoSystem;
   private ECCryptoToken _cryptoToken;
   private CryptoTokenPrivateKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;

   public final CryptoTokenPrivateKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final ECCryptoToken getECCryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getPublicKeyData() {
      return this._cryptoToken.extractECPublicKeyData(this._cryptoTokenData);
   }

   public final byte[] getPrivateKeyData() {
      return this._cryptoToken.extractECPrivateKeyData(this._cryptoTokenData);
   }

   @Override
   public final void verify() throws InvalidKeyException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/ECPrivateKey._verified Z
      // 04: ifeq 08
      // 07: return
      // 08: aload 0
      // 09: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getECCryptoSystem ()Lnet/rim/device/api/crypto/ECCryptoSystem;
      // 0c: astore 1
      // 0d: aload 1
      // 0e: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.verify ()V
      // 11: aload 1
      // 12: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.getPrivateKeyLength ()I
      // 15: istore 2
      // 16: aload 0
      // 17: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getPrivateKeyData ()[B
      // 1a: astore 3
      // 1b: aload 3
      // 1c: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.isZero ([B)Z
      // 1f: ifeq 2a
      // 22: new net/rim/device/api/crypto/InvalidKeyException
      // 25: dup
      // 26: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> ()V
      // 29: athrow
      // 2a: aload 3
      // 2b: arraylength
      // 2c: iload 2
      // 2d: if_icmple 53
      // 30: new net/rim/device/api/crypto/InvalidKeyException
      // 33: dup
      // 34: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> ()V
      // 37: athrow
      // 38: astore 2
      // 39: new net/rim/device/api/crypto/InvalidKeyException
      // 3c: dup
      // 3d: aload 2
      // 3e: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 41: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> (Ljava/lang/String;)V
      // 44: athrow
      // 45: astore 2
      // 46: new net/rim/device/api/crypto/InvalidKeyException
      // 49: dup
      // 4a: aload 2
      // 4b: invokevirtual net/rim/device/api/crypto/InvalidCryptoSystemException.toString ()Ljava/lang/String;
      // 4e: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> (Ljava/lang/String;)V
      // 51: athrow
      // 52: astore 2
      // 53: aload 0
      // 54: bipush 1
      // 55: putfield net/rim/device/api/crypto/ECPrivateKey._verified Z
      // 58: return
      // 59: astore 2
      // 5a: return
      // try (7 -> 30): 30 null
      // try (7 -> 30): 37 null
      // try (7 -> 30): 44 null
      // try (45 -> 48): 49 null
   }

   @Override
   public final CryptoSystem getCryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final ECCryptoSystem getECCryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final String getAlgorithm() {
      return "EC";
   }

   private final void initialize(ECCryptoSystem cryptoSystem, ECCryptoToken cryptoToken, CryptoTokenPrivateKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public ECPrivateKey(ECCryptoSystem cryptoSystem, CryptoTokenPrivateKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         ECCryptoToken cryptoToken = (ECCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public ECPrivateKey(ECCryptoSystem cryptoSystem, byte[] data) throws InvalidKeyException {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         if (data.length > cryptoSystem.getPrivateKeyLength()) {
            throw new InvalidKeyException();
         }

         ECCryptoToken cryptoToken = (ECCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectECPrivateKey(cryptoSystem.getCryptoTokenData(), data));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoSystem.hashCode() ^ this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ECPrivateKey)) {
         return false;
      }

      ECPrivateKey other = (ECPrivateKey)obj;
      return this._hashCode == other._hashCode
         && this._cryptoSystem.equals(other._cryptoSystem)
         && this._cryptoToken.equals(other._cryptoToken)
         && this._cryptoTokenData.equals(other._cryptoTokenData);
   }
}
