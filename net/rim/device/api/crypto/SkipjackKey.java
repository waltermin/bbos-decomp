package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class SkipjackKey implements SymmetricKey, Persistable {
   private SkipjackCryptoToken _cryptoToken;
   private CryptoTokenSymmetricKeyData _cryptoTokenData;
   private int _hashCode;
   public static final int LENGTH;

   public final CryptoTokenSymmetricKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final SkipjackCryptoToken getSkipjackCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final SymmetricCryptoToken getSymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final String getAlgorithm() {
      return "Skipjack";
   }

   @Override
   public final int getLength() {
      return 10;
   }

   @Override
   public final int getBitLength() {
      return 80;
   }

   @Override
   public final byte[] getData() {
      return this._cryptoToken.extractKeyData(this._cryptoTokenData);
   }

   private final void initialize(SkipjackCryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public SkipjackKey(byte[] param1, int param2) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareSkipjackCryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareSkipjackCryptoToken;
      // 08: aload 1
      // 09: iload 2
      // 0a: invokespecial net/rim/device/api/crypto/SkipjackKey.initialize (Lnet/rim/device/api/crypto/SkipjackCryptoToken;[BI)V
      // 0d: return
      // 0e: astore 3
      // 0f: new java/lang/Object
      // 12: dup
      // 13: aload 3
      // 14: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 17: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1a: athrow
      // 1b: astore 3
      // 1c: new java/lang/Object
      // 1f: dup
      // 20: aload 3
      // 21: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 24: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 27: athrow
      // try (2 -> 7): 8 null
      // try (2 -> 7): 15 null
   }

   public SkipjackKey(byte[] data) {
      this(data, 0);
   }

   public SkipjackKey() {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareSkipjackCryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareSkipjackCryptoToken;
      // 08: invokespecial net/rim/device/api/crypto/SkipjackKey.initialize (Lnet/rim/device/api/crypto/SkipjackCryptoToken;)V
      // 0b: return
      // 0c: astore 1
      // 0d: new java/lang/Object
      // 10: dup
      // 11: aload 1
      // 12: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 15: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 18: athrow
      // 19: astore 1
      // 1a: new java/lang/Object
      // 1d: dup
      // 1e: aload 1
      // 1f: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 22: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 25: athrow
      // try (2 -> 5): 6 null
      // try (2 -> 5): 13 null
   }

   public SkipjackKey(SkipjackCryptoToken cryptoToken, byte[] data, int offset) {
      this.initialize(cryptoToken, data, offset);
   }

   public SkipjackKey(SkipjackCryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   private final void initialize(SkipjackCryptoToken cryptoToken) {
      if (cryptoToken == null) {
         throw new Object();
      }

      this.initialize(cryptoToken, cryptoToken.createKey());
   }

   private final void initialize(SkipjackCryptoToken cryptoToken, byte[] data, int offset) {
      if (cryptoToken != null && data != null && offset >= 0 && data.length - 10 >= offset) {
         this.initialize(cryptoToken, cryptoToken.injectKey(data, offset));
      } else {
         throw new Object();
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
      if (this._hashCode == 0) {
         this._hashCode = 1;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SkipjackKey) {
         SkipjackKey other = (SkipjackKey)obj;
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
