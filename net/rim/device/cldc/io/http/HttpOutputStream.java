package net.rim.device.cldc.io.http;

import java.io.OutputStream;

class HttpOutputStream extends OutputStream {
   OutputStream _outputStream;
   HttpProtocolBase _owner;

   public HttpOutputStream(HttpProtocolBase owner, OutputStream outputStream) {
      this._owner = owner;
      this._outputStream = outputStream;
   }

   @Override
   public void write(int b) {
      this._outputStream.write(b);
   }

   @Override
   public void write(byte[] b) {
      this._outputStream.write(b);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      this._outputStream.write(b, off, len);
   }

   @Override
   public void flush() {
      this._owner.transitionToState(1);
   }

   @Override
   public void close() {
      this._outputStream.close();
   }
}
