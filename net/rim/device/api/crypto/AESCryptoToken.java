package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class AESCryptoToken implements SymmetricCryptoToken, Persistable {
   protected AESCryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "AES";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data, int blockLength) {
      throw new Object();
   }

   public CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data, int blockLength, boolean isInECMMode) {
      throw new Object();
   }

   public void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      throw new Object();
   }

   public CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data, int blockLength) {
      throw new Object();
   }

   public CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data, int blockLength, boolean isInECMMode) {
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
