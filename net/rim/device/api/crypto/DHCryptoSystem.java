package net.rim.device.api.crypto;

public final class DHCryptoSystem implements CryptoSystem {
   private DHCryptoToken _cryptoToken;
   private CryptoTokenCryptoSystemData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   public static final String WTLS1 = "WTLS1";
   public static final String WTLS2 = "WTLS2";
   public static final String SUN512 = "SUN512";
   public static final String SUN768 = "SUN768";
   public static final String SUN1024 = "SUN1024";
   public static final int DEFAULT_PRIVATE_KEY_MIN_RANDOM_BITS = -1;
   static final byte[] WTLS1_P = new byte[]{
      -6,
      -13,
      12,
      99,
      -47,
      113,
      -27,
      74,
      -127,
      49,
      -51,
      51,
      29,
      124,
      -115,
      108,
      -118,
      -19,
      65,
      -80,
      53,
      78,
      26,
      41,
      -40,
      -38,
      -48,
      62,
      46,
      103,
      -1,
      -114,
      0,
      5,
      58,
      7,
      -3,
      40,
      -95,
      -18,
      106,
      -15,
      -103,
      -3,
      112,
      51,
      14,
      -88,
      -60,
      -58,
      2,
      -72,
      110,
      -33,
      -65,
      71,
      -3,
      29,
      123,
      -5,
      100,
      86,
      -67,
      87
   };
   static final byte[] WTLS1_G = new byte[]{
      -25,
      115,
      78,
      -69,
      -49,
      80,
      -119,
      60,
      118,
      1,
      -127,
      -78,
      -86,
      45,
      -80,
      -84,
      -14,
      -43,
      -74,
      -25,
      117,
      -18,
      -120,
      -70,
      -4,
      122,
      -91,
      -90,
      -69,
      32,
      -90,
      78,
      -71,
      -11,
      67,
      1,
      20,
      31,
      -112,
      41,
      27,
      123,
      55,
      81,
      53,
      57,
      69,
      4,
      -127,
      -55,
      -7,
      -53,
      43,
      -93,
      -26,
      123,
      69,
      -128,
      -30,
      21,
      63,
      -46,
      43,
      -128
   };
   static final byte[] WTLS2_P = new byte[]{
      -123,
      -37,
      93,
      -79,
      -123,
      9,
      10,
      -19,
      59,
      -37,
      59,
      -85,
      -4,
      -76,
      102,
      105,
      -7,
      86,
      62,
      104,
      30,
      -37,
      67,
      89,
      -110,
      65,
      -2,
      -10,
      -86,
      -101,
      93,
      -7,
      -17,
      -29,
      -100,
      12,
      -73,
      -103,
      74,
      4,
      -14,
      -67,
      -113,
      87,
      -75,
      -78,
      42,
      -9,
      94,
      54,
      5,
      38,
      33,
      100,
      32,
      -68,
      -96,
      -113,
      -51,
      -7,
      -113,
      -10,
      65,
      125,
      -49,
      -35,
      28,
      64,
      -28,
      -1,
      -79,
      -125,
      38,
      14,
      59,
      40,
      -17,
      11,
      49,
      -93,
      99,
      55,
      -120,
      -55,
      -120,
      -79,
      -68,
      103,
      52,
      -88,
      27,
      49,
      -94,
      -116,
      -42,
      -5
   };
   static final byte[] WTLS2_G = new byte[]{
      0,
      27,
      21,
      -61,
      -59,
      114,
      99,
      -80,
      -35,
      26,
      -99,
      -103,
      103,
      104,
      -72,
      -125,
      112,
      -19,
      69,
      -115,
      123,
      0,
      -127,
      -94,
      32,
      5,
      78,
      -3,
      -46,
      59,
      -100,
      -40,
      41,
      -117,
      113,
      -97,
      -45,
      -74,
      124,
      -80,
      -109,
      -127,
      115,
      50,
      -48,
      51,
      100,
      45,
      33,
      19,
      15,
      -125,
      -39,
      -53,
      44,
      -59,
      -84,
      -35,
      54,
      -26,
      -26,
      -35,
      -78,
      65,
      10,
      -77,
      3,
      17,
      -51,
      -66,
      -23,
      34,
      44,
      -49,
      -26,
      68,
      68,
      59,
      12,
      114,
      4,
      -14,
      -47,
      47,
      122,
      55,
      25,
      -56,
      -122,
      106,
      32,
      -96,
      -25,
      120,
      -21,
      -70
   };
   static final byte[] SUN512_P = new byte[]{
      -4,
      -90,
      -126,
      -50,
      -114,
      18,
      -54,
      -70,
      38,
      -17,
      -52,
      -9,
      17,
      14,
      82,
      109,
      -80,
      120,
      -80,
      94,
      -34,
      -53,
      -51,
      30,
      -76,
      -94,
      8,
      -13,
      -82,
      22,
      23,
      -82,
      1,
      -13,
      91,
      -111,
      -92,
      126,
      109,
      -10,
      52,
      19,
      -59,
      -31,
      46,
      -48,
      -119,
      -101,
      -51,
      19,
      42,
      -51,
      80,
      -39,
      -111,
      81,
      -67,
      -60,
      62,
      -25,
      55,
      89,
      46,
      23
   };
   static final byte[] SUN512_Q = new byte[]{-106, 46, -35, -52, 54, -100, -70, -114, -69, 38, 14, -26, -74, -95, 38, -39, 52, 110, 56, -59};
   static final byte[] SUN512_G = new byte[]{
      103,
      -124,
      113,
      -78,
      122,
      -100,
      -12,
      78,
      -23,
      26,
      73,
      -59,
      20,
      125,
      -79,
      -87,
      -86,
      -14,
      68,
      -16,
      90,
      67,
      77,
      100,
      -122,
      -109,
      29,
      45,
      20,
      39,
      27,
      -98,
      53,
      3,
      11,
      113,
      -3,
      115,
      -38,
      23,
      -112,
      105,
      -77,
      46,
      41,
      53,
      99,
      14,
      28,
      32,
      98,
      53,
      77,
      13,
      -94,
      10,
      108,
      65,
      110,
      80,
      -66,
      121,
      76,
      -92
   };
   static final byte[] SUN768_P = new byte[]{
      -23,
      -26,
      66,
      89,
      -99,
      53,
      95,
      55,
      -55,
      127,
      -3,
      53,
      103,
      18,
      11,
      -114,
      37,
      -55,
      -51,
      67,
      -23,
      39,
      -77,
      -87,
      103,
      15,
      -66,
      -59,
      -40,
      -112,
      20,
      25,
      34,
      -46,
      -61,
      -77,
      -83,
      36,
      -128,
      9,
      55,
      -103,
      -122,
      -99,
      30,
      -124,
      106,
      -85,
      73,
      -6,
      -80,
      -83,
      38,
      -46,
      -50,
      106,
      34,
      33,
      -99,
      71,
      11,
      -50,
      125,
      119,
      125,
      74,
      33,
      -5,
      -23,
      -62,
      112,
      -75,
      127,
      96,
      112,
      2,
      -13,
      -50,
      -8,
      57,
      54,
      -108,
      -49,
      69,
      -18,
      54,
      -120,
      -63,
      26,
      -116,
      86,
      -85,
      18,
      122,
      61,
      -81
   };
   static final byte[] SUN768_Q = new byte[]{-100, -37, -40, 76, -97, 26, -62, -13, -115, 15, -128, -12, 42, -71, 82, -25, 51, -117, -11, 17};
   static final byte[] SUN768_G = new byte[]{
      48,
      71,
      10,
      -43,
      -96,
      5,
      -5,
      20,
      -50,
      45,
      -99,
      -51,
      -121,
      -29,
      -117,
      -57,
      -47,
      -79,
      -59,
      -6,
      -53,
      -82,
      -53,
      -23,
      95,
      25,
      10,
      -89,
      -93,
      29,
      35,
      -60,
      -37,
      -68,
      -66,
      6,
      23,
      69,
      68,
      64,
      26,
      91,
      44,
      2,
      9,
      101,
      -40,
      -62,
      -67,
      33,
      113,
      -45,
      102,
      -124,
      69,
      119,
      31,
      116,
      -70,
      8,
      77,
      32,
      41,
      -40,
      60,
      28,
      21,
      -123,
      71,
      -13,
      -87,
      -15,
      -94,
      113,
      91,
      -30,
      61,
      81,
      -82,
      77,
      62,
      90,
      31,
      106,
      112,
      100,
      -13,
      22,
      -109,
      58,
      52,
      109,
      63,
      82,
      -110,
      82
   };
   static final byte[] SUN1024_P = new byte[]{
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
   static final byte[] SUN1024_Q = new byte[]{-105, 96, 80, -113, 21, 35, 11, -52, -78, -110, -71, -126, -94, -21, -124, 11, -16, 88, 28, -11};
   static final byte[] SUN1024_G = new byte[]{
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

   final void initialize(DHCryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public final DHKeyPair createDHKeyPair() {
      return this._cryptoToken.createDHKeyPair(this._cryptoTokenData);
   }

   public final int getPrivateKeyMinRandomBits() {
      return this._cryptoToken.getDHPrivateKeyMinRandomBits(this._cryptoTokenData);
   }

   public final int getPrivateKeyLength() {
      return this._cryptoToken.getDHPrivateKeyLength(this._cryptoTokenData);
   }

   public final CryptoTokenCryptoSystemData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final int getPublicKeyLength() {
      return this._cryptoToken.getDHPublicKeyLength(this._cryptoTokenData);
   }

   public final byte[] getG() {
      return this._cryptoToken.getDHCryptoSystemG(this._cryptoTokenData);
   }

   public final byte[] getQ() {
      return this._cryptoToken.getDHCryptoSystemQ(this._cryptoTokenData);
   }

   public final byte[] getP() {
      return this._cryptoToken.getDHCryptoSystemP(this._cryptoTokenData);
   }

   @Override
   public final String getName() {
      return this._cryptoToken.getDHCryptoSystemName(this._cryptoTokenData);
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
      // 01: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getBitLength ()I
      // 04: sipush 1024
      // 07: if_icmpge 0c
      // 0a: bipush 0
      // 0b: ireturn
      // 0c: aload 0
      // 0d: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getP ()[B
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
      // 21: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getBitLength ()I
      // 24: if_icmpge 29
      // 27: bipush 0
      // 28: ireturn
      // 29: aload 0
      // 2a: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getQ ()[B
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
      return this._cryptoToken.getDHCryptoSystemBitLength(this._cryptoTokenData);
   }

   @Override
   public final AsymmetricCryptoToken getAsymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final String getAlgorithm() {
      return "DH";
   }

   @Override
   public final KeyPair createKeyPair() {
      return this.createDHKeyPair();
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         label156:
         try {
            byte[] p = this.getP();
            byte[] q = this.getQ();
            byte[] g = this.getG();
            if (CryptoByteArrayArithmetic.isZero(p) || CryptoByteArrayArithmetic.isZero(g)) {
               throw new InvalidCryptoSystemException();
            }

            if ((p[p.length - 1] & 1) == 0) {
               throw new InvalidCryptoSystemException();
            }

            if (CryptoByteArrayArithmetic.isOne(g) || CryptoByteArrayArithmetic.compare(g, p) >= 0) {
               throw new InvalidCryptoSystemException();
            }

            if (q != null) {
               if (CryptoByteArrayArithmetic.isZero(q)) {
                  throw new InvalidCryptoSystemException();
               }

               if ((q[q.length - 1] & 1) == 0) {
                  throw new InvalidCryptoSystemException();
               }

               if (q.length < 20 || q.length == 20 && (q[0] & 128) == 0) {
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
            }
         } finally {
            break label156;
         }

         DHCryptoToken dhCryptoToken = (DHCryptoToken)this.getAsymmetricCryptoToken();
         dhCryptoToken.verifyDHCryptoSystemData(this.getCryptoTokenData());

         try {
            this._verified = true;
         } finally {
            return;
         }
      }
   }

   private final void initialize(DHCryptoToken cryptoToken, byte[] p, byte[] q, byte[] g, int privateKeyMinRandomBits, String name) throws InvalidCryptoSystemException {
      if (cryptoToken != null && p != null && g != null) {
         p = CryptoByteArrayArithmetic.trim(p);
         g = CryptoByteArrayArithmetic.trim(g);
         if (p.length < g.length) {
            throw new InvalidCryptoSystemException();
         }

         if (q != null) {
            q = CryptoByteArrayArithmetic.trim(q);
            if (p.length < q.length) {
               throw new InvalidCryptoSystemException();
            }
         }

         if (privateKeyMinRandomBits == -1) {
            int bitLength = p.length * 8;
            if (bitLength <= 1024) {
               privateKeyMinRandomBits = 160;
            } else if (bitLength <= 2048) {
               privateKeyMinRandomBits = 256;
            } else {
               privateKeyMinRandomBits = 512;
            }
         }

         this.initialize(cryptoToken, cryptoToken.getDHCryptoSystemData(p, q, g, privateKeyMinRandomBits, name));
      } else {
         throw new Object();
      }
   }

   private final void initialize(DHCryptoToken cryptoToken, String name, int privateKeyMinRandomBits) {
      byte[] p = null;
      byte[] q = null;
      byte[] g = null;
      if (name == null) {
         throw new Object();
      }

      if (name.equals("WTLS1")) {
         p = WTLS1_P;
         q = null;
         g = WTLS1_G;
      } else if (name.equals("WTLS2")) {
         p = WTLS2_P;
         q = null;
         g = WTLS2_G;
      } else if (name.equals("SUN512")) {
         p = SUN512_P;
         q = SUN512_Q;
         g = SUN512_G;
      } else if (name.equals("SUN768")) {
         p = SUN768_P;
         q = SUN768_Q;
         g = SUN768_G;
      } else if (name.equals("SUN1024")) {
         p = SUN1024_P;
         q = SUN1024_Q;
         g = SUN1024_G;
      }

      try {
         this.initialize(cryptoToken, p, q, g, privateKeyMinRandomBits, name);
      } catch (InvalidCryptoSystemException e) {
         throw new Object(e.toString());
      }
   }

   public DHCryptoSystem(DHCryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   public DHCryptoSystem(DHCryptoToken cryptoToken, byte[] p, byte[] q, byte[] g, int privateKeyMinRandomBits, String name) {
      this.initialize(cryptoToken, p, q, g, privateKeyMinRandomBits, name);
   }

   public DHCryptoSystem(byte[] param1, byte[] param2, byte[] param3, int param4, String param5) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareDHCryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareDHCryptoToken;
      // 08: aload 1
      // 09: aload 2
      // 0a: aload 3
      // 0b: iload 4
      // 0d: aload 5
      // 0f: invokespecial net/rim/device/api/crypto/DHCryptoSystem.initialize (Lnet/rim/device/api/crypto/DHCryptoToken;[B[B[BILjava/lang/String;)V
      // 12: return
      // 13: astore 6
      // 15: new java/lang/Object
      // 18: dup
      // 19: aload 6
      // 1b: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 1e: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 21: athrow
      // 22: astore 6
      // 24: new java/lang/Object
      // 27: dup
      // 28: aload 6
      // 2a: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 2d: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 30: athrow
      // try (2 -> 10): 11 null
      // try (2 -> 10): 18 null
   }

   public DHCryptoSystem(byte[] p, byte[] q, byte[] g, int privateKeyMinRandomBits) {
      this(p, q, g, privateKeyMinRandomBits, null);
   }

   public DHCryptoSystem(byte[] p, byte[] q, byte[] g) {
      this(p, q, g, q == null ? -1 : q.length * 8, null);
   }

   public DHCryptoSystem(byte[] p, byte[] g, int privateKeyMinRandomBits) {
      this(p, null, g, privateKeyMinRandomBits, null);
   }

   public DHCryptoSystem(byte[] p, byte[] g) {
      this(p, null, g, -1, null);
   }

   public DHCryptoSystem(DHCryptoToken cryptoToken, String name, int privateKeyMinRandomBits) {
      this.initialize(cryptoToken, name, privateKeyMinRandomBits);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public DHCryptoSystem(String name, int privateKeyMinRandomBits) {
      try {
         this.initialize(SoftwareDHCryptoToken.getInstance(), name, privateKeyMinRandomBits);
      } catch (Throwable var5) {
         throw new Object(e.toString());
      }
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

      if (!(obj instanceof DHCryptoSystem)) {
         return false;
      }

      DHCryptoSystem other = (DHCryptoSystem)obj;
      return this._hashCode == other._hashCode && this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData);
   }

   public DHCryptoSystem() {
      this("SUN1024", -1);
   }

   public DHCryptoSystem(String name) {
      this(name, -1);
   }
}
