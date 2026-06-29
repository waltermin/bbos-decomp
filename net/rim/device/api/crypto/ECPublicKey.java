package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class ECPublicKey implements PublicKey, ECKey, Persistable {
   private ECCryptoSystem _cryptoSystem;
   private ECCryptoToken _cryptoToken;
   private CryptoTokenPublicKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   private boolean _usePublicKeyData;
   private boolean _compress;

   public final CryptoTokenPublicKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   final ECCryptoToken getECCryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getPublicKeyData(boolean compress) {
      return this._cryptoToken.extractECPublicKeyData(this._cryptoTokenData, compress);
   }

   public final byte[] getPublicKeyData() {
      return this._cryptoToken.extractECPublicKeyData(this._cryptoTokenData);
   }

   @Override
   public final void verify() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/ECPublicKey._verified Z
      // 04: ifeq 08
      // 07: return
      // 08: aload 0
      // 09: invokevirtual net/rim/device/api/crypto/ECPublicKey.getECCryptoSystem ()Lnet/rim/device/api/crypto/ECCryptoSystem;
      // 0c: astore 1
      // 0d: aload 1
      // 0e: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.verify ()V
      // 11: aload 0
      // 12: invokevirtual net/rim/device/api/crypto/ECPublicKey.getPublicKeyData ()[B
      // 15: astore 2
      // 16: aload 1
      // 17: aload 2
      // 18: invokestatic net/rim/device/api/crypto/ECPublicKey.isCompressed (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)Z
      // 1b: istore 3
      // 1c: aload 1
      // 1d: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.getName ()Ljava/lang/String;
      // 20: aload 2
      // 21: invokestatic net/rim/device/api/crypto/NativeEC.verifyPublicKey (Ljava/lang/String;[B)Z
      // 24: ifne 2f
      // 27: new java/lang/Object
      // 2a: dup
      // 2b: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> ()V
      // 2e: athrow
      // 2f: aload 1
      // 30: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.getFieldLength ()I
      // 33: istore 4
      // 35: iload 4
      // 37: newarray 8
      // 39: astore 5
      // 3b: aload 2
      // 3c: bipush 1
      // 3d: aload 5
      // 3f: bipush 0
      // 40: iload 4
      // 42: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 45: aconst_null
      // 46: astore 6
      // 48: iload 3
      // 49: ifne 5f
      // 4c: iload 4
      // 4e: newarray 8
      // 50: astore 6
      // 52: aload 2
      // 53: bipush 1
      // 54: iload 4
      // 56: iadd
      // 57: aload 6
      // 59: bipush 0
      // 5a: iload 4
      // 5c: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 5f: aload 1
      // 60: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.getFieldReductor ()[B
      // 63: astore 7
      // 65: aload 5
      // 67: aload 7
      // 69: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.compare ([B[B)I
      // 6c: iflt 77
      // 6f: new java/lang/Object
      // 72: dup
      // 73: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> ()V
      // 76: athrow
      // 77: aload 6
      // 79: ifnull a9
      // 7c: aload 6
      // 7e: aload 7
      // 80: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.compare ([B[B)I
      // 83: iflt a9
      // 86: new java/lang/Object
      // 89: dup
      // 8a: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> ()V
      // 8d: athrow
      // 8e: astore 2
      // 8f: new java/lang/Object
      // 92: dup
      // 93: aload 2
      // 94: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 97: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> (Ljava/lang/String;)V
      // 9a: athrow
      // 9b: astore 2
      // 9c: new java/lang/Object
      // 9f: dup
      // a0: aload 2
      // a1: invokevirtual net/rim/device/api/crypto/InvalidCryptoSystemException.toString ()Ljava/lang/String;
      // a4: invokespecial net/rim/device/api/crypto/InvalidKeyException.<init> (Ljava/lang/String;)V
      // a7: athrow
      // a8: astore 2
      // a9: aload 0
      // aa: bipush 1
      // ab: putfield net/rim/device/api/crypto/ECPublicKey._verified Z
      // ae: return
      // af: astore 2
      // b0: return
      // try (7 -> 73): 73 null
      // try (7 -> 73): 80 null
      // try (7 -> 73): 87 null
      // try (88 -> 91): 92 null
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

   private final void initialize(ECCryptoSystem cryptoSystem, ECCryptoToken cryptoToken, CryptoTokenPublicKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public static final boolean isCompressed(ECCryptoSystem cryptoSystem, byte[] publicKeyData) {
      return publicKeyData.length < cryptoSystem.getFieldLength() * 2;
   }

   public ECPublicKey(ECCryptoSystem cryptoSystem, CryptoTokenPublicKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         ECCryptoToken cryptoToken = (ECCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new Object();
      }
   }

   public ECPublicKey(ECCryptoSystem cryptoSystem, byte[] data) {
      if (cryptoSystem != null && data != null) {
         if (data.length > cryptoSystem.getPublicKeyLength(false)) {
            throw new Object();
         }

         ECCryptoToken cryptoToken = (ECCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectECPublicKey(cryptoSystem.getCryptoTokenData(), data));
      } else {
         throw new Object();
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
      // 04: bipush 0
      // 05: invokevirtual net/rim/device/api/crypto/ECPublicKey.getPublicKeyData (Z)[B
      // 08: invokestatic net/rim/device/api/util/CRC32.update (I[B)I
      // 0b: putfield net/rim/device/api/crypto/ECPublicKey._hashCode I
      // 0e: aload 0
      // 0f: bipush 1
      // 10: putfield net/rim/device/api/crypto/ECPublicKey._usePublicKeyData Z
      // 13: aload 0
      // 14: bipush 0
      // 15: putfield net/rim/device/api/crypto/ECPublicKey._compress Z
      // 18: return
      // 19: astore 1
      // 1a: aload 0
      // 1b: bipush -1
      // 1d: aload 0
      // 1e: bipush 1
      // 1f: invokevirtual net/rim/device/api/crypto/ECPublicKey.getPublicKeyData (Z)[B
      // 22: invokestatic net/rim/device/api/util/CRC32.update (I[B)I
      // 25: putfield net/rim/device/api/crypto/ECPublicKey._hashCode I
      // 28: aload 0
      // 29: bipush 1
      // 2a: putfield net/rim/device/api/crypto/ECPublicKey._usePublicKeyData Z
      // 2d: aload 0
      // 2e: bipush 1
      // 2f: putfield net/rim/device/api/crypto/ECPublicKey._compress Z
      // 32: return
      // 33: astore 1
      // 34: return
      // 35: astore 1
      // 36: aload 0
      // 37: aload 0
      // 38: getfield net/rim/device/api/crypto/ECPublicKey._cryptoToken Lnet/rim/device/api/crypto/ECCryptoToken;
      // 3b: invokevirtual net/rim/device/api/crypto/ECCryptoToken.hashCode ()I
      // 3e: aload 0
      // 3f: getfield net/rim/device/api/crypto/ECPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 42: invokevirtual net/rim/device/api/crypto/CryptoTokenPublicKeyData.hashCode ()I
      // 45: ixor
      // 46: putfield net/rim/device/api/crypto/ECPublicKey._hashCode I
      // 49: aload 0
      // 4a: bipush 0
      // 4b: putfield net/rim/device/api/crypto/ECPublicKey._usePublicKeyData Z
      // 4e: return
      // try (0 -> 13): 14 null
      // try (0 -> 13): 29 null
      // try (14 -> 28): 29 null
      // try (0 -> 13): 31 null
      // try (14 -> 28): 31 null
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
      // 09: instanceof net/rim/device/api/crypto/ECPublicKey
      // 0c: ifne 13
      // 0f: pop
      // 10: goto 84
      // 13: checkcast net/rim/device/api/crypto/ECPublicKey
      // 16: astore 2
      // 17: aload 0
      // 18: getfield net/rim/device/api/crypto/ECPublicKey._usePublicKeyData Z
      // 1b: ifeq 64
      // 1e: aload 2
      // 1f: getfield net/rim/device/api/crypto/ECPublicKey._usePublicKeyData Z
      // 22: ifeq 64
      // 25: aload 0
      // 26: aload 0
      // 27: getfield net/rim/device/api/crypto/ECPublicKey._compress Z
      // 2a: invokevirtual net/rim/device/api/crypto/ECPublicKey.getPublicKeyData (Z)[B
      // 2d: aload 2
      // 2e: aload 0
      // 2f: getfield net/rim/device/api/crypto/ECPublicKey._compress Z
      // 32: invokevirtual net/rim/device/api/crypto/ECPublicKey.getPublicKeyData (Z)[B
      // 35: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 38: ireturn
      // 39: astore 3
      // 3a: aload 0
      // 3b: aload 0
      // 3c: getfield net/rim/device/api/crypto/ECPublicKey._compress Z
      // 3f: ifne 46
      // 42: bipush 1
      // 43: goto 47
      // 46: bipush 0
      // 47: invokevirtual net/rim/device/api/crypto/ECPublicKey.getPublicKeyData (Z)[B
      // 4a: aload 2
      // 4b: aload 0
      // 4c: getfield net/rim/device/api/crypto/ECPublicKey._compress Z
      // 4f: ifne 56
      // 52: bipush 1
      // 53: goto 57
      // 56: bipush 0
      // 57: invokevirtual net/rim/device/api/crypto/ECPublicKey.getPublicKeyData (Z)[B
      // 5a: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 5d: ireturn
      // 5e: astore 3
      // 5f: bipush 0
      // 60: ireturn
      // 61: astore 3
      // 62: bipush 0
      // 63: ireturn
      // 64: aload 0
      // 65: getfield net/rim/device/api/crypto/ECPublicKey._cryptoToken Lnet/rim/device/api/crypto/ECCryptoToken;
      // 68: aload 2
      // 69: getfield net/rim/device/api/crypto/ECPublicKey._cryptoToken Lnet/rim/device/api/crypto/ECCryptoToken;
      // 6c: invokevirtual net/rim/device/api/crypto/ECCryptoToken.equals (Ljava/lang/Object;)Z
      // 6f: ifeq 82
      // 72: aload 0
      // 73: getfield net/rim/device/api/crypto/ECPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 76: aload 2
      // 77: getfield net/rim/device/api/crypto/ECPublicKey._cryptoTokenData Lnet/rim/device/api/crypto/CryptoTokenPublicKeyData;
      // 7a: invokevirtual net/rim/device/api/crypto/CryptoTokenPublicKeyData.equals (Ljava/lang/Object;)Z
      // 7d: ifeq 82
      // 80: bipush 1
      // 81: ireturn
      // 82: bipush 0
      // 83: ireturn
      // 84: bipush 0
      // 85: ireturn
      // try (19 -> 28): 29 null
      // try (19 -> 28): 48 null
      // try (29 -> 47): 48 null
      // try (19 -> 28): 51 null
      // try (29 -> 47): 51 null
   }
}
