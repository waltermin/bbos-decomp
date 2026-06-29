package net.rim.device.apps.internal.keystore.browser.certificate;

import net.rim.device.apps.internal.api.crypto.certificate.CertificateConverter;

public class X509WTLSCertificateConverter extends CertificateConverter {
   @Override
   public Object createCertificateAttachmentModel(Object initialData) {
      return new X509WTLSCertificateAttachmentModel(initialData);
   }

   @Override
   public boolean isCertificateContentType(String contentType) {
      return X509WTLSCertificateAttachmentModel.isCertificateContentType(contentType);
   }

   @Override
   public boolean isCertificateFileName(String fileName) {
      return X509WTLSCertificateAttachmentModel.isCertificateFileName(fileName);
   }
}
