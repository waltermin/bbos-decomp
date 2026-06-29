package net.rim.device.api.crypto;

public final class CAST128DecryptorEngine implements SymmetricKeyDecryptorEngine {
   private CAST128CryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH;

   public CAST128DecryptorEngine(CAST128Key key) {
      if (key == null) {
         throw new Object();
      }

      this._cryptoToken = key.getCAST128CryptoToken();
      this._context = this._cryptoToken.initializeDecrypt(key.getCryptoTokenData());
   }

   @Override
   public final String getAlgorithm() {
      return "CAST128";
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
