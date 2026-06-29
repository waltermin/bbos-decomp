package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.apps.internal.api.crypto.certificate.CertificateConverter;

public class PKCS12CertificateKeyConverter extends CertificateConverter {
   @Override
   public Object createCertificateAttachmentModel(Object initialData) {
      return new PKCS12CertificateKeyAttachmentModel(initialData);
   }

   @Override
   public boolean isCertificateContentType(String contentType) {
      return PKCS12CertificateKeyAttachmentModel.isCertificateContentType(contentType);
   }

   @Override
   public boolean isCertificateFileName(String fileName) {
      return PKCS12CertificateKeyAttachmentModel.isCertificateFileName(fileName);
   }
}
