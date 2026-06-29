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

   public byte[] extractRSAPublicKeyE(CryptoTokenPublicKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPublicKeyN(CryptoTokenPublicKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyE(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyN(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyD(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyP(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyQ(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyDModPm1(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyDModQm1(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractRSAPrivateKeyQInvModP(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void verifyRSA(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public RSACryptoSystem[] getSuggestedRSACryptoSystems() throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public RSAKeyPair createRSAKeyPair(RSACryptoSystem cryptoSystem, byte[] e) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPublicKeyData injectRSAPublicKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] n) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] p, byte[] q) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(
      RSACryptoSystem cryptoSystem, byte[] e, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectRSAPrivateKey(
      RSACryptoSystem cryptoSystem, byte[] e, byte[] d, byte[] n, byte[] p, byte[] q, byte[] dModPm1, byte[] dModQm1, byte[] qInvModP
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteRSAPublicKey(CryptoTokenPublicKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteRSAPrivateKey(CryptoTokenPrivateKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public boolean isSupportedEncryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData) {
      return false;
   }

   public void encryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPublicKeyData publicKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public boolean isSupportedDecryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData) {
      return false;
   }

   public void decryptRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public void signRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void signRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset, Object context
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }

   protected RSACryptoToken() {
   }
}
