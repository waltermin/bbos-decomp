package net.rim.device.api.crypto;

public interface PrivateKey extends Key {
   CryptoSystem getCryptoSystem();

   void verify();
}
