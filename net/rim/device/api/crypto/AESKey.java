package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class AESKey implements SymmetricKey, Persistable {
   private AESCryptoToken _cryptoToken;
   private CryptoTokenSymmetricKeyData _cryptoTokenData;
   private int _hashCode;

   public final CryptoTokenSymmetricKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final AESCryptoToken getAESCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final SymmetricCryptoToken getSymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final String getAlgorithm() {
      return "AES";
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

   private final void initialize(AESCryptoToken cryptoToken, byte[] data, int offset) {
      if (cryptoToken != null && data != null) {
         int bitLength = data.length - offset << 3;
         short var5;
         if (bitLength >= 256) {
            var5 = 256;
         } else if (bitLength >= 192) {
            var5 = 192;
         } else {
            if (bitLength < 128) {
               throw new IllegalArgumentException();
            }

            var5 = 128;
         }

         this.initialize(cryptoToken, cryptoToken.injectKey(data, offset, var5));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void initialize(AESCryptoToken cryptoToken, byte[] data, int offset, int bitLength) {
      if (cryptoToken != null
         && (bitLength == 128 || bitLength == 192 || bitLength == 256)
         && data != null
         && offset >= 0
         && offset + bitLength >>> 3 <= data.length) {
         this.initialize(cryptoToken, cryptoToken.injectKey(data, offset, bitLength));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void initialize(AESCryptoToken cryptoToken, int bitLength) {
      if (cryptoToken != null && (bitLength == 128 || bitLength == 192 || bitLength == 256)) {
         this.initialize(cryptoToken, cryptoToken.createKey(bitLength));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void initialize(AESCryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public AESKey(byte[] data) {
      this(data, 0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public AESKey(int bitLength) {
      try {
         this.initialize(SoftwareAESCryptoToken.getInstance(), bitLength);
      } catch (Throwable var4) {
         throw new RuntimeException(e.toString());
      }
   }

   public AESKey() {
      this(128);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public AESKey(byte[] data, int offset) {
      try {
         this.initialize(SoftwareAESCryptoToken.getInstance(), data, offset);
      } catch (Throwable var5) {
         throw new RuntimeException(e.toString());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public AESKey(byte[] data, int offset, int bitLength) {
      try {
         this.initialize(SoftwareAESCryptoToken.getInstance(), data, offset, bitLength);
      } catch (Throwable var6) {
         throw new RuntimeException(e.toString());
      }
   }

   public AESKey(AESCryptoToken cryptoToken, byte[] data, int offset, int bitLength) {
      this.initialize(cryptoToken, data, offset, bitLength);
   }

   public AESKey(AESCryptoToken cryptoToken, CryptoTokenSymmetricKeyData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
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

      if (!(obj instanceof AESKey)) {
         return false;
      }

      AESKey other = (AESKey)obj;
      return other.hashCode() == this._hashCode && this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData);
   }
}
