package net.rim.device.api.crypto;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class ARC4Key implements SymmetricKey, Persistable {
   private byte[] _data;
   private int _hashCode;
   private static ARC4CryptoToken _cryptoToken = new ARC4CryptoToken();

   public ARC4Key() {
      this(128);
   }

   public ARC4Key(int length) {
      if (length <= 0) {
         throw new Object();
      }

      this._data = RandomSource.getBytes(length);
      MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
      PersistentContent.markAsPlaintext(this._data);
      this.setHashCode();
   }

   public ARC4Key(byte[] data) {
      if (data != null && data.length != 0) {
         this._data = Arrays.copy(data);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._data);
         PersistentContent.markAsPlaintext(data);
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public ARC4Key(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length > 0 && data.length - length >= offset) {
         this._data = new byte[length];
         System.arraycopy(data, offset, this._data, 0, length);
         MemoryCleanerManager.getInstance().setCryptoAPISecureOldObjects(true);
         PersistentContent.markAsPlaintext(this._data);
         PersistentContent.markAsPlaintext(data);
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "ARC4";
   }

   @Override
   public final int getLength() {
      return this._data.length;
   }

   @Override
   public final int getBitLength() {
      return this._data.length * 8;
   }

   @Override
   public final byte[] getData() {
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
   public final SymmetricCryptoToken getSymmetricCryptoToken() {
      return _cryptoToken;
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

      if (obj instanceof ARC4Key) {
         ARC4Key other = (ARC4Key)obj;
         if (this._hashCode == other._hashCode) {
            return Arrays.equals(this._data, other._data);
         }
      }

      return false;
   }
}
