package net.rim.device.internal.io.file;

import java.io.InputStream;

final class CounterInputStream extends InputStream {
   private long _offset;
   private InputStream _in;

   public CounterInputStream(InputStream in) {
      this._in = in;
   }

   @Override
   public final int read() {
      int result = this._in.read();
      if (result != -1) {
         this._offset += 1;
      }

      return result;
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      int result = this._in.read(b, off, len);
      if (result != -1) {
         this._offset += result;
      }

      return result;
   }

   @Override
   public final long skip(long n) {
      long result = this._in.skip(n);
      this._offset += result;
      return result;
   }

   @Override
   public final void close() {
      this._in.close();
   }

   public final long getOffset() {
      return this._offset;
   }
}
