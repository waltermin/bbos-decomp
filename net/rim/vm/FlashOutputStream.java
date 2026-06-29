package net.rim.vm;

import java.io.OutputStream;

public class FlashOutputStream extends OutputStream {
   private int _bufferSize;
   private byte[] _buffer;
   private int _headerSize;
   private int _index;
   private int _writeIndex;
   private long _guid;
   private boolean _startUserBlock;

   private static native int headerSize();

   private static native int bufferSize();

   private static native void writeBuffer(byte[] var0, int var1, int var2, long var3, boolean var5, boolean var6);

   public FlashOutputStream(long guid) {
      this(guid, false);
   }

   public FlashOutputStream(long guid, boolean append) {
      FlashInputStream in = new FlashInputStream(guid);
      if (append) {
         this._writeIndex = in.getNextWriteIndex();
         in.close();
      } else {
         in.erase();
      }

      this._headerSize = headerSize();
      this._bufferSize = bufferSize();
      this._buffer = new byte[this._bufferSize];
      this._guid = guid;
      this._index = this._headerSize;
      this._startUserBlock = true;
   }

   @Override
   public void write(int b) {
      if (this._index >= this._bufferSize) {
         this.internalFlush(false);
      }

      this._buffer[this._index++] = (byte)b;
   }

   @Override
   public void flush() {
      this.internalFlush(true);
      this._startUserBlock = true;
   }

   private void internalFlush(boolean endUserBlock) {
      if (this._index > this._headerSize) {
         writeBuffer(this._buffer, this._index, this._writeIndex, this._guid, this._startUserBlock, endUserBlock);
         this._startUserBlock = false;
         this._index = this._headerSize;
         this._writeIndex++;
      }
   }

   @Override
   public void close() {
      this.flush();
   }
}
