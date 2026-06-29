package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class DESCryptoToken implements SymmetricCryptoToken, Persistable {
   protected DESCryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "DES";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      throw new Object();
   }

   public CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      throw new Object();
   }

   public byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public CryptoTokenSymmetricKeyData createKey() {
      throw new Object();
   }

   public CryptoTokenSymmetricKeyData injectKey(byte[] key, int offset) {
      throw new Object();
   }

   public void deleteKey(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }
}
