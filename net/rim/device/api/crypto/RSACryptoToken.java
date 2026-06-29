package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class RSACryptoToken implements AsymmetricCryptoToken, Persistable {
   @Override
   public final String getAlgorithm() {
      return "RSA";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public byte[] extractRSAPublicKeyE(CryptoTokenPublicKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPublicKeyN(CryptoTokenPublicKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyE(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyN(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyD(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyP(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyQ(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyDModPm1(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyDModQm1(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractRSAPrivateKeyQInvModP(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public void verifyRSA(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) {
      throw new Object();
   }

   public RSACryptoSystem[] getSuggestedRSACryptoSystems() {
      throw new Object();
   }

   public RSAKeyPair createRSAKeyPair(RSACryptoSystem cryptoSystem, byte[] e) {
      throw new Object();
   }

   public CryptoTokenPublicKeyData injectRSAPublicKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] n) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] p, byte[] q) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(
      RSACryptoSystem cryptoSystem, byte[] e, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP
   ) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(
      RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP
   ) {
      throw new Object();
   }

   public void deleteRSAPublicKey(CryptoTokenPublicKeyData data) {
      throw new Object();
   }

   public void deleteRSAPrivateKey(CryptoTokenPrivateKeyData data) {
      throw new Object();
   }

   public boolean isSupportedEncryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData) {
      return false;
   }

   public void encryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) {
      throw new Object();
   }

   public boolean isSupportedDecryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData) {
      return false;
   }

   public void decryptRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      throw new Object();
   }

   public void signRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) {
      throw new Object();
   }

   public void signRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset, Object context
   ) {
      throw new Object();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }

   protected RSACryptoToken() {
   }
}
