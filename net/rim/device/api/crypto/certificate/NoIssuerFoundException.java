package net.rim.device.api.crypto.certificate;

public class NoIssuerFoundException extends CertificateVerificationException {
   public NoIssuerFoundException() {
   }

   public NoIssuerFoundException(String msg) {
      super(msg);
   }
}
