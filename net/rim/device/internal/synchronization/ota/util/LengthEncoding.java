package net.rim.device.internal.synchronization.ota.util;

import java.io.DataInput;
import java.io.DataOutput;

public final class LengthEncoding {
   public static final int getLengthEncodingOf(int length) {
      if (length < 0) {
         throw new IllegalArgumentException();
      } else if (length < 128) {
         return 1;
      } else if (length < 16384) {
         return 2;
      } else if (length < 2097152) {
         return 3;
      } else {
         return length < 268435456 ? 4 : 5;
      }
   }

   public static final int read(DataInput din) {
      int xLength = 0;

      int xLengthByte;
      do {
         xLengthByte = din.readUnsignedByte();
         xLength <<= 7;
         xLength |= xLengthByte & 127;
      } while (xLengthByte >= 128);

      return xLength;
   }

   public static final void write(DataOutput dout, int length) {
      int xLengthOfLength = getLengthEncodingOf(length);
      switch (xLengthOfLength) {
         case 5:
         default:
            dout.writeByte(length >>> 28 & 127 | 128);
         case 4:
            dout.writeByte(length >>> 21 & 127 | 128);
         case 3:
            dout.writeByte(length >>> 14 & 127 | 128);
         case 2:
            dout.writeByte(length >>> 7 & 127 | 128);
         case 1:
            dout.writeByte(length & 127);
         case 0:
      }
   }

   public static final int getFixedEncodingLengthFor(int length) {
      int xReturnValue = length >>> 21 & 127 | 128;
      xReturnValue <<= 8;
      xReturnValue |= length >>> 14 & 127 | 128;
      xReturnValue <<= 8;
      xReturnValue |= length >>> 7 & 127 | 128;
      xReturnValue <<= 8;
      return xReturnValue | length & 127;
   }
}
