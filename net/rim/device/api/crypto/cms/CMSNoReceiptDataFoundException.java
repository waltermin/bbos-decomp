package net.rim.device.api.crypto.cms;

public class CMSNoReceiptDataFoundException extends CMSVerificationException {
   public CMSNoReceiptDataFoundException() {
   }

   public CMSNoReceiptDataFoundException(String message) {
      super(message);
   }
}
