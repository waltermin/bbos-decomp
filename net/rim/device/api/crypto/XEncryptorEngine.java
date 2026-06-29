package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class XEncryptorEngine implements BlockEncryptorEngine {
   private BlockEncryptorEngine _engine;
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
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (plaintext != null
         && plaintext.length - this._blockLength >= plaintextOffset
         && plaintextOffset >= 0
         && ciphertext != null
         && ciphertext.length - this._blockLength >= ciphertextOffset
         && ciphertextOffset >= 0) {
         for (int i = 0; i < this._blockLength; i++) {
            this._buffer[i] = (byte)(plaintext[plaintextOffset + i] ^ this._preWhitening[i]);
         }

         this._engine.encrypt(this._buffer, 0, ciphertext, ciphertextOffset);

         for (int i = 0; i < this._blockLength; i++) {
            ciphertext[ciphertextOffset + i] = (byte)(ciphertext[ciphertextOffset + i] ^ this._postWhitening[i]);
         }
      } else {
         throw new Object();
      }
   }

   public XEncryptorEngine(BlockEncryptorEngine engine) {
      this(engine, null, null);
   }

   public XEncryptorEngine(BlockEncryptorEngine engine, InitializationVector preWhitening, InitializationVector postWhitening) {
      if (engine == null) {
         throw new Object();
      }

      this._engine = engine;
      this._blockLength = engine.getBlockLength();
      if (preWhitening == null) {
         preWhitening = (InitializationVector)(new Object(this._blockLength));
      } else if (preWhitening.getLength() != this._blockLength) {
         throw new Object();
      }

      this._preWhitening = preWhitening.getData();
      if (postWhitening == null) {
         postWhitening = (InitializationVector)(new Object(this._blockLength));
      } else if (postWhitening.getLength() != this._blockLength) {
         throw new Object();
      }

      this._postWhitening = postWhitening.getData();
      this._buffer = new byte[this._blockLength];
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
         XEncryptorEngine encryptorEngine = new XEncryptorEngine((BlockEncryptorEngine)(new Object()), preIv, postIv);
         encryptorEngine.encrypt(SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, target, 0);
         if (Arrays.equals(target, 0, CIPHER_TEXT, 0, blockLength)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_ENCRYPTOR_X = -696213289464992669L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_ENCRYPTOR_X) == null) {
         selfTest();
         appRegistry.put(ID_TEST_ENCRYPTOR_X, appRegistry);
      }
   }
}
