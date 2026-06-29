package net.rim.device.api.crypto;

import java.io.OutputStream;

public class SignatureSignerOutputStream extends CryptoOutputStream {
   protected SignatureSigner _signer;
   protected boolean _on = true;

   public SignatureSignerOutputStream(SignatureSigner signer, OutputStream out) {
      super(out == null ? new CryptoDummyOutputStream() : out);
      if (signer == null) {
         throw new Object();
      }

      this._signer = signer;
   }

   @Override
   public String getAlgorithm() {
      return this._signer.getAlgorithm();
   }

   public void on(boolean on) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public void write(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && offset < buffer.length && offset <= buffer.length - length) {
         if (this._on) {
            this._signer.update(buffer, offset, length);
         }

         super._out.write(buffer, offset, length);
      } else {
         throw new Object();
      }
   }

   public SignatureSigner getSignatureSigner() {
      return this._signer;
   }
}
