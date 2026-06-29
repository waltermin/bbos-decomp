package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class KeyPair implements Persistable {
   private PublicKey _publicKey;
   private PrivateKey _privateKey;
   private int _hashCode;
   private boolean _verified;

   protected KeyPair() {
   }

   protected KeyPair(KeyPair keypair) {
      if (keypair == null) {
         throw new Object();
      }

      this.setKeyPair(keypair.getPublicKey(), keypair.getPrivateKey());
   }

   protected final void setKeyPair(PublicKey publicKey, PrivateKey privateKey) {
      if (publicKey != null && privateKey != null) {
         this._publicKey = publicKey;
         this._privateKey = privateKey;
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
      this.setKeyPair(publicKey, privateKey);
   }

   public PublicKey getPublicKey() {
      return this._publicKey;
   }

   public PrivateKey getPrivateKey() {
      return this._privateKey;
   }

   public CryptoSystem getCryptoSystem() {
      return this._publicKey == null ? null : this._publicKey.getCryptoSystem();
   }

   public void verify() {
      throw null;
   }

   protected void verified() {
      try {
         this._verified = true;
      } finally {
         return;
      }
   }

   public boolean isVerified() {
      return this._verified;
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }

   private void setHashCode() {
      this._hashCode = this._publicKey.hashCode() ^ this._privateKey.hashCode();
      if (this._hashCode == 0) {
         this._hashCode = 1;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof KeyPair) {
         KeyPair other = (KeyPair)obj;
         if (this._hashCode == other._hashCode) {
            if (this._publicKey.equals(other._publicKey) && this._privateKey.equals(other._privateKey)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
