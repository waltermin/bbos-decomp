package net.rim.device.internal.io.file;

import java.io.OutputStream;

final class CounterOutputStream extends OutputStream {
   private long _offset;
   private OutputStream _out;

   public CounterOutputStream(OutputStream out) {
      this(out, 0);
   }

   public CounterOutputStream(OutputStream out, long initialOffset) {
      this._out = out;
      this._offset = initialOffset;
   }

   @Override
   public final void write(int value) {
      this._out.write(value);
      this._offset += 1;
   }

   @Override
   public final void write(byte[] b, int off, int len) {
      this._out.write(b, off, len);
      this._offset += len;
   }

   @Override
   public final void close() {
      this._out.close();
   }

   public final long getOffset() {
      return this._offset;
   }
}
