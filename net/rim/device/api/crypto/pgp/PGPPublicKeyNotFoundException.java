package net.rim.device.api.crypto.pgp;

public class PGPPublicKeyNotFoundException extends PGPVerificationException {
   public PGPPublicKeyNotFoundException() {
   }

   public PGPPublicKeyNotFoundException(String message) {
      super(message);
   }
}
