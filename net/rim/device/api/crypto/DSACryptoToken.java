package net.rim.device.api.crypto;

public class DSACryptoToken implements AsymmetricCryptoToken {
   protected DSACryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "DSA";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public int getDSACryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public String getDSACryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getDSACryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getDSACryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getDSACryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getDSAPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getDSAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractDSAPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractDSAPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractDSAPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public CryptoTokenCryptoSystemData getDSACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) {
      throw new Object();
   }

   public void verifyDSACryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public DSACryptoSystem[] getSuggestedDSACryptoSystems() {
      throw new Object();
   }

   public DSAKeyPair createDSAKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public CryptoTokenPublicKeyData injectDSAPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectDSAPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public void deleteDSAPublicKey(CryptoTokenPublicKeyData data) {
      throw new Object();
   }

   public void deleteDSAPrivateKey(CryptoTokenPrivateKeyData data) {
      throw new Object();
   }

   public void signDSA(
      CryptoTokenCryptoSystemData cryptoSystemData,
      CryptoTokenPrivateKeyData privateKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      throw new Object();
   }

   public void signDSA(
      CryptoTokenCryptoSystemData cryptoSystemData,
      CryptoTokenPrivateKeyData privateKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset,
      Object context
   ) {
      throw new Object();
   }

   public boolean verifyDSA(
      CryptoTokenCryptoSystemData cryptoSystemData,
      CryptoTokenPublicKeyData publicKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      throw new Object();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }
}
