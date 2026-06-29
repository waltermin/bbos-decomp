package net.rim.device.apps.internal.browser.util;

import java.io.InputStream;

public final class CountedInputStream extends InputStream {
   private InputStream _inputStream;
   private CountedInputStream _compressedInputStream;
   private int _bytesRead;

   public CountedInputStream(InputStream inputStream) {
      this(inputStream, null);
   }

   public CountedInputStream(InputStream inputStream, CountedInputStream compressedInputStream) {
      if (inputStream == null) {
         throw new IllegalArgumentException();
      }

      this._inputStream = inputStream;
      this._compressedInputStream = compressedInputStream;
   }

   @Override
   public final int read() {
      int b = this._inputStream.read();
      if (b >= 0) {
         this._bytesRead++;
      }

      return b;
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      int newBytesRead = this._inputStream.read(buffer, offset, length);
      if (newBytesRead > 0) {
         this._bytesRead += newBytesRead;
      }

      return newBytesRead;
   }

   @Override
   public final int available() {
      return this._inputStream.available();
   }

   @Override
   public final void mark(int readlimit) {
      this._inputStream.mark(readlimit);
   }

   @Override
   public final boolean markSupported() {
      return this._inputStream.markSupported();
   }

   @Override
   public final void reset() {
      this._inputStream.reset();
   }

   @Override
   public final long skip(long n) {
      return this._inputStream.skip(n);
   }

   @Override
   public final void close() {
      this._inputStream.close();
   }

   public final int getBytesRead() {
      return this._bytesRead;
   }

   public final int getCompressedBytesRead() {
      return this._compressedInputStream != null ? this._compressedInputStream.getCompressedBytesRead() : this._bytesRead;
   }
}
