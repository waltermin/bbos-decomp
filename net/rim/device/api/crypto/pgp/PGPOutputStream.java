package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;
import net.rim.device.api.io.SharedOutputStream;

public class PGPOutputStream extends OutputStream {
   private byte[] _buffer;
   protected SharedOutputStream _out;
   protected PGPOutputStream _pgpOut;
   protected int _tagFormat;
   public static final int OLD_FORMAT = 3;
   public static final int NEW_FORMAT = 4;

   protected PGPOutputStream(OutputStream out, int tagFormat) {
      if (out != null && (tagFormat == 3 || tagFormat == 4)) {
         this._out = new SharedOutputStream(out);
         this._tagFormat = tagFormat;
         if (out instanceof PGPOutputStream) {
            this._pgpOut = (PGPOutputStream)out;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void write(byte[] _1, int _2, int _3) {
      throw null;
   }

   void update(byte[] _1, int _2, int _3) {
      throw null;
   }

   @Override
   public void write(int b) {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      this._buffer[0] = (byte)b;
      this.write(this._buffer, 0, 1);
   }

   @Override
   public void write(byte[] b) {
      this.write(b, 0, b == null ? 0 : b.length);
   }

   @Override
   public void flush() {
   }

   @Override
   public void close() {
      this._out.close();
   }
}
