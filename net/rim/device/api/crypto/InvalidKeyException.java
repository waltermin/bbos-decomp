package net.rim.device.api.crypto;

public class InvalidKeyException extends CryptoException {
   public InvalidKeyException() {
   }

   public InvalidKeyException(String msg) {
      super(msg);
   }
}
