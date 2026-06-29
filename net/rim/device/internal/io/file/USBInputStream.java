package net.rim.device.internal.io.file;

import java.io.InputStream;

final class USBInputStream extends InputStream {
   private Connection _conn;
   private byte[] _data;
   private int _offset;
   private int _length;

   USBInputStream(Connection conn) {
      this._conn = conn;
      this._data = new byte[conn._readBufferSize];
   }

   private final void fill() {
      while (this._conn._exception == null) {
         if (this._data == null) {
            throw new ConnectionShutdown();
         }

         if (this._conn._port != null && this._conn._readSemaphore._ready) {
            this._offset = 0;
            this._length = this._conn._port.read(this._data, 0, this._conn._readSemaphore._length);
            this._conn._readSemaphore._ready = false;
            if (this._length > 0) {
               return;
            }
         }

         try {
            this._conn._readSemaphore.wait();
         } finally {
            continue;
         }
      }

      throw this._conn._exception;
   }

   @Override
   public final int read() {
      synchronized (this._conn._readSemaphore) {
         if (this._data == null) {
            throw new ConnectionShutdown();
         }

         if (this._length <= 0) {
            this.fill();
         }

         if (this._length > 0) {
            this._length--;
            return this._data[this._offset++] & 0xFF;
         } else {
            return -1;
         }
      }
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      synchronized (this._conn._readSemaphore) {
         if (this._data == null) {
            throw new ConnectionShutdown();
         }

         if (this._length <= 0) {
            this.fill();
         }

         int len = this._length;
         if (len > length) {
            len = length;
         }

         System.arraycopy(this._data, this._offset, buffer, offset, len);
         this._offset += len;
         this._length -= len;
         return len;
      }
   }

   @Override
   public final int available() {
      return this._length;
   }

   @Override
   public final void close() {
      synchronized (this._conn._readSemaphore) {
         this._data = null;
      }
   }
}
