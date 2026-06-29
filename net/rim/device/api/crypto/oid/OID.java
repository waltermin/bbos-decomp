package net.rim.device.api.crypto.oid;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class OID implements Persistable {
   private byte[] _derEncoding;
   private int _hashCode;

   public OID(byte[] oidEncoding) {
      this(oidEncoding, 0, oidEncoding == null ? 0 : oidEncoding.length);
   }

   public OID(byte[] oidEncoding, int offset, int length) {
      if (oidEncoding != null && oidEncoding.length - length >= offset && offset >= 0 && length >= 0) {
         if (offset == 0 && length >= oidEncoding.length) {
            this._derEncoding = oidEncoding;
         } else {
            this._derEncoding = Arrays.copy(oidEncoding, offset, length);
         }

         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public OID(String oid) {
      if (oid != null && oid.length() != 0) {
         ByteArrayOutputStream bytes = (ByteArrayOutputStream)(new Object(16));
         int numInts = 0;
         int firstInt = 0;
         int length = oid.length();
         int value = 0;

         for (int i = 0; i < length; i++) {
            char ch = oid.charAt(i);
            if (ch != '.') {
               value = value * 10 + ch - 48;
            }

            if (ch == '.' || i == length - 1) {
               if (++numInts == 1) {
                  firstInt = value;
               } else if (numInts == 2) {
                  bytes.write(firstInt * 40 + value);
               } else {
                  for (int shift = 28; shift >= 0; shift -= 7) {
                     int shiftedValue = value >>> shift;
                     if (shiftedValue != 0) {
                        bytes.write(shiftedValue & 127 | (shift == 0 ? 0 : 128));
                     }
                  }
               }

               value = 0;
            }
         }

         this._derEncoding = bytes.toByteArray();
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   public final byte[] toByteArray() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._derEncoding);
   }

   @Override
   public final String toString() {
      StringBuffer value = (StringBuffer)(new Object());
      value.append(this._derEncoding[0] / 40);
      value.append('.');
      value.append(this._derEncoding[0] % 40);

      for (int i = 1; i < this._derEncoding.length; i++) {
         value.append('.');
         int decodedValue = 0;

         while (i < this._derEncoding.length) {
            decodedValue = decodedValue << 7 | this._derEncoding[i] & 127;
            if ((this._derEncoding[i] & 128) == 0) {
               break;
            }

            i++;
         }

         value.append(decodedValue);
      }

      return value.toString();
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof OID)) {
         return false;
      }

      OID oid = (OID)object;
      return this._hashCode == oid._hashCode && Arrays.equals(this._derEncoding, oid._derEncoding);
   }

   final boolean equals(byte[] encoding, int offset, int length) {
      return encoding != null && offset + length <= encoding.length && offset >= 0 && length >= 0
         ? Arrays.equals(this._derEncoding, 0, encoding, offset, length)
         : false;
   }

   private final void setHashCode() {
      this._hashCode = HashCodeCalculator.getCRC32(this._derEncoding);
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }
}
