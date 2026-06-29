package net.rim.device.cldc.io.tcp;

import java.io.IOException;
import java.io.OutputStream;
import net.rim.device.cldc.io.utility.EventThreadCheck;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.internal.io.streamdatagram.StreamDatagramConnectionBase;

final class TcpOutputStream extends OutputStream {
   private Protocol _connection;
   protected boolean _isClosed;
   private byte[] _oneByteArray = new byte[1];
   private Object _writeLock = new Object();
   private PacketLogger _packetLogger;

   TcpOutputStream(StreamDatagramConnectionBase streamConnection) {
      this._connection = (Protocol)streamConnection;
      this._packetLogger = PacketLogger.getInstance();
   }

   @Override
   public final void write(int someByte) {
      synchronized (this._writeLock) {
         this._oneByteArray[0] = (byte)(someByte & 0xFF);
         this.write(this._oneByteArray, 0, 1);
      }
   }

   @Override
   public final void write(byte[] b, int off, int len) throws IOException {
      if (this._isClosed) {
         throw new Object();
      }

      EventThreadCheck.throwException();
      if (b == null) {
         throw new Object();
      }

      if (off < 0 || len < 0 || off + len > b.length) {
         throw new Object();
      }

      if (len != 0) {
         try {
            synchronized (this._writeLock) {
               this._connection.addToOutputBuffer(b, off, len);
            }

            if (this._packetLogger._lowLoggingEnabled) {
               this._packetLogger
                  .logPacket(
                     b,
                     off,
                     len,
                     ((StringBuffer)(new Object("TCP:")))
                        .append(this._connection.getAddress())
                        .append(':')
                        .append(this._connection.getLocalPort())
                        .append(':')
                        .append(this._connection.getPort())
                        .toString(),
                     true
                  );
            }
         } finally {
            this.close();
         }
      }
   }

   @Override
   public final void flush() {
      if (!this._isClosed) {
         EventThreadCheck.throwException();
         this._connection.sendAllPendingDataNow();
      }
   }

   @Override
   public final void close() {
      if (!this._isClosed) {
         this._isClosed = true;
         this._connection.outputStreamClosed();
      }
   }
}
