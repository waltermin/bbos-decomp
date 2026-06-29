package net.rim.device.api.crypto;

import java.io.InputStream;

public class DigestInputStream extends CryptoInputStream {
   protected Digest _digest;
   protected boolean _on = true;

   public DigestInputStream(Digest digest, InputStream inputStream) {
      super(inputStream);
      if (digest == null) {
         throw new Object();
      }

      this._digest = digest;
   }

   @Override
   public String getAlgorithm() {
      return this._digest.getAlgorithm();
   }

   public void on(boolean on) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         int bytesRead = super._inputStream.read(buffer, offset, length);
         if (this._on && bytesRead > 0) {
            this._digest.update(buffer, offset, bytesRead);
         }

         return bytesRead;
      } else {
         throw new Object();
      }
   }

   public Digest getDigest() {
      return this._digest;
   }
}
