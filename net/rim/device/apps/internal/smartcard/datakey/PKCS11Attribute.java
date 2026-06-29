package net.rim.device.apps.internal.smartcard.datakey;

class PKCS11Attribute {
   private int _length;
   private byte[] _value;

   PKCS11Attribute(byte[] value) {
      if (value == null) {
         throw new Object();
      }

      this._value = value;
      this._length = value.length;
   }

   public int getIntValue() {
      return this._length < 4 ? -1 : this._value[3] << 24 | this._value[2] << 16 | this._value[1] << 8 | this._value[0];
   }

   public byte[] getByteArrayValue() {
      return this._value;
   }

   public int getLength() {
      return this._length;
   }

   public String getStringValue() {
      return (String)(new Object(this._value));
   }
}
