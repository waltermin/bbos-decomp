package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class KEACryptoToken implements AsymmetricCryptoToken, Persistable {
   protected KEACryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "KEA";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public int getKEACryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public String getKEACryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getKEACryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getKEACryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getKEACryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getKEAPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getKEAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractKEAPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractKEAPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractKEAPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void verifyKEACryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public CryptoTokenCryptoSystemData getKEACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public KEAKeyPair createKEAKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPublicKeyData injectKEAPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectKEAPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteKEAPublicKey(CryptoTokenPublicKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteKEAPrivateKey(CryptoTokenPrivateKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] generateKEASharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalStaticPrivateKeyData,
      CryptoTokenPrivateKeyData cryptoTokenLocalEphemeralPrivateKeyData,
      byte[] remoteStaticPublicKeyData,
      byte[] remoteEphemeralPublicKeyData
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }
}
