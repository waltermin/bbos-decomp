package net.rim.device.api.crypto;

public final class RSAEncryptorEngine implements PublicKeyEncryptorEngine {
   private RSAPublicKey _key;
   private RSACryptoToken _cryptoToken;

   public RSAEncryptorEngine(RSAPublicKey key) {
      if (key != null && (key.getCryptoSystem().getBitLength() & 7) == 0) {
         this._key = key;
         this._cryptoToken = this._key.getRSACryptoToken();
         if (!this._cryptoToken.isSupportedEncryptRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData())) {
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

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (plaintext != null
         && plaintextOffset >= 0
         && plaintext.length > plaintextOffset
         && ciphertext != null
         && ciphertextOffset >= 0
         && ciphertext.length > ciphertextOffset) {
         try {
            byte[] n = this._key.getN();
            if (CryptoByteArrayArithmetic.compare(plaintext, plaintextOffset, n.length, n, 0, n.length) >= 0) {
               throw new Object();
            }

            this._cryptoToken
               .encryptRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), plaintext, plaintextOffset, ciphertext, ciphertextOffset);
         } catch (Throwable var7) {
            throw new Object(e.toString());
         }
      } else {
         throw new Object();
      }
   }
}
