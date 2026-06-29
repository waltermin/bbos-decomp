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

   public int getKEACryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public String getKEACryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getKEACryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getKEACryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getKEACryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getKEAPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getKEAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractKEAPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractKEAPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractKEAPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public void verifyKEACryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public CryptoTokenCryptoSystemData getKEACryptoSystemData(byte[] p, byte[] q, byte[] g, String name) {
      throw new Object();
   }

   public KEAKeyPair createKEAKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public CryptoTokenPublicKeyData injectKEAPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectKEAPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public void deleteKEAPublicKey(CryptoTokenPublicKeyData data) {
      throw new Object();
   }

   public void deleteKEAPrivateKey(CryptoTokenPrivateKeyData data) {
      throw new Object();
   }

   public byte[] generateKEASharedSecret(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenLocalStaticPrivateKeyData,
      CryptoTokenPrivateKeyData cryptoTokenLocalEphemeralPrivateKeyData,
      byte[] remoteStaticPublicKeyData,
      byte[] remoteEphemeralPublicKeyData
   ) {
      throw new Object();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }
}
