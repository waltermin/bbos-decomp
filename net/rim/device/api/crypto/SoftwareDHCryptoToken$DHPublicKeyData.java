package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDHCryptoToken$DHPublicKeyData implements CryptoTokenPublicKeyData, Persistable {
   private SoftwareDHCryptoToken$DHCryptoSystemData _cryptoSystem;
   private byte[] _data;
   private int _hashCode;

   SoftwareDHCryptoToken$DHPublicKeyData(SoftwareDHCryptoToken$DHCryptoSystemData cryptoSystem, byte[] data) {
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

   final byte[] copyPublicKeyData() {
      return Arrays.copy(this._data);
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoSystem.hashCode() ^ HashCodeCalculator.getCRC32(this._data);
      if (this._hashCode == 0) {
         this._hashCode = 1;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SoftwareDHCryptoToken$DHPublicKeyData) {
         SoftwareDHCryptoToken$DHPublicKeyData other = (SoftwareDHCryptoToken$DHPublicKeyData)obj;
         if (this._hashCode == other._hashCode) {
            if (this._cryptoSystem.equals(other._cryptoSystem) && Arrays.equals(this._data, other._data)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
