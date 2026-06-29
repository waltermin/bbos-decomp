package net.rim.device.api.crypto;

public interface SymmetricKey extends Key {
   int getLength();

   byte[] getData();

   int getBitLength();

   SymmetricCryptoToken getSymmetricCryptoToken();
}
