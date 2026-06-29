package net.rim.device.api.crypto;

public class CryptoSystemProperties {
   public boolean isCryptoSystemStrong(CryptoSystem cryptoSystem) {
      if (cryptoSystem == null) {
         throw new Object();
      } else {
         return cryptoSystem.isStrong();
      }
   }
}
