package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class CBCDecryptorEngine implements BlockDecryptorEngine {
   private BlockDecryptorEngine _engine;
   private int _blockLength;
   private InitializationVector _iv;
   private byte[] _ciphertextBuffer;

   public final InitializationVector getIV() {
      return this._iv;
   }

   public final void setIV(InitializationVector iv) {
      if (iv == null) {
         iv = new InitializationVector(this._blockLength);
      } else if (iv.getLength() != this._blockLength) {
         throw new IllegalArgumentException();
      }

      this._iv = iv;
      this._ciphertextBuffer = iv.getData();
   }

   @Override
   public final int getBlockLength() {
      return this._blockLength;
   }

   @Override
   public final String getAlgorithm() {
      return this._engine.getAlgorithm() + "/CBC";
   }

   @Override
   public final void decrypt(byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (ciphertext != null
         && ciphertextOffset >= 0
         && ciphertext.length - this._blockLength >= ciphertextOffset
         && plaintext != null
         && plaintextOffset >= 0
         && plaintext.length - this._blockLength >= plaintextOffset) {
         this._engine.decrypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);

         for (int i = 0; i < this._blockLength; i++) {
            plaintext[plaintextOffset + i] = (byte)(plaintext[plaintextOffset + i] ^ this._ciphertextBuffer[i]);
         }

         System.arraycopy(ciphertext, ciphertextOffset, this._ciphertextBuffer, 0, this._blockLength);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public CBCDecryptorEngine(BlockDecryptorEngine engine, InitializationVector iv) {
      if (engine == null) {
         throw new IllegalArgumentException();
      }

      this._engine = engine;
      this._blockLength = engine.getBlockLength();
      this.setIV(iv);
   }

   public static final void selfTest() {
      byte[] CIPHER_TEXT = new byte[]{72, -64, -94, -125, -89, 118, -111, 26};
      byte[] IV = new byte[]{4, -1, -36, -60, -31, 53, -55, 95};
      int blockLength = 8;
      byte[] target = new byte[blockLength];

      try {
         InitializationVector iv = new InitializationVector(IV);
         CBCDecryptorEngine decryptorEngine = new CBCDecryptorEngine(new TestEngine(), iv);
         decryptorEngine.decrypt(CIPHER_TEXT, 0, target, 0);
         if (Arrays.equals(target, 0, SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, blockLength)) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_DECRYPTOR_CBC = 2803174765563958834L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DECRYPTOR_CBC) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DECRYPTOR_CBC, appRegistry);
      }
   }
}
