package net.rim.device.api.smartcard;

import net.rim.vm.Array;

public class ResponseAPDU {
   private byte[] _data;
   private byte _sw1;
   private byte _sw2;

   public ResponseAPDU() {
      this._data = null;
      this._sw1 = 0;
      this._sw2 = 0;
   }

   public ResponseAPDU(byte[] data, byte sw1, byte sw2) {
      this.set(data, sw1, sw2);
   }

   public void set(byte[] data, byte sw1, byte sw2) {
      this._data = data;
      this._sw1 = sw1;
      this._sw2 = sw2;
   }

   public byte[] getData() {
      return this._data;
   }

   public byte getSW1() {
      return this._sw1;
   }

   public byte getSW2() {
      return this._sw2;
   }

   public byte[] getAPDU() {
      int length = 2;
      if (this._data != null) {
         length += this._data.length;
      }

      byte[] result = new byte[length];
      if (this._data != null) {
         System.arraycopy(this._data, 0, result, 0, this._data.length);
      }

      result[length - 2] = this._sw1;
      result[length - 1] = this._sw2;
      return result;
   }

   @Override
   public String toString() {
      byte[] apdu = this.getAPDU();
      StringBuffer data = new StringBuffer();
      if (apdu != null) {
         int length = apdu.length;

         for (int i = 0; i < length; i++) {
            data.append(Integer.toHexString(apdu[i] & 255));
            if (i != length - 1) {
               data.append(' ');
            }
         }
      }

      return data.toString();
   }

   public boolean checkStatusWords(byte sw1, byte sw2) {
      return this._sw1 == sw1 && this._sw2 == sw2;
   }

   public void appendData(byte[] data, byte sw1, byte sw2) {
      if (this._data == null) {
         this._data = data;
      } else {
         int oldLength = this._data.length;
         int newLength = data.length;
         Array.resize(this._data, oldLength + newLength);
         System.arraycopy(data, 0, this._data, oldLength, newLength);
      }

      this._sw1 = sw1;
      this._sw2 = sw2;
   }
}
