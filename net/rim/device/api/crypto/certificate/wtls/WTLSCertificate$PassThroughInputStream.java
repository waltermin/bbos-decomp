package net.rim.device.api.crypto.certificate.wtls;

import java.io.InputStream;
import java.io.OutputStream;

class WTLSCertificate$PassThroughInputStream extends InputStream {
   InputStream _in;
   OutputStream _out;

   public WTLSCertificate$PassThroughInputStream(InputStream in, OutputStream out) {
      this._in = in;
      this._out = out;
   }

   @Override
   public int read() {
      int data = this._in.read();
      if (data != -1) {
         this._out.write(data);
      }

      return data;
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         int len = this._in.read(buffer, offset, length);
         if (len != -1) {
            this._out.write(buffer, offset, len);
         }

         return len;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
