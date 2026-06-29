package net.rim.device.api.io;

import java.io.OutputStream;

class SharedOutputStreamPart extends OutputStream {
   private int _index;
   private SharedOutputStream _sink;
   private byte[] _buffer;
   private NoCopyByteArrayOutputStream _stream;
   private boolean _writable;

   public SharedOutputStreamPart(SharedOutputStream sink, int index) {
      this._index = index;
      this._sink = sink;
      this._buffer = null;
      this._stream = new NoCopyByteArrayOutputStream();
      this._writable = true;
   }

   @Override
   public void write(int data) {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      this._buffer[0] = (byte)data;
      this.write(this._buffer, 0, 1);
   }

   @Override
   public void write(byte[] data) {
      this.write(data, 0, data.length);
   }

   @Override
   public void write(byte[] data, int offset, int length) {
      if (offset >= 0 && length >= 0 && data.length - length >= offset) {
         this._sink.write(this._index, data, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void flush() {
      this._sink.flush(this._index);
   }

   @Override
   public void close() {
      this._sink.close(this._index);
   }

   public NoCopyByteArrayOutputStream getStream() {
      return this._stream;
   }

   public void setStream(NoCopyByteArrayOutputStream stream) {
      this._stream = stream;
   }

   public boolean isWritable() {
      return this._writable;
   }

   public void setIsWritable(boolean writable) {
      this._writable = writable;
   }
}
