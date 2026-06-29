package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockUnformatterEngine;

final class CMSBlockUnformatterEngine implements BlockUnformatterEngine {
   private BlockDecryptorEngine _decryptorEngine;
   private int _blockLength;

   public CMSBlockUnformatterEngine(BlockDecryptorEngine decryptorEngine) {
      this._decryptorEngine = decryptorEngine;
      this._blockLength = this._decryptorEngine.getBlockLength();
   }

   @Override
   public final String getAlgorithm() {
      return this._decryptorEngine.getAlgorithm() + "_CMS";
   }

   @Override
   public final int getInputBlockLength() {
      return this._decryptorEngine.getBlockLength();
   }

   @Override
   public final int getOutputBlockLength() {
      return this._decryptorEngine.getBlockLength();
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset) {
      this._decryptorEngine.decrypt(input, inputOffset, output, outputOffset);
      return this._blockLength;
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset, boolean lastBlock) {
      this._decryptorEngine.decrypt(input, inputOffset, output, outputOffset);
      if (lastBlock) {
         int padding = output[outputOffset + this._blockLength - 1];
         return padding < 0 ? this._blockLength : Math.max(this._blockLength - padding, 0);
      } else {
         return this._blockLength;
      }
   }
}
