package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.util.DataBuffer;

public class LengthEncodedValueDataBuffer extends DataBuffer {
   public int ReadLengthEncodedValue() {
      int numberOfBytes = this.readUnsignedByte();
      if (numberOfBytes != 0 && numberOfBytes <= 4) {
         int value = 0;

         for (int i = 0; i < numberOfBytes; i++) {
            value <<= 8;
            int b = this.readUnsignedByte();
            value |= b;
         }

         if (value < 0) {
            throw new NumberFormatException();
         } else {
            return value;
         }
      } else {
         throw new NumberFormatException();
      }
   }

   public void WriteLengthEncodedValue(int i) {
      int origPos = this.getPosition();
      byte[] array = this.getArray();
      int valuePos = origPos + 1;
      this.ensureBuffer(valuePos);
      this.setPosition(valuePos);
      boolean firstByteFound = false;

      for (int j = 3; j >= 0; j--) {
         byte currByte = (byte)(i >>> 8 * j & 0xFF);
         if (!firstByteFound && currByte != 0) {
            firstByteFound = true;
            array[origPos] = (byte)(j + 1 & 0xFF);
         }

         if (firstByteFound) {
            this.writeByte(currByte);
         }
      }
   }
}
