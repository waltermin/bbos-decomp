package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS5FormatterEngine implements BlockFormatterEngine {
   private BlockEncryptorEngine _encryptorEngine;
   private int _blockLength;

   public PKCS5FormatterEngine(BlockEncryptorEngine encryptorEngine) {
      if (encryptorEngine == null) {
         throw new Object();
      }

      this._encryptorEngine = encryptorEngine;
      this._blockLength = encryptorEngine.getBlockLength();
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._encryptorEngine.getAlgorithm()).append("/PKCS5").toString();
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
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset, boolean lastBlock) {
      if (lastBlock) {
         if (input == null
            || inputOffset < 0
            || inputLength < 0
            || input.length - inputLength < inputOffset
            || output == null
            || outputOffset < 0
            || output.length - this._blockLength < outputOffset) {
            throw new Object();
         }

         if (inputLength > this._blockLength) {
            throw new MessageTooLongException();
         }

         byte[] buffer = new byte[this._blockLength];
         System.arraycopy(input, inputOffset, buffer, 0, inputLength);
         int n = this._blockLength - inputLength;

         while (inputLength < this._blockLength) {
            buffer[inputLength++] = (byte)n;
         }

         this._encryptorEngine.encrypt(buffer, 0, output, outputOffset);
      } else {
         this._encryptorEngine.encrypt(input, inputOffset, output, outputOffset);
      }

      return this._blockLength;
   }

   @Override
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) {
      this._encryptorEngine.encrypt(input, inputOffset, output, outputOffset);
      return this._blockLength;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
      byte[] CIPHER_TEXT = new byte[]{120, -9, 74, -23, -111, -116, -78, 80, 81, -70, -64, -42, 106, -13, 81, 112};
      byte[] TEST_KEY = new byte[]{49, 112, 93, 104, 124, 1, -85, 25};

      try {
         PKCS5FormatterEngine formatter = new PKCS5FormatterEngine(new DESEncryptorEngine(new DESKey(TEST_KEY)));
         int blockLength = formatter.getInputBlockLength();
         byte[] target = new byte[2 * blockLength];
         formatter.formatAndEncrypt(PLAIN_TEXT, 0, blockLength, target, 0, false);
         formatter.formatAndEncrypt(PLAIN_TEXT, blockLength, PLAIN_TEXT.length - blockLength, target, blockLength, true);
         if (Arrays.equals(target, 0, CIPHER_TEXT, 0, 2 * blockLength)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_FORMATTER_PKCS5 = -1550947069774806244L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_FORMATTER_PKCS5) == null) {
         selfTest();
         appRegistry.put(ID_TEST_FORMATTER_PKCS5, appRegistry);
      }
   }
}
