package net.rim.device.api.crypto;

public final class SkipjackEncryptorEngine implements SymmetricKeyEncryptorEngine {
   private SkipjackCryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH = 8;

   public SkipjackEncryptorEngine(SkipjackKey key) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      this._cryptoToken = key.getSkipjackCryptoToken();
      this._context = this._cryptoToken.initializeEncrypt(key.getCryptoTokenData());
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
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      this._cryptoToken.encrypt(this._context, plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }
}
