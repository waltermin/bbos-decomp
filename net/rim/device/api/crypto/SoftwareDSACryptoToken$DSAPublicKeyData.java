package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDSACryptoToken$DSAPublicKeyData implements CryptoTokenPublicKeyData, Persistable {
   private SoftwareDSACryptoToken$DSACryptoSystemData _cryptoSystem;
   private byte[] _data;
   private int _hashCode;

   public SoftwareDSACryptoToken$DSAPublicKeyData(SoftwareDSACryptoToken$DSACryptoSystemData cryptoSystem, byte[] data) {
      if (cryptoSystem != null && data != null) {
         data = CryptoByteArrayArithmetic.trim(data);
         if (!CryptoByteArrayArithmetic.isZero(data) && data.length <= cryptoSystem.getPublicKeyLength()) {
            this._cryptoSystem = cryptoSystem;
            this._data = CryptoUtilities.copyKey(data, cryptoSystem.getPublicKeyLength());
            this.setHashCode();
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   public final byte[] copyPublicKeyData() {
      return Arrays.copy(this._data);
   }

   public final byte[] getPublicKeyData() {
      return this._data;
   }

   private final void setHashCode() {
      if (this._hashCode == 0) {
         this._hashCode = this._cryptoSystem.hashCode() ^ HashCodeCalculator.getCRC32(this._data);
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

      if (!(obj instanceof SoftwareDSACryptoToken$DSAPublicKeyData)) {
         return false;
      }

      SoftwareDSACryptoToken$DSAPublicKeyData other = (SoftwareDSACryptoToken$DSAPublicKeyData)obj;
      return other.hashCode() == this._hashCode && this._cryptoSystem.equals(other._cryptoSystem) && Arrays.equals(this._data, other._data);
   }
}
