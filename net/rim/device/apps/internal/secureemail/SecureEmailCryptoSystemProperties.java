package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoSystemProperties;

public class SecureEmailCryptoSystemProperties extends CryptoSystemProperties {
   @Override
   public boolean isCryptoSystemStrong(CryptoSystem cryptoSystem) {
      return cryptoSystem == null ? false : this.isCryptoSystemStrong(cryptoSystem.getAlgorithm(), cryptoSystem.getBitLength(), cryptoSystem);
   }

   public boolean isCryptoSystemStrong(String algorithm, int bitLength) {
      return this.isCryptoSystemStrong(algorithm, bitLength, null);
   }

   protected boolean isCryptoSystemStrong(String _1, int _2, CryptoSystem _3) {
      throw null;
   }
}
