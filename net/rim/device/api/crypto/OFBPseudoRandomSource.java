package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class OFBPseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private byte[] _outputFeedback;
   private byte[] _outputCopy;
   private int _outputFeedbackOffset;
   private int _blockLength;
   private SymmetricKeyEncryptorEngine _engine;

   public OFBPseudoRandomSource(SymmetricKeyEncryptorEngine engine, InitializationVector iv) {
      if (engine != null && iv != null) {
         this._engine = engine;
         this._blockLength = engine.getBlockLength();
         if (iv.getLength() != this._blockLength) {
            throw new Object();
         }

         this._outputFeedback = new byte[this._blockLength];
         this._outputCopy = iv.getData();
         this._outputFeedbackOffset = 0;
         this._engine.encrypt(this._outputCopy, 0, this._outputFeedback, 0);
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._engine.getAlgorithm()).append("/OFB").toString();
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._outputFeedbackOffset == this._blockLength) {
               this._engine.encrypt(this._outputFeedback, 0, this._outputCopy, 0);
               this._outputFeedbackOffset = 0;
               byte[] temp = this._outputCopy;
               this._outputCopy = this._outputFeedback;
               this._outputFeedback = temp;
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
         OFBPseudoRandomSource source = new OFBPseudoRandomSource((SymmetricKeyEncryptorEngine)(new Object()), (InitializationVector)(new Object(IV)));
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
      long ID_TEST_PRS_OFB = 3665565819366422269L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_PRS_OFB) == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_OFB, appRegistry);
      }
   }
}
