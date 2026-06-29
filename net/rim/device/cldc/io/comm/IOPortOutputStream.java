package net.rim.device.cldc.io.comm;

import java.io.OutputStream;

class IOPortOutputStream extends OutputStream {
   private Protocol _conn;
   private byte[] _data;

   IOPortOutputStream(Protocol conn) {
      this._conn = conn;
      this._data = new byte[1];
   }

   @Override
   public void write(int b) {
      this._data[0] = (byte)b;
      this._conn.write(this._data, 0, 1);
   }

   @Override
   public void write(byte[] buffer, int offset, int length) {
      this._conn.write(buffer, offset, length);
   }
}
