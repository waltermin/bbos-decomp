package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class TripleDESCryptoToken implements SymmetricCryptoToken, Persistable {
   protected TripleDESCryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "TripleDES";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public byte[] extractKeyData(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenSymmetricKeyData createKey() throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenSymmetricKeyData injectKey(byte[] key, int offset) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteKey(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }
}
