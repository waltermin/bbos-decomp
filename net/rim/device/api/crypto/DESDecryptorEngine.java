package net.rim.device.api.crypto;

public final class DESDecryptorEngine implements SymmetricKeyDecryptorEngine {
   private DESCryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH = 8;

   public DESDecryptorEngine(DESKey key) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      this._cryptoToken = key.getDESCryptoToken();
      this._context = this._cryptoToken.initializeDecrypt(key.getCryptoTokenData());
   }

   @Override
   public final String getAlgorithm() {
      return "DES";
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
