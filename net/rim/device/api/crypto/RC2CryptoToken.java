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

   public int extractKeyDataLength(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public int extractKeyEffectiveBitLength(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }

   public CryptoTokenSymmetricKeyData createKey(int bitLength, int effectiveBitLength) {
      throw new Object();
   }

   public CryptoTokenSymmetricKeyData injectKey(byte[] key, int offset, int bitLength, int effectiveBitLength) {
      throw new Object();
   }

   public void deleteKey(CryptoTokenSymmetricKeyData data) {
      throw new Object();
   }
}
