package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.BlockFormatterEngine;

final class CMSBlockFormatterEngine implements BlockFormatterEngine {
   private BlockEncryptorEngine _encryptorEngine;
   private int _blockLength;

   public CMSBlockFormatterEngine(BlockEncryptorEngine encryptorEngine) {
      this._encryptorEngine = encryptorEngine;
      this._blockLength = this._encryptorEngine.getBlockLength();
   }

   @Override
   public final String getAlgorithm() {
      return this._encryptorEngine.getAlgorithm() + "_CMS";
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
      if (inputLength == 0) {
         byte[] padding = new byte[this._blockLength];

         for (int i = 0; i < this._blockLength; i++) {
            padding[i] = (byte)this._blockLength;
         }

         this._encryptorEngine.encrypt(padding, 0, output, outputOffset);
         return this._blockLength;
      } else {
         if (lastBlock) {
            int padByte = this._blockLength - inputLength;
            int startPos = inputOffset + inputLength;

            for (int i = startPos; i < startPos + padByte; i++) {
               input[i] = (byte)padByte;
            }
         }

         this._encryptorEngine.encrypt(input, inputOffset, output, outputOffset);
         return this._blockLength;
      }
   }

   @Override
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) {
      if (inputLength == 0) {
         return 0;
      }

      this._encryptorEngine.encrypt(input, inputOffset, output, outputOffset);
      return this._blockLength;
   }
}
