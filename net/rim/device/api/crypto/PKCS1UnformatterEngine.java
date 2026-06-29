package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;

public final class PKCS1UnformatterEngine implements BlockUnformatterEngine {
   private PrivateKeyDecryptorEngine _decryptorEngine;
   private int _inputBlockLength;
   private int _outputBlockLength;
   private byte[] _decryptedInput;
   private static final int MIN_OVERHEAD = 11;

   public PKCS1UnformatterEngine(PrivateKeyDecryptorEngine decryptorEngine) {
      if (decryptorEngine == null) {
         throw new Object();
      }

      this._decryptorEngine = decryptorEngine;
      this._inputBlockLength = decryptorEngine.getBlockLength();
      this._outputBlockLength = decryptorEngine.getBlockLength() - 11;
      this._decryptedInput = new byte[this._inputBlockLength];
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._decryptorEngine.getAlgorithm()).append("/PKCS1").toString();
   }

   @Override
   public final int getInputBlockLength() {
      return this._inputBlockLength;
   }

   @Override
   public final int getOutputBlockLength() {
      return this._outputBlockLength;
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset, boolean lastBlock) throws DecodeException {
      if (input != null
         && inputOffset >= 0
         && input.length - this._inputBlockLength >= inputOffset
         && output != null
         && outputOffset >= 0
         && output.length - this._outputBlockLength >= outputOffset) {
         this._decryptorEngine.decrypt(input, inputOffset, this._decryptedInput, 0);
         input = this._decryptedInput;
         inputOffset = 0;
         if (inputOffset + 2 <= this._inputBlockLength && input[inputOffset++] == 0 && input[inputOffset++] == 2) {
            int randomDataLength = 0;

            while (inputOffset < this._inputBlockLength && input[inputOffset] != 0) {
               randomDataLength++;
               inputOffset++;
            }

            if (randomDataLength < 8) {
               throw new DecodeException();
            } else if (inputOffset + 1 <= this._inputBlockLength && input[inputOffset++] == 0) {
               int inputLength = this._inputBlockLength - inputOffset;
               System.arraycopy(input, inputOffset, output, outputOffset, inputLength);
               return inputLength;
            } else {
               throw new DecodeException();
            }
         } else {
            throw new DecodeException();
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset) {
      return this.decryptAndUnformat(input, inputOffset, output, outputOffset, false);
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.get(PKCS1FormatterEngine.ID_TEST_FORMATTER_PKCS1) == null) {
      }
   }
}
