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

   public int getDSACryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public String getDSACryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getDSACryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getDSACryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getDSACryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getDSAPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getDSAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractDSAPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractDSAPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractDSAPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenCryptoSystemData getDSACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void verifyDSACryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public DSACryptoSystem[] getSuggestedDSACryptoSystems() throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public DSAKeyPair createDSAKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPublicKeyData injectDSAPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectDSAPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteDSAPublicKey(CryptoTokenPublicKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteDSAPrivateKey(CryptoTokenPrivateKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
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
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
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
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
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
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }
}
