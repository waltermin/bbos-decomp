package net.rim.device.api.crypto;

public interface PublicKey extends Key {
   CryptoSystem getCryptoSystem();

   void verify();
}
