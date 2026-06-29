package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class TripleDESKey implements SymmetricKey, Persistable {
   private TripleDESCryptoToken _cryptoToken;
   private CryptoTokenSymmetricKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _twoKey;
   public static final int LENGTH = 24;

   public final CryptoTokenSymmetricKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final TripleDESCryptoToken getTripleDESCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final SymmetricCryptoToken getSymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final String getAlgorithm() {
      return "TripleDES";
   }

   @Override
   public final int getLength() {
      return 24;
   }

   @Override
   public final int getBitLength() {
      return this._twoKey ? 112 : 168;
   }

   @Override
   public final byte[] getData() {
      return this._cryptoToken.extractKeyData(this._cryptoTokenData);
   }

   public TripleDESKey(TripleDESCryptoToken cryptoToken, DESKey key1, DESKey key2, DESKey key3) {
      this.initialize(cryptoToken, key1, key2, key3);
   }

   public TripleDESKey(TripleDESCryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   private final void initialize(TripleDESCryptoToken cryptoToken, byte[] data, int offset) {
      if (cryptoToken != null && data != null) {
         if (data.length == 16) {
            this._twoKey = true;
         }

         if (data.length == 24 && Arrays.equals(data, 0, data, 16, 8)) {
            this._twoKey = true;
         }

         this.initialize(cryptoToken, cryptoToken.injectKey(data, offset));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void initialize(TripleDESCryptoToken cryptoToken, DESKey key1, DESKey key2, DESKey key3) {
      if (key1 != null && key2 != null && key3 != null) {
         if (key1.equals(key3)) {
            this._twoKey = true;
         }

         byte[] data = new byte[24];
         System.arraycopy(key1.getData(), 0, data, 0, 8);
         System.arraycopy(key2.getData(), 0, data, 8, 8);
         System.arraycopy(key3.getData(), 0, data, 16, 8);
         this.initialize(cryptoToken, cryptoToken.injectKey(data, 0));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void initialize(TripleDESCryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public TripleDESKey(byte[] param1, int param2) {
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
      // 05: invokestatic net/rim/device/api/crypto/SoftwareTripleDESCryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareTripleDESCryptoToken;
      // 08: aload 1
      // 09: iload 2
      // 0a: invokespecial net/rim/device/api/crypto/TripleDESKey.initialize (Lnet/rim/device/api/crypto/TripleDESCryptoToken;[BI)V
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

   public TripleDESKey(byte[] data) {
      this(data, 0);
   }

   public TripleDESKey() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: invokestatic net/rim/device/api/crypto/SoftwareTripleDESCryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareTripleDESCryptoToken;
      // 07: astore 1
      // 08: aload 0
      // 09: aload 1
      // 0a: aload 1
      // 0b: invokevirtual net/rim/device/api/crypto/TripleDESCryptoToken.createKey ()Lnet/rim/device/api/crypto/CryptoTokenSymmetricKeyData;
      // 0e: invokespecial net/rim/device/api/crypto/TripleDESKey.initialize (Lnet/rim/device/api/crypto/TripleDESCryptoToken;Lnet/rim/device/api/crypto/CryptoTokenSymmetricKeyData;)V
      // 11: return
      // 12: astore 1
      // 13: new java/lang/RuntimeException
      // 16: dup
      // 17: aload 1
      // 18: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 1b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1e: athrow
      // 1f: astore 1
      // 20: new java/lang/RuntimeException
      // 23: dup
      // 24: aload 1
      // 25: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 28: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // try (2 -> 9): 10 null
      // try (2 -> 9): 17 null
   }

   public TripleDESKey(TripleDESCryptoToken cryptoToken, byte[] data, int offset) {
      this.initialize(cryptoToken, data, offset);
   }

   public TripleDESKey(DESKey key1, DESKey key2) {
      this(key1, key2, key1);
   }

   public TripleDESKey(DESKey key1, DESKey key2, DESKey key3) {
      this.initialize(SoftwareTripleDESCryptoToken.getInstance(), key1, key2, key3);
   }

   public TripleDESKey(TripleDESCryptoToken cryptoToken, DESKey key1, DESKey key2) {
      this(cryptoToken, key1, key2, key1);
   }

   private final void setHashCode() {
      if (this._hashCode == 0) {
         this._hashCode = this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
         if (this._hashCode == 0) {
            this._hashCode = 1;
         }
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

      if (!(obj instanceof TripleDESKey)) {
         return false;
      }

      TripleDESKey other = (TripleDESKey)obj;
      return other.hashCode() == this._hashCode && this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData);
   }
}
