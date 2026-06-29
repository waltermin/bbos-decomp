package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareECCryptoToken$ECPublicKeyData implements CryptoTokenPublicKeyData, Persistable {
   private SoftwareECCryptoToken$ECCryptoSystemData _cryptoSystem;
   private byte[] _data;
   private int _hashCode;

   public SoftwareECCryptoToken$ECPublicKeyData(SoftwareECCryptoToken$ECCryptoSystemData cryptoSystem, byte[] data) throws InvalidKeyException {
      if (cryptoSystem != null && data != null) {
         if (data.length != cryptoSystem.getPublicKeyLength(true) && data.length != cryptoSystem.getPublicKeyLength(false)) {
            throw new InvalidKeyException();
         }

         if (CryptoByteArrayArithmetic.isZero(data)) {
            throw new InvalidKeyException();
         }

         this._cryptoSystem = cryptoSystem;
         this._data = Arrays.copy(data);
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final byte[] copyPublicKeyData() {
      return Arrays.copy(this._data);
   }

   public final byte[] getPublicKeyData() {
      return this._data;
   }

   public final byte[] copyPublicKeyData(boolean compress) {
      if (this._cryptoSystem.getPublicKeyLength(compress) == this._data.length) {
         return Arrays.copy(this._data);
      } else if (compress) {
         byte[] compressedData = new byte[this._cryptoSystem.getPublicKeyLength(true)];
         Certicom.assertAccessAllowed();
         NativeEC.compressPublicKey(this._cryptoSystem.getName(), this._data, compressedData);
         return compressedData;
      } else {
         byte[] uncompressedData = new byte[this._cryptoSystem.getPublicKeyLength(false)];
         Certicom.assertAccessAllowed();
         NativeEC.uncompressPublicKey(this._cryptoSystem.getName(), this._data, uncompressedData);
         return uncompressedData;
      }
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoSystem.hashCode() ^ HashCodeCalculator.getCRC32(this._data);
      if (this._hashCode == 0) {
         this._hashCode = 1;
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

      if (obj instanceof SoftwareECCryptoToken$ECPublicKeyData) {
         SoftwareECCryptoToken$ECPublicKeyData other = (SoftwareECCryptoToken$ECPublicKeyData)obj;
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
