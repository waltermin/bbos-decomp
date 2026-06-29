package net.rim.vm;

import java.io.IOException;
import java.io.InputStream;

public class FlashInputStream extends InputStream {
   private Object _stream;
   private byte[] _buffer;
   private int _totalSize;
   private int _amountRead;
   private int _bytesInBuffer;
   private int _index;
   private int _handleIndex;

   private static native Object open(long var0);

   private static native int bufferSize();

   private static native int readBuffer(Object var0, byte[] var1, int var2);

   private static native int totalSize(Object var0);

   private static native int getNextWriteIndex(Object var0);

   private static native int getNumHandles(Object var0);

   private static native boolean purge(Object var0, int var1);

   private static native void erase(Object var0);

   public FlashInputStream(long guid) {
      int bufferSize = bufferSize();
      this._buffer = new byte[bufferSize];
      this._stream = open(guid);
      this._totalSize = totalSize(this._stream);
      this._handleIndex = -1;
   }

   @Override
   public int read() {
      if (this._index == this._bytesInBuffer) {
         int bytesRead = readBuffer(this._stream, this._buffer, this._handleIndex + 1);
         if (bytesRead == 0) {
            return -1;
         }

         this._bytesInBuffer = bytesRead;
         this._handleIndex++;
         this._index = 0;
      }

      this._amountRead++;
      return this._buffer[this._index++] & 0xFF;
   }

   int getNumHandles() {
      return getNumHandles(this._stream);
   }

   int getNextWriteIndex() {
      return getNextWriteIndex(this._stream);
   }

   public void erase() {
      erase(this._stream);
   }

   public void purge() {
      if (this._index != this._bytesInBuffer || !purge(this._stream, this._handleIndex + 1)) {
         throw new IOException("not at flush point");
      }
   }

   @Override
   public int available() {
      return this._totalSize - this._amountRead;
   }

   @Override
   public void close() {
   }

   public static boolean exists(long guid) {
      FlashInputStream fis = new FlashInputStream(guid);

      try {
         return fis.available() != 0;
      } catch (Throwable e) {
         return false;
      }
   }

   public static void erase(long guid) {
      FlashInputStream fis = new FlashInputStream(guid);
      fis.erase();
      fis.close();
   }
}
