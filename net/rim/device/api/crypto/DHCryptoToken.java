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

   public int getDHCryptoSystemBitLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public String getDHCryptoSystemName(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getDHCryptoSystemP(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getDHCryptoSystemQ(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] getDHCryptoSystemG(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getDHPublicKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getDHPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public int getDHPrivateKeyMinRandomBits(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractDHPublicKeyData(CryptoTokenPublicKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractDHPublicKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractDHPrivateKeyData(CryptoTokenPrivateKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenCryptoSystemData getDHCryptoSystemData(byte[] p, byte[] q, byte[] g, int privateKeyMinRandomBits, String name) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void verifyDHCryptoSystemData(CryptoTokenCryptoSystemData cryptoSystemData) {
   }

   public DHCryptoSystem[] getSuggestedDHCryptoSystems() throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public DHKeyPair createDHKeyPair(CryptoTokenCryptoSystemData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPublicKeyData injectDHPublicKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenPrivateKeyData injectDHPrivateKey(CryptoTokenCryptoSystemData cryptoSystemData, byte[] data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteDHPublicKey(CryptoTokenPublicKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteDHPrivateKey(CryptoTokenPrivateKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] generateDHSharedSecret(
      CryptoTokenCryptoSystemData cryptoSystemData, CryptoTokenPrivateKeyData localPrivateKeyData, byte[] remotePublicKeyData, boolean useCofactor
   ) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   @Override
   public boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return false;
   }
}
