package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class RSACryptoSystem implements CryptoSystem, Persistable {
   private RSACryptoToken _cryptoToken;
   private int _modulusBitLength;
   private int _hashCode;

   public final RSAKeyPair createRSAKeyPair(byte[] e) {
      try {
         return this._cryptoToken.createRSAKeyPair(this, e);
      } catch (UnsupportedCryptoSystemException ex) {
         throw new Object(ex.toString());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final RSAKeyPair createRSAKeyPair() {
      try {
         return this.createRSAKeyPair(new byte[]{1, 0, 1});
      } catch (Throwable var3) {
         throw new Object(e.toString());
      }
   }

   public final int getModulusLength() {
      return this._modulusBitLength >>> 3;
   }

   @Override
   public final AsymmetricCryptoToken getAsymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final int getBitLength() {
      return this._modulusBitLength;
   }

   @Override
   public final boolean isStrong() {
      return this.getBitLength() >= 1024;
   }

   @Override
   public final String getName() {
      return ((StringBuffer)(new Object())).append(this.getAlgorithm()).append(this.getBitLength()).toString();
   }

   @Override
   public final KeyPair createKeyPair() {
      return this.createRSAKeyPair();
   }

   @Override
   public final String getAlgorithm() {
      return "RSA";
   }

   @Override
   public final void verify() {
   }

   public RSACryptoSystem(RSACryptoToken cryptoToken, int modulusBitLength) {
      this.initialize(cryptoToken, modulusBitLength);
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoToken.hashCode() + this._modulusBitLength;
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

      if (!(obj instanceof RSACryptoSystem)) {
         return false;
      }

      RSACryptoSystem other = (RSACryptoSystem)obj;
      return this._hashCode == other._hashCode && this._cryptoToken.equals(other._cryptoToken) && this._modulusBitLength == other._modulusBitLength;
   }

   private final void initialize(RSACryptoToken cryptoToken, int modulusBitLength) {
      if (cryptoToken != null && modulusBitLength >= 0) {
         this._cryptoToken = cryptoToken;
         this._modulusBitLength = modulusBitLength;
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public RSACryptoSystem(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 0
      // 05: invokestatic net/rim/device/api/crypto/SoftwareRSACryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareRSACryptoToken;
      // 08: iload 1
      // 09: invokespecial net/rim/device/api/crypto/RSACryptoSystem.initialize (Lnet/rim/device/api/crypto/RSACryptoToken;I)V
      // 0c: return
      // 0d: astore 2
      // 0e: new java/lang/Object
      // 11: dup
      // 12: aload 2
      // 13: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 16: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 19: athrow
      // 1a: astore 2
      // 1b: new java/lang/Object
      // 1e: dup
      // 1f: aload 2
      // 20: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 23: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 26: athrow
      // try (2 -> 6): 7 null
      // try (2 -> 6): 14 null
   }

   public RSACryptoSystem() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 0
      // 05: invokestatic net/rim/device/api/crypto/SoftwareRSACryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareRSACryptoToken;
      // 08: sipush 1024
      // 0b: invokespecial net/rim/device/api/crypto/RSACryptoSystem.initialize (Lnet/rim/device/api/crypto/RSACryptoToken;I)V
      // 0e: return
      // 0f: astore 1
      // 10: new java/lang/Object
      // 13: dup
      // 14: aload 1
      // 15: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 18: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1b: athrow
      // 1c: astore 1
      // 1d: new java/lang/Object
      // 20: dup
      // 21: aload 1
      // 22: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 25: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 28: athrow
      // 29: astore 1
      // 2a: new java/lang/Object
      // 2d: dup
      // 2e: aload 1
      // 2f: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 32: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 35: athrow
      // try (2 -> 6): 7 null
      // try (2 -> 6): 14 null
      // try (2 -> 6): 21 net/rim/device/api/crypto/UnsupportedCryptoSystemException
   }
}
