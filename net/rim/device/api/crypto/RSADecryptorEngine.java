package net.rim.device.api.crypto;

public final class RSADecryptorEngine implements PrivateKeyDecryptorEngine {
   private RSAPrivateKey _key;
   private RSACryptoToken _cryptoToken;

   public RSADecryptorEngine(RSAPrivateKey key) {
      if (key != null && (key.getCryptoSystem().getBitLength() & 7) == 0) {
         this._key = key;
         this._cryptoToken = this._key.getRSACryptoToken();
         if (!this._cryptoToken.isSupportedDecryptRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData())) {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "RSA";
   }

   @Override
   public final int getBlockLength() {
      return this._key.getRSACryptoSystem().getModulusLength();
   }

   @Override
   public final void decrypt(byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (plaintext != null
         && plaintextOffset >= 0
         && plaintext.length > plaintextOffset
         && ciphertext != null
         && ciphertextOffset >= 0
         && ciphertext.length > ciphertextOffset) {
         this._cryptoToken.decryptRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), ciphertext, ciphertextOffset, plaintext, plaintextOffset);
      } else {
         throw new Object();
      }
   }
}
