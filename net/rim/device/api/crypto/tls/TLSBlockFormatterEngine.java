package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.BlockFormatterEngine;
import net.rim.device.api.crypto.MessageTooLongException;
import net.rim.device.api.crypto.RandomSource;
import net.rim.vm.Array;

public final class TLSBlockFormatterEngine implements BlockFormatterEngine {
   private BlockEncryptorEngine _encryptorEngine;
   private int _inputBlockLength;
   private int _outputBlockLength;
   private boolean _useRandomPadLength;

   public TLSBlockFormatterEngine(BlockEncryptorEngine encryptorEngine) {
      if (encryptorEngine == null) {
         throw new Object();
      }

      this._encryptorEngine = encryptorEngine;
      this._inputBlockLength = encryptorEngine.getBlockLength();
      this._outputBlockLength = encryptorEngine.getBlockLength();
   }

   public TLSBlockFormatterEngine(BlockEncryptorEngine encryptorEngine, boolean useRandomPadLength) {
      if (encryptorEngine == null) {
         throw new Object();
      }

      this._encryptorEngine = encryptorEngine;
      this._useRandomPadLength = useRandomPadLength;
      this._inputBlockLength = encryptorEngine.getBlockLength();
      this._outputBlockLength = encryptorEngine.getBlockLength();
   }

   @Override
   public final String getAlgorithm() {
      return this._encryptorEngine.getAlgorithm();
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
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset, boolean lastBlock) {
      if (input == null
         || inputOffset < 0
         || inputLength < 0
         || input.length - inputLength < inputOffset
         || output == null
         || outputOffset < 0
         || output.length - this._outputBlockLength < outputOffset) {
         throw new Object();
      }

      if (inputLength > this._inputBlockLength) {
         throw new MessageTooLongException();
      }

      if (!lastBlock) {
         this._encryptorEngine.encrypt(input, inputOffset, output, outputOffset);
      } else {
         byte[] encodedBlock = new byte[this._outputBlockLength];
         System.arraycopy(input, inputOffset, encodedBlock, 0, inputLength);
         int encodedMessageOffset = inputLength;
         int padLength = (byte)(this._outputBlockLength - encodedMessageOffset - 1);
         if (this._useRandomPadLength) {
            byte[] randomByte = RandomSource.getBytes(1);
            int numExtraBlock = ((randomByte[0] & 255) - padLength) / this._outputBlockLength;
            if (numExtraBlock < 0) {
               numExtraBlock = 0;
            }

            if (numExtraBlock == 0) {
               while (encodedMessageOffset < this._outputBlockLength) {
                  encodedBlock[encodedMessageOffset++] = (byte)padLength;
               }

               this._encryptorEngine.encrypt(encodedBlock, 0, output, outputOffset);
               return this._outputBlockLength;
            }

            int extraPadding = numExtraBlock * this._outputBlockLength;
            padLength += extraPadding;
            Array.resize(output, output.length + extraPadding);

            while (encodedMessageOffset < this._inputBlockLength) {
               encodedBlock[encodedMessageOffset++] = (byte)padLength;
            }

            this._encryptorEngine.encrypt(encodedBlock, 0, output, outputOffset);
            outputOffset += this._outputBlockLength;
            byte[] padBlock = new byte[this._outputBlockLength];

            for (int i = 0; i < this._outputBlockLength; i++) {
               padBlock[i] = (byte)padLength;
            }

            while (numExtraBlock > 0) {
               this._encryptorEngine.encrypt(padBlock, 0, output, outputOffset);
               outputOffset += this._outputBlockLength;
               numExtraBlock--;
            }

            return padLength + inputLength + 1;
         }

         while (encodedMessageOffset < this._outputBlockLength) {
            encodedBlock[encodedMessageOffset++] = (byte)padLength;
         }

         this._encryptorEngine.encrypt(encodedBlock, 0, output, outputOffset);
      }

      return this._outputBlockLength;
   }

   @Override
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) {
      if (inputLength != this._inputBlockLength) {
         throw new Object();
      } else {
         return this.formatAndEncrypt(input, inputOffset, inputLength, output, outputOffset, false);
      }
   }
}
