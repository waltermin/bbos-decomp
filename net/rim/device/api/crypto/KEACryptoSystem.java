package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class KEACryptoSystem implements CryptoSystem, Persistable {
   private KEACryptoToken _cryptoToken;
   private CryptoTokenCryptoSystemData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   public static final String SUN1024;
   public static final String FORTEZZA;
   private static final byte[] FORTEZZA_P = new byte[]{
      -3,
      127,
      83,
      -127,
      29,
      117,
      18,
      41,
      82,
      -33,
      74,
      -100,
      46,
      -20,
      -28,
      -25,
      -10,
      17,
      -73,
      82,
      60,
      -17,
      68,
      0,
      -61,
      30,
      63,
      -128,
      -74,
      81,
      38,
      105,
      69,
      93,
      64,
      34,
      81,
      -5,
      89,
      61,
      -115,
      88,
      -6,
      -65,
      -59,
      -11,
      -70,
      48,
      -10,
      -53,
      -101,
      85,
      108,
      -41,
      -127,
      59,
      -128,
      29,
      52,
      111,
      -14,
      102,
      96,
      -73,
      107,
      -103,
      80,
      -91,
      -92,
      -97,
      -97,
      -24,
      4,
      123,
      16,
      34,
      -62,
      79,
      -69,
      -87,
      -41,
      -2,
      -73,
      -58,
      27,
      -8,
      59,
      87,
      -25,
      -58,
      -88,
      -90,
      21,
      15,
      4,
      -5,
      -125,
      -10,
      -45,
      -59,
      30,
      -61,
      2,
      53,
      84,
      19,
      90,
      22,
      -111,
      50,
      -10,
      117,
      -13,
      -82,
      43,
      97,
      -41,
      42,
      -17,
      -14,
      34,
      3,
      25,
      -99,
      -47,
      72,
      1,
      -57
   };
   private static final byte[] FORTEZZA_Q = new byte[]{-105, 96, 80, -113, 21, 35, 11, -52, -78, -110, -71, -126, -94, -21, -124, 11, -16, 88, 28, -11};
   private static final byte[] FORTEZZA_G = new byte[]{
      -9,
      -31,
      -96,
      -123,
      -42,
      -101,
      61,
      -34,
      -53,
      -68,
      -85,
      92,
      54,
      -72,
      87,
      -71,
      121,
      -108,
      -81,
      -69,
      -6,
      58,
      -22,
      -126,
      -7,
      87,
      76,
      11,
      61,
      7,
      -126,
      103,
      81,
      89,
      87,
      -114,
      -70,
      -44,
      89,
      79,
      -26,
      113,
      7,
      16,
      -127,
      -128,
      -76,
      73,
      22,
      113,
      35,
      -24,
      76,
      40,
      22,
      19,
      -73,
      -49,
      9,
      50,
      -116,
      -56,
      -90,
      -31,
      60,
      22,
      122,
      -117,
      84,
      124,
      -115,
      40,
      -32,
      -93,
      -82,
      30,
      43,
      -77,
      -90,
      117,
      -111,
      110,
      -93,
      127,
      11,
      -6,
      33,
      53,
      98,
      -15,
      -5,
      98,
      122,
      1,
      36,
      59,
      -52,
      -92,
      -15,
      -66,
      -88,
      81,
      -112,
      -119,
      -88,
      -125,
      -33,
      -31,
      90,
      -27,
      -97,
      6,
      -110,
      -117,
      102,
      94,
      -128,
      123,
      85,
      37,
      100,
      1,
      76,
      59,
      -2,
      -49,
      73,
      42
   };

   public final KEAKeyPair createKEAKeyPair() {
      return this._cryptoToken.createKEAKeyPair(this._cryptoTokenData);
   }

   public final int getPrivateKeyLength() {
      return this._cryptoToken.getKEAPrivateKeyLength(this._cryptoTokenData);
   }

   public final int getPublicKeyLength() {
      return this._cryptoToken.getKEAPublicKeyLength(this._cryptoTokenData);
   }

   public final CryptoTokenCryptoSystemData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final byte[] getG() {
      return this._cryptoToken.getKEACryptoSystemG(this._cryptoTokenData);
   }

   public final byte[] getQ() {
      return this._cryptoToken.getKEACryptoSystemQ(this._cryptoTokenData);
   }

   public final byte[] getP() {
      return this._cryptoToken.getKEACryptoSystemP(this._cryptoTokenData);
   }

   @Override
   public final String getName() {
      return this._cryptoToken.getKEACryptoSystemName(this._cryptoTokenData);
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
      // 01: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getBitLength ()I
      // 04: sipush 1024
      // 07: if_icmpge 0c
      // 0a: bipush 0
      // 0b: ireturn
      // 0c: aload 0
      // 0d: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getP ()[B
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
      // 21: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getBitLength ()I
      // 24: if_icmpge 29
      // 27: bipush 0
      // 28: ireturn
      // 29: aload 0
      // 2a: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getQ ()[B
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
      return this._cryptoToken.getKEACryptoSystemBitLength(this._cryptoTokenData);
   }

   @Override
   public final AsymmetricCryptoToken getAsymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final String getAlgorithm() {
      return "KEA";
   }

   @Override
   public final KeyPair createKeyPair() {
      return this.createKEAKeyPair();
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
               throw new Object();
            }

            if ((p[p.length - 1] & 1) == 0 || (q[q.length - 1] & 1) == 0) {
               throw new Object();
            }

            if (q.length != 20 || (q[0] & 128) == 0) {
               throw new Object();
            }

            if (CryptoByteArrayArithmetic.isOne(g) || CryptoByteArrayArithmetic.compare(g, p) >= 0) {
               throw new Object();
            }

            byte[] pMinus1 = new byte[p.length];
            byte[] result = new byte[q.length];
            CryptoByteArrayArithmetic.decrement(p, p, pMinus1);
            CryptoByteArrayArithmetic.mod(pMinus1, q, result);
            if (!CryptoByteArrayArithmetic.isZero(result)) {
               throw new Object();
            }

            result = new byte[p.length];
            CryptoByteArrayArithmetic.exponent(g, q, p, result);
            if (!CryptoByteArrayArithmetic.isOne(result)) {
               throw new Object();
            }
         } finally {
            break label131;
         }

         KEACryptoToken keaCryptoToken = (KEACryptoToken)this.getAsymmetricCryptoToken();
         keaCryptoToken.verifyKEACryptoSystemData(this.getCryptoTokenData());

         try {
            this._verified = true;
         } finally {
            return;
         }
      }
   }

   private final void initialize(KEACryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   private final void initialize(KEACryptoToken cryptoToken, byte[] p, byte[] q, byte[] g, String name) {
      if (cryptoToken != null && p != null && q != null && g != null) {
         p = CryptoByteArrayArithmetic.trim(p);
         q = CryptoByteArrayArithmetic.trim(q);
         g = CryptoByteArrayArithmetic.trim(g);
         if (p.length == 128 && q.length == 20 && g.length == 128) {
            this.initialize(cryptoToken, cryptoToken.getKEACryptoSystemData(p, q, g, name));
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   public KEACryptoSystem(KEACryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   public KEACryptoSystem(KEACryptoToken cryptoToken, byte[] p, byte[] q, byte[] g, String name) {
      if (cryptoToken == null) {
         throw new Object();
      }

      this.initialize(cryptoToken, cryptoToken.getKEACryptoSystemData(p, q, g, name));
   }

   public KEACryptoSystem(byte[] param1, byte[] param2, byte[] param3, String param4) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareKEACryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareKEACryptoToken;
      // 08: aload 1
      // 09: aload 2
      // 0a: aload 3
      // 0b: aload 4
      // 0d: invokespecial net/rim/device/api/crypto/KEACryptoSystem.initialize (Lnet/rim/device/api/crypto/KEACryptoToken;[B[B[BLjava/lang/String;)V
      // 10: return
      // 11: astore 5
      // 13: new java/lang/Object
      // 16: dup
      // 17: aload 5
      // 19: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 1c: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1f: athrow
      // 20: astore 5
      // 22: new java/lang/Object
      // 25: dup
      // 26: aload 5
      // 28: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 2b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2e: athrow
      // 2f: astore 5
      // 31: new java/lang/Object
      // 34: dup
      // 35: aload 5
      // 37: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 3a: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 3d: athrow
      // try (2 -> 9): 10 null
      // try (2 -> 9): 17 null
      // try (2 -> 9): 24 null
   }

   public KEACryptoSystem(byte[] p, byte[] q, byte[] g) {
      this(p, q, g, null);
   }

   public KEACryptoSystem(KEACryptoToken cryptoToken, String name) {
      this.initialize(cryptoToken, name);
   }

   public KEACryptoSystem(String param1) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareKEACryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareKEACryptoToken;
      // 08: aload 1
      // 09: invokespecial net/rim/device/api/crypto/KEACryptoSystem.initialize (Lnet/rim/device/api/crypto/KEACryptoToken;Ljava/lang/String;)V
      // 0c: return
      // 0d: astore 2
      // 0e: new java/lang/Object
      // 11: dup
      // 12: aload 2
      // 13: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 16: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 19: athrow
      // 1a: astore 2
      // 1b: new java/lang/Object
      // 1e: dup
      // 1f: aload 2
      // 20: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 23: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 26: athrow
      // 27: astore 2
      // 28: new java/lang/Object
      // 2b: dup
      // 2c: aload 2
      // 2d: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 30: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 33: athrow
      // try (2 -> 6): 7 null
      // try (2 -> 6): 14 null
      // try (2 -> 6): 21 null
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof KEACryptoSystem)) {
         return false;
      }

      KEACryptoSystem other = (KEACryptoSystem)obj;
      return this._hashCode == other._hashCode && this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void initialize(KEACryptoToken cryptoToken, String name) {
      byte[] p = null;
      byte[] q = null;
      byte[] g = null;
      if (name == null) {
         throw new Object();
      }

      if (name.equals("FORTEZZA")) {
         p = FORTEZZA_P;
         q = FORTEZZA_Q;
         g = FORTEZZA_G;
      } else if (name.equals("SUN1024")) {
         p = DHCryptoSystem.SUN1024_P;
         q = DHCryptoSystem.SUN1024_Q;
         g = DHCryptoSystem.SUN1024_G;
      }

      try {
         this.initialize(cryptoToken, p, q, g, name);
      } catch (Throwable var8) {
         throw new Object(e.toString());
      }
   }

   public KEACryptoSystem() {
      this("FORTEZZA");
   }
}
