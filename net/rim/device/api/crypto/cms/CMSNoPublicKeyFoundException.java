package net.rim.device.api.crypto.cms;

public class CMSNoPublicKeyFoundException extends CMSVerificationException {
   public CMSNoPublicKeyFoundException() {
   }

   public CMSNoPublicKeyFoundException(String message) {
      super(message);
   }
}
