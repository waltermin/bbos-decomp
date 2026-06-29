package net.rim.device.api.crypto;

public final class DSACryptoSystem implements CryptoSystem {
   private DSACryptoToken _cryptoToken;
   private CryptoTokenCryptoSystemData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   public static final String SUN512 = "SUN512";
   public static final String SUN768 = "SUN768";
   public static final String SUN1024 = "SUN1024";

   public final DSAKeyPair createDSAKeyPair() {
      return this._cryptoToken.createDSAKeyPair(this._cryptoTokenData);
   }

   public final int getPrivateKeyLength() {
      return this._cryptoToken.getDSAPrivateKeyLength(this._cryptoTokenData);
   }

   public final int getPublicKeyLength() {
      return this._cryptoToken.getDSAPublicKeyLength(this._cryptoTokenData);
   }

   public final CryptoTokenCryptoSystemData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final byte[] getG() {
      return this._cryptoToken.getDSACryptoSystemG(this._cryptoTokenData);
   }

   public final byte[] getQ() {
      return this._cryptoToken.getDSACryptoSystemQ(this._cryptoTokenData);
   }

   public final byte[] getP() {
      return this._cryptoToken.getDSACryptoSystemP(this._cryptoTokenData);
   }

   @Override
   public final String getName() {
      return this._cryptoToken.getDSACryptoSystemName(this._cryptoTokenData);
   }

   @Override
   public final boolean isStrong() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getBitLength ()I
      // 04: sipush 1024
      // 07: if_icmpge 0c
      // 0a: bipush 0
      // 0b: ireturn
      // 0c: aload 0
      // 0d: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getP ()[B
      // 10: astore 2
      // 11: aload 2
      // 12: bipush 0
      // 13: aload 2
      // 14: arraylength
      // 15: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.findFirstNonZeroByte ([BII)I
      // 18: istore 1
      // 19: aload 2
      // 1a: arraylength
      // 1b: iload 1
      // 1c: isub
      // 1d: bipush 8
      // 1f: imul
      // 20: aload 0
      // 21: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getBitLength ()I
      // 24: if_icmpge 29
      // 27: bipush 0
      // 28: ireturn
      // 29: aload 0
      // 2a: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getQ ()[B
      // 2d: astore 3
      // 2e: aload 3
      // 2f: ifnull 51
      // 32: aload 3
      // 33: bipush 0
      // 34: aload 3
      // 35: arraylength
      // 36: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.findFirstNonZeroByte ([BII)I
      // 39: istore 1
      // 3a: aload 3
      // 3b: arraylength
      // 3c: iload 1
      // 3d: isub
      // 3e: bipush 8
      // 40: imul
      // 41: istore 4
      // 43: iload 4
      // 45: sipush 160
      // 48: if_icmpge 51
      // 4b: bipush 0
      // 4c: ireturn
      // 4d: astore 3
      // 4e: bipush 1
      // 4f: ireturn
      // 50: astore 3
      // 51: bipush 1
      // 52: ireturn
      // try (26 -> 48): 49 null
      // try (26 -> 48): 52 null
   }

   @Override
   public final int getBitLength() {
      return this._cryptoToken.getDSACryptoSystemBitLength(this._cryptoTokenData);
   }

   @Override
   public final AsymmetricCryptoToken getAsymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final String getAlgorithm() {
      return "DSA";
   }

   @Override
   public final KeyPair createKeyPair() {
      return this.createDSAKeyPair();
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         label131:
         try {
            byte[] p = this.getP();
            byte[] q = this.getQ();
            byte[] g = this.getG();
            if (CryptoByteArrayArithmetic.isZero(p) || CryptoByteArrayArithmetic.isZero(q) || CryptoByteArrayArithmetic.isZero(g)) {
               throw new InvalidCryptoSystemException();
            }

            if ((p[p.length - 1] & 1) == 0 || (q[q.length - 1] & 1) == 0) {
               throw new InvalidCryptoSystemException();
            }

            if (q.length != 20 || (q[0] & 128) == 0) {
               throw new InvalidCryptoSystemException();
            }

            if (CryptoByteArrayArithmetic.isOne(g) || CryptoByteArrayArithmetic.compare(g, p) >= 0) {
               throw new InvalidCryptoSystemException();
            }

            byte[] pMinus1 = new byte[p.length];
            byte[] result = new byte[q.length];
            CryptoByteArrayArithmetic.decrement(p, p, pMinus1);
            CryptoByteArrayArithmetic.mod(pMinus1, q, result);
            if (!CryptoByteArrayArithmetic.isZero(result)) {
               throw new InvalidCryptoSystemException();
            }

            result = new byte[p.length];
            CryptoByteArrayArithmetic.exponent(g, q, p, result);
            if (!CryptoByteArrayArithmetic.isOne(result)) {
               throw new InvalidCryptoSystemException();
            }
         } finally {
            break label131;
         }

         DSACryptoToken dsaCryptoToken = (DSACryptoToken)this.getAsymmetricCryptoToken();
         dsaCryptoToken.verifyDSACryptoSystemData(this.getCryptoTokenData());

         try {
            this._verified = true;
         } finally {
            return;
         }
      }
   }

   private final void initialize(DSACryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void initialize(DSACryptoToken cryptoToken, byte[] p, byte[] q, byte[] g, String name) throws InvalidCryptoSystemException {
      if (cryptoToken != null && p != null && q != null && g != null) {
         p = CryptoByteArrayArithmetic.trim(p);
         q = CryptoByteArrayArithmetic.trim(q);
         g = CryptoByteArrayArithmetic.trim(g);
         if (p.length >= q.length && p.length >= g.length) {
            this.initialize(cryptoToken, cryptoToken.getDSACryptoSystemData(p, q, g, name));
         } else {
            throw new InvalidCryptoSystemException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public DSACryptoSystem(DSACryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   public DSACryptoSystem(DSACryptoToken cryptoToken, byte[] p, byte[] q, byte[] g, String name) {
      this.initialize(cryptoToken, p, q, g, name);
   }

   public DSACryptoSystem(byte[] param1, byte[] param2, byte[] param3, String param4) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareDSACryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareDSACryptoToken;
      // 08: aload 1
      // 09: aload 2
      // 0a: aload 3
      // 0b: aload 4
      // 0d: invokespecial net/rim/device/api/crypto/DSACryptoSystem.initialize (Lnet/rim/device/api/crypto/DSACryptoToken;[B[B[BLjava/lang/String;)V
      // 10: return
      // 11: astore 5
      // 13: new java/lang/RuntimeException
      // 16: dup
      // 17: aload 5
      // 19: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 1c: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1f: athrow
      // 20: astore 5
      // 22: new java/lang/RuntimeException
      // 25: dup
      // 26: aload 5
      // 28: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 2b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2e: athrow
      // try (2 -> 9): 10 null
      // try (2 -> 9): 17 null
   }

   public DSACryptoSystem(byte[] p, byte[] q, byte[] g) {
      this(p, q, g, null);
   }

   public DSACryptoSystem(DSACryptoToken cryptoToken, String name) {
      this.initialize(cryptoToken, name);
   }

   public DSACryptoSystem(String param1) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareDSACryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareDSACryptoToken;
      // 08: aload 1
      // 09: invokespecial net/rim/device/api/crypto/DSACryptoSystem.initialize (Lnet/rim/device/api/crypto/DSACryptoToken;Ljava/lang/String;)V
      // 0c: return
      // 0d: astore 2
      // 0e: new java/lang/RuntimeException
      // 11: dup
      // 12: aload 2
      // 13: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 16: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 19: athrow
      // 1a: astore 2
      // 1b: new java/lang/RuntimeException
      // 1e: dup
      // 1f: aload 2
      // 20: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 23: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 26: athrow
      // 27: astore 2
      // 28: new java/lang/RuntimeException
      // 2b: dup
      // 2c: aload 2
      // 2d: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 30: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 33: athrow
      // try (2 -> 6): 7 null
      // try (2 -> 6): 14 null
      // try (2 -> 6): 21 net/rim/device/api/crypto/UnsupportedCryptoSystemException
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
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

      if (!(obj instanceof DSACryptoSystem)) {
         return false;
      }

      DSACryptoSystem other = (DSACryptoSystem)obj;
      return this._hashCode == other._hashCode && this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData);
   }

   private final void initialize(DSACryptoToken cryptoToken, String name) {
      byte[] p = null;
      byte[] q = null;
      byte[] g = null;
      if (name == null) {
         throw new IllegalArgumentException();
      }

      if (name.equals("SUN512")) {
         p = DHCryptoSystem.SUN512_P;
         q = DHCryptoSystem.SUN512_Q;
         g = DHCryptoSystem.SUN512_G;
      } else if (name.equals("SUN768")) {
         p = DHCryptoSystem.SUN768_P;
         q = DHCryptoSystem.SUN768_Q;
         g = DHCryptoSystem.SUN768_G;
      } else if (name.equals("SUN1024")) {
         p = DHCryptoSystem.SUN1024_P;
         q = DHCryptoSystem.SUN1024_Q;
         g = DHCryptoSystem.SUN1024_G;
      }

      try {
         this.initialize(cryptoToken, p, q, g, name);
      } catch (InvalidCryptoSystemException e) {
         throw new RuntimeException(e.toString());
      }
   }

   public DSACryptoSystem() {
      this("SUN1024");
   }
}
