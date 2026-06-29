package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareSkipjackCryptoToken$SkipjackKeyData implements CryptoTokenSymmetricKeyData, Persistable {
   private byte[] _data;
   private int _hashCode;

   SoftwareSkipjackCryptoToken$SkipjackKeyData() {
      this._data = RandomSource.getBytes(10);
      MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
      PersistentContent.markAsPlaintext(this._data);
      this.setHashCode();
   }

   SoftwareSkipjackCryptoToken$SkipjackKeyData(byte[] data, int offset) {
      if (data != null && offset >= 0 && data.length - 10 >= offset && offset + 10 >= 0) {
         this._data = Arrays.copy(data, offset, 10);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._data);
         PersistentContent.markAsPlaintext(data);
         this.setHashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   final byte[] getData() {
      byte[] data = Arrays.copy(this._data);
      PersistentContent.markAsPlaintext(data);
      return data;
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   private final void setHashCode() {
      this._hashCode = HashCodeCalculator.getDigest32(this._data);
      if (this._hashCode == 0) {
         this._hashCode = 1;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SoftwareSkipjackCryptoToken$SkipjackKeyData) {
         SoftwareSkipjackCryptoToken$SkipjackKeyData other = (SoftwareSkipjackCryptoToken$SkipjackKeyData)obj;
         if (this._hashCode == other._hashCode) {
            return Arrays.equals(this._data, other._data);
         }
      }

      return false;
   }
}
