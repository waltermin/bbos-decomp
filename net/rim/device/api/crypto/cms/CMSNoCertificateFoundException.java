package net.rim.device.api.crypto.cms;

public class CMSNoCertificateFoundException extends CMSVerificationException {
   public CMSNoCertificateFoundException() {
   }

   public CMSNoCertificateFoundException(String message) {
      super(message);
   }
}
