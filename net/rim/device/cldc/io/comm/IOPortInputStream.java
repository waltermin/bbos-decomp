package net.rim.device.cldc.io.comm;

import java.io.InputStream;

final class IOPortInputStream extends InputStream {
   private Protocol _conn;
   private byte[] _data;
   private int _offset;
   private int _length;

   IOPortInputStream(Protocol conn) {
      this._conn = conn;
      this._data = new byte[1024];
   }

   private final void fill() {
      if (this._length < 1) {
         this._offset = 0;
         this._length = this._conn.read(this._data, 0, this._data.length);
      }
   }

   @Override
   public final int read() {
      this.fill();
      if (this._length > 0) {
         this._length--;
         return this._data[this._offset++] & 0xFF;
      } else {
         return -1;
      }
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      this.fill();
      int len = this._length;
      if (len > length) {
         len = length;
      }

      System.arraycopy(this._data, this._offset, buffer, offset, len);
      this._offset += len;
      this._length -= len;
      return len;
   }

   @Override
   public final int available() {
      return this._length;
   }
}
