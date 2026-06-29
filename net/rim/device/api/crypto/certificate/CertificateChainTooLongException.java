package net.rim.device.api.crypto.certificate;

public class CertificateChainTooLongException extends CertificateException {
   public CertificateChainTooLongException() {
   }

   public CertificateChainTooLongException(String msg) {
      super(msg);
   }
}
