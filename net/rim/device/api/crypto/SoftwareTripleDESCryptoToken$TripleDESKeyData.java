package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareTripleDESCryptoToken$TripleDESKeyData implements CryptoTokenSymmetricKeyData, Persistable {
   private byte[] _data;
   private int _hashCode;

   SoftwareTripleDESCryptoToken$TripleDESKeyData() {
      this._data = new byte[24];
      SoftwareDESCryptoToken.generateKey(this._data, 0);
      SoftwareDESCryptoToken.generateKey(this._data, 8);
      SoftwareDESCryptoToken.generateKey(this._data, 16);
      MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
      PersistentContent.markAsPlaintext(this._data);
      this.setHashCode();
   }

   SoftwareTripleDESCryptoToken$TripleDESKeyData(byte[] data, int offset) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      int length = data.length - offset;
      if (offset >= 0 && offset <= data.length && data.length - length >= offset && offset + length >= 0) {
         this._data = new byte[24];
         if (length >= 24) {
            System.arraycopy(data, offset, this._data, 0, 24);
         } else {
            if (length != 16) {
               throw new IllegalArgumentException();
            }

            System.arraycopy(data, offset, this._data, 0, 16);
            System.arraycopy(data, offset, this._data, 16, 8);
         }

         SoftwareDESCryptoToken.generateOddParityInLSB(this._data, 0, 24);
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

      if (!(obj instanceof SoftwareTripleDESCryptoToken$TripleDESKeyData)) {
         return false;
      }

      SoftwareTripleDESCryptoToken$TripleDESKeyData other = (SoftwareTripleDESCryptoToken$TripleDESKeyData)obj;
      return other.hashCode() == this._hashCode && Arrays.equals(this._data, other._data);
   }
}
