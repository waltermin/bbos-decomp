package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class DHPrivateKey implements PrivateKey, DHKey, Persistable {
   private DHCryptoSystem _cryptoSystem;
   private DHCryptoToken _cryptoToken;
   private CryptoTokenPrivateKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;

   public final CryptoTokenPrivateKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final DHCryptoToken getDHCryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getPublicKeyData() {
      return this._cryptoToken.extractDHPublicKeyData(this._cryptoTokenData);
   }

   public final byte[] getPrivateKeyData() {
      return this._cryptoToken.extractDHPrivateKeyData(this._cryptoTokenData);
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         DHCryptoSystem cs = this.getDHCryptoSystem();
         cs.verify();

         label55:
         try {
            byte[] q = cs.getQ();
            byte[] x = this.getPrivateKeyData();
            if (CryptoByteArrayArithmetic.isZero(x)) {
               throw new Object();
            }

            if (q != null && CryptoByteArrayArithmetic.compare(x, q) >= 0) {
               throw new Object();
            }
         } finally {
            break label55;
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
   public final DHCryptoSystem getDHCryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final String getAlgorithm() {
      return "DH";
   }

   private final void initialize(DHCryptoSystem cryptoSystem, DHCryptoToken cryptoToken, CryptoTokenPrivateKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public DHPrivateKey(DHCryptoSystem cryptoSystem, CryptoTokenPrivateKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         DHCryptoToken cryptoToken = (DHCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new Object();
      }
   }

   public DHPrivateKey(DHCryptoSystem cryptoSystem, byte[] data) {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         int maxPrivateKeyLength = cryptoSystem.getPrivateKeyLength();
         if (data.length > maxPrivateKeyLength) {
            throw new Object();
         }

         DHCryptoToken cryptoToken = (DHCryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectDHPrivateKey(cryptoSystem.getCryptoTokenData(), data));
      } else {
         throw new Object();
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

      if (!(obj instanceof DHPrivateKey)) {
         return false;
      }

      DHPrivateKey other = (DHPrivateKey)obj;
      return this._hashCode == other._hashCode
         && this._cryptoSystem.equals(other._cryptoSystem)
         && this._cryptoToken.equals(other._cryptoToken)
         && this._cryptoTokenData.equals(other._cryptoTokenData);
   }
}
