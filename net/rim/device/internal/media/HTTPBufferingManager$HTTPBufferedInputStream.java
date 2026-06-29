package net.rim.device.internal.media;

import java.io.IOException;
import java.io.InputStream;

class HTTPBufferingManager$HTTPBufferedInputStream extends InputStream {
   private final byte[] readBuffer;
   private boolean _closed;
   private int _markingPos;
   private boolean _skipping;
   private final HTTPBufferingManager this$0;

   HTTPBufferingManager$HTTPBufferedInputStream(HTTPBufferingManager _1) {
      this.this$0 = _1;
      this.readBuffer = new byte[1];
   }

   @Override
   public void mark(int readlimit) {
      this._markingPos = this.this$0._readOffset;
   }

   @Override
   public int available() {
      return this.this$0._dataLength;
   }

   @Override
   public void reset() {
      if (!this.markSupported()) {
         throw new IOException("Mark not supported on this stream");
      }

      this.this$0._readOffset = this._markingPos;
      synchronized (this.this$0._lock) {
         if (this.this$0._writeOffset >= this.this$0._readOffset) {
            this.this$0._dataLength = this.this$0._writeOffset - this.this$0._readOffset;
         } else {
            this.this$0._dataLength = this.this$0._writeOffset + (this.this$0._buffer.length - this.this$0._readOffset);
         }
      }
   }

   @Override
   public boolean markSupported() {
      return this.this$0._totalInputLength <= 1048576;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long skip(long n) {
      synchronized (this.this$0._lock) {
         if (n > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value to skip is too large");
         }

         int intN = (int)n;
         int avail = this.available();
         this._skipping = true;
         boolean var12 = false /* VF: Semaphore variable */;

         long var15;
         label51: {
            try {
               var12 = true;
               if (avail >= intN) {
                  var15 = this.read(null, 0, intN);
                  var12 = false;
                  break label51;
               }

               var15 = this.read(null, 0, avail);
               var12 = false;
            } finally {
               if (var12) {
                  this._skipping = false;
               }
            }

            this._skipping = false;
            return var15;
         }

         this._skipping = false;
         return var15;
      }
   }

   @Override
   public int read() {
      synchronized (this.this$0._lock) {
         int read = this.read(this.readBuffer, 0, 1);
         return read > 0 ? this.readBuffer[0] & 0xFF : -1;
      }
   }

   @Override
   public int read(byte[] buffer, int start, int length) {
      if (this._closed) {
         return -1;
      }

      synchronized (this.this$0._lock) {
         while (this.this$0._dataLength == 0) {
            if (this.this$0._shutdown || this._closed) {
               return -1;
            }

            try {
               this.this$0._lock.wait();
            } catch (InterruptedException var11) {
            }
         }

         int bytesRead = Math.min(length, this.this$0._dataLength);
         int len1 = Math.min(bytesRead, this.this$0._buffer.length - this.this$0._readOffset);
         if (!this._skipping) {
            this.arrayCopy(this.this$0._buffer, this.this$0._readOffset, buffer, start, len1);
         }

         HTTPBufferingManager.access$512(this.this$0, len1);
         if (len1 < bytesRead) {
            start += len1;
            this.this$0._readOffset = 0;
            len1 = bytesRead - len1;
            if (!this._skipping) {
               this.arrayCopy(this.this$0._buffer, this.this$0._readOffset, buffer, start, len1);
            }

            HTTPBufferingManager.access$512(this.this$0, len1);
         }

         if (this.this$0._readOffset == this.this$0._buffer.length) {
            this.this$0._readOffset = 0;
         }

         synchronized (this.this$0._lock) {
            HTTPBufferingManager.access$620(this.this$0, bytesRead);
            this.this$0._lock.notifyAll();
         }

         return bytesRead;
      }
   }

   private void arrayCopy(byte[] src, int srcIndex, byte[] dest, int destIndex, int length) {
      if (length == 1) {
         dest[destIndex] = src[srcIndex];
      } else {
         System.arraycopy(src, srcIndex, dest, destIndex, length);
      }
   }

   @Override
   public void close() {
      this._closed = true;
      synchronized (this.this$0._lock) {
         this.this$0._lock.notifyAll();
      }
   }
}
