package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareCAST128CryptoToken$CAST128KeyData implements CryptoTokenSymmetricKeyData, Persistable {
   private byte[] _data;
   private int _hashCode;

   SoftwareCAST128CryptoToken$CAST128KeyData() {
      this._data = RandomSource.getBytes(16);
      MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
      PersistentContent.markAsPlaintext(this._data);
      this.setHashCode();
   }

   SoftwareCAST128CryptoToken$CAST128KeyData(byte[] data, int offset) {
      if (data != null && offset >= 0 && data.length - 16 >= offset) {
         this._data = Arrays.copy(data, offset, 16);
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

      if (!(obj instanceof SoftwareCAST128CryptoToken$CAST128KeyData)) {
         return false;
      }

      SoftwareCAST128CryptoToken$CAST128KeyData other = (SoftwareCAST128CryptoToken$CAST128KeyData)obj;
      return Arrays.equals(this._data, other._data);
   }
}
