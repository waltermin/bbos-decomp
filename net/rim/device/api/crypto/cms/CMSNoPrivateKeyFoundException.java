package net.rim.device.api.crypto.cms;

public class CMSNoPrivateKeyFoundException extends CMSDecryptionException {
   public CMSNoPrivateKeyFoundException() {
   }

   public CMSNoPrivateKeyFoundException(String message) {
      super(message);
   }
}
