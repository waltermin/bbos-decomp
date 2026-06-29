package net.rim.device.api.crypto;

import java.io.OutputStream;

public final class PRNGEncryptor extends StreamEncryptor {
   private PseudoRandomSource _keystream;

   public PRNGEncryptor(PseudoRandomSource keystream, OutputStream output) {
      super(output);
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
   protected final void encrypt(byte[] plaintext, int plaintextOffset, int plaintextLength, byte[] ciphertext) {
      if (plaintext != null
         && plaintextOffset >= 0
         && plaintextLength >= 0
         && plaintext.length - plaintextLength >= plaintextOffset
         && ciphertext != null
         && plaintextLength <= ciphertext.length) {
         System.arraycopy(plaintext, plaintextOffset, ciphertext, 0, plaintextLength);
         this._keystream.xorBytes(ciphertext, 0, plaintextLength);
      } else {
         throw new Object();
      }
   }
}
