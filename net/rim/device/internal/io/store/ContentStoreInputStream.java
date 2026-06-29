package net.rim.device.internal.io.store;

import java.io.InputStream;

final class ContentStoreInputStream extends InputStream {
   private ContentStoreConnection _connection;
   private InputStream _in;
   private boolean _closed;

   public ContentStoreInputStream(ContentStoreConnection connection, InputStream sub) {
      this._in = sub;
      this._connection = connection;
   }

   @Override
   public final int read() {
      if (this._closed) {
         throw new Object(1000);
      } else {
         return this._in.read();
      }
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      if (this._closed) {
         throw new Object(1000);
      } else {
         return this._in.read(b, off, len);
      }
   }

   @Override
   public final long skip(long n) {
      if (this._closed) {
         throw new Object(1000);
      } else {
         return this._in.skip(n);
      }
   }

   @Override
   public final int available() {
      if (this._closed) {
         throw new Object(1000);
      } else {
         return this._in.available();
      }
   }

   @Override
   public final synchronized void mark(int readlimit) {
      this._in.mark(readlimit);
   }

   @Override
   public final synchronized void reset() {
      if (this._closed) {
         throw new Object(1000);
      }

      this._in.reset();
   }

   @Override
   public final boolean markSupported() {
      return this._in.markSupported();
   }

   @Override
   public final void close() {
      if (!this._closed) {
         this._closed = true;
         this._connection.inputClosed();
         this._in.close();
      }
   }
}
