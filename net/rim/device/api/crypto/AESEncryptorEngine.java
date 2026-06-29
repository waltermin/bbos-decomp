package net.rim.device.api.crypto;

public final class AESEncryptorEngine implements SymmetricKeyEncryptorEngine {
   private AESCryptoToken _cryptoToken;
   private CryptoTokenCipherContext _context;
   private int _blockLength;
   private int _keyLength;
   public static final int BLOCK_LENGTH_DEFAULT;

   public final int getKeyLength() {
      return this._keyLength;
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object("AES_"))).append(this._keyLength << 3).append('_').append(this._blockLength << 3).toString();
   }

   @Override
   public final int getBlockLength() {
      return this._blockLength;
   }

   @Override
   public final void encrypt(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      this._cryptoToken.encrypt(this._context, plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   public AESEncryptorEngine(AESKey key) {
      this(key, 16);
   }

   public AESEncryptorEngine(AESKey key, int blockLength, boolean inECMMode) {
      if (key != null && blockLength >= 16 && blockLength <= 32 && (blockLength & 3) == 0) {
         this._cryptoToken = key.getAESCryptoToken();
         this._context = (CryptoTokenCipherContext)this._cryptoToken.initializeEncrypt(key.getCryptoTokenData(), blockLength, inECMMode);
         this._blockLength = blockLength;
         this._keyLength = key.getLength();
      } else {
         throw new Object();
      }
   }

   public AESEncryptorEngine(AESKey key, int blockLength) {
      if (key != null && blockLength >= 16 && blockLength <= 32 && (blockLength & 3) == 0) {
         this._cryptoToken = key.getAESCryptoToken();
         this._context = (CryptoTokenCipherContext)this._cryptoToken.initializeEncrypt(key.getCryptoTokenData(), blockLength);
         this._blockLength = blockLength;
         this._keyLength = key.getLength();
      } else {
         throw new Object();
      }
   }
}
