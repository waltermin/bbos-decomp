package net.rim.device.api.crypto;

public final class CAST128EncryptorEngine implements SymmetricKeyEncryptorEngine {
   private CAST128CryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH = 8;

   public CAST128EncryptorEngine(CAST128Key key) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      this._cryptoToken = key.getCAST128CryptoToken();
      this._context = this._cryptoToken.initializeEncrypt(key.getCryptoTokenData());
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
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      this._cryptoToken.encrypt(this._context, plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }
}
