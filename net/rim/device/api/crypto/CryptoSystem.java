package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public interface CryptoSystem extends Persistable {
   String getAlgorithm();

   int getBitLength();

   String getName();

   boolean isStrong();

   void verify();

   KeyPair createKeyPair();

   AsymmetricCryptoToken getAsymmetricCryptoToken();
}
