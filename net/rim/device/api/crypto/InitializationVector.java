package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class InitializationVector implements Persistable {
   private byte[] _data;
   private int _hashCode;

   public InitializationVector(int length) {
      if (length <= 0) {
         throw new Object();
      }

      this._data = RandomSource.getBytes(length);
   }

   public InitializationVector(byte[] data) {
      this(data, 0, data == null ? 0 : data.length);
   }

   public InitializationVector(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length > 0 && data.length - length >= offset) {
         this._data = new byte[length];
         System.arraycopy(data, offset, this._data, 0, length);
      } else {
         throw new Object();
      }
   }

   public final int getLength() {
      return this._data.length;
   }

   public final byte[] getData() {
      return Arrays.copy(this._data);
   }

   @Override
   public final int hashCode() {
      if (this._hashCode == 0) {
         this._hashCode = HashCodeCalculator.getCRC32(this._data);
         if (this._hashCode == 0) {
            this._hashCode = 1;
         }
      }

      return this._hashCode;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof InitializationVector)) {
         return false;
      }

      InitializationVector other = (InitializationVector)obj;
      return Arrays.equals(this._data, other._data);
   }
}
