package net.rim.device.api.crypto;

import java.io.InputStream;

public class SignatureVerifierInputStream extends CryptoInputStream {
   protected SignatureVerifier _verifier;
   protected boolean _on = true;

   public SignatureVerifierInputStream(SignatureVerifier verifier, InputStream inputStream) {
      super(inputStream);
      if (verifier == null) {
         throw new Object();
      }

      this._verifier = verifier;
   }

   @Override
   public String getAlgorithm() {
      return this._verifier.getAlgorithm();
   }

   public void on(boolean on) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public int read(byte[] data, int offset, int length) {
      int bytesRead = super._inputStream.read(data, offset, length);
      if (this._on && bytesRead > 0) {
         this._verifier.update(data, offset, bytesRead);
      }

      return bytesRead;
   }

   public SignatureVerifier getSignatureVerifier() {
      return this._verifier;
   }
}
