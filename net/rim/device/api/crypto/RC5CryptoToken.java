package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class RC5CryptoToken implements SymmetricCryptoToken, Persistable {
   protected RC5CryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "RC5";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data, int blockLength, int numberOfRounds) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data, int blockLength, int numberOfRounds) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public int extractKeyDataLength(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractKeyData(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenSymmetricKeyData createKey(int bitLength) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenSymmetricKeyData injectKey(byte[] key, int offset, int bitLength) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteKey(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }
}
