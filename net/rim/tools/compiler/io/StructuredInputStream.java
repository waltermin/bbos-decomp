package net.rim.tools.compiler.io;

import java.io.InputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.tools.compiler.vm.Constants;

public final class StructuredInputStream implements Constants {
   private byte[] _bytes;
   private int _delta;
   private int _length;
   private int _offset;
   private static final boolean _littleEndian;

   public StructuredInputStream(byte[] bytes, int start, int length, boolean littleEndian, int offset) {
      this._bytes = bytes;
      this._delta = start;
      this._length = length;
      this._offset = offset;
   }

   public StructuredInputStream(byte[] _bytes, boolean littleEndian, int offset) {
      this(_bytes, 0, _bytes.length, littleEndian, offset);
   }

   public StructuredInputStream(byte[] _bytes, boolean littleEndian) {
      this(_bytes, 0, _bytes.length, littleEndian, 0);
   }

   public final byte[] getBytes() {
      return this._bytes;
   }

   public final int getOffset() {
      return this._offset;
   }

   public final int skipBytes(int n) {
      int remaining = this._length - this._offset;
      if (remaining < 0) {
         throw new Object();
      }

      if (n > remaining) {
         throw new Object();
      }

      this._offset += n;
      return n;
   }

   public final int read(byte[] b) {
      int remaining = this._length - this._offset;
      if (remaining < 0) {
         throw new Object();
      }

      int length = b.length;
      if (length > remaining) {
         length = remaining;
      }

      System.arraycopy(this._bytes, this._delta + this._offset, b, 0, length);
      this._offset += length;
      return length;
   }

   public final int read() {
      if (this._offset == this._length) {
         this._offset++;
         return -1;
      } else if (this._offset > this._length) {
         throw new Object();
      } else {
         return this._bytes[this._delta + this._offset++];
      }
   }

   public final byte readByte() {
      return (byte)this.read();
   }

   public final int readUnsignedByte() {
      return this.read() & 0xFF;
   }

   public final int readUnsignedShort() {
      int b2 = this.readByte();
      int b1 = this.readByte();
      return (b1 & 0xFF | (b2 & 0xFF) << 8) & 65535;
   }

   public final int readInt() {
      int b4 = this.readByte();
      int b3 = this.readUnsignedByte();
      int b2 = this.readUnsignedByte();
      int b1 = this.readUnsignedByte();
      return b1 & 0xFF | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | b4 << 24;
   }

   public final long readLong() {
      long b8 = this.readByte();
      long b7 = this.readUnsignedByte();
      long b6 = this.readUnsignedByte();
      long b5 = this.readUnsignedByte();
      long b4 = this.readUnsignedByte();
      long b3 = this.readUnsignedByte();
      long b2 = this.readUnsignedByte();
      long b1 = this.readUnsignedByte();
      return b1 & 255 | (b2 & 255) << 8 | (b3 & 255) << 16 | (b4 & 255) << 24 | (b5 & 255) << 32 | (b6 & 255) << 40 | (b7 & 255) << 48 | b8 << 56;
   }

   public final void close() {
   }

   public static final byte[] readFully(InputStream in, int length, String name) {
      byte[] bytes = readAll(in, length, name);
      in.close();
      return bytes;
   }

   public static final byte[] readAll(InputStream in, int length, String name) {
      byte[] bytes = null;
      if (length == -1) {
         return IOUtilities.streamToBytes(in);
      }

      bytes = new byte[length];
      int got = 0;

      while (got < length) {
         int more = in.read(bytes, got, length - got);
         if (more <= 0) {
            throw new Object(((StringBuffer)(new Object("Unable to read all input from: "))).append(name).toString());
         }

         got += more;
      }

      return bytes;
   }
}
