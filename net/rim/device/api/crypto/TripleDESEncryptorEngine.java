package net.rim.device.api.crypto;

public final class TripleDESEncryptorEngine implements SymmetricKeyEncryptorEngine {
   private TripleDESCryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH;

   public TripleDESEncryptorEngine(TripleDESKey key) {
      if (key == null) {
         throw new Object();
      }

      this._cryptoToken = key.getTripleDESCryptoToken();
      this._context = this._cryptoToken.initializeEncrypt(key.getCryptoTokenData());
   }

   @Override
   public final String getAlgorithm() {
      return "TripleDES";
   }

   @Override
   public final int getBlockLength() {
      return 8;
   }

   @Override
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      this._cryptoToken.encrypt(this._context, plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }
}
