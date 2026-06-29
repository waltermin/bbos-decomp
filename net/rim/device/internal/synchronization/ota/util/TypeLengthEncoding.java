package net.rim.device.internal.synchronization.ota.util;

import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

public final class TypeLengthEncoding {
   private static final byte[] ZeroArray = new byte[0];

   private TypeLengthEncoding() {
   }

   public static final int getNumberOfBytesRequiredFor(int value) {
      if (value == 0) {
         return 1;
      }

      int xMask = -16777216;

      int xCounter;
      for (xCounter = 4; (xMask & value) == 0; xCounter--) {
         xMask >>= 8;
      }

      return xCounter;
   }

   public static final void writeByte(DataBuffer dout, int tag, int v) {
      dout.writeByte(tag);
      LengthEncoding.write(dout, 1);
      dout.writeByte(v);
   }

   public static final void writeByte(DataBuffer dout, int tag, byte v) {
      writeInt(dout, tag, v & 255);
   }

   public static final void writeShort(DataBuffer dout, int tag, short v) {
      writeInt(dout, tag, v & 65535);
   }

   public static final void writeUnisgnedShort(DataBuffer dout, int tag, int v) {
      writeInt(dout, tag, v & 65535);
   }

   public static final void writeInt(DataBuffer dout, int tag, int v) {
      int xDataSize = getNumberOfBytesRequiredFor(v);
      dout.writeByte(tag);
      LengthEncoding.write(dout, xDataSize);

      do {
         dout.write(v >>> 8 * --xDataSize & 0xFF);
      } while (xDataSize > 0);
   }

   public static final void writeString(DataBuffer dout, int tag, String value) {
      byte[] xValueBytes = value != null ? value.getBytes() : ZeroArray;
      dout.write(tag);
      LengthEncoding.write(dout, xValueBytes.length);
      dout.write(xValueBytes);
   }

   public static final void writeRimUTF(DataBuffer dout, int tag, String value, String encoding) {
      writeBytes(dout, tag, value.getBytes(encoding));
   }

   public static final void writeBytes(DataBuffer dout, int tag, byte[] v) {
      writeBytes(dout, tag, v, 0, v == null ? 0 : v.length);
   }

   public static final void writeBytes(DataBuffer dout, int tag, byte[] v, int offset, int length) {
      if (v == null) {
         v = ZeroArray;
         length = 0;
         offset = 0;
      }

      dout.writeByte(tag);
      LengthEncoding.write(dout, length);
      dout.write(v, offset, length);
   }

   public static final void writeBoolean(DataBuffer dout, int tag, boolean value) {
      writeByte(dout, tag, value ? 1 : 0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void writeTLESerializableObject(DataBuffer dout, int tag, TLESerializableObject aTLEObject) {
      int xOriginalPosition = dout.getPosition();

      try {
         dout.write(tag);
         dout.writeInt(0);
         int xTLEObjectStartPosition = dout.getPosition();
         aTLEObject.writeTo(dout);
         int xTLEObjectEndPosition = dout.getPosition();
         dout.setPosition(xTLEObjectStartPosition - 4);
         dout.writeInt(LengthEncoding.getFixedEncodingLengthFor(xTLEObjectEndPosition - xTLEObjectStartPosition));
         dout.setPosition(xTLEObjectEndPosition);
      } catch (Throwable var7) {
         dout.setPosition(xOriginalPosition);
         throw new Object(e.getMessage());
      }
   }

   public static final short readShort(DataBuffer din) {
      return (short)readInt(din);
   }

   public static final int readUnsignedShort(DataBuffer din) {
      return readShort(din) & 65535;
   }

   public static final int readInt(DataBuffer din) {
      int xLength = LengthEncoding.read(din);
      if (xLength == 0) {
         throw new Object();
      }

      int xValue = 0;

      do {
         xValue |= din.readUnsignedByte() << 8 * --xLength;
      } while (xLength > 0);

      return xValue;
   }

   public static final String readRimUTF(DataBuffer din, String encoding) {
      return (String)(new Object(readBytes(din), encoding));
   }

   public static final String readString(DataBuffer din) {
      return (String)(new Object(readBytes(din)));
   }

   public static final void readBytes(DataBuffer din, byte[] aValue) {
      Array.resize(aValue, LengthEncoding.read(din));
      din.readFully(aValue);
   }

   public static final byte[] readBytes(DataBuffer din) {
      byte[] xValue = new byte[LengthEncoding.read(din)];
      din.readFully(xValue);
      return xValue;
   }

   public static final int readTag(DataBuffer din) {
      return din.readUnsignedByte();
   }

   public static final boolean readBoolean(DataBuffer din) {
      return readUnsignedByte(din) != 0;
   }

   public static final int readUnsignedByte(DataBuffer din) {
      if (LengthEncoding.read(din) == 0) {
         throw new Object();
      } else {
         return din.readUnsignedByte();
      }
   }

   public static final void skipValue(DataBuffer din) {
      din.skipBytes(LengthEncoding.read(din));
   }
}
