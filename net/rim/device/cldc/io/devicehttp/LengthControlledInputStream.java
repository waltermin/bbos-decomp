package net.rim.device.cldc.io.devicehttp;

import java.io.InputStream;

public final class LengthControlledInputStream extends InputStream {
   private int _length;
   private InputStream _in;
   private boolean _closeOnLastRead;
   private boolean _closed;
   private boolean _closeUnderlying;

   public LengthControlledInputStream(InputStream in, int length, boolean closeOnLastRead, boolean closeUnderlying) {
      this._in = in;
      this._length = length;
      this._closeOnLastRead = closeOnLastRead;
      this._closeUnderlying = closeUnderlying;
   }

   @Override
   public final synchronized int read() {
      if (this._closed) {
         throw new Object();
      }

      if (this._length <= 0) {
         return -1;
      }

      this._length--;
      int read = this._in.read();
      if (this._length <= 0 && this._closeOnLastRead && this._closeUnderlying) {
         this._in.close();
         this._closeUnderlying = false;
      }

      return read;
   }

   @Override
   public final int read(byte[] b, int offset, int length) {
      if (this._closed) {
         throw new Object();
      }

      if (this._length <= 0) {
         return -1;
      }

      int numRead = this._in.read(b, offset, Math.min(length, this._length));
      this._length -= numRead;
      if (this._closeOnLastRead && this._closeUnderlying && this._length <= 0) {
         this._in.close();
         this._closeUnderlying = false;
      }

      return numRead;
   }

   public final int getLength() {
      return this._length;
   }

   @Override
   public final int available() {
      return !this._closed && this._length <= 0 ? this._length : Math.min(this._in.available(), this._length);
   }

   @Override
   public final void close() {
      this._closed = true;
      if (this._closeUnderlying) {
         this._in.close();
      }
   }
}
