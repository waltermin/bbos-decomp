package net.rim.device.cldc.io.tcpsocket;

import java.io.OutputStream;

final class TcpOutputStream extends OutputStream {
   private TcpSocket _socket;

   public TcpOutputStream(TcpSocket socket) {
      this._socket = socket;
   }

   @Override
   public final void write(int b) {
      this._socket.write(b);
   }

   @Override
   public final void write(byte[] b, int off, int len) {
      this._socket.write(b, off, len);
   }

   @Override
   public final void close() {
      this._socket.outputClosed();
   }
}
