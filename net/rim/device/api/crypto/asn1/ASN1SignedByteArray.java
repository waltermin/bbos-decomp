package net.rim.device.api.crypto.asn1;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Memory;

public class ASN1SignedByteArray implements Persistable {
   private byte[] _value;
   private boolean _isPositive;

   public ASN1SignedByteArray(byte[] value, boolean isPositive) {
      if (isPositive || value.length >= 1 && (value[0] & 128) != 0) {
         this._value = value;
         this._isPositive = isPositive;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public byte[] getValue() {
      return this._value;
   }

   public boolean isPositive() {
      return this._isPositive;
   }

   public ASN1SignedByteArray getImmutableOrCopy() {
      return Memory.isObjectInGroup(this) ? this : new ASN1SignedByteArray(Arrays.copy(this._value), this._isPositive);
   }
}
