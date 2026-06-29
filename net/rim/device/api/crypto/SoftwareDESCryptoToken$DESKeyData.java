package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDESCryptoToken$DESKeyData implements CryptoTokenSymmetricKeyData, Persistable {
   private byte[] _data;
   private int _hashCode;

   SoftwareDESCryptoToken$DESKeyData() {
      this._data = new byte[8];
      SoftwareDESCryptoToken.generateKey(this._data, 0);
      MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
      PersistentContent.markAsPlaintext(this._data);
      this.setHashCode();
   }

   SoftwareDESCryptoToken$DESKeyData(byte[] data, int offset) {
      if (data != null && offset >= 0 && data.length - 8 >= offset && offset + 8 >= 0) {
         this._data = Arrays.copy(data, offset, 8);
         SoftwareDESCryptoToken.generateOddParityInLSB(this._data, 0, this._data.length);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._data);
         PersistentContent.markAsPlaintext(data);
         this.setHashCode();
      } else {
         throw new Object();
      }
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

      if (!(obj instanceof SoftwareDESCryptoToken$DESKeyData)) {
         return false;
      }

      SoftwareDESCryptoToken$DESKeyData other = (SoftwareDESCryptoToken$DESKeyData)obj;
      return other.hashCode() == this._hashCode && Arrays.equals(this._data, other._data);
   }
}
