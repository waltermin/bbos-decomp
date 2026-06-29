package net.rim.device.api.crypto;

public interface AsymmetricCryptoToken extends CryptoToken {
   int KEY_GENERATION = 1;
   int PUBLIC_KEY_OPERATION = 2;
   int PRIVATE_KEY_OPERATION = 4;

   boolean isSupported(CryptoSystem var1, int var2);
}
