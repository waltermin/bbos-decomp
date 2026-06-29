package net.rim.device.api.crypto.certificate.x509;

import java.io.InputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;

final class X509CertificateFactory extends CertificateFactory {
   @Override
   public final Certificate createCertificate(InputStream stream) {
      return new X509Certificate(stream);
   }

   @Override
   public final Certificate createCertificate(byte[] encoding) {
      return new X509Certificate(encoding);
   }

   @Override
   public final String getType() {
      return "X509";
   }
}
