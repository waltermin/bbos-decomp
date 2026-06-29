package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class KEAKeyPair extends KeyPair implements Persistable {
   public KEAKeyPair(KEACryptoSystem cryptoSystem) {
      if (cryptoSystem == null) {
         throw new Object();
      }

      KeyPair keypair = cryptoSystem.createKEAKeyPair();
      this.setKeyPair(keypair.getPublicKey(), keypair.getPrivateKey());
   }

   public KEAKeyPair(KEAPublicKey publicKey, KEAPrivateKey privateKey) {
   }

   public final KEAPublicKey getKEAPublicKey() {
      return (KEAPublicKey)this.getPublicKey();
   }

   public final KEAPrivateKey getKEAPrivateKey() {
      return (KEAPrivateKey)this.getPrivateKey();
   }

   public final KEACryptoSystem getKEACryptoSystem() {
      return (KEACryptoSystem)this.getCryptoSystem();
   }

   @Override
   public final void verify() {
      if (!this.isVerified()) {
         KEACryptoSystem cs = this.getKEACryptoSystem();
         KEAPrivateKey privateKey = this.getKEAPrivateKey();
         privateKey.verify();
         KEAPublicKey publicKey = this.getKEAPublicKey();
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
               throw new Object();
            }
         } finally {
            break label26;
         }

         this.verified();
      }
   }

   @Override
   public final int hashCode() {
      return super.hashCode() ^ 4932929;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof KEAKeyPair && super.equals(obj);
   }
}
