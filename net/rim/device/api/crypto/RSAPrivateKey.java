package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class RSAPrivateKey implements PrivateKey, RSAKey, Persistable {
   private RSACryptoSystem _cryptoSystem;
   private RSACryptoToken _cryptoToken;
   private CryptoTokenPrivateKeyData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   private static final byte[] DEFAULT_E = new byte[]{1, 0, 1};

   public final CryptoTokenPrivateKeyData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final RSACryptoToken getRSACryptoToken() {
      return this._cryptoToken;
   }

   public final byte[] getQInvModP() {
      return this._cryptoToken.extractRSAPrivateKeyQInvModP(this._cryptoTokenData);
   }

   public final byte[] getDModQm1() {
      return this._cryptoToken.extractRSAPrivateKeyDModQm1(this._cryptoTokenData);
   }

   public final byte[] getE() {
      return this._cryptoToken.extractRSAPrivateKeyE(this._cryptoTokenData);
   }

   public final byte[] getD() {
      return this._cryptoToken.extractRSAPrivateKeyD(this._cryptoTokenData);
   }

   public final byte[] getN() {
      return this._cryptoToken.extractRSAPrivateKeyN(this._cryptoTokenData);
   }

   public final byte[] getP() {
      return this._cryptoToken.extractRSAPrivateKeyP(this._cryptoTokenData);
   }

   public final byte[] getQ() {
      return this._cryptoToken.extractRSAPrivateKeyQ(this._cryptoTokenData);
   }

   public final byte[] getDModPm1() {
      return this._cryptoToken.extractRSAPrivateKeyDModPm1(this._cryptoTokenData);
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         RSACryptoSystem cs = this.getRSACryptoSystem();
         cs.verify();
         byte[] qInv = null;
         byte[] q = null;
         byte[] p = null;
         byte[] n = null;

         label146:
         try {
            q = this.getQ();
            p = this.getP();
         } finally {
            break label146;
         }

         label143:
         try {
            if (q != null && p != null) {
               qInv = this.getQInvModP();
               if (qInv != null) {
                  byte[] result = new byte[p.length];
                  CryptoByteArrayArithmetic.multiply(qInv, q, p, result);
                  if (!CryptoByteArrayArithmetic.isOne(result)) {
                     throw new InvalidKeyException();
                  }
               }
            }
         } finally {
            break label143;
         }

         label140:
         try {
            if (p != null && q != null) {
               n = this.getN();
               if (n != null) {
                  byte[] result = new byte[n.length];
                  CryptoByteArrayArithmetic.multiply(p, q, n.length * 8, result);
                  if (CryptoByteArrayArithmetic.compare(n, result) != 0) {
                     throw new InvalidKeyException("p * q != n");
                  }
               }
            }
         } finally {
            break label140;
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
   public final RSACryptoSystem getRSACryptoSystem() {
      return this._cryptoSystem;
   }

   @Override
   public final String getAlgorithm() {
      return "RSA";
   }

   private final void initialize(RSACryptoSystem cryptoSystem, RSACryptoToken cryptoToken, CryptoTokenPrivateKeyData cryptoTokenData) {
      this._cryptoSystem = cryptoSystem;
      this._cryptoToken = cryptoToken;
      this._cryptoTokenData = cryptoTokenData;
      this.setHashCode();
   }

   public RSAPrivateKey(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData cryptoTokenData) {
      if (cryptoSystem != null && cryptoTokenData != null) {
         RSACryptoToken cryptoToken = (RSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
         this.initialize(cryptoSystem, cryptoToken, cryptoTokenData);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP) throws InvalidKeyException {
      if (cryptoSystem != null && e != null && d != null && n != null && p != null && q != null && dModPm1 != null && dModQm1 != null && qInvModP != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         e = CryptoByteArrayArithmetic.trim(e);
         d = CryptoByteArrayArithmetic.trim(d);
         n = CryptoByteArrayArithmetic.trim(n);
         p = CryptoByteArrayArithmetic.trim(p);
         q = CryptoByteArrayArithmetic.trim(q);
         dModPm1 = CryptoByteArrayArithmetic.trim(dModPm1);
         dModQm1 = CryptoByteArrayArithmetic.trim(dModQm1);
         qInvModP = CryptoByteArrayArithmetic.trim(qInvModP);
         if (p.length + q.length <= modulusLength
            && dModPm1.length <= p.length
            && dModQm1.length <= q.length
            && qInvModP.length <= p.length
            && d.length <= modulusLength
            && e.length <= modulusLength
            && n.length <= modulusLength) {
            RSACryptoToken cryptoToken = (RSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
            this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectRSAPrivateKey(cryptoSystem, e, d, n, p, q, dModPm1, dModQm1, qInvModP));
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP) throws InvalidKeyException {
      if (cryptoSystem != null && p != null && q != null && dModPm1 != null && dModQm1 != null && qInvModP != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         p = CryptoByteArrayArithmetic.trim(p);
         q = CryptoByteArrayArithmetic.trim(q);
         dModPm1 = CryptoByteArrayArithmetic.trim(dModPm1);
         dModQm1 = CryptoByteArrayArithmetic.trim(dModQm1);
         qInvModP = CryptoByteArrayArithmetic.trim(qInvModP);
         if (e != null) {
            e = CryptoByteArrayArithmetic.trim(e);
         } else {
            e = Arrays.copy(DEFAULT_E);
         }

         if (p.length + q.length <= modulusLength
            && dModPm1.length <= p.length
            && dModQm1.length <= q.length
            && qInvModP.length <= p.length
            && e.length <= modulusLength) {
            RSACryptoToken cryptoToken = (RSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
            this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectRSAPrivateKey(cryptoSystem, e, p, q, dModPm1, dModQm1, qInvModP));
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] p, byte[] q) throws InvalidKeyException {
      if (cryptoSystem != null && d != null && p != null && q != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (e != null) {
            e = CryptoByteArrayArithmetic.trim(e);
         } else {
            e = Arrays.copy(DEFAULT_E);
         }

         d = CryptoByteArrayArithmetic.trim(d);
         p = CryptoByteArrayArithmetic.trim(p);
         q = CryptoByteArrayArithmetic.trim(q);
         if (p.length + q.length <= modulusLength && d.length <= modulusLength && e.length <= modulusLength) {
            RSACryptoToken cryptoToken = (RSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
            this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectRSAPrivateKey(cryptoSystem, e, d, p, q));
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public RSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n) throws InvalidKeyException {
      if (cryptoSystem != null && d != null && n != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (e != null) {
            e = CryptoByteArrayArithmetic.trim(e);
         } else {
            e = Arrays.copy(DEFAULT_E);
         }

         d = CryptoByteArrayArithmetic.trim(d);
         n = CryptoByteArrayArithmetic.trim(n);
         if (d.length <= modulusLength && n.length <= modulusLength && e.length <= modulusLength) {
            RSACryptoToken cryptoToken = (RSACryptoToken)cryptoSystem.getAsymmetricCryptoToken();
            this.initialize(cryptoSystem, cryptoToken, cryptoToken.injectRSAPrivateKey(cryptoSystem, e, d, n));
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
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

      if (!(obj instanceof RSAPrivateKey)) {
         return false;
      }

      RSAPrivateKey other = (RSAPrivateKey)obj;
      return this._hashCode == other.hashCode()
         && this._cryptoSystem.equals(other._cryptoSystem)
         && this._cryptoToken.equals(other._cryptoToken)
         && this._cryptoTokenData.equals(other._cryptoTokenData);
   }
}
