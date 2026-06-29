package net.rim.tools.compiler.io;

import java.io.InputStream;
import net.rim.device.api.crypto.Digest;

public class DigestInputStream extends InputStream {
   private Digest _digest;
   private InputStream _inputStream;
   private int _length;

   public DigestInputStream(Digest digest, InputStream inputStream) {
      if (digest != null && inputStream != null) {
         this._digest = digest;
         this._inputStream = inputStream;
      } else {
         throw new Object();
      }
   }

   @Override
   public int read() {
      int b = this._inputStream.read();
      if (b != -1) {
         this._length++;
         this._digest.update(b);
      }

      return b;
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         int bytesRead = this._inputStream.read(buffer, offset, length);
         if (bytesRead > 0) {
            this._length += bytesRead;
            this._digest.update(buffer, offset, bytesRead);
         }

         return bytesRead;
      } else {
         throw new Object();
      }
   }

   public int getLength() {
      return this._length;
   }

   public Digest getDigest() {
      return this._digest;
   }
}
