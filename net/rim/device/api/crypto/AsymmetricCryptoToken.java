package net.rim.device.api.crypto;

public interface AsymmetricCryptoToken extends CryptoToken {
   int KEY_GENERATION;
   int PUBLIC_KEY_OPERATION;
   int PRIVATE_KEY_OPERATION;

   boolean isSupported(CryptoSystem var1, int var2);
}
