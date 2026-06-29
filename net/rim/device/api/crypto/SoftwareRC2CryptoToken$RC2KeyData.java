package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareRC2CryptoToken$RC2KeyData implements CryptoTokenSymmetricKeyData, Persistable {
   private byte[] _data;
   private int _effectiveBitLength;
   private int _hashCode;

   SoftwareRC2CryptoToken$RC2KeyData(int bitLength, int effectiveBitLength) {
      if (bitLength >= 8 && bitLength <= 1024 && (bitLength & 7) == 0 && effectiveBitLength >= 1 && effectiveBitLength <= 1024) {
         this._data = RandomSource.getBytes(bitLength >> 3);
         this._effectiveBitLength = effectiveBitLength;
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._data);
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   SoftwareRC2CryptoToken$RC2KeyData(byte[] data, int offset, int bitLength, int effectiveBitLength) {
      if (bitLength >= 8 && bitLength <= 1024 && (bitLength & 7) == 0 && effectiveBitLength >= 1 && effectiveBitLength <= 1024) {
         int length = bitLength >> 3;
         if (data != null && offset >= 0 && data.length - length >= offset && offset + length >= 0) {
            this._data = Arrays.copy(data, offset, length);
            this._effectiveBitLength = effectiveBitLength;
            MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
            PersistentContent.markAsPlaintext(this._data);
            PersistentContent.markAsPlaintext(data);
            this.setHashCode();
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   final int getLength() {
      return this._data.length;
   }

   final byte[] getData() {
      byte[] data = Arrays.copy(this._data);
      PersistentContent.markAsPlaintext(data);
      return data;
   }

   final int getEffectiveBitLength() {
      return this._effectiveBitLength;
   }

   private final void setHashCode() {
      this._hashCode = HashCodeCalculator.getDigest32(this._data) ^ this._effectiveBitLength;
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

      if (obj instanceof SoftwareRC2CryptoToken$RC2KeyData) {
         SoftwareRC2CryptoToken$RC2KeyData other = (SoftwareRC2CryptoToken$RC2KeyData)obj;
         if (this._hashCode == other._hashCode) {
            if (Arrays.equals(this._data, other._data) && this._effectiveBitLength == other._effectiveBitLength) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
