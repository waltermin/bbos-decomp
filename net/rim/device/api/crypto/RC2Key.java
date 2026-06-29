package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class RC2Key implements SymmetricKey, Persistable {
   private RC2CryptoToken _cryptoToken;
   private CryptoTokenSymmetricKeyData _cryptoTokenData;
   private int _hashCode;
   private static final byte[] VERSION_TO_EBL = new byte[]{
      93,
      -66,
      -101,
      -117,
      17,
      -103,
      110,
      77,
      89,
      -13,
      -123,
      -90,
      63,
      -73,
      -125,
      -59,
      -28,
      115,
      107,
      58,
      104,
      90,
      -64,
      71,
      -96,
      100,
      52,
      12,
      -15,
      -48,
      82,
      -91,
      -71,
      30,
      -106,
      67,
      65,
      -40,
      -44,
      44,
      -37,
      -8,
      7,
      119,
      42,
      -54,
      -21,
      -17,
      16,
      28,
      22,
      13,
      56,
      114,
      47,
      -119,
      -63,
      -7,
      -128,
      -60,
      109,
      -82,
      48,
      61,
      -50,
      32,
      99,
      -2,
      -26,
      26,
      -57,
      -72,
      80,
      -24,
      36,
      23,
      -4,
      37,
      111,
      -69,
      106,
      -93,
      68,
      83,
      -39,
      -94,
      1,
      -85,
      -68,
      -74,
      31,
      -104,
      -18,
      -102,
      -89,
      45,
      79,
      -98,
      -114,
      -84,
      -32,
      -58,
      73,
      70,
      41,
      -12,
      -108,
      -118,
      -81,
      -31,
      91,
      -61,
      -77,
      123,
      87,
      -47,
      124,
      -100,
      -19,
      -121,
      64,
      -116,
      -30,
      -53,
      -109,
      20,
      -55,
      97,
      46,
      -27,
      -52,
      -10,
      94,
      -88,
      92,
      -42,
      117,
      -115,
      98,
      -107,
      88,
      105,
      118,
      -95,
      74,
      -75,
      85,
      9,
      120,
      51,
      -126,
      -41,
      -35,
      121,
      -11,
      27,
      11,
      -34,
      38,
      33,
      40,
      116,
      4,
      -105,
      86,
      -33,
      60,
      -16,
      55,
      57,
      -36,
      -1,
      6,
      -92,
      -22,
      66,
      8,
      -38,
      -76,
      113,
      -80,
      -49,
      18,
      122,
      78,
      -6,
      108,
      29,
      -124,
      0,
      -56,
      127,
      -111,
      69,
      -86,
      43,
      -62,
      -79,
      -113,
      -43,
      -70,
      -14,
      -83,
      25,
      -78,
      103,
      54,
      -9,
      15,
      10,
      -110,
      125,
      -29,
      -99,
      -23,
      -112,
      62,
      35,
      39,
      102,
      19,
      -20,
      -127,
      21,
      -67,
      34,
      -65,
      -97,
      126,
      -87,
      81,
      75,
      76,
      -5,
      2,
      -45,
      112,
      -122,
      49,
      -25,
      59,
      5,
      3,
      84,
      96,
      72,
      101,
      24,
      -46,
      -51,
      95,
      50,
      -120,
      14,
      53,
      -3
   };
   private static final byte[] EBL_TO_VERSION = new byte[]{
      -67,
      86,
      -22,
      -14,
      -94,
      -15,
      -84,
      42,
      -80,
      -109,
      -47,
      -100,
      27,
      51,
      -3,
      -48,
      48,
      4,
      -74,
      -36,
      125,
      -33,
      50,
      75,
      -9,
      -53,
      69,
      -101,
      49,
      -69,
      33,
      90,
      65,
      -97,
      -31,
      -39,
      74,
      77,
      -98,
      -38,
      -96,
      104,
      44,
      -61,
      39,
      95,
      -128,
      54,
      62,
      -18,
      -5,
      -107,
      26,
      -2,
      -50,
      -88,
      52,
      -87,
      19,
      -16,
      -90,
      63,
      -40,
      12,
      120,
      36,
      -81,
      35,
      82,
      -63,
      103,
      23,
      -11,
      102,
      -112,
      -25,
      -24,
      7,
      -72,
      96,
      72,
      -26,
      30,
      83,
      -13,
      -110,
      -92,
      114,
      -116,
      8,
      21,
      110,
      -122,
      0,
      -124,
      -6,
      -12,
      127,
      -118,
      66,
      25,
      -10,
      -37,
      -51,
      20,
      -115,
      80,
      18,
      -70,
      60,
      6,
      78,
      -20,
      -77,
      53,
      17,
      -95,
      -120,
      -114,
      43,
      -108,
      -103,
      -73,
      113,
      116,
      -45,
      -28,
      -65,
      58,
      -34,
      -106,
      14,
      -68,
      10,
      -19,
      119,
      -4,
      55,
      107,
      3,
      121,
      -119,
      98,
      -58,
      -41,
      -64,
      -46,
      124,
      106,
      -117,
      34,
      -93,
      91,
      5,
      93,
      2,
      117,
      -43,
      97,
      -29,
      24,
      -113,
      85,
      81,
      -83,
      31,
      11,
      94,
      -123,
      -27,
      -62,
      87,
      99,
      -54,
      61,
      108,
      -76,
      -59,
      -52,
      112,
      -78,
      -111,
      89,
      13,
      71,
      32,
      -56,
      79,
      88,
      -32,
      1,
      -30,
      22,
      56,
      -60,
      111,
      59,
      15,
      101,
      70,
      -66,
      126,
      45,
      123,
      -126,
      -7,
      64,
      -75,
      29,
      115,
      -8,
      -21,
      38,
      -57,
      -121,
      -105,
      37,
      84,
      -79,
      40,
      -86,
      -104,
      -99,
      -91,
      100,
      109,
      122,
      -44,
      16,
      -127,
      68,
      -17,
      73,
      -42,
      -82,
      46,
      -35,
      118,
      92,
      47,
      -89,
      28,
      -55,
      9,
      105,
      -102,
      -125,
      -49,
      41,
      57,
      -71,
      -23,
      76,
      -1,
      67,
      -85
   };

   public final int getEffectiveBitLength() {
      return this._cryptoToken.extractKeyEffectiveBitLength(this._cryptoTokenData);
   }

   public final RC2CryptoToken getRC2CryptoToken() {
      return this._cryptoToken;
   }

   public final CryptoTokenSymmetricKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   @Override
   public final String getAlgorithm() {
      return "RC2";
   }

   @Override
   public final int getLength() {
      return this._cryptoToken.extractKeyDataLength(this._cryptoTokenData);
   }

   @Override
   public final int getBitLength() {
      return this._cryptoToken.extractKeyDataLength(this._cryptoTokenData) * 8;
   }

   @Override
   public final byte[] getData() {
      return this._cryptoToken.extractKeyData(this._cryptoTokenData);
   }

   @Override
   public final SymmetricCryptoToken getSymmetricCryptoToken() {
      return this._cryptoToken;
   }

   public RC2Key(RC2CryptoToken cryptoToken, byte[] data, int offset, int bitLength, int effectiveBitLength) {
      if (cryptoToken == null) {
         throw new IllegalArgumentException();
      }

      this.initialize(cryptoToken, cryptoToken.injectKey(data, offset, bitLength, effectiveBitLength));
   }

   public RC2Key(RC2CryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   private final void initialize(RC2CryptoToken cryptoToken, int bitLength, int effectiveBitLength) {
      if (cryptoToken == null) {
         throw new IllegalArgumentException();
      }

      this.initialize(cryptoToken, cryptoToken.createKey(bitLength, effectiveBitLength));
   }

   private final void initialize(byte[] param1, int param2, int param3, int param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokestatic net/rim/device/api/crypto/SoftwareRC2CryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareRC2CryptoToken;
      // 04: aload 1
      // 05: iload 2
      // 06: iload 3
      // 07: iload 4
      // 09: invokespecial net/rim/device/api/crypto/RC2Key.initialize (Lnet/rim/device/api/crypto/RC2CryptoToken;[BIII)V
      // 0c: return
      // 0d: astore 5
      // 0f: new java/lang/RuntimeException
      // 12: dup
      // 13: aload 5
      // 15: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 18: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1b: athrow
      // 1c: astore 5
      // 1e: new java/lang/RuntimeException
      // 21: dup
      // 22: aload 5
      // 24: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 27: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2a: athrow
      // try (0 -> 7): 8 null
      // try (0 -> 7): 15 null
   }

   private final void initialize(RC2CryptoToken cryptoToken, byte[] data, int offset, int bitLength, int effectiveBitLength) {
      this.initialize(cryptoToken, cryptoToken.injectKey(data, offset, bitLength, effectiveBitLength));
   }

   private final void initialize(RC2CryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RC2Key(byte[] data, int offset, int bitLength, int effectiveBitLength) {
      this.initialize(data, offset, bitLength, effectiveBitLength);
   }

   public RC2Key(int bitLength) {
      this(bitLength, bitLength);
   }

   public RC2Key(int param1, int param2) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareRC2CryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareRC2CryptoToken;
      // 08: iload 1
      // 09: iload 2
      // 0a: invokespecial net/rim/device/api/crypto/RC2Key.initialize (Lnet/rim/device/api/crypto/RC2CryptoToken;II)V
      // 0d: return
      // 0e: astore 3
      // 0f: new java/lang/RuntimeException
      // 12: dup
      // 13: aload 3
      // 14: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 17: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1a: athrow
      // 1b: astore 3
      // 1c: new java/lang/RuntimeException
      // 1f: dup
      // 20: aload 3
      // 21: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 24: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 27: athrow
      // try (2 -> 7): 8 null
      // try (2 -> 7): 15 null
   }

   public RC2Key(RC2CryptoToken cryptoToken, int bitLength, int effectiveBitLength) {
      this.initialize(cryptoToken, bitLength, effectiveBitLength);
   }

   public RC2Key(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      int bitLength = data.length << 3;
      this.initialize(data, 0, bitLength, bitLength);
   }

   public RC2Key(byte[] data, int effectiveBitLength) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      this.initialize(data, 0, data.length << 3, effectiveBitLength);
   }

   public RC2Key(byte[] data, int offset, int bitLength) {
      this.initialize(data, offset, bitLength, bitLength);
   }

   public RC2Key() {
      this(128);
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
      if (this._hashCode == 0) {
         this._hashCode = 1;
      }
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

      if (obj instanceof RC2Key) {
         RC2Key other = (RC2Key)obj;
         if (this._hashCode == other._hashCode) {
            if (this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   public static final int decodeEffectiveBitLength(int encodedEffectiveBitLength) {
      if (encodedEffectiveBitLength < 0 || encodedEffectiveBitLength == 189 || encodedEffectiveBitLength > 1024) {
         throw new IllegalArgumentException();
      } else {
         return encodedEffectiveBitLength < 256 ? VERSION_TO_EBL[encodedEffectiveBitLength] & 0xFF : encodedEffectiveBitLength;
      }
   }

   public static final int encodeEffectiveBitLength(int effectiveBitLength) {
      if (effectiveBitLength < 1 || effectiveBitLength > 1024) {
         throw new IllegalArgumentException();
      } else {
         return effectiveBitLength < 256 ? EBL_TO_VERSION[effectiveBitLength] & 0xFF : effectiveBitLength;
      }
   }
}
