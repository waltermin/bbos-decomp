package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public interface CryptoToken extends Persistable {
   String getAlgorithm();

   boolean providesUserAuthentication();
}
