package net.rim.device.api.crypto;

import java.io.InputStream;

public final class PRNGDecryptor extends StreamDecryptor {
   private PseudoRandomSource _keystream;

   public PRNGDecryptor(PseudoRandomSource keystream, InputStream input) {
      super(input);
      if (keystream == null) {
         throw new Object();
      }

      this._keystream = keystream;
   }

   @Override
   public final String getAlgorithm() {
      return this._keystream.getAlgorithm();
   }

   @Override
   protected final void decrypt(byte[] data, int offset, int length) {
      this._keystream.xorBytes(data, offset, length);
   }
}
