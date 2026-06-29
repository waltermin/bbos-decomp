package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class RSAPublicKey implements PublicKey, RSAKey, Persistable {
   private RSACryptoSystem _cryptoSystem;
   private RSACryptoToken _cryptoToken;
   private CryptoTokenPublicKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   private boolean _usePublicKeyData;

   public final CryptoTokenPublicKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final RSACryptoToken getRSACryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getN() {
      return this._cryptoToken.extractRSAPublicKeyN(this._cryptoTokenData);
   }

   public final byte[] getE() {
      return this._cryptoToken.extractRSAPublicKeyE(this._cryptoTokenData);
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         RSACryptoSystem cs = this.getRSACryptoSystem();
         cs.verify();

         try {
            this._verified = true;
         } finally {
            return;
         }
      }
   }

   @Override
   public final CryptoSystem getCryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final RSACryptoSystem getRSACryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final String getAlgorithm() {
      return "RSA";
   }

   private final void initialize(RSACryptoSystem cryptoSystem, RSACryptoToken cryptoToken, CryptoTokenPublicKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public RSAPublicKey(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         RSACryptoToken cryptoToken = (RSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RSAPublicKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] n) throws InvalidKeyException {
      if (cryptoSystem != null && e != null && n != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         e = CryptoByteArrayArithmetic.trim(e);
         n = CryptoByteArrayArithmetic.trim(n);
         if (n.length == modulusLength && e.length <= n.length) {
            RSACryptoToken cryptoToken = (RSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
            this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectRSAPublicKey(cryptoSystem, e, n));
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void setHashCode() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush -1
      // 03: aload 0
      // 04: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getE ()[B
      // 07: invokestatic net/rim/device/api/util/CRC32.update (I[B)I
      // 0a: bipush -1
      // 0c: aload 0
      // 0d: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getN ()[B
      // 10: invokestatic net/rim/device/api/util/CRC32.update (I[B)I
      // 13: ixor
      // 14: putfield net/rim/device/api/crypto/RSAPublicKey._hashCode I
      // 17: aload 0
      // 18: bipush 1
      // 19: putfield net/rim/device/api/crypto/RSAPublicKey._usePublicKeyData Z
      // 1c: return
      // 1d: astore 1
      // 1e: return
      // 1f: astore 1
      // 20: aload 0
      // 21: aload 0
      // 22: getfield net/rim/device/api/crypto/RSAPublicKey._cryptoToken Lnet/rim/device/api/crypto/RSACryptoToken;
      // 25: invokevirtual net/rim/device/api/crypto/RSACryptoToken.hashCode ()I
      // 28: aload 0
      // 29: getfield net/rim/device/api/crypto/RSAPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 2c: invokevirtual net/rim/device/api/crypto/CryptoTokenPublicKeyData.hashCode ()I
      // 2f: ixor
      // 30: putfield net/rim/device/api/crypto/RSAPublicKey._hashCode I
      // 33: aload 0
      // 34: bipush 0
      // 35: putfield net/rim/device/api/crypto/RSAPublicKey._usePublicKeyData Z
      // 38: return
      // try (0 -> 14): 15 null
      // try (0 -> 14): 17 null
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final boolean equals(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: if_acmpne 07
      // 05: bipush 1
      // 06: ireturn
      // 07: aload 1
      // 08: dup
      // 09: instanceof net/rim/device/api/crypto/RSAPublicKey
      // 0c: ifne 13
      // 0f: pop
      // 10: goto 78
      // 13: checkcast net/rim/device/api/crypto/RSAPublicKey
      // 16: astore 2
      // 17: aload 0
      // 18: getfield net/rim/device/api/crypto/RSAPublicKey._usePublicKeyData Z
      // 1b: ifeq 58
      // 1e: aload 2
      // 1f: getfield net/rim/device/api/crypto/RSAPublicKey._usePublicKeyData Z
      // 22: ifeq 58
      // 25: aload 0
      // 26: getfield net/rim/device/api/crypto/RSAPublicKey._hashCode I
      // 29: aload 2
      // 2a: getfield net/rim/device/api/crypto/RSAPublicKey._hashCode I
      // 2d: if_icmpne 50
      // 30: aload 0
      // 31: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getE ()[B
      // 34: aload 2
      // 35: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getE ()[B
      // 38: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 3b: ifeq 50
      // 3e: aload 0
      // 3f: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getN ()[B
      // 42: aload 2
      // 43: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getN ()[B
      // 46: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 49: ifeq 50
      // 4c: bipush 1
      // 4d: goto 51
      // 50: bipush 0
      // 51: ireturn
      // 52: astore 3
      // 53: bipush 0
      // 54: ireturn
      // 55: astore 3
      // 56: bipush 0
      // 57: ireturn
      // 58: aload 0
      // 59: getfield net/rim/device/api/crypto/RSAPublicKey._cryptoToken Lnet/rim/device/api/crypto/RSACryptoToken;
      // 5c: aload 2
      // 5d: getfield net/rim/device/api/crypto/RSAPublicKey._cryptoToken Lnet/rim/device/api/crypto/RSACryptoToken;
      // 60: invokevirtual net/rim/device/api/crypto/RSACryptoToken.equals (Ljava/lang/Object;)Z
      // 63: ifeq 76
      // 66: aload 0
      // 67: getfield net/rim/device/api/crypto/RSAPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 6a: aload 2
      // 6b: getfield net/rim/device/api/crypto/RSAPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 6e: invokevirtual net/rim/device/api/crypto/CryptoTokenPublicKeyData.equals (Ljava/lang/Object;)Z
      // 71: ifeq 76
      // 74: bipush 1
      // 75: ireturn
      // 76: bipush 0
      // 77: ireturn
      // 78: bipush 0
      // 79: ireturn
      // try (19 -> 39): 40 null
      // try (19 -> 39): 43 null
   }
}
