package net.rim.device.internal.synchronization.ota.util;

import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

public final class TLEField implements TLESerializableObject {
   private int _tag;
   private byte[] _value;
   private int _hashCode;
   private boolean _calculateHashCode;
   private boolean _encoded;

   public final boolean isEncoded() {
      return this._encoded;
   }

   public final void setEncoded(boolean value) {
      this._encoded = value;
   }

   public final int getTag() {
      return this._tag;
   }

   public final byte[] getValue() {
      return this._value;
   }

   public final void setTag(int aTag) {
      boolean xEncoded = (aTag & 128) == 128;
      if (xEncoded) {
         aTag &= 127;
      }

      this.setEncoded(xEncoded);
      this._tag = aTag;
      this._calculateHashCode = true;
   }

   public final void setValue(int aValue) {
      int xNumberOfBytesRequired = TypeLengthEncoding.getNumberOfBytesRequiredFor(aValue);
      this.makeBufferReady(xNumberOfBytesRequired);

      for (int xIndex = 0; xIndex < xNumberOfBytesRequired; xIndex++) {
         this._value[xIndex] = (byte)aValue;
         aValue >>>= 8;
      }

      this._calculateHashCode = true;
   }

   public final void setValue(byte[] aValue) {
      this.setValue(aValue, 0, aValue == null ? 0 : aValue.length);
   }

   public final void setValue(byte[] aValue, int offset, int length) {
      int aValueLength = aValue == null ? 0 : length;
      this.makeBufferReady(aValueLength);
      if (aValueLength != 0) {
         System.arraycopy(aValue, offset, this._value, 0, aValueLength);
      }

      this._calculateHashCode = true;
   }

   public final void reset() {
      this.makeBufferReady(0);
      this._calculateHashCode = true;
   }

   @Override
   public final void readFrom(DataBuffer dins) {
      this.setTag(TypeLengthEncoding.readTag(dins));
      this.setValue(TypeLengthEncoding.readBytes(dins));
   }

   @Override
   public final void writeTo(DataBuffer dout) {
      int xTag = this._tag;
      if (this._encoded) {
         xTag = this._tag | 128;
      }

      TypeLengthEncoding.writeBytes(dout, xTag, this._value);
   }

   public TLEField() {
   }

   public TLEField(DataBuffer din) {
      this.readFrom(din);
   }

   public TLEField(int tag) {
      this(tag, null);
   }

   private final void makeBufferReady(int aLength) {
      if (this._value == null) {
         this._value = new byte[aLength];
      } else {
         if (this._value.length != aLength) {
            Array.resize(this._value, aLength);
         }
      }
   }

   public TLEField(TLEField aTLEField) {
      this.setTag(aTLEField.getTag());
      this.setValue(aTLEField.getValue());
   }

   @Override
   public final int hashCode() {
      if (this._calculateHashCode) {
         this._hashCode = CRC32.update(-1, this._tag);
         if (this._value != null) {
            this._hashCode = CRC32.update(this._hashCode, this._value);
         }

         this._calculateHashCode = false;
      }

      return this._hashCode;
   }

   @Override
   public final boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      } else {
         return anObject instanceof TLEField ? anObject.hashCode() == this.hashCode() : false;
      }
   }

   public TLEField(int aTag, byte[] aValue) {
      this.setTag(aTag);
      this.setValue(aValue);
   }
}
