package net.rim.device.api.crypto;

import java.io.OutputStream;

public class CryptoOutputStream extends OutputStream {
   protected OutputStream _out;
   private byte[] _buffer;

   public CryptoOutputStream(OutputStream out) {
      if (out == null) {
         throw new IllegalArgumentException();
      }

      this._out = out;
   }

   public String getAlgorithm() {
      throw null;
   }

   public OutputStream getOutputStream() {
      return this._out;
   }

   @Override
   public void write(int datum) {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      this._buffer[0] = (byte)datum;
      this.write(this._buffer, 0, 1);
   }

   @Override
   public void write(byte[] buffer) {
      this.write(buffer, 0, buffer.length);
   }

   @Override
   public void write(byte[] _1, int _2, int _3) {
      throw null;
   }

   @Override
   public void flush() {
      this._out.flush();
   }

   @Override
   public void close() {
      this.flush();
      this._out.close();
   }
}
