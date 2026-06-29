package net.rim.device.cldc.io.tcpsocket;

import java.io.InputStream;

final class TcpInputStream extends InputStream {
   private TcpSocket _socket;

   public TcpInputStream(TcpSocket socket) {
      this._socket = socket;
   }

   @Override
   public final int read() {
      return this._socket.read();
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      return this._socket.read(b, off, len);
   }

   @Override
   public final int available() {
      return this._socket.available();
   }

   @Override
   public final void close() {
      this._socket.inputClosed();
   }
}
