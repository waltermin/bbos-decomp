package com.fourthpass.wmls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class WMLInputStream extends InputStream {
   private InputStream _in;

   public WMLInputStream(byte[] buf) {
      this(new ByteArrayInputStream(buf));
   }

   public WMLInputStream(InputStream in) {
      this._in = in;
      this._in.mark(Integer.MAX_VALUE);
   }

   @Override
   public final void reset() {
      label17:
      try {
         this._in.reset();
      } finally {
         break label17;
      }

      this._in.mark(Integer.MAX_VALUE);
   }

   @Override
   public final int read() {
      return this._in.read();
   }

   public final int readByte() {
      try {
         return this.read();
      } finally {
         ;
      }
   }

   public final int readMBInt() {
      int result = 0;

      int i;
      do {
         i = this.readByte();
         if (i == -1) {
            return -1;
         }

         result = result << 7 | i & 127;
      } while ((i & 128) != 0);

      return result;
   }

   public final byte readInt8() {
      return (byte)this.read();
   }

   public final short readInt16() {
      return (short)((this.read() << 8) + this.read());
   }

   public final int readInt32() {
      return (this.read() << 24) + (this.read() << 16) + (this.read() << 8) + this.read();
   }

   public final int readUInt8() {
      return this.read();
   }

   public final void readBytes(byte[] data) {
      this._in.read(data, 0, data.length);
   }

   public final float readIEEE754() {
      int bits = this.readInt32();
      return Float.intBitsToFloat(bits);
   }
}
