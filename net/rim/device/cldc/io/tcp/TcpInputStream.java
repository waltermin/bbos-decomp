package net.rim.device.cldc.io.tcp;

import java.io.IOException;
import java.io.InputStream;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.internal.io.streamdatagram.StreamDatagramConnectionBase;

final class TcpInputStream extends InputStream {
   protected boolean _isClosed;
   private Protocol _connection;
   private byte[] _buffer;
   private int _bufferOffset;
   private int _bufferEndOffset;
   private PacketLogger _packetLogger;
   private static String STR_INSTREAM_CLOSED = "TcpInputStream is closed";

   TcpInputStream(StreamDatagramConnectionBase streamConnection) {
      this._connection = (Protocol)streamConnection;
      this._packetLogger = PacketLogger.getInstance();
   }

   @Override
   public final synchronized int read() {
      if (this._isClosed) {
         throw new Object();
      }

      if (this._bufferOffset >= this._bufferEndOffset) {
         this.getNextBlock();
         if (this._buffer == null || this._bufferOffset >= this._bufferEndOffset) {
            return -1;
         }
      }

      return this._buffer[this._bufferOffset++] & 0xFF;
   }

   @Override
   public final synchronized int read(byte[] b, int off, int len) throws IOException {
      if (this._isClosed) {
         throw new Object();
      }

      if (b == null) {
         throw new Object();
      }

      if (off < 0 || len < 0 || off + len > b.length) {
         throw new Object();
      }

      if (len == 0) {
         return 0;
      }

      try {
         int bytesRead = 0;

         while (len > 0) {
            if (this._bufferOffset >= this._bufferEndOffset) {
               this.getNextBlock();
               if (this._buffer == null || this._bufferOffset >= this._bufferEndOffset) {
                  return bytesRead == 0 ? -1 : bytesRead;
               }
            }

            int bytesToRead = Math.min(len, this._bufferEndOffset - this._bufferOffset);
            System.arraycopy(this._buffer, this._bufferOffset, b, off, bytesToRead);
            this._bufferOffset += bytesToRead;
            off += bytesToRead;
            len -= bytesToRead;
            bytesRead += bytesToRead;
         }

         return bytesRead;
      } finally {
         this.close();
      }
   }

   @Override
   public final void close() {
      if (!this._isClosed) {
         this._isClosed = true;
         this._connection.inputStreamClosed();
      }
   }

   @Override
   public final int available() {
      if (this._isClosed) {
         throw new Object(STR_INSTREAM_CLOSED);
      } else {
         return this._connection.getInStreamDataAvailable() + (this._bufferEndOffset - this._bufferOffset);
      }
   }

   private final void getNextBlock() {
      TcpReassembleDataNode node = this._connection.readNextBlock();
      if (node != null) {
         this._buffer = node._data;
         this._bufferOffset = node._offset;
         this._bufferEndOffset = node._offset + node._length;
         if (this._packetLogger._lowLoggingEnabled) {
            this._packetLogger
               .logPacket(
                  node._data,
                  node._offset,
                  node._length,
                  ((StringBuffer)(new Object("TCP:")))
                     .append(this._connection.getAddress())
                     .append(':')
                     .append(this._connection.getLocalPort())
                     .append(':')
                     .append(this._connection.getPort())
                     .toString(),
                  false
               );
            return;
         }
      } else {
         this._bufferOffset = 0;
         this._bufferEndOffset = 0;
         this._buffer = null;
      }
   }
}
