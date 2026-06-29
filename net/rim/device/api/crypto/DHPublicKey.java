package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class DHPublicKey implements PublicKey, DHKey, Persistable {
   private DHCryptoSystem _cryptoSystem;
   private DHCryptoToken _cryptoToken;
   private CryptoTokenPublicKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   private boolean _usePublicKeyData;

   public final CryptoTokenPublicKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final DHCryptoToken getDHCryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getPublicKeyData() {
      return this._cryptoToken.extractDHPublicKeyData(this._cryptoTokenData);
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         DHCryptoSystem cs = this.getDHCryptoSystem();
         cs.verify();

         label66:
         try {
            byte[] p = cs.getP();
            byte[] y = this.getPublicKeyData();
            if (CryptoByteArrayArithmetic.isZero(y)) {
               throw new InvalidKeyException();
            }

            if (CryptoByteArrayArithmetic.isOne(y) || CryptoByteArrayArithmetic.compare(y, p) >= 0) {
               throw new InvalidKeyException();
            }
         } finally {
            break label66;
         }

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
   public final DHCryptoSystem getDHCryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final String getAlgorithm() {
      return "DH";
   }

   private final void initialize(DHCryptoSystem cryptoSystem, DHCryptoToken cryptoToken, CryptoTokenPublicKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public DHPublicKey(DHCryptoSystem cryptoSystem, CryptoTokenPublicKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         DHCryptoToken cryptoToken = (DHCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public DHPublicKey(DHCryptoSystem cryptoSystem, byte[] data) throws InvalidKeyException {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         if (data.length > cryptoSystem.getPublicKeyLength()) {
            throw new InvalidKeyException();
         }

         DHCryptoToken cryptoToken = (DHCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectDHPublicKey(cryptoSystem.getCryptoTokenData(), data));
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
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
      // 04: invokevirtual net/rim/device/api/crypto/DHPublicKey.getPublicKeyData ()[B
      // 07: invokestatic net/rim/device/api/util/CRC32.update (I[B)I
      // 0a: putfield net/rim/device/api/crypto/DHPublicKey._hashCode I
      // 0d: aload 0
      // 0e: bipush 1
      // 0f: putfield net/rim/device/api/crypto/DHPublicKey._usePublicKeyData Z
      // 12: return
      // 13: astore 1
      // 14: return
      // 15: astore 1
      // 16: aload 0
      // 17: aload 0
      // 18: getfield net/rim/device/api/crypto/DHPublicKey._cryptoToken Lnet/rim/device/api/crypto/DHCryptoToken;
      // 1b: invokevirtual net/rim/device/api/crypto/DHCryptoToken.hashCode ()I
      // 1e: aload 0
      // 1f: getfield net/rim/device/api/crypto/DHPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 22: invokevirtual net/rim/device/api/crypto/CryptoTokenPublicKeyData.hashCode ()I
      // 25: ixor
      // 26: putfield net/rim/device/api/crypto/DHPublicKey._hashCode I
      // 29: aload 0
      // 2a: bipush 0
      // 2b: putfield net/rim/device/api/crypto/DHPublicKey._usePublicKeyData Z
      // 2e: return
      // try (0 -> 9): 10 null
      // try (0 -> 9): 12 null
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
      // 09: instanceof net/rim/device/api/crypto/DHPublicKey
      // 0c: ifne 13
      // 0f: pop
      // 10: goto 6a
      // 13: checkcast net/rim/device/api/crypto/DHPublicKey
      // 16: astore 2
      // 17: aload 0
      // 18: getfield net/rim/device/api/crypto/DHPublicKey._usePublicKeyData Z
      // 1b: ifeq 4a
      // 1e: aload 2
      // 1f: getfield net/rim/device/api/crypto/DHPublicKey._usePublicKeyData Z
      // 22: ifeq 4a
      // 25: aload 0
      // 26: getfield net/rim/device/api/crypto/DHPublicKey._hashCode I
      // 29: aload 2
      // 2a: getfield net/rim/device/api/crypto/DHPublicKey._hashCode I
      // 2d: if_icmpne 42
      // 30: aload 0
      // 31: invokevirtual net/rim/device/api/crypto/DHPublicKey.getPublicKeyData ()[B
      // 34: aload 2
      // 35: invokevirtual net/rim/device/api/crypto/DHPublicKey.getPublicKeyData ()[B
      // 38: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 3b: ifeq 42
      // 3e: bipush 1
      // 3f: goto 43
      // 42: bipush 0
      // 43: ireturn
      // 44: astore 3
      // 45: bipush 0
      // 46: ireturn
      // 47: astore 3
      // 48: bipush 0
      // 49: ireturn
      // 4a: aload 0
      // 4b: getfield net/rim/device/api/crypto/DHPublicKey._cryptoToken Lnet/rim/device/api/crypto/DHCryptoToken;
      // 4e: aload 2
      // 4f: getfield net/rim/device/api/crypto/DHPublicKey._cryptoToken Lnet/rim/device/api/crypto/DHCryptoToken;
      // 52: invokevirtual net/rim/device/api/crypto/DHCryptoToken.equals (Ljava/lang/Object;)Z
      // 55: ifeq 68
      // 58: aload 0
      // 59: getfield net/rim/device/api/crypto/DHPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 5c: aload 2
      // 5d: getfield net/rim/device/api/crypto/DHPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 60: invokevirtual net/rim/device/api/crypto/CryptoTokenPublicKeyData.equals (Ljava/lang/Object;)Z
      // 63: ifeq 68
      // 66: bipush 1
      // 67: ireturn
      // 68: bipush 0
      // 69: ireturn
      // 6a: bipush 0
      // 6b: ireturn
      // try (19 -> 33): 34 null
      // try (19 -> 33): 37 null
   }
}
