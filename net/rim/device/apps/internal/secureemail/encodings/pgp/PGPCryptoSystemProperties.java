package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.apps.internal.secureemail.SecureEmailCryptoSystemProperties;

class PGPCryptoSystemProperties extends SecureEmailCryptoSystemProperties {
   @Override
   protected boolean isCryptoSystemStrong(String algorithm, int bitLength, CryptoSystem cryptoSystem) {
      if (algorithm == null) {
         return false;
      } else if (algorithm.equals("RSA")) {
         int minsize = ITPolicy.getInteger(26, 6, 1024);
         return bitLength >= minsize;
      } else if (algorithm.equals("DH")) {
         int minsize = ITPolicy.getInteger(26, 1, 1024);
         return bitLength >= minsize;
      } else if (algorithm.equals("DSA")) {
         int minsize = ITPolicy.getInteger(26, 7, 1024);
         return bitLength >= minsize;
      } else {
         return cryptoSystem != null ? cryptoSystem.isStrong() : false;
      }
   }
}
