package net.rim.device.api.compress;

import java.io.InputStream;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.internal.compress.Inflater;

public class GZIPInputStream extends InputStream {
   private InputStream _inputStream;
   private byte[] _currentChunk;
   private int _currentOffset;
   private byte[] _tempBuffer;
   private Inflater _inflater;
   private boolean _isClosed;

   public GZIPInputStream(InputStream inputStream) {
      this(inputStream, 5120);
   }

   public GZIPInputStream(InputStream inputStream, int workingBufferSize) {
      if (inputStream != null && workingBufferSize >= 1024) {
         this._inputStream = inputStream;
         this._tempBuffer = new byte[workingBufferSize];
         this._inflater = new Inflater(31);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public synchronized int read() {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      if (this._currentChunk == null || this._currentOffset >= this._currentChunk.length) {
         do {
            if (!this.readNextChunk()) {
               return -1;
            }
         } while (this._currentChunk.length <= 0);
      }

      return this._currentChunk[this._currentOffset++] & 0xFF;
   }

   @Override
   public synchronized int read(byte[] buffer, int bufferOffset, int bufferLength) {
      if (buffer == null) {
         throw new NullPointerException();
      }

      if (bufferOffset >= 0 && bufferLength >= 0 && bufferOffset + bufferLength <= buffer.length) {
         if (this._isClosed) {
            throw new IOCancelledException();
         }

         if (bufferLength == 0) {
            return 0;
         }

         int numRead = 0;

         while (bufferLength > 0) {
            if (this._currentChunk != null && this._currentOffset < this._currentChunk.length) {
               int numToWrite = Math.min(this._currentChunk.length - this._currentOffset, bufferLength);
               System.arraycopy(this._currentChunk, this._currentOffset, buffer, bufferOffset, numToWrite);
               this._currentOffset += numToWrite;
               bufferOffset += numToWrite;
               numRead += numToWrite;
               bufferLength -= numToWrite;
            } else if (!this.readNextChunk()) {
               if (numRead > 0) {
                  return numRead;
               }

               return -1;
            }
         }

         return numRead > 0 ? numRead : -1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public synchronized int available() {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._currentChunk != null ? this._currentChunk.length - this._currentOffset : 0;
      }
   }

   @Override
   public void close() {
      this._inputStream.close();
      this._isClosed = true;
   }

   private boolean readNextChunk() {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      int numRead = this._inputStream.read(this._tempBuffer);
      if (numRead < 0) {
         return false;
      }

      this._currentChunk = this._inflater.decompress(this._tempBuffer, 0, numRead);
      this._currentOffset = 0;
      return true;
   }
}
