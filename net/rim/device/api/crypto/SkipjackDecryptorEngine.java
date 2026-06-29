package net.rim.device.api.crypto;

public final class SkipjackDecryptorEngine implements SymmetricKeyDecryptorEngine {
   private SkipjackCryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH = 8;

   public SkipjackDecryptorEngine(SkipjackKey key) {
      if (key == null) {
         throw new Object();
      }

      this._cryptoToken = key.getSkipjackCryptoToken();
      this._context = this._cryptoToken.initializeDecrypt(key.getCryptoTokenData());
   }

   @Override
   public final String getAlgorithm() {
      return "Skipjack";
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
