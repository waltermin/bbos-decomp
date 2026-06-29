package net.rim.device.internal.compress;

import java.io.InputStream;
import net.rim.device.api.io.IOCancelledException;

public final class YKInputStream extends InputStream {
   private InputStream _inputStream;
   private byte[] _currentChunk;
   private int _currentOffset;
   private byte[] _inputBuffer;
   private YKDecode _decode;
   private boolean _isClosed;

   public YKInputStream(InputStream inputStream) {
      this(inputStream, 4096);
   }

   public YKInputStream(InputStream inputStream, int inputBufferSize) {
      if (inputStream != null && inputBufferSize >= 1024) {
         this._inputStream = inputStream;
         this._inputBuffer = new byte[inputBufferSize];
         this._decode = new YKDecode(false);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final synchronized int read() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      if (this._currentChunk == null || this._currentOffset >= this._currentChunk.length) {
         do {
            if (!this.readNextChunk()) {
               return -1;
            }
         } while (this._currentChunk == null || this._currentChunk.length <= 0);
      }

      return this._currentChunk[this._currentOffset++] & 0xFF;
   }

   @Override
   public final synchronized int read(byte[] buffer, int bufferOffset, int bufferLength) throws IOCancelledException {
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

   public final synchronized byte[] saveContextMap() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._decode.getContextMap();
      }
   }

   public final synchronized void loadContextMap(byte[] contextMap) throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      this._decode.loadContextMap(contextMap);
   }

   public final synchronized void loadSideData(InputStream sideDataStream) {
      byte[] buffer = new byte[4096];

      for (int count = sideDataStream.read(buffer); count >= 0; count = sideDataStream.read(buffer)) {
         if (count > 0) {
            this._decode.yk_load_side_data(buffer, 0, count);
         }
      }
   }

   @Override
   public final synchronized int available() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._currentChunk != null ? this._currentChunk.length - this._currentOffset : 0;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this._inputStream.close();
         var3 = false;
      } finally {
         if (var3) {
            this._decode.yk_uninit();
            this._isClosed = true;
         }
      }

      this._decode.yk_uninit();
      this._isClosed = true;
   }

   private final boolean readNextChunk() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      int numRead = this._inputStream.read(this._inputBuffer);
      if (numRead < 0) {
         return false;
      }

      this._currentChunk = this._decode.yk_decode(this._inputBuffer, 0, numRead);
      this._currentOffset = 0;
      return true;
   }
}
