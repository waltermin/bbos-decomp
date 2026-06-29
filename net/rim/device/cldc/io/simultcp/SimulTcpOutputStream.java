package net.rim.device.cldc.io.simultcp;

import java.io.OutputStream;
import net.rim.device.internal.io.streamdatagram.StreamDatagramConnectionBase;

final class SimulTcpOutputStream extends OutputStream {
   private Protocol _connection;
   private Object SendBlock = new Object();
   private Object CloseBlock = new Object();
   private boolean _isClosed;

   SimulTcpOutputStream(StreamDatagramConnectionBase streamConnection) {
      this._connection = (Protocol)streamConnection;
   }

   @Override
   public final void write(int someChar) {
      synchronized (this.SendBlock) {
         if (this._isClosed) {
            throw new Object();
         }

         byte[] temp = new byte[]{(byte)(someChar & 0xFF)};
         this._connection.addToOutputBuffer(temp, 0, 1);
         this._connection.checkCloseOutput();
      }
   }

   @Override
   public final void write(byte[] output, int offset, int length) {
      synchronized (this.SendBlock) {
         if (this._isClosed) {
            throw new Object();
         }

         if (offset < 0 || length < 0 || offset + length > output.length) {
            throw new Object();
         }

         if (length != 0) {
            this._connection.addToOutputBuffer(output, offset, length);
            this._connection.checkCloseOutput();
         }
      }
   }

   @Override
   public final void close() {
      synchronized (this.CloseBlock) {
         this._connection.closeOutput();
         this._isClosed = true;
      }
   }

   @Override
   public final void flush() {
      if (this._isClosed) {
         throw new Object();
      }
   }

   final boolean isClosed() {
      return this._isClosed;
   }
}
