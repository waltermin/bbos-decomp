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

   public CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data, int blockLength, int numberOfRounds) {
      throw new Object();
   }

   public void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      throw new Object();
   }

   public CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data, int blockLength, int numberOfRounds) {
      throw new Object();
   }

   public void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      throw new Object();
   }

   public int extractKeyDataLength(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public CryptoTokenSymmetricKeyData createKey(int bitLength) {
      throw new Object();
   }

   public CryptoTokenSymmetricKeyData injectKey(byte[] key, int offset, int bitLength) {
      throw new Object();
   }

   public void deleteKey(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }
}
