package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class HMACCryptoToken implements SymmetricCryptoToken, Persistable {
   protected HMACCryptoToken() {
   }

   @Override
   public final String getAlgorithm() {
      return "HMAC";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }

   public int extractKeyLength(CryptoTokenMACKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public byte[] extractKeyData(CryptoTokenMACKeyData cryptoTokenData) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenMACContext initialize(CryptoTokenMACKeyData keyData, Digest digest) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void reset(CryptoTokenMACContext context) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public int getMAC(CryptoTokenMACContext context, byte[] buffer, int offset, boolean reset) throws CryptoTokenException {
      throw new CryptoTokenException();
   }

   public CryptoTokenMACKeyData createKey(int length) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public CryptoTokenMACKeyData injectKey(byte[] key, int offset, int length) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }

   public void deleteKey(CryptoTokenMACKeyData data) throws CryptoUnsupportedOperationException {
      throw new CryptoUnsupportedOperationException();
   }
}
