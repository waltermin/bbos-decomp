package net.rim.device.api.crypto.pgp;

public class PGPIncompleteKeyException extends PGPEncodingException {
   public PGPIncompleteKeyException() {
   }

   public PGPIncompleteKeyException(String msg) {
      super(msg);
   }
}
