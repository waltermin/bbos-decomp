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
      return (InitializationVector)(new Object(this._postWhitening));
   }

   public final InitializationVector getPreWhitening() {
      return (InitializationVector)(new Object(this._preWhitening));
   }

   public final void setWhiteningVectors(InitializationVector preWhitening, InitializationVector postWhitening) {
      if (preWhitening != null && postWhitening != null && preWhitening.getLength() == this._blockLength && postWhitening.getLength() == this._blockLength) {
         this._preWhitening = preWhitening.getData();
         this._postWhitening = postWhitening.getData();
      } else {
         throw new Object();
      }
   }

   @Override
   public final int getBlockLength() {
      return this._blockLength;
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._engine.getAlgorithm()).append("/X").toString();
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
         throw new Object();
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
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   public static final void selfTest() {
      byte[] CIPHER_TEXT = new byte[]{76, -12, 93, 126, -74, 35, 69, 23};
      byte[] PRE_IV = new byte[]{4, -1, -36, -60, -31, 53, -55, 95};
      byte[] POST_IV = new byte[]{4, 52, -1, -3, 17, 85, -44, 13};
      int blockLength = 8;
      byte[] target = new byte[blockLength];

      try {
         InitializationVector preIv = (InitializationVector)(new Object(PRE_IV));
         InitializationVector postIv = (InitializationVector)(new Object(POST_IV));
         XDecryptorEngine decryptorEngine = new XDecryptorEngine((BlockDecryptorEngine)(new Object()), preIv, postIv);
         decryptorEngine.decrypt(CIPHER_TEXT, 0, target, 0);
         if (Arrays.equals(target, 0, SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, blockLength)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
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
