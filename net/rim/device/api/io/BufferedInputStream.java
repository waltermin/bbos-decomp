package net.rim.device.api.io;

import java.io.IOException;
import java.io.InputStream;
import net.rim.vm.Array;

public final class BufferedInputStream extends InputStream {
   private InputStream _in;
   private byte[] _buffer;
   private int _bufferPos;
   private int _currentMarkPos = -1;
   private int _maxMarkPos;
   private boolean _closed;
   private static final int BUFFER_SIZE;

   public BufferedInputStream(InputStream in) {
      this(in, 2048);
   }

   public BufferedInputStream(InputStream in, int size) {
      this._in = in;
      if (size > 0 && in != null) {
         this._buffer = new byte[size];
         this._bufferPos = this._buffer.length;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final synchronized void reset() {
      if (!this._closed && this._currentMarkPos >= 0) {
         this._bufferPos = this._currentMarkPos;
      } else {
         throw new IOException();
      }
   }

   @Override
   public final boolean markSupported() {
      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         super.close();
         var3 = false;
      } finally {
         if (var3) {
            this._closed = true;
         }
      }

      this._closed = true;
   }

   @Override
   public final synchronized int available() {
      if (this._closed) {
         throw new IOException();
      } else {
         return this._buffer.length - this._bufferPos;
      }
   }

   @Override
   public final synchronized void mark(int limit) {
      this._maxMarkPos = limit;
      this._currentMarkPos = this._bufferPos;
   }

   @Override
   public final synchronized int read() {
      if (this._closed) {
         throw new IOException();
      } else {
         return this._bufferPos >= this._buffer.length && !this.fillBuffer() ? -1 : this._buffer[this._bufferPos++] & 0xFF;
      }
   }

   private final boolean fillBuffer() {
      if (this._currentMarkPos < 0) {
         this._bufferPos = 0;
      } else if (this._currentMarkPos > 0) {
         int bytesRemainingAsMarked = this._bufferPos - this._currentMarkPos;
         System.arraycopy(this._buffer, this._currentMarkPos, this._buffer, 0, bytesRemainingAsMarked);
         this._currentMarkPos = bytesRemainingAsMarked;
         this._currentMarkPos = 0;
      } else if (this._buffer.length >= this._maxMarkPos) {
         this._currentMarkPos = -1;
         this._bufferPos = 0;
      } else {
         int size = this._buffer.length + 2048;
         Array.resize(this._buffer, size);
      }

      int numToRead = this._buffer.length - this._bufferPos;
      int numRead = this._in.read(this._buffer, this._bufferPos, numToRead);
      if (numRead > 0 && numRead != numToRead) {
         Array.resize(this._buffer, this._bufferPos + numRead);
      }

      return numRead > 0;
   }

   @Override
   public final synchronized int read(byte[] buffer, int bufferOffset, int bufferLength) {
      if (this._closed) {
         throw new IOException();
      }

      if (buffer == null || bufferOffset < 0 || bufferLength < 0 || buffer.length - bufferLength < bufferOffset) {
         throw new IllegalArgumentException();
      }

      if (bufferLength == 0) {
         return 0;
      }

      int numRead = 0;

      while (bufferLength > 0) {
         if (this._buffer.length == this._bufferPos) {
            if (bufferLength >= this._buffer.length && this._currentMarkPos < 0) {
               int numAuxRead = this._in.read(buffer, bufferOffset, bufferLength);
               if (numAuxRead >= 0) {
                  return numAuxRead + numRead;
               }

               if (numRead > 0) {
                  return numRead;
               }

               return -1;
            }

            if (!this.fillBuffer()) {
               break;
            }
         }

         int toRead = Math.min(this._buffer.length - this._bufferPos, bufferLength);
         System.arraycopy(this._buffer, this._bufferPos, buffer, bufferOffset, toRead);
         this._bufferPos += toRead;
         numRead += toRead;
         bufferLength -= toRead;
         bufferOffset += toRead;
      }

      return numRead > 0 ? numRead : -1;
   }
}
