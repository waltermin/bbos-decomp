package net.rim.device.cldc.io.commlink;

import java.io.InputStream;

class SerialInputStream extends InputStream {
   private SerialTransport _transport;

   SerialInputStream(SerialTransport transport) {
      this._transport = transport;
   }

   @Override
   public int read() {
      return this._transport.read();
   }

   @Override
   public int read(byte[] b, int off, int len) {
      return this._transport.read(b, off, len);
   }
}
