package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class ECKeyPair extends KeyPair implements Persistable {
   public ECKeyPair(ECCryptoSystem cryptoSystem) {
      if (cryptoSystem == null) {
         throw new IllegalArgumentException();
      }

      KeyPair keypair = cryptoSystem.createECKeyPair();
      this.setKeyPair(keypair.getPublicKey(), keypair.getPrivateKey());
   }

   public ECKeyPair(ECPublicKey publicKey, ECPrivateKey privateKey) {
   }

   public final ECPublicKey getECPublicKey() {
      return (ECPublicKey)this.getPublicKey();
   }

   public final ECPrivateKey getECPrivateKey() {
      return (ECPrivateKey)this.getPrivateKey();
   }

   public final ECCryptoSystem getECCryptoSystem() {
      return (ECCryptoSystem)this.getCryptoSystem();
   }

   @Override
   public final void verify() {
      if (!this.isVerified()) {
         ECPrivateKey privateKey = this.getECPrivateKey();
         privateKey.verify();
         ECPublicKey publicKey = this.getECPublicKey();
         publicKey.verify();
         this.verified();
      }
   }

   @Override
   public final int hashCode() {
      return super.hashCode() ^ 17731;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof ECKeyPair && super.equals(obj);
   }
}
