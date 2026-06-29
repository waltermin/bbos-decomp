package javax.bluetooth;

import net.rim.device.api.util.Arrays;
import net.rim.device.internal.bluetooth.UUIDUtilities;

public class UUID {
   private String _data;

   public UUID(long uuidValue) {
      if (uuidValue >= 0 && uuidValue <= 4294967295L) {
         String value = Integer.toHexString((int)uuidValue);
         this._data = this.pad(value, value.length() > 4 ? 8 : 4);
      } else {
         throw new Object();
      }
   }

   public UUID(String uuidValue, boolean shortUUID) {
      int length = uuidValue.length();
      if (length == 0) {
         throw new Object();
      }

      for (int i = 0; i < length; i++) {
         if (Character.digit(uuidValue.charAt(i), 16) == -1) {
            throw new Object();
         }
      }

      int maxLength;
      if (shortUUID) {
         maxLength = length > 4 ? 8 : 4;
      } else {
         maxLength = 32;
      }

      if (length > maxLength) {
         throw new Object();
      }

      this._data = this.pad(uuidValue, maxLength);
   }

   private String pad(String value, int length) {
      StringBuffer sb = (StringBuffer)(new Object());
      int padding = length - value.length();

      for (int i = 0; i < padding; i++) {
         sb.append('0');
      }

      sb.append(value);
      return sb.toString().toUpperCase();
   }

   @Override
   public String toString() {
      return this._data;
   }

   @Override
   public boolean equals(Object value) {
      if (!(value instanceof UUID)) {
         return false;
      }

      UUID other = (UUID)value;
      return this._data.equals(other._data) ? true : Arrays.equals(UUIDUtilities.promoteTo128Bits(this._data), UUIDUtilities.promoteTo128Bits(other._data));
   }

   @Override
   public int hashCode() {
      return this._data.hashCode();
   }
}
