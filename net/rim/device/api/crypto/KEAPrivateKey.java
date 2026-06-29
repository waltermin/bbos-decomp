package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class KEAPrivateKey implements KEAKey, PrivateKey, Persistable {
   private KEACryptoSystem _cryptoSystem;
   private KEACryptoToken _cryptoToken;
   private CryptoTokenPrivateKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;

   public final CryptoTokenPrivateKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final KEACryptoToken getKEACryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getPublicKeyData() {
      return this._cryptoToken.extractKEAPublicKeyData(this._cryptoTokenData);
   }

   public final byte[] getPrivateKeyData() {
      return this._cryptoToken.extractKEAPrivateKeyData(this._cryptoTokenData);
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         KEACryptoSystem cs = this.getKEACryptoSystem();
         cs.verify();

         label51:
         try {
            byte[] q = cs.getQ();
            byte[] x = this.getPrivateKeyData();
            if (CryptoByteArrayArithmetic.isZero(x)) {
               throw new InvalidKeyException();
            }

            if (CryptoByteArrayArithmetic.compare(x, q) >= 0) {
               throw new InvalidKeyException();
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
   public final CryptoSystem getCryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final KEACryptoSystem getKEACryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final String getAlgorithm() {
      return "KEA";
   }

   private final void initialize(KEACryptoSystem cryptoSystem, KEACryptoToken cryptoToken, CryptoTokenPrivateKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public KEAPrivateKey(KEACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         KEACryptoToken cryptoToken = (KEACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public KEAPrivateKey(KEACryptoSystem cryptoSystem, byte[] data) throws InvalidKeyException {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         if (data.length > cryptoSystem.getPrivateKeyLength()) {
            throw new InvalidKeyException();
         }

         KEACryptoToken cryptoToken = (KEACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectKEAPrivateKey(cryptoSystem.getCryptoTokenData(), data));
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoSystem.hashCode() ^ this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof KEAPrivateKey)) {
         return false;
      }

      KEAPrivateKey other = (KEAPrivateKey)obj;
      return this._hashCode == other._hashCode
         && this._cryptoSystem.equals(other._cryptoSystem)
         && this._cryptoToken.equals(other._cryptoToken)
         && this._cryptoTokenData.equals(other._cryptoTokenData);
   }
}
