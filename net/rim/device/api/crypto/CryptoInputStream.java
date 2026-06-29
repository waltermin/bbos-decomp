package net.rim.device.api.crypto;

import java.io.InputStream;

public class CryptoInputStream extends InputStream {
   protected InputStream _inputStream;
   private byte[] _buffer;

   protected CryptoInputStream(InputStream in) {
      if (in == null) {
         throw new Object();
      }

      this._inputStream = in;
   }

   public String getAlgorithm() {
      throw null;
   }

   @Override
   public int read() {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      return this.read(this._buffer, 0, 1) < 0 ? -1 : this._buffer[0] & 0xFF;
   }

   @Override
   public int read(byte[] buffer) {
      return this.read(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public int read(byte[] _1, int _2, int _3) {
      throw null;
   }

   @Override
   public int available() {
      return this._inputStream.available();
   }

   @Override
   public void close() {
      this._inputStream.close();
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   public InputStream getInputStream() {
      return this._inputStream;
   }
}
