package net.rim.device.api.crypto;

class BlockCipherContext implements CryptoTokenCipherContext {
   private NativeBlockCipher _cipher;

   BlockCipherContext(NativeBlockCipher cipher) throws CryptoUnsupportedOperationException {
      if (cipher == null) {
         throw new CryptoUnsupportedOperationException();
      }

      this._cipher = cipher;
   }

   NativeBlockCipher getNativeBlockCipher() {
      return this._cipher;
   }
}
