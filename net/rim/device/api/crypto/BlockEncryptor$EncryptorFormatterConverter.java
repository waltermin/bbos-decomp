package net.rim.device.api.crypto;

class BlockEncryptor$EncryptorFormatterConverter implements BlockFormatterEngine {
   private BlockEncryptorEngine _encryptorEngine;
   private int _blockLength;

   public BlockEncryptor$EncryptorFormatterConverter(BlockEncryptorEngine encryptorEngine) {
      if (encryptorEngine == null) {
         throw new IllegalArgumentException();
      }

      this._encryptorEngine = encryptorEngine;
      this._blockLength = this._encryptorEngine.getBlockLength();
   }

   @Override
   public String getAlgorithm() {
      return this._encryptorEngine.getAlgorithm();
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
   public int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset, boolean lastBlock) {
      return this.formatAndEncrypt(input, inputOffset, inputLength, output, outputOffset);
   }

   @Override
   public int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) {
      if (inputLength == 0) {
         return 0;
      }

      this._encryptorEngine.encrypt(input, inputOffset, output, outputOffset);
      return this._blockLength;
   }
}
