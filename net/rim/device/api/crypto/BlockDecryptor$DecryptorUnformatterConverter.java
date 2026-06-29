package net.rim.device.api.crypto;

class BlockDecryptor$DecryptorUnformatterConverter implements BlockUnformatterEngine {
   private BlockDecryptorEngine _decryptorEngine;
   private int _blockLength;

   public BlockDecryptor$DecryptorUnformatterConverter(BlockDecryptorEngine decryptorEngine) {
      if (decryptorEngine == null) {
         throw new IllegalArgumentException();
      }

      this._decryptorEngine = decryptorEngine;
      this._blockLength = decryptorEngine.getBlockLength();
   }

   @Override
   public String getAlgorithm() {
      return this._decryptorEngine.getAlgorithm();
   }

   @Override
   public int getInputBlockLength() {
      return this._blockLength;
   }

   @Override
   public int getOutputBlockLength() {
      return this._blockLength;
   }

   @Override
   public int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset) {
      this._decryptorEngine.decrypt(input, inputOffset, output, outputOffset);
      return this._blockLength;
   }

   @Override
   public int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset, boolean lastBlock) {
      this._decryptorEngine.decrypt(input, inputOffset, output, outputOffset);
      return this._blockLength;
   }
}
