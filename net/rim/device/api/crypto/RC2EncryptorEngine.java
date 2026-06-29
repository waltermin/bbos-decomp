package net.rim.device.api.crypto;

public final class RC2EncryptorEngine implements SymmetricKeyEncryptorEngine {
   private RC2CryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   private int _keyLength;
   public static final int BLOCK_LENGTH = 8;

   public final int getKeyLength() {
      return this._keyLength;
   }

   @Override
   public final int getBlockLength() {
      return 8;
   }

   @Override
   public final String getAlgorithm() {
      return "RC2_" + (this.getKeyLength() << 3);
   }

   @Override
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      this._cryptoToken.encrypt(this._context, plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   public RC2EncryptorEngine(RC2Key key) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      this._cryptoToken = key.getRC2CryptoToken();
      this._context = this._cryptoToken.initializeEncrypt(key.getCryptoTokenData());
      this._keyLength = key.getLength();
   }
}
