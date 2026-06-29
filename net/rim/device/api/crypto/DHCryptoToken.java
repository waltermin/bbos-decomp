package net.rim.device.api.crypto;

public class DHCryptoToken implements AsymmetricCryptoToken {
   protected DHCryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "DH";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public int getDHCryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public String getDHCryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getDHCryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getDHCryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] getDHCryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getDHPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getDHPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public int getDHPrivateKeyMinRandomBits(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractDHPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractDHPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public byte[] extractDHPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) {
      throw new Object();
   }

   public CryptoTokenCryptoSystemData getDHCryptoSystemData(byte[] p, byte[] q, byte[] g, int privateKeyMinRandomBits, String name) {
      throw new Object();
   }

   public void verifyDHCryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public DHCryptoSystem[] getSuggestedDHCryptoSystems() {
      throw new Object();
   }

   public DHKeyPair createDHKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) {
      throw new Object();
   }

   public CryptoTokenPublicKeyData injectDHPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public CryptoTokenPrivateKeyData injectDHPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) {
      throw new Object();
   }

   public void deleteDHPublicKey(CryptoTokenPublicKeyData data) {
      throw new Object();
   }

   public void deleteDHPrivateKey(CryptoTokenPrivateKeyData data) {
      throw new Object();
   }

   public byte[] generateDHSharedSecret(
      CryptoTokenCryptoSystemData cryptoSystemData, CryptoTokenPrivateKeyData localPrivateKeyData, byte[] remotePublicKeyData, boolean useCofactor
   ) {
      throw new Object();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }
}
