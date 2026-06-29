package net.rim.device.api.crypto;

public final class RC5EncryptorEngine implements SymmetricKeyEncryptorEngine {
   private RC5CryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   private int _blockLength;
   private int _keyLength;
   private int _numberOfRounds;

   public final int getNumberOfRounds() {
      return this._numberOfRounds;
   }

   public final int getKeyLength() {
      return this._keyLength;
   }

   @Override
   public final int getBlockLength() {
      return this._blockLength;
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object("RC5_")))
         .append(this.getKeyLength() << 3)
         .append('_')
         .append(this.getBlockLength() << 3)
         .append('_')
         .append(this._numberOfRounds)
         .toString();
   }

   @Override
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      this._cryptoToken.encrypt(this._context, plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   public RC5EncryptorEngine(RC5Key key, int blockLength) {
      this(key, blockLength, 16);
   }

   public RC5EncryptorEngine(RC5Key key) {
      this(key, 8, 16);
   }

   public RC5EncryptorEngine(RC5Key key, int blockLength, int numberOfRounds) {
      if (key != null && (blockLength == 8 || blockLength == 16) && numberOfRounds >= 0 && numberOfRounds <= 255) {
         this._cryptoToken = key.getRC5CryptoToken();
         this._context = this._cryptoToken.initializeEncrypt(key.getCryptoTokenData(), blockLength, numberOfRounds);
         this._blockLength = blockLength;
         this._keyLength = key.getLength();
         this._numberOfRounds = numberOfRounds;
      } else {
         throw new Object();
      }
   }
}
