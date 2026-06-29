package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.apps.internal.secureemail.SecureEmailCryptoSystemProperties;

class SMIMECryptoSystemProperties extends SecureEmailCryptoSystemProperties {
   @Override
   protected boolean isCryptoSystemStrong(String algorithm, int bitLength, CryptoSystem cryptoSystem) {
      if (algorithm == null) {
         return false;
      } else if (algorithm.equals("RSA")) {
         int minsize = ITPolicy.getInteger(25, 3, 1024);
         return bitLength >= minsize;
      } else if (algorithm.equals("DH")) {
         int minsize = ITPolicy.getInteger(25, 4, 1024);
         return bitLength >= minsize;
      } else if (algorithm.equals("EC")) {
         int minsize = ITPolicy.getInteger(25, 5, 163);
         return bitLength >= minsize;
      } else if (algorithm.equals("DSA")) {
         int minsize = ITPolicy.getInteger(25, 11, 1024);
         return bitLength >= minsize;
      } else {
         return cryptoSystem != null ? cryptoSystem.isStrong() : false;
      }
   }
}
