package net.rim.device.internal.io.file;

import java.io.InputStream;
import java.io.OutputStream;

final class USBOutputStream extends OutputStream {
   private Connection _conn;
   private byte[] _buffer;
   private int _endPos;

   USBOutputStream(Connection conn) {
      this._conn = conn;
      this._buffer = new byte[conn._writeBufferSize];
   }

   @Override
   public final void write(int b) {
      synchronized (this._conn._writeSemaphore) {
         if (this._buffer == null) {
            throw new ConnectionShutdown();
         }

         this._buffer[this._endPos++] = (byte)b;
         if (this._endPos >= this._conn._writeBufferSize) {
            this.flush();
         }
      }
   }

   public final void copy(InputStream in, long bytes) {
      synchronized (this._conn._writeSemaphore) {
         if (this._buffer == null) {
            throw new ConnectionShutdown();
         }

         if (this._endPos != 0) {
            this.flush();
         }

         while (bytes > 0) {
            int bytesRead = in.read(this._buffer, 0, Math.min((int)bytes, this._conn._writeBufferSize));
            if (bytesRead == -1) {
               throw new Object();
            }

            this.writeInternal(this._buffer, 0, bytesRead);
            bytes -= bytesRead;
         }
      }
   }

   @Override
   public final void write(byte[] buffer, int offset, int length) {
      synchronized (this._conn._writeSemaphore) {
         if (this._buffer == null) {
            throw new ConnectionShutdown();
         }

         if (this._endPos + length <= this._buffer.length) {
            System.arraycopy(buffer, offset, this._buffer, this._endPos, length);
            this._endPos += length;
            if (this._endPos >= this._conn._writeBufferSize) {
               this.flush();
            }
         } else {
            if (this._endPos > 0) {
               this.flush();
            }

            this.writeInternal(buffer, offset, length);
         }
      }
   }

   @Override
   public final void flush() {
      synchronized (this._conn._writeSemaphore) {
         if (this._buffer == null) {
            throw new ConnectionShutdown();
         }

         this.writeInternal(this._buffer, 0, this._endPos);
         this._endPos = 0;
      }
   }

   @Override
   public final void close() {
      synchronized (this._conn._writeSemaphore) {
         this._buffer = null;
      }
   }

   private final void writeInternal(byte[] buffer, int offset, int length) {
      while (this._conn._exception == null) {
         if (this._buffer == null) {
            throw new ConnectionShutdown();
         }

         if (this._conn._port != null && this._conn._writeSemaphore._ready) {
            if (length == 0) {
               return;
            }

            this._conn._writeSemaphore._ready = false;
            int len = length;
            if (len > this._conn._writeBufferSize) {
               len = this._conn._writeBufferSize;
            }

            int ret = this._conn._port.write(buffer, offset, len);
            offset += ret;
            length -= ret;
            if (length == 0) {
               return;
            }
         }

         try {
            this._conn._writeSemaphore.wait();
         } finally {
            continue;
         }
      }

      throw this._conn._exception;
   }
}
