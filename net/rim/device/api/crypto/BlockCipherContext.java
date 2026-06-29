package net.rim.device.api.crypto;

class BlockCipherContext implements CryptoTokenCipherContext {
   private NativeBlockCipher _cipher;

   BlockCipherContext(NativeBlockCipher cipher) {
      if (cipher == null) {
         throw new Object();
      }

      this._cipher = cipher;
   }

   NativeBlockCipher getNativeBlockCipher() {
      return this._cipher;
   }
}
