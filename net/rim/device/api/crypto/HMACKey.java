package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class HMACKey implements SymmetricKey, Persistable {
   private HMACCryptoToken _cryptoToken;
   private CryptoTokenMACKeyData _cryptoTokenData;
   private int _hashCode;

   public final CryptoTokenMACKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final HMACCryptoToken getHMACCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final int getLength() {
      return this._cryptoToken.extractKeyLength(this._cryptoTokenData);
   }

   @Override
   public final int getBitLength() {
      return this._cryptoToken.extractKeyLength(this._cryptoTokenData) * 8;
   }

   @Override
   public final SymmetricCryptoToken getSymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final byte[] getData() {
      return this._cryptoToken.extractKeyData(this._cryptoTokenData);
   }

   @Override
   public final String getAlgorithm() {
      return "HMAC";
   }

   private final void initialize(HMACCryptoToken cryptoToken, int length) {
      if (cryptoToken != null && length >= 0) {
         this.initialize(cryptoToken, cryptoToken.createKey(length));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void intialize(HMACCryptoToken cryptoToken, byte[] data, int offset, int length) {
      if (cryptoToken != null && data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         this.initialize(cryptoToken, cryptoToken.injectKey(data, offset, length));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void initialize(HMACCryptoToken cryptoToken, CryptoTokenMACKeyData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public HMACKey(HMACCryptoToken cryptoToken, CryptoTokenMACKeyData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   public HMACKey(HMACCryptoToken cryptoToken, byte[] data, int offset, int length) {
      this.intialize(cryptoToken, data, offset, length);
   }

   public HMACKey(HMACCryptoToken cryptoToken, int length) {
      this.initialize(cryptoToken, length);
   }

   public HMACKey(byte[] data) {
      this(data, 0, data == null ? 0 : data.length);
   }

   public HMACKey(byte[] data, int offset, int length) {
      try {
         this.intialize(SoftwareHMACCryptoToken.getInstance(), data, offset, length);
      } catch (CryptoTokenException e) {
         throw new RuntimeException(e.toString());
      } catch (CryptoUnsupportedOperationException e) {
         throw new RuntimeException(e.toString());
      }
   }

   public HMACKey(int length) {
      try {
         this.initialize(SoftwareHMACCryptoToken.getInstance(), length);
      } catch (CryptoTokenException e) {
         throw new RuntimeException(e.toString());
      } catch (CryptoUnsupportedOperationException e) {
         throw new RuntimeException(e.toString());
      }
   }

   public HMACKey() {
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

      if (obj instanceof HMACKey) {
         HMACKey other = (HMACKey)obj;
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
