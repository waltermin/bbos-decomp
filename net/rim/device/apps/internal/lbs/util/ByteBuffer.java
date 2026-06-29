package net.rim.device.apps.internal.lbs.util;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class ByteBuffer {
   private byte[] _buffer;
   private int _count;

   public ByteBuffer() {
      this(128);
   }

   public ByteBuffer(int length) {
      this._buffer = new byte[length];
   }

   public final int length() {
      return this._count;
   }

   public final int capacity() {
      return this._buffer.length;
   }

   private final void expandCapacity(int minimumCapacity) {
      int oldCapacity = this.capacity();
      int newCapacity = (oldCapacity + 1) * 2;
      if (minimumCapacity > newCapacity) {
         newCapacity = minimumCapacity;
      }

      if (newCapacity < 4096) {
         Array.resize(this._buffer, newCapacity);
      } else {
         int incSize = minimumCapacity - oldCapacity;
         if (incSize <= 0) {
            incSize = 1;
         }

         Array.extend(this._buffer, incSize);
      }
   }

   public final void setLength(int newLength) {
      if (newLength < 0) {
         throw new StringIndexOutOfBoundsException(newLength);
      }

      if (newLength > this.capacity()) {
         this.expandCapacity(newLength);
      }

      if (this._count < newLength) {
         Arrays.fill(this._buffer, (byte)0, this._count, newLength - this._count);
      }

      this._count = newLength;
   }

   public final ByteBuffer append(String str) {
      if (str == null) {
         str = String.valueOf(str);
      }

      this.append(str.getBytes());
      return this;
   }

   public final ByteBuffer append(byte[] str) {
      return this.append(str, 0, str.length);
   }

   public final ByteBuffer append(byte[] str, int offset, int len) {
      int newcount = this._count + len;
      if (newcount > this.capacity()) {
         this.expandCapacity(newcount);
      }

      System.arraycopy(str, offset, this._buffer, this._count, len);
      this._count = newcount;
      return this;
   }

   public final ByteBuffer append(byte c) {
      int newcount = this._count + 1;
      if (newcount > this.capacity()) {
         this.expandCapacity(newcount);
      }

      this._buffer[this._count++] = c;
      return this;
   }

   public final ByteBuffer insert(int offset, String str) {
      return this.insert(offset, str.getBytes());
   }

   public final ByteBuffer insert(int offset, byte[] str) {
      if (offset >= 0 && offset <= this._count) {
         int len = str.length;
         int newcount = this._count + len;
         if (newcount > this.capacity()) {
            this.expandCapacity(newcount);
         }

         System.arraycopy(this._buffer, offset, this._buffer, offset + len, this._count - offset);
         this._count = newcount;
         System.arraycopy(str, 0, this._buffer, offset, len);
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public final byte[] toArray() {
      if (this._count > 0) {
         byte[] copy = new byte[this._count];
         System.arraycopy(this._buffer, 0, copy, 0, this._count);
         return copy;
      } else {
         return new byte[0];
      }
   }

   @Override
   public final String toString() {
      return this._count == 0 ? "" : new String(this._buffer, 0, this._count);
   }
}
