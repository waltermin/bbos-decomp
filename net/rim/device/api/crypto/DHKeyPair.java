package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class DHKeyPair extends KeyPair implements Persistable {
   public DHKeyPair(DHCryptoSystem cryptoSystem) {
      if (cryptoSystem == null) {
         throw new IllegalArgumentException();
      }

      KeyPair keypair = cryptoSystem.createDHKeyPair();
      this.setKeyPair(keypair.getPublicKey(), keypair.getPrivateKey());
   }

   public DHKeyPair(DHPublicKey publicKey, DHPrivateKey privateKey) {
      super(publicKey, privateKey);
   }

   public final DHPublicKey getDHPublicKey() {
      return (DHPublicKey)this.getPublicKey();
   }

   public final DHPrivateKey getDHPrivateKey() {
      return (DHPrivateKey)this.getPrivateKey();
   }

   public final DHCryptoSystem getDHCryptoSystem() {
      return (DHCryptoSystem)this.getCryptoSystem();
   }

   @Override
   public final void verify() {
      if (!this.isVerified()) {
         DHCryptoSystem cs = this.getDHCryptoSystem();
         DHPrivateKey privateKey = this.getDHPrivateKey();
         privateKey.verify();
         DHPublicKey publicKey = this.getDHPublicKey();
         publicKey.verify();

         label26:
         try {
            byte[] p = cs.getP();
            byte[] g = cs.getG();
            byte[] x = privateKey.getPrivateKeyData();
            byte[] y = publicKey.getPublicKeyData();
            byte[] result = new byte[p.length];
            CryptoByteArrayArithmetic.exponent(g, x, p, result);
            if (CryptoByteArrayArithmetic.compare(result, y) != 0) {
               throw new InvalidKeyPairException();
            }
         } finally {
            break label26;
         }

         this.verified();
      }
   }

   @Override
   public final int hashCode() {
      return super.hashCode() ^ 17480;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof DHKeyPair && super.equals(obj);
   }
}
