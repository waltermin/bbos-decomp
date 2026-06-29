package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareRSACryptoToken$RSAPublicKeyData implements CryptoTokenPublicKeyData, Persistable {
   private RSACryptoSystem _cryptoSystem;
   private byte[] _e;
   private byte[] _n;
   private int _hashCode;

   public SoftwareRSACryptoToken$RSAPublicKeyData(RSACryptoSystem cryptoSystem, byte[] e, byte[] n) throws InvalidKeyException {
      if (cryptoSystem != null && e != null && n != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (e.length != 0 && e.length <= modulusLength && n.length == modulusLength && (e.length != 1 || e[0] != 1)) {
            this._cryptoSystem = cryptoSystem;
            this._e = Arrays.copy(e);
            this._n = Arrays.copy(n);
            this.setHashCode();
         } else {
            throw new InvalidKeyException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final byte[] copyE() {
      return Arrays.copy(this._e);
   }

   public final byte[] copyN() {
      return Arrays.copy(this._n);
   }

   public final byte[] getE() {
      return this._e;
   }

   public final byte[] getN() {
      return this._n;
   }

   private final void setHashCode() {
      if (this._hashCode == 0) {
         this._hashCode = this._cryptoSystem.hashCode() ^ HashCodeCalculator.getCRC32(this._e) ^ HashCodeCalculator.getCRC32(this._n);
         if (this._hashCode == 0) {
            this._hashCode = 1;
         }
      }
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

      if (!(obj instanceof SoftwareRSACryptoToken$RSAPublicKeyData)) {
         return false;
      }

      SoftwareRSACryptoToken$RSAPublicKeyData other = (SoftwareRSACryptoToken$RSAPublicKeyData)obj;
      return other.hashCode() == this._hashCode
         && this._cryptoSystem.equals(other._cryptoSystem)
         && CryptoByteArrayArithmetic.compare(this._e, other._e) == 0
         && CryptoByteArrayArithmetic.compare(this._n, other._n) == 0;
   }
}
