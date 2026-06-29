package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class RSAKeyPair extends KeyPair implements Persistable {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public RSAKeyPair(RSACryptoSystem cryptoSystem) {
      try {
         this.initialize(cryptoSystem, new byte[]{1, 0, 1});
      } catch (Throwable var4) {
         throw new RuntimeException(e.toString());
      }
   }

   public RSAKeyPair(RSACryptoSystem cryptoSystem, byte[] e) {
      this.initialize(cryptoSystem, e);
   }

   private final void initialize(RSACryptoSystem cryptoSystem, byte[] e) throws InvalidKeyException {
      if (cryptoSystem != null && e != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (e.length != 0 && e.length <= modulusLength) {
            RSAKeyPair keyPair = cryptoSystem.createRSAKeyPair(e);
            this.setKeyPair(keyPair.getRSAPublicKey(), keyPair.getRSAPrivateKey());
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RSAKeyPair(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
      super(publicKey, privateKey);
   }

   public final RSAPublicKey getRSAPublicKey() {
      return (RSAPublicKey)this.getPublicKey();
   }

   public final RSAPrivateKey getRSAPrivateKey() {
      return (RSAPrivateKey)this.getPrivateKey();
   }

   public final RSACryptoSystem getRSACryptoSystem() {
      return (RSACryptoSystem)this.getCryptoSystem();
   }

   @Override
   public final void verify() {
      if (!this.isVerified()) {
         RSAPrivateKey privateKey = this.getRSAPrivateKey();
         privateKey.verify();
         RSAPublicKey publicKey = this.getRSAPublicKey();
         publicKey.verify();

         label47:
         try {
            byte[] p = privateKey.getP();
            byte[] q = privateKey.getQ();
            if (p != null && q != null) {
               byte[] e = publicKey.getE();
               byte[] n = publicKey.getN();
               byte[] d = privateKey.getD();
               byte[] result = new byte[p.length];
               byte[] pMinus1 = new byte[p.length];
               CryptoByteArrayArithmetic.decrement(p, p, pMinus1);
               CryptoByteArrayArithmetic.multiply(e, d, pMinus1, result);
               if (!CryptoByteArrayArithmetic.isOne(result)) {
                  throw new InvalidKeyPairException();
               }

               result = new byte[q.length];
               byte[] qMinus1 = new byte[q.length];
               CryptoByteArrayArithmetic.decrement(q, q, qMinus1);
               CryptoByteArrayArithmetic.multiply(e, d, qMinus1, result);
               if (!CryptoByteArrayArithmetic.isOne(result)) {
                  throw new InvalidKeyPairException();
               }

               result = new byte[n.length];
               CryptoByteArrayArithmetic.multiply(p, q, n, result);
               if (!CryptoByteArrayArithmetic.isZero(result)) {
                  throw new InvalidKeyPairException();
               }
            }
         } finally {
            break label47;
         }

         this.verified();
      }
   }

   @Override
   public final int hashCode() {
      return super.hashCode() ^ 5395265;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof RSAKeyPair && super.equals(obj);
   }
}
