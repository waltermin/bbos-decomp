package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class DSAPrivateKey implements DSAKey, PrivateKey, Persistable {
   private DSACryptoSystem _cryptoSystem;
   private DSACryptoToken _cryptoToken;
   private CryptoTokenPrivateKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;

   public final CryptoTokenPrivateKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final DSACryptoToken getDSACryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getPublicKeyData() {
      return this._cryptoToken.extractDSAPublicKeyData(this._cryptoTokenData);
   }

   public final byte[] getPrivateKeyData() {
      return this._cryptoToken.extractDSAPrivateKeyData(this._cryptoTokenData);
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         DSACryptoSystem cs = this.getDSACryptoSystem();
         cs.verify();

         label51:
         try {
            byte[] q = cs.getQ();
            byte[] x = this.getPrivateKeyData();
            if (CryptoByteArrayArithmetic.isZero(x)) {
               throw new Object();
            }

            if (CryptoByteArrayArithmetic.compare(x, q) >= 0) {
               throw new Object();
            }
         } finally {
            break label51;
         }

         try {
            this._verified = true;
         } finally {
            return;
         }
      }
   }

   @Override
   public final DSACryptoSystem getDSACryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final CryptoSystem getCryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final String getAlgorithm() {
      return "DSA";
   }

   private final void initialize(DSACryptoSystem cryptoSystem, DSACryptoToken cryptoToken, CryptoTokenPrivateKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public DSAPrivateKey(DSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         DSACryptoToken cryptoToken = (DSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new Object();
      }
   }

   public DSAPrivateKey(DSACryptoSystem cryptoSystem, byte[] data) {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         if (data.length > cryptoSystem.getPrivateKeyLength()) {
            throw new Object();
         }

         DSACryptoToken cryptoToken = (DSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectDSAPrivateKey(cryptoSystem.getCryptoTokenData(), data));
      } else {
         throw new Object();
      }
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoSystem.hashCode() ^ this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
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

      if (!(obj instanceof DSAPrivateKey)) {
         return false;
      }

      DSAPrivateKey other = (DSAPrivateKey)obj;
      return this._hashCode == other.hashCode()
         && this._cryptoSystem.equals(other._cryptoSystem)
         && this._cryptoToken.equals(other._cryptoToken)
         && this._cryptoTokenData.equals(other._cryptoTokenData);
   }
}
