package net.rim.device.api.crypto;

public final class TripleDESDecryptorEngine implements SymmetricKeyDecryptorEngine {
   private TripleDESCryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH = 8;

   public TripleDESDecryptorEngine(TripleDESKey key) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      this._cryptoToken = key.getTripleDESCryptoToken();
      this._context = this._cryptoToken.initializeDecrypt(key.getCryptoTokenData());
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
   public final void decrypt(byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      this._cryptoToken.decrypt(this._context, ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }
}
