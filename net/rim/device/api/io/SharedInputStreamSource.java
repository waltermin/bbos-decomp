package net.rim.device.api.io;

import java.io.IOException;
import java.io.InputStream;
import net.rim.vm.Array;

final class SharedInputStreamSource {
   private byte[] _data;
   private InputStream _sourceStream;
   private static final int RESIZE_DELTA = 1024;

   public SharedInputStreamSource(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      this._data = data;
      this._sourceStream = null;
   }

   public SharedInputStreamSource(InputStream input) {
      if (input == null) {
         throw new IllegalArgumentException();
      }

      this._data = new byte[0];
      this._sourceStream = input;
   }

   public final synchronized int read(int position) {
      if (position < 0) {
         throw new IllegalArgumentException();
      }

      while (position >= this._data.length) {
         if (this._sourceStream == null) {
            return -1;
         }

         this.expandData();
      }

      return this._data[position] & 0xFF;
   }

   public final synchronized int read(int position, byte[] buffer) {
      return this.read(position, buffer, 0, buffer == null ? 0 : buffer.length);
   }

   public final synchronized int read(int position, byte[] buffer, int offset, int length) {
      if (position < 0 || buffer == null || offset < 0 || length < 0 || buffer.length - length < offset) {
         throw new IllegalArgumentException();
      }

      if (length == 0) {
         return 0;
      }

      int endPosition = position + length;

      while (endPosition > this._data.length) {
         if (this._sourceStream == null) {
            length = this._data.length - position;
            if (length <= 0) {
               return -1;
            }
            break;
         }

         this.expandData();
      }

      System.arraycopy(this._data, position, buffer, offset, length);
      return length;
   }

   public final synchronized int available(int position) {
      if (position < 0) {
         throw new IllegalArgumentException();
      } else {
         int available = this._data.length - position;
         if (available <= 0 && this._sourceStream != null) {
            return Math.max(0, this._sourceStream.available());
         } else {
            return available <= 0 ? 0 : available;
         }
      }
   }

   public final synchronized long skip(int position, long n) {
      int available = Math.max(this._data.length - position, 0);
      if (available >= n) {
         return n;
      }

      while (this._sourceStream != null) {
         try {
            this.expandData();
            available = Math.max(this._data.length - position, 0);
            if (available >= n) {
               return n;
            }
         } catch (IOException e) {
            return Math.max(available, 0);
         }
      }

      return Math.max(Math.min(available, n), 0);
   }

   private final void expandData() {
      int dataLength = this._data.length;
      Array.resize(this._data, dataLength + 1024);
      int readLength = this._sourceStream.read(this._data, dataLength, 1024);
      if (readLength < 1024) {
         if (readLength > 0) {
            dataLength += readLength;
         }

         Array.resize(this._data, dataLength);
         this._sourceStream = null;
      }
   }
}
