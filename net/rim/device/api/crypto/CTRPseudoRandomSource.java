package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class CTRPseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private byte[] _outputFeedback;
   private byte[] _counter;
   private int _outputFeedbackOffset;
   private int _blockLength;
   private SymmetricKeyEncryptorEngine _engine;

   public CTRPseudoRandomSource(SymmetricKeyEncryptorEngine engine, long iv) {
      if (engine == null) {
         throw new Object();
      }

      this._engine = engine;
      this._blockLength = engine.getBlockLength();
      this._outputFeedback = new byte[this._blockLength];
      this._counter = this.longToBlock(engine, iv);
      this._outputFeedbackOffset = 0;
      this._engine.encrypt(this._counter, 0, this._outputFeedback, 0);
   }

   private final byte[] longToBlock(SymmetricKeyEncryptorEngine engine, long iv) {
      if (engine == null) {
         throw new Object();
      }

      byte[] counter = new byte[engine.getBlockLength()];
      int index = counter.length - 1;

      while (iv != 0 && index >= 0) {
         counter[index--] = (byte)iv;
         iv >>= 8;
      }

      return counter;
   }

   public CTRPseudoRandomSource(SymmetricKeyEncryptorEngine engine, InitializationVector iv) {
      if (engine != null && iv != null) {
         this._engine = engine;
         this._blockLength = engine.getBlockLength();
         if (iv.getLength() != this._blockLength) {
            throw new Object();
         }

         this._outputFeedback = new byte[this._blockLength];
         this._counter = iv.getData();
         this._outputFeedbackOffset = 0;
         this._engine.encrypt(this._counter, 0, this._outputFeedback, 0);
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._engine.getAlgorithm()).append("/CTR").toString();
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._outputFeedbackOffset == this._blockLength) {
               CryptoByteArrayArithmetic.increment(this._counter, this._counter.length << 3, this._counter);
               this._engine.encrypt(this._counter, 0, this._outputFeedback, 0);
               this._outputFeedbackOffset = 0;
            }

            int xorLength = Math.min(length, this._blockLength - this._outputFeedbackOffset);
            length -= xorLength;

            while (--xorLength >= 0) {
               buffer[offset++] ^= this._outputFeedback[this._outputFeedbackOffset++];
            }
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   public static final void selfTest() {
      byte[] CIPHER_TEXT = new byte[]{70, 64, -100, -125, -95, 126, -117, 42};
      byte[] IV = new byte[]{4, -1, -36, -60, -31, 53, -55, 95};
      int length = SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT.length;

      try {
         CTRPseudoRandomSource source = new CTRPseudoRandomSource((SymmetricKeyEncryptorEngine)(new Object()), (InitializationVector)(new Object(IV)));
         byte[] cipherText = Arrays.copy(SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, length);
         source.xorBytes(cipherText, 0, length);
         if (Arrays.equals(cipherText, 0, CIPHER_TEXT, 0, length)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_PRS_CTR = -2109673065881336640L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_PRS_CTR) == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_CTR, appRegistry);
      }
   }
}
