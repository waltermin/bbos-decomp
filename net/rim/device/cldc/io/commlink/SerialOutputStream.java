package net.rim.device.cldc.io.commlink;

import java.io.OutputStream;

class SerialOutputStream extends OutputStream {
   private SerialTransport _transport;

   SerialOutputStream(SerialTransport transport) {
      this._transport = transport;
   }

   @Override
   public void write(int b) {
      this._transport.write(b);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      this._transport.write(b, off, len);
   }

   @Override
   public void flush() {
      this._transport.flush();
   }
}
