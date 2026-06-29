package net.rim.device.api.crypto;

import java.io.OutputStream;

public class DigestOutputStream extends CryptoOutputStream {
   protected Digest _digest;
   protected boolean _on = true;

   public DigestOutputStream(Digest digest, OutputStream out) {
      super(out == null ? new CryptoDummyOutputStream() : out);
      if (digest == null) {
         throw new IllegalArgumentException();
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
   public void write(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         if (this._on) {
            this._digest.update(buffer, offset, length);
         }

         super._out.write(buffer, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Digest getDigest() {
      return this._digest;
   }
}
