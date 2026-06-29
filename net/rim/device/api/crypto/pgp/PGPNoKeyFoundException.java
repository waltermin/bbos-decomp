package net.rim.device.api.crypto.pgp;

public class PGPNoKeyFoundException extends PGPException {
   public PGPNoKeyFoundException() {
   }

   public PGPNoKeyFoundException(String message) {
      super(message);
   }
}
