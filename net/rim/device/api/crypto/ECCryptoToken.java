package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class ECCryptoToken implements AsymmetricCryptoToken, Persistable {
   protected ECCryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "EC";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public String getECCryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getECCryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getECCryptoSystemFieldLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getECCryptoSystemBasePoint(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getECCryptoSystemGroupOrder(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getECCryptoSystemA(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getECCryptoSystemB(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getECCryptoSystemCofactor(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getECCryptoSystemFieldReductor(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getECPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData, boolean compressed) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getECPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData, boolean compress) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData, boolean compress) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractECPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public ECCryptoSystem[] getSuggestedECCryptoSystems() throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenCryptoSystemData getECCryptoSystemData(String name) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void verifyECCryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public ECKeyPair createECKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPublicKeyData injectECPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectECPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteECPublicKey(CryptoTokenPublicKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteECPrivateKey(CryptoTokenPrivateKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] generateECDHSharedSecret(
      CryptoTokenCryptoSystemData cryptoSystemData, CryptoTokenPrivateKeyData localPrivateKeyData, byte[] remotePublicKeyData, boolean useCofactor
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void signECDSA(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData,
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

   public void signECDSA(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData,
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

   public boolean verifyECDSA(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPublicKeyData cryptoTokenPublicKeyData,
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

   public byte[] generateECMQVSharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalStaticPrivateKeyData,
      CryptoTokenPrivateKeyData cryptoTokenLocalEphemeralPrivateKeyData,
      CryptoTokenPublicKeyData cryptoTokenLocalEphemeralPublicKeyData,
      byte[] remoteStaticPublicKeyData,
      byte[] remoteEphemeralPublicKeyData,
      boolean useCofactor
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void signECNR(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData,
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

   public boolean verifyECNR(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPublicKeyData cryptoTokenPublicKeyData,
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
