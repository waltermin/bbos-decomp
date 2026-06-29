package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS5UnformatterEngine implements BlockUnformatterEngine {
   private BlockDecryptorEngine _decryptorEngine;
   private int _blockLength;

   public PKCS5UnformatterEngine(BlockDecryptorEngine decryptorEngine) {
      if (decryptorEngine == null) {
         throw new Object();
      }

      this._decryptorEngine = decryptorEngine;
      this._blockLength = decryptorEngine.getBlockLength();
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._decryptorEngine.getAlgorithm()).append("/PKCS5").toString();
   }

   @Override
   public final int getInputBlockLength() {
      return this._blockLength;
   }

   @Override
   public final int getOutputBlockLength() {
      return this._blockLength;
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset, boolean lastBlock) throws BadPaddingException {
      if (input != null
         && inputOffset >= 0
         && input.length - this._blockLength >= inputOffset
         && output != null
         && outputOffset >= 0
         && output.length - this._blockLength >= outputOffset) {
         this._decryptorEngine.decrypt(input, inputOffset, output, outputOffset);
         int actualLength = this._blockLength;
         if (lastBlock) {
            actualLength -= output[outputOffset + this._blockLength - 1];
            if (actualLength < 0 || actualLength >= this._blockLength) {
               throw new BadPaddingException();
            }
         }

         return actualLength;
      } else {
         throw new Object();
      }
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset) {
      this._decryptorEngine.decrypt(input, inputOffset, output, outputOffset);
      return this._blockLength;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
      byte[] CIPHER_TEXT = new byte[]{120, -9, 74, -23, -111, -116, -78, 80, 81, -70, -64, -42, 106, -13, 81, 112};
      byte[] TEST_KEY = new byte[]{49, 112, 93, 104, 124, 1, -85, 25};

      try {
         PKCS5UnformatterEngine unformatter = new PKCS5UnformatterEngine(new DESDecryptorEngine(new DESKey(TEST_KEY)));
         int blockLength = unformatter.getInputBlockLength();
         byte[] target = new byte[2 * blockLength];
         unformatter.decryptAndUnformat(CIPHER_TEXT, 0, target, 0, false);
         unformatter.decryptAndUnformat(CIPHER_TEXT, blockLength, target, blockLength, true);
         if (Arrays.equals(target, 0, PLAIN_TEXT, 0, PLAIN_TEXT.length)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_UNFORMATTER_PKCS5 = 7454133345239025208L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_UNFORMATTER_PKCS5) == null) {
         selfTest();
         appRegistry.put(ID_TEST_UNFORMATTER_PKCS5, appRegistry);
      }
   }
}
