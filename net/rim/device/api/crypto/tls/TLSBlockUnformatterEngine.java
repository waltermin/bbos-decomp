package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockUnformatterEngine;
import net.rim.device.api.crypto.DecodeException;
import net.rim.device.api.util.DataBuffer;

public final class TLSBlockUnformatterEngine implements BlockUnformatterEngine {
   private BlockDecryptorEngine _decryptorEngine;
   private int _inputBlockLength;
   private int _outputBlockLength;
   private byte[] _decryptedDataBuffer;
   private boolean _paddingVerification;
   public static final int SLIDING_WINDOW_SIZE = 256;

   public final int decryptAndUnformat(DataBuffer inBuffer, DataBuffer outBuffer) {
      byte[] tempOutBuf = new byte[256];
      int length = -1;
      if (inBuffer.available() > 0) {
         length++;
      }

      while (inBuffer.available() > 0) {
         byte[] tempInBuf = new byte[256];
         int count = inBuffer.read(tempInBuf);
         int numBlock = count / this._inputBlockLength;
         int tempLength;
         if (count == 256 && inBuffer.available() > 0) {
            tempLength = this.decryptAndUnformatBigBlock(tempInBuf, 0, tempOutBuf, 0, false, numBlock);
         } else if (count > -1 && count <= 256) {
            tempLength = this.decryptAndUnformatBigBlock(tempInBuf, 0, tempOutBuf, 0, true, numBlock);
         } else {
            tempLength = 0;
         }

         if (tempLength > 0) {
            outBuffer.write(tempOutBuf, 0, tempLength);
            length += tempLength;
         }
      }

      return length;
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
   public final String getAlgorithm() {
      return this._decryptorEngine.getAlgorithm();
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset, boolean lastBlock) {
      if (input != null
         && inputOffset >= 0
         && input.length - this._inputBlockLength >= inputOffset
         && output != null
         && outputOffset >= 0
         && output.length - this._outputBlockLength >= outputOffset) {
         this._decryptorEngine.decrypt(input, inputOffset, this._decryptedDataBuffer, 0);
         System.arraycopy(this._decryptedDataBuffer, 0, output, outputOffset, this._inputBlockLength);
         return this._inputBlockLength;
      } else {
         throw new Object();
      }
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset) {
      return this.decryptAndUnformat(input, inputOffset, output, outputOffset, false);
   }

   public TLSBlockUnformatterEngine(BlockDecryptorEngine decryptorEngine) {
      this(decryptorEngine, false);
   }

   public TLSBlockUnformatterEngine(BlockDecryptorEngine decryptorEngine, boolean paddingVerification) {
      if (decryptorEngine == null) {
         throw new Object();
      }

      this._decryptorEngine = decryptorEngine;
      this._inputBlockLength = decryptorEngine.getBlockLength();
      this._outputBlockLength = decryptorEngine.getBlockLength();
      this._decryptedDataBuffer = new byte[this._inputBlockLength];
      this._paddingVerification = paddingVerification;
   }

   private final int decryptAndUnformatBigBlock(byte[] input, int inputOffset, byte[] output, int outputOffset, boolean lastBigBlock, int numBlock) throws DecodeException {
      if (input == null
         || inputOffset < 0
         || input.length - this._inputBlockLength < inputOffset
         || output == null
         || outputOffset < 0
         || output.length - this._outputBlockLength < outputOffset) {
         throw new Object();
      }

      if (!lastBigBlock) {
         for (int i = 1; i <= numBlock; i++) {
            this.decryptAndUnformat(input, inputOffset, output, outputOffset);
            inputOffset += this._inputBlockLength;
            outputOffset += this._outputBlockLength;
         }

         return 256;
      } else {
         int length = 0;

         for (int i = 1; i <= numBlock; i++) {
            if (i == numBlock) {
               this.decryptAndUnformat(input, inputOffset, output, outputOffset);
               outputOffset += this._inputBlockLength;
               inputOffset += this._inputBlockLength;
               int padLength = output[outputOffset - 1] & 255;
               length = i * this._inputBlockLength - padLength - 1;
               if (length < 0 || length > 256) {
                  throw new SessionInformation();
               }

               if (this._paddingVerification) {
                  for (int j = length; j < outputOffset; j++) {
                     if ((output[j] & 255) != padLength) {
                        throw new DecodeException();
                     }
                  }
               }
            } else {
               this.decryptAndUnformat(input, inputOffset, output, outputOffset);
               inputOffset += this._inputBlockLength;
               outputOffset += this._outputBlockLength;
            }
         }

         return length;
      }
   }
}
