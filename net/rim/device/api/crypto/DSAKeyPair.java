package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class DSAKeyPair extends KeyPair implements Persistable {
   public DSAKeyPair(DSACryptoSystem cryptoSystem) {
      if (cryptoSystem == null) {
         throw new IllegalArgumentException();
      }

      KeyPair keypair = cryptoSystem.createDSAKeyPair();
      this.setKeyPair(keypair.getPublicKey(), keypair.getPrivateKey());
   }

   public DSAKeyPair(DSAPublicKey publicKey, DSAPrivateKey privateKey) {
      super(publicKey, privateKey);
   }

   public final DSAPublicKey getDSAPublicKey() {
      return (DSAPublicKey)this.getPublicKey();
   }

   public final DSAPrivateKey getDSAPrivateKey() {
      return (DSAPrivateKey)this.getPrivateKey();
   }

   public final DSACryptoSystem getDSACryptoSystem() {
      return (DSACryptoSystem)this.getCryptoSystem();
   }

   @Override
   public final void verify() {
      if (!this.isVerified()) {
         DSACryptoSystem cs = this.getDSACryptoSystem();
         DSAPrivateKey privateKey = this.getDSAPrivateKey();
         privateKey.verify();
         DSAPublicKey publicKey = this.getDSAPublicKey();
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
      return super.hashCode() ^ 4477761;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof DSAKeyPair && super.equals(obj);
   }
}
