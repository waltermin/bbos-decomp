package net.rim.device.api.crypto;

public final class DESEncryptorEngine implements SymmetricKeyEncryptorEngine {
   private DESCryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   public static final int BLOCK_LENGTH;

   public DESEncryptorEngine(DESKey key) {
      if (key == null) {
         throw new Object();
      }

      this._cryptoToken = key.getDESCryptoToken();
      this._context = this._cryptoToken.initializeEncrypt(key.getCryptoTokenData());
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
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      this._cryptoToken.encrypt(this._context, plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }
}
