package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class XDecryptorEngine implements BlockDecryptorEngine {
   private BlockDecryptorEngine _engine;
   private int _blockLength;
   private byte[] _preWhitening;
   private byte[] _postWhitening;
   private byte[] _buffer;

   public final InitializationVector getPostWhitening() {
      return new InitializationVector(this._postWhitening);
   }

   public final InitializationVector getPreWhitening() {
      return new InitializationVector(this._preWhitening);
   }

   public final void setWhiteningVectors(InitializationVector preWhitening, InitializationVector postWhitening) {
      if (preWhitening != null && postWhitening != null && preWhitening.getLength() == this._blockLength && postWhitening.getLength() == this._blockLength) {
         this._preWhitening = preWhitening.getData();
         this._postWhitening = postWhitening.getData();
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getBlockLength() {
      return this._blockLength;
   }

   @Override
   public final String getAlgorithm() {
      return this._engine.getAlgorithm() + "/X";
   }

   @Override
   public final void decrypt(byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (plaintext != null
         && plaintext.length - this._blockLength >= plaintextOffset
         && plaintextOffset >= 0
         && ciphertext != null
         && ciphertext.length - this._blockLength >= ciphertextOffset
         && ciphertextOffset >= 0) {
         for (int i = 0; i < this._blockLength; i++) {
            this._buffer[i] = (byte)(ciphertext[ciphertextOffset + i] ^ this._postWhitening[i]);
         }

         this._engine.decrypt(this._buffer, 0, plaintext, plaintextOffset);

         for (int i = 0; i < this._blockLength; i++) {
            plaintext[plaintextOffset + i] = (byte)(plaintext[plaintextOffset + i] ^ this._preWhitening[i]);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public XDecryptorEngine(BlockDecryptorEngine engine, InitializationVector preWhitening, InitializationVector postWhitening) {
      if (engine != null && preWhitening != null && postWhitening != null) {
         this._engine = engine;
         this._blockLength = engine.getBlockLength();
         if (preWhitening.getLength() == this._blockLength && postWhitening.getLength() == this._blockLength) {
            this._preWhitening = preWhitening.getData();
            this._postWhitening = postWhitening.getData();
            this._buffer = new byte[this._blockLength];
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final void selfTest() {
      byte[] CIPHER_TEXT = new byte[]{76, -12, 93, 126, -74, 35, 69, 23};
      byte[] PRE_IV = new byte[]{4, -1, -36, -60, -31, 53, -55, 95};
      byte[] POST_IV = new byte[]{4, 52, -1, -3, 17, 85, -44, 13};
      int blockLength = 8;
      byte[] target = new byte[blockLength];

      try {
         InitializationVector preIv = new InitializationVector(PRE_IV);
         InitializationVector postIv = new InitializationVector(POST_IV);
         XDecryptorEngine decryptorEngine = new XDecryptorEngine(new TestEngine(), preIv, postIv);
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
      long ID_TEST_DECRYPTOR_X = 1668093231383062277L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DECRYPTOR_X) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DECRYPTOR_X, appRegistry);
      }
   }
}
