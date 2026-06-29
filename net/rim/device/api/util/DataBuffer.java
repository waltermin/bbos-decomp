package net.rim.device.api.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public class DataBuffer implements DataInput, DataOutput, Persistable {
   private byte[] _buffer;
   private int _start;
   private int _position;
   private int _length;
   private boolean _useBigEndianFlag;

   public void setData(byte[] contents, int offset, int numBytes) {
      this.setData(contents, offset, numBytes, this._useBigEndianFlag);
   }

   public void setData(byte[] contents, int offset, int numBytes, boolean bigEndianFlag) {
      if (offset >= 0 && numBytes >= 0) {
         if (contents != null) {
            this._buffer = contents;
            this._length = Math.min(this._buffer.length, offset + numBytes);
         } else if (numBytes != 0) {
            this._buffer = new byte[numBytes];
            this._length = 0;
         } else {
            this._buffer = null;
            this._length = 0;
         }

         this._start = Math.min(this._length, offset);
         this._position = this._start;
         this._useBigEndianFlag = bigEndianFlag;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void reset() {
      this._buffer = null;
      this._length = 0;
      this._start = 0;
      this._position = 0;
   }

   public int getPosition() {
      return this._position - this._start;
   }

   public void setPosition(int newPosition) {
      this._position = MathUtilities.clamp(this._start, this._start + newPosition, this._length);
   }

   public void zero() {
      if (this._buffer != null && Memory.getSecureOldObjects()) {
         Arrays.fill(this._buffer, (byte)0, this._start, this._length - this._start);
      }
   }

   public int getLength() {
      return this._length - this._start;
   }

   public void setLength(int newLength) {
      if (newLength < 0) {
         throw new IllegalArgumentException();
      }

      newLength += this._start;
      this.ensureBuffer(newLength);
      this._length = newLength;
      this._position = Math.min(this._length, this._position);
   }

   public void ensureLength(int newLength) {
      this.ensureBuffer(this._start + newLength);
   }

   public void ensureCapacity(int dataLength) {
      this.ensureBuffer(this._position + dataLength);
   }

   protected void ensureBuffer(int newLength) {
      if (newLength >= this._length) {
         if (this._buffer == null) {
            this._buffer = new byte[newLength];
         } else if (newLength > this._buffer.length) {
            int incSize = newLength - this._buffer.length;
            Array.extend(this._buffer, incSize);
         }

         this._length = newLength;
      }
   }

   public void trim() {
      this.trim(true);
   }

   public void trim(boolean resize) {
      if (this._buffer != null) {
         this._length = this._position;
         if (resize) {
            Array.resize(this._buffer, this._length);
         }
      }
   }

   public void trimHead(boolean resize) {
      if (this._buffer != null && this._position > 0) {
         int size = this._length - this._position;
         System.arraycopy(this._buffer, this._position, this._buffer, 0, size);
         this._length = size;
         this._position = 0;
         if (resize) {
            Array.resize(this._buffer, this._length);
         }
      }
   }

   public int available() {
      return this._length - this._position;
   }

   public boolean eof() {
      return this._position >= this._length;
   }

   public void rewind() {
      this._position = this._start;
   }

   public boolean isBigEndian() {
      return this._useBigEndianFlag;
   }

   public void setBigEndian(boolean flag) {
      this._useBigEndianFlag = flag;
   }

   public byte[] toArray() {
      if (this._buffer != null) {
         byte[] copy = new byte[this._length - this._start];
         System.arraycopy(this._buffer, this._start, copy, 0, copy.length);
         return copy;
      } else {
         return new byte[0];
      }
   }

   public byte[] getArray() {
      return this._buffer != null ? this._buffer : new byte[0];
   }

   public int getArrayStart() {
      return this._start;
   }

   public int getArrayPosition() {
      return this._position;
   }

   public int getArrayLength() {
      return this._length;
   }

   public void writeCompressedLong(long i) {
      int s = 63;

      int n;
      for (n = 10; s > 0 && ((int)(i >>> s) & 127) == 0; n--) {
         s -= 7;
      }

      this.ensureBuffer(this._position + n);

      while (s > 0) {
         this.nextByte(128 | (int)(i >>> s));
         s -= 7;
      }

      this.nextByte((int)(i & 127));
   }

   public void writeCompressedInt(int i) {
      int s = 28;

      int n;
      for (n = 5; s > 0 && (i >>> s & 127) == 0; n--) {
         s -= 7;
      }

      this.ensureBuffer(this._position + n);

      while (s > 0) {
         this.nextByte(128 | i >>> s);
         s -= 7;
      }

      this.nextByte(i & 127);
   }

   public void writeByteArray(byte[] b, int offset, int length, boolean writeLength) {
      if (offset >= 0 && length >= 0 && offset + length <= b.length) {
         if (writeLength) {
            this.writeCompressedInt(length);
         }

         this.ensureBuffer(this._position + length);
         System.arraycopy(b, offset, this._buffer, this._position, length);
         this._position += length;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void writeByteArray(byte[] b, int offset, int length) {
      this.writeByteArray(b, offset, length, true);
   }

   public void writeByteArray(byte[] b) {
      this.writeByteArray(b, 0, b.length);
   }

   public int read(byte[] outputBuffer) {
      return this.read(outputBuffer, 0, outputBuffer.length);
   }

   public int read(byte[] outputBuffer, int outputBufferOffset, int outputBufferLength) {
      int copyLength = Math.min(outputBufferLength, this._length - this._position);
      if (copyLength != 0) {
         System.arraycopy(this._buffer, this._position, outputBuffer, outputBufferOffset, copyLength);
      }

      this._position += copyLength;
      return copyLength;
   }

   public int read(OutputStream outputStream) {
      return this.read(outputStream, this._length - this._position);
   }

   public int read(OutputStream outputStream, int length) {
      if (outputStream != null && length >= 0) {
         int bytesToWrite = Math.min(length, this._length - this._position);
         if (bytesToWrite != 0) {
            outputStream.write(this._buffer, this._position, bytesToWrite);
            this._position += bytesToWrite;
         }

         return bytesToWrite;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public byte[] readByteArray() {
      int size = this.readCompressedInt();
      if (this._position + size > this._length) {
         throw new EOFException();
      }

      byte[] data = new byte[size];
      System.arraycopy(this._buffer, this._position, data, 0, size);
      this._position += size;
      return data;
   }

   public void write(DataInput input, int length) {
      int count = 0;
      int oldLength = this._length;
      this.ensureCapacity(length);

      try {
         while (count < length) {
            this._buffer[this._position++] = input.readByte();
            count++;
         }
      } catch (EOFException e) {
         this._position--;
         this._length = oldLength + count;
         throw e;
      }
   }

   public void write(InputStream input, int length) {
      length = Math.min(length, input.available());
      this.ensureCapacity(length);
      this._position = this._position + input.read(this._buffer, this._position, length);
   }

   public void write(InputStream input) {
      this.write(input, input.available());
   }

   public void write(DataBuffer dataBuffer, int length) {
      this.write(dataBuffer.getArray(), dataBuffer.getArrayPosition(), length);
   }

   public long readCompressedLong() {
      long i = 0;
      int used = 0;

      while (true) {
         byte b = this.readByte();
         i |= b & 127;
         if ((b & 128) == 0) {
            return i;
         }

         if (++used > 9) {
            throw new NumberFormatException();
         }

         i <<= 7;
      }
   }

   public int readCompressedInt() {
      int i = 0;
      int used = 0;

      while (true) {
         byte b = this.readByte();
         i |= b & 127;
         if ((b & 128) == 0) {
            return i;
         }

         used++;
         if (used > 4 || used == 4 && (i & 234881024) != 0) {
            throw new NumberFormatException();
         }

         i <<= 7;
      }
   }

   @Override
   public int readUnsignedShort() {
      return this.readShort() & 65535;
   }

   @Override
   public String readUTF() {
      return DataInputStream.readUTF(this);
   }

   @Override
   public int readUnsignedByte() {
      return this.readByte() & 0xFF;
   }

   @Override
   public short readShort() {
      if (this._position + 2 > this._length) {
         throw new EOFException();
      }

      int b1;
      int b2;
      if (this._useBigEndianFlag) {
         b1 = this.nextByte();
         b2 = this.nextByte();
      } else {
         b2 = this.nextByte();
         b1 = this.nextByte();
      }

      return (short)(b1 << 8 | b2);
   }

   @Override
   public void write(byte[] b) {
      this.write(b, 0, b.length);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      if (off >= 0 && len >= 0 && off + len <= b.length) {
         this.ensureBuffer(this._position + len);
         System.arraycopy(b, off, this._buffer, this._position, len);
         this._position += len;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public final double readDouble() {
      return Double.longBitsToDouble(this.readLong());
   }

   @Override
   public final float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   @Override
   public long readLong() {
      if (this._position + 8 > this._length) {
         throw new EOFException();
      }

      long b1;
      long b2;
      long b3;
      long b4;
      long b5;
      long b6;
      long b7;
      long b8;
      if (this._useBigEndianFlag) {
         b1 = this.nextByte();
         b2 = this.nextByte();
         b3 = this.nextByte();
         b4 = this.nextByte();
         b5 = this.nextByte();
         b6 = this.nextByte();
         b7 = this.nextByte();
         b8 = this.nextByte();
      } else {
         b8 = this.nextByte();
         b7 = this.nextByte();
         b6 = this.nextByte();
         b5 = this.nextByte();
         b4 = this.nextByte();
         b3 = this.nextByte();
         b2 = this.nextByte();
         b1 = this.nextByte();
      }

      return b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 | b5 << 24 | b6 << 16 | b7 << 8 | b8;
   }

   @Override
   public int readInt() {
      if (this._position + 4 > this._length) {
         throw new EOFException();
      }

      int b1;
      int b2;
      int b3;
      int b4;
      if (this._useBigEndianFlag) {
         b1 = this.nextByte();
         b2 = this.nextByte();
         b3 = this.nextByte();
         b4 = this.nextByte();
      } else {
         b4 = this.nextByte();
         b3 = this.nextByte();
         b2 = this.nextByte();
         b1 = this.nextByte();
      }

      return b1 << 24 | b2 << 16 | b3 << 8 | b4;
   }

   @Override
   public void write(int b) {
      int old = this._position;
      int curr = old + 1;
      this.ensureBuffer(curr);
      this._buffer[old] = (byte)b;
      this._position = curr;
   }

   @Override
   public void writeBoolean(boolean v) {
      this.ensureBuffer(this._position + 1);
      this.nextByte(v ? 1 : 0);
   }

   @Override
   public void writeByte(int v) {
      int old = this._position;
      int curr = old + 1;
      this.ensureBuffer(curr);
      this._buffer[old] = (byte)v;
      this._position = curr;
   }

   @Override
   public void writeChar(int v) {
      this.ensureBuffer(this._position + 2);
      if (this._useBigEndianFlag) {
         this.nextByte(v >>> 8);
         this.nextByte(v);
      } else {
         this.nextByte(v);
         this.nextByte(v >>> 8);
      }
   }

   @Override
   public void writeChars(String s) {
      int len = s.length();
      this.ensureBuffer(this._position + len * 2);
      int i = 0;

      while (--len >= 0) {
         char c = s.charAt(i++);
         if (this._useBigEndianFlag) {
            this.nextByte(c >>> '\b');
            this.nextByte(c);
         } else {
            this.nextByte(c);
            this.nextByte(c >>> '\b');
         }
      }
   }

   @Override
   public void readFully(byte[] outputBuffer, int outputBufferOffset, int outputBufferLength) {
      if (outputBufferLength > this._length - this._position) {
         throw new EOFException();
      }

      if (outputBufferLength != 0) {
         System.arraycopy(this._buffer, this._position, outputBuffer, outputBufferOffset, outputBufferLength);
      }

      this._position += outputBufferLength;
   }

   @Override
   public void readFully(byte[] outputBuffer) {
      this.readFully(outputBuffer, 0, outputBuffer.length);
   }

   @Override
   public char readChar() {
      if (this._position + 2 > this._length) {
         throw new EOFException();
      }

      int b1;
      int b2;
      if (this._useBigEndianFlag) {
         b1 = this.nextByte();
         b2 = this.nextByte();
      } else {
         b2 = this.nextByte();
         b1 = this.nextByte();
      }

      return (char)(b1 << 8 | b2);
   }

   @Override
   public void writeInt(int i) {
      this.ensureBuffer(this._position + 4);
      if (this._useBigEndianFlag) {
         this.nextByte(i >>> 24);
         this.nextByte(i >>> 16);
         this.nextByte(i >>> 8);
         this.nextByte(i);
      } else {
         this.nextByte(i);
         this.nextByte(i >>> 8);
         this.nextByte(i >>> 16);
         this.nextByte(i >>> 24);
      }
   }

   @Override
   public void writeLong(long v) {
      this.ensureBuffer(this._position + 8);
      if (this._useBigEndianFlag) {
         this.nextByte((int)(v >>> 56));
         this.nextByte((int)(v >>> 48));
         this.nextByte((int)(v >>> 40));
         this.nextByte((int)(v >>> 32));
         this.nextByte((int)(v >>> 24));
         this.nextByte((int)(v >>> 16));
         this.nextByte((int)(v >>> 8));
         this.nextByte((int)v);
      } else {
         this.nextByte((int)v);
         this.nextByte((int)(v >>> 8));
         this.nextByte((int)(v >>> 16));
         this.nextByte((int)(v >>> 24));
         this.nextByte((int)(v >>> 32));
         this.nextByte((int)(v >>> 40));
         this.nextByte((int)(v >>> 48));
         this.nextByte((int)(v >>> 56));
      }
   }

   @Override
   public final void writeFloat(float v) {
      this.writeInt(Float.floatToIntBits(v));
   }

   @Override
   public final void writeDouble(double v) {
      this.writeLong(Double.doubleToLongBits(v));
   }

   @Override
   public void writeShort(int v) {
      this.ensureBuffer(this._position + 2);
      if (this._useBigEndianFlag) {
         this.nextByte(v >>> 8);
         this.nextByte(v);
      } else {
         this.nextByte(v);
         this.nextByte(v >>> 8);
      }
   }

   @Override
   public void writeUTF(String str) {
      StringUtilities.writeUTF(str, this);
   }

   @Override
   public boolean readBoolean() {
      return this.readByte() != 0;
   }

   @Override
   public byte readByte() {
      if (this._position >= this._length) {
         throw new EOFException();
      } else {
         return this._buffer[this._position++];
      }
   }

   @Override
   public int skipBytes(int n) {
      if (n <= 0) {
         return 0;
      }

      int skipped = Math.min(this._length - this._position, n);
      this._position += skipped;
      return skipped;
   }

   public DataBuffer(boolean bigEndianFlag) {
      this.setData(null, 0, 0, bigEndianFlag);
   }

   private final void nextByte(int i) {
      this._buffer[this._position++] = (byte)i;
   }

   public DataBuffer(byte[] contents, int offset, int numBytes, boolean bigEndianFlag) {
      this.setData(contents, offset, numBytes, bigEndianFlag);
   }

   public DataBuffer(int bufferSize, boolean bigEndianFlag) {
      this.setData(null, 0, bufferSize, bigEndianFlag);
   }

   private final int nextByte() {
      return this._buffer[this._position++] & 0xFF;
   }

   public static int getCompressedIntSize(int i) {
      int s = 28;

      int n;
      for (n = 5; s > 0; n--) {
         if ((i >>> s & 127) != 0) {
            return n;
         }

         s -= 7;
      }

      return n;
   }

   public DataBuffer(DataBuffer contentBuffer, int numBytes) {
      this.setData(contentBuffer.getArray(), contentBuffer.getArrayPosition(), numBytes, contentBuffer._useBigEndianFlag);
      contentBuffer.skipBytes(numBytes);
   }

   public DataBuffer() {
      this.setData(null, 0, 0, true);
   }
}
