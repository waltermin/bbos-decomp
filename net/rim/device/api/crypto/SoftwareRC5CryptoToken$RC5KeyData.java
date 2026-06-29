package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareRC5CryptoToken$RC5KeyData implements CryptoTokenSymmetricKeyData, Persistable {
   private byte[] _data;
   private int _hashCode;

   SoftwareRC5CryptoToken$RC5KeyData(int bitLength) {
      if (bitLength >= 0 && bitLength <= 2040 && (bitLength & 7) == 0) {
         this._data = RandomSource.getBytes(bitLength >> 3);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._data);
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   SoftwareRC5CryptoToken$RC5KeyData(byte[] data, int offset, int bitLength) {
      if (bitLength >= 0 && bitLength <= 2040 && (bitLength & 7) == 0) {
         int length = bitLength >> 3;
         if (data != null && offset >= 0 && data.length - length >= offset && offset + length >= 0) {
            this._data = Arrays.copy(data, offset, length);
            MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
            PersistentContent.markAsPlaintext(this._data);
            PersistentContent.markAsPlaintext(data);
            this.setHashCode();
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException();
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

   private final void setHashCode() {
      this._hashCode = HashCodeCalculator.getDigest32(this._data);
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

      if (obj instanceof SoftwareRC5CryptoToken$RC5KeyData) {
         SoftwareRC5CryptoToken$RC5KeyData other = (SoftwareRC5CryptoToken$RC5KeyData)obj;
         if (this._hashCode == other._hashCode) {
            return Arrays.equals(this._data, other._data);
         }
      }

      return false;
   }
}
