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

   public String getECCryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getECCryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getECCryptoSystemFieldLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getECCryptoSystemBasePoint(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getECCryptoSystemGroupOrder(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getECCryptoSystemA(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getECCryptoSystemB(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getECCryptoSystemCofactor(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getECCryptoSystemFieldReductor(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getECPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData, boolean compressed) {
      throw new Object();
   }

   public int getECPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData, boolean compress) {
      throw new Object();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractECPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData, boolean compress) {
      throw new Object();
   }

   public byte[] extractECPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public ECCryptoSystem[] getSuggestedECCryptoSystems() {
      throw new Object();
   }

   public CryptoTokenCryptoSystemData getECCryptoSystemData(String name) {
      throw new Object();
   }

   public void verifyECCryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public ECKeyPair createECKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public CryptoTokenPublicKeyData injectECPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectECPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public void deleteECPublicKey(CryptoTokenPublicKeyData data) {
      throw new Object();
   }

   public void deleteECPrivateKey(CryptoTokenPrivateKeyData data) {
      throw new Object();
   }

   public byte[] generateECDHSharedSecret(
      CryptoTokenCryptoSystemData cryptoSystemData, CryptoTokenPrivateKeyData localPrivateKeyData, byte[] remotePublicKeyData, boolean useCofactor
   ) {
      throw new Object();
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
   ) {
      throw new Object();
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
   ) {
      throw new Object();
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
   ) {
      throw new Object();
   }

   public byte[] generateECMQVSharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalStaticPrivateKeyData,
      CryptoTokenPrivateKeyData cryptoTokenLocalEphemeralPrivateKeyData,
      CryptoTokenPublicKeyData cryptoTokenLocalEphemeralPublicKeyData,
      byte[] remoteStaticPublicKeyData,
      byte[] remoteEphemeralPublicKeyData,
      boolean useCofactor
   ) {
      throw new Object();
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
   ) {
      throw new Object();
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
   ) {
      throw new Object();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }
}
