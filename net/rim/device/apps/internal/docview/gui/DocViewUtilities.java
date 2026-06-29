package net.rim.device.apps.internal.docview.gui;

final class DocViewUtilities {
   public static final int readInt(byte[] arr, int index, boolean bigEndian) {
      int b1 = readByte(arr, index);
      int b2 = readByte(arr, index + 1);
      int b3 = readByte(arr, index + 2);
      int b4 = readByte(arr, index + 3);
      return bigEndian ? b1 << 24 | b2 << 16 | b3 << 8 | b4 : b4 << 24 | b3 << 16 | b2 << 8 | b1;
   }

   public static final short readShort(byte[] arr, int index, boolean bigEndian) {
      int b1 = readByte(arr, index);
      int b2 = readByte(arr, index + 1);
      return bigEndian ? (short)(b1 << 8 | b2) : (short)(b2 << 8 | b1);
   }

   public static final int readUnsignedByte(byte[] arr, int index, boolean bigEndian) {
      return readByte(arr, index) & 0xFF;
   }

   public static final int readUnsignedShort(byte[] arr, int index, boolean bigEndian) {
      return readShort(arr, index, bigEndian) & 65535;
   }

   public static final int readUnsignedInt(byte[] arr, int index, boolean bigEndian) {
      return readInt(arr, index, bigEndian) & -1;
   }

   public static final String readString(byte[] arr, int index, boolean bigEndian) {
      int len = readUnsignedShort(arr, index, bigEndian);
      StringBuffer tmp = (StringBuffer)(new Object(len));

      for (int i = 0; i < len * 2; i += 2) {
         tmp.append((char)readShort(arr, i + index + 2, bigEndian));
      }

      return tmp.toString();
   }

   public static final void writeInt(byte[] arr, int index, int value, boolean bigEndian) {
      int iCrtIndex = index;
      if (bigEndian) {
         writeByte(arr, iCrtIndex++, value >>> 24);
         writeByte(arr, iCrtIndex++, value >>> 16);
         writeByte(arr, iCrtIndex++, value >>> 8);
         writeByte(arr, iCrtIndex++, value);
      } else {
         writeByte(arr, iCrtIndex++, value);
         writeByte(arr, iCrtIndex++, value >>> 8);
         writeByte(arr, iCrtIndex++, value >>> 16);
         writeByte(arr, iCrtIndex++, value >>> 24);
      }
   }

   public static final void writeShort(byte[] arr, int index, short value, boolean bigEndian) {
      int iCrtIndex = index;
      if (bigEndian) {
         writeByte(arr, iCrtIndex++, value >>> 8);
         writeByte(arr, iCrtIndex++, value);
      } else {
         writeByte(arr, iCrtIndex++, value);
         writeByte(arr, iCrtIndex++, value >>> 8);
      }
   }

   private static final void writeByte(byte[] arr, int index, int value) {
      arr[index] = (byte)value;
   }

   private static final int readByte(byte[] arr, int index) {
      return arr[index] & 0xFF;
   }
}
