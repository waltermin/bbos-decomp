package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class RC2CryptoToken implements SymmetricCryptoToken, Persistable {
   protected RC2CryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "RC2";
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

   public int extractKeyDataLength(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractKeyData(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int extractKeyEffectiveBitLength(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenSymmetricKeyData createKey(int bitLength, int effectiveBitLength) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenSymmetricKeyData injectKey(byte[] key, int offset, int bitLength, int effectiveBitLength) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteKey(CryptoTokenSymmetricKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }
}
