package net.rim.device.api.crypto.certificate.wtls;

import java.io.InputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;

final class WTLSCertificateFactory extends CertificateFactory {
   @Override
   public final Certificate createCertificate(InputStream stream) {
      return new WTLSCertificate(stream);
   }

   @Override
   public final Certificate createCertificate(byte[] encoding) {
      return new WTLSCertificate(encoding);
   }

   @Override
   public final String getType() {
      return "WTLS";
   }
}
