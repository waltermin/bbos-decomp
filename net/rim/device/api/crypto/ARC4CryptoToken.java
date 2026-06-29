package net.rim.device.api.crypto;

class ARC4CryptoToken implements SymmetricCryptoToken {
   @Override
   public String getAlgorithm() {
      return "ARC4";
   }

   @Override
   public boolean providesUserAuthentication() {
      return false;
   }
}
