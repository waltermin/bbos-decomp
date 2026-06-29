package net.rim.wica.transport.util;

public class CompressedBuffer {
   protected byte[] _buffer;
   protected int _cursor;
   protected int _start;
   protected int _end;

   public CompressedBuffer() {
      this(256);
   }

   public CompressedBuffer(int bufferLength) {
      if (bufferLength < 0) {
         throw new Object();
      }

      this._buffer = new byte[bufferLength];
   }

   public CompressedBuffer(byte[] buffer) {
      this(buffer, 0, buffer.length);
   }

   public CompressedBuffer(byte[] buffer, int offset, int length) {
      if (offset >= 0 && offset + length <= buffer.length) {
         this._buffer = buffer;
         this._cursor = offset;
         this._start = offset;
         this._end = offset + length;
      } else {
         throw new Object();
      }
   }

   public byte[] getBuffer() {
      return this._buffer;
   }

   public void trimToSize() {
      if (this._start > 0 || this._end < this._buffer.length) {
         byte[] newBuffer = new byte[this._end - this._start];
         System.arraycopy(this._buffer, this._start, newBuffer, 0, this._end);
         this._buffer = newBuffer;
      }
   }

   public int start() {
      return this._start;
   }

   public int end() {
      return this._end;
   }

   public int cursor() {
      return this._cursor;
   }

   public int getLength() {
      return this._end - this._start;
   }

   public int available() {
      return this._end - this._cursor;
   }

   public boolean eod() {
      return this._cursor >= this._end;
   }

   public byte readByte() {
      this.ensureAvailable(this._cursor + 1);
      return this.nextByte();
   }

   public int readUnsignedByte() {
      return this.readByte() & 0xFF;
   }

   public int readInt() {
      return this.readCompressedInt();
   }

   public long readLong() {
      return this.readCompressedLong();
   }

   protected int readCompressedInt() throws DataException {
      int i = 0;
      int used = 0;

      while (true) {
         this.ensureAvailable(this._cursor + 1);
         byte b = this.nextByte();
         i |= b & 127;
         if ((b & 128) == 0) {
            return i;
         }

         used++;
         if (used > 4 || used == 4 && (i & 234881024) != 0) {
            throw new DataException(1);
         }

         i <<= 7;
      }
   }

   public int readUncompressedInt() {
      this.ensureAvailable(this._cursor + 4);
      int b1 = this.nextByte() & 255;
      int b2 = this.nextByte() & 255;
      int b3 = this.nextByte() & 255;
      int b4 = this.nextByte() & 255;
      return b1 << 24 | b2 << 16 | b3 << 8 | b4;
   }

   protected long readCompressedLong() throws DataException {
      long i = 0;
      int used = 0;

      while (true) {
         this.ensureAvailable(this._cursor + 1);
         byte b = this.nextByte();
         i |= b & 127;
         if ((b & 128) == 0) {
            return i;
         }

         if (++used > 9) {
            throw new DataException(1);
         }

         i <<= 7;
      }
   }

   public long readUncompressedLong() {
      this.ensureAvailable(this._cursor + 8);
      long b1 = this.nextByte() & 255;
      long b2 = this.nextByte() & 255;
      long b3 = this.nextByte() & 255;
      long b4 = this.nextByte() & 255;
      long b5 = this.nextByte() & 255;
      long b6 = this.nextByte() & 255;
      long b7 = this.nextByte() & 255;
      long b8 = this.nextByte() & 255;
      return b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 | b5 << 24 | b6 << 16 | b7 << 8 | b8;
   }

   protected void nextByte(byte b) {
      this._buffer[this._cursor++] = b;
   }

   protected void ensureAvailable(int length) throws DataException {
      if (length > this._end) {
         throw new DataException(2);
      }
   }

   public void writeByte(byte value) {
      this.ensureBuffer(this._end + 1);
      this.nextByte(value);
   }

   public void writeInt(int value) {
      this.writeCompressedInt(value);
   }

   public void writeLong(long value) {
      this.writeCompressedLong(value);
   }

   protected void writeCompressedInt(int value) {
      int s = 28;

      int n;
      for (n = 5; s > 0 && (value >>> s & 127) == 0; n--) {
         s -= 7;
      }

      this.ensureBuffer(this._cursor + n);

      while (s > 0) {
         this.nextByte((byte)(128 | value >>> s));
         s -= 7;
      }

      this.nextByte((byte)(value & 127));
   }

   public void writeUncompressedInt(int value) {
      this.ensureBuffer(this._cursor + 4);
      this.nextByte((byte)(value >>> 24));
      this.nextByte((byte)(value >>> 16));
      this.nextByte((byte)(value >>> 8));
      this.nextByte((byte)value);
   }

   public static int getCompressedIntSize(int value) {
      int s = 28;

      int n;
      for (n = 5; s > 0; n--) {
         if ((value >>> s & 127) != 0) {
            return n;
         }

         s -= 7;
      }

      return n;
   }

   public static int getCompressedLongSize(long value) {
      int s = 63;

      int n;
      for (n = 10; s > 0; n--) {
         if (((int)(value >>> s) & 127) != 0) {
            return n;
         }

         s -= 7;
      }

      return n;
   }

   protected void writeCompressedLong(long value) {
      int s = 63;

      int n;
      for (n = 10; s > 0 && ((int)(value >>> s) & 127) == 0; n--) {
         s -= 7;
      }

      this.ensureBuffer(this._cursor + n);

      while (s > 0) {
         this.nextByte((byte)(128 | value >>> s));
         s -= 7;
      }

      this.nextByte((byte)(value & 127));
   }

   public void writeUncompressedLong(long value) {
      this.ensureBuffer(this._cursor + 8);
      this.nextByte((byte)(value >>> 56));
      this.nextByte((byte)(value >>> 48));
      this.nextByte((byte)(value >>> 40));
      this.nextByte((byte)(value >>> 32));
      this.nextByte((byte)(value >>> 24));
      this.nextByte((byte)(value >>> 16));
      this.nextByte((byte)(value >>> 8));
      this.nextByte((byte)value);
   }

   protected byte nextByte() {
      return this._buffer[this._cursor++];
   }

   protected void ensureBuffer(int newLength) {
      if (this._buffer == null) {
         this._buffer = new byte[newLength];
      } else if (newLength > this._buffer.length) {
         byte[] newBuffer = new byte[newLength * 5 >> 2];
         System.arraycopy(this._buffer, 0, newBuffer, 0, this._buffer.length);
         this._buffer = newBuffer;
      }

      this._end = newLength;
   }

   public void ensureCapacity(int capacity) {
      this.ensureBuffer(this._cursor + capacity);
   }

   public void rewind() {
      this._cursor = this._start;
   }

   public void rewind(int cursor) {
      if (cursor < this._start) {
         throw new Object();
      }

      this._cursor = cursor;
   }

   public void forward(int numBytes) {
      if (numBytes > this.available()) {
         throw new Object();
      }

      this._cursor += numBytes;
   }

   public void copy(CompressedBuffer src, int offset, int length, boolean advanceCursor) {
      this.copy(src.getBuffer(), offset, length, advanceCursor);
   }

   public void copy(byte[] src, int offset, int length, boolean advanceCursor) {
      if (offset >= 0 && length >= 0 && offset + length <= src.length) {
         this.ensureBuffer(this._cursor + length);
         System.arraycopy(src, offset, this._buffer, this._cursor, length);
         if (advanceCursor) {
            this._cursor += length;
         }
      } else {
         throw new Object();
      }
   }
}
