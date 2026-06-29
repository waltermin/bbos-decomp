package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class RC5Key implements SymmetricKey, Persistable {
   private RC5CryptoToken _cryptoToken;
   private CryptoTokenSymmetricKeyData _cryptoTokenData;
   private int _hashCode;

   public final CryptoTokenSymmetricKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final RC5CryptoToken getRC5CryptoToken() {
      return this._cryptoToken;
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

   @Override
   public final int getLength() {
      return this._cryptoToken.extractKeyDataLength(this._cryptoTokenData);
   }

   @Override
   public final String getAlgorithm() {
      return "RC5";
   }

   private final void initialize(RC5CryptoToken cryptoToken, int bitLength) {
      if (cryptoToken == null) {
         throw new IllegalArgumentException();
      }

      this.initialize(cryptoToken, cryptoToken.createKey(bitLength));
   }

   private final void initialize(RC5CryptoToken cryptoToken, byte[] data, int offset, int bitLength) {
      if (cryptoToken == null) {
         throw new IllegalArgumentException();
      }

      this.initialize(cryptoToken, cryptoToken.injectKey(data, offset, bitLength));
   }

   private final void initialize(RC5CryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RC5Key(RC5CryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   public RC5Key(RC5CryptoToken cryptoToken, byte[] data, int offset, int bitLength) {
      this.initialize(cryptoToken, data, offset, bitLength);
   }

   public RC5Key(byte[] data) {
      this(data, 0, data == null ? 0 : data.length << 3);
   }

   public RC5Key(byte[] data, int offset) {
      this(data, offset, data == null ? 0 : Math.min(data.length - offset << 3, 128));
   }

   public RC5Key(byte[] param1, int param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 1
      // 05: ifnull 17
      // 08: iload 2
      // 09: iflt 17
      // 0c: aload 1
      // 0d: arraylength
      // 0e: iload 3
      // 0f: bipush 3
      // 11: ishr
      // 12: isub
      // 13: iload 2
      // 14: if_icmpge 1f
      // 17: new java/lang/IllegalArgumentException
      // 1a: dup
      // 1b: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1e: athrow
      // 1f: aload 0
      // 20: invokestatic net/rim/device/api/crypto/SoftwareRC5CryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareRC5CryptoToken;
      // 23: aload 1
      // 24: iload 2
      // 25: iload 3
      // 26: invokespecial net/rim/device/api/crypto/RC5Key.initialize (Lnet/rim/device/api/crypto/RC5CryptoToken;[BII)V
      // 29: return
      // 2a: astore 4
      // 2c: new java/lang/RuntimeException
      // 2f: dup
      // 30: aload 4
      // 32: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 35: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 38: athrow
      // 39: astore 4
      // 3b: new java/lang/RuntimeException
      // 3e: dup
      // 3f: aload 4
      // 41: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 44: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 47: athrow
      // try (18 -> 24): 25 null
      // try (18 -> 24): 32 null
   }

   public RC5Key(int param1) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareRC5CryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareRC5CryptoToken;
      // 08: iload 1
      // 09: invokespecial net/rim/device/api/crypto/RC5Key.initialize (Lnet/rim/device/api/crypto/RC5CryptoToken;I)V
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
      // try (2 -> 6): 7 null
      // try (2 -> 6): 14 null
   }

   public RC5Key() {
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

      if (obj instanceof RC5Key) {
         RC5Key other = (RC5Key)obj;
         if (this._hashCode == other._hashCode) {
            if (this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
