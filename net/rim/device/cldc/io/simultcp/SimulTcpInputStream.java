package net.rim.device.cldc.io.simultcp;

import java.io.InputStream;
import net.rim.device.internal.io.streamdatagram.StreamDatagramConnectionBase;

final class SimulTcpInputStream extends InputStream {
   private byte[] _oneByteArray = new byte[1];
   private Protocol _connection;
   private Object ReceiveBlock = new Object();
   private Object CloseBlock = new Object();
   private boolean _isClosed;

   SimulTcpInputStream(StreamDatagramConnectionBase streamConnection) {
      this._connection = (Protocol)streamConnection;
   }

   @Override
   public final int read() {
      synchronized (this.ReceiveBlock) {
         if (this._isClosed) {
            throw new Object();
         }

         int ret = this._connection.readFromInputBuffer(this._oneByteArray, 0, 1);
         this._connection.checkCloseInput();
         return ret != 1 ? ret : this._oneByteArray[0] & 0xFF;
      }
   }

   @Override
   public final int read(byte[] b, int offset, int length) {
      synchronized (this.ReceiveBlock) {
         if (this._isClosed) {
            throw new Object();
         }

         if (offset < 0 || length < 0 || offset + length > b.length) {
            throw new Object();
         }

         if (length == 0) {
            return 0;
         }

         if (this._isClosed) {
            throw new Object();
         }

         int ret = this._connection.readFromInputBuffer(b, offset, length);
         this._connection.checkCloseInput();
         return ret;
      }
   }

   @Override
   public final void close() {
      synchronized (this.CloseBlock) {
         this._connection.closeInput();
         this._isClosed = true;
      }
   }

   @Override
   public final int available() {
      if (this._isClosed) {
         throw new Object();
      } else {
         return this._connection.available();
      }
   }

   final boolean isClosed() {
      return this._isClosed;
   }
}
