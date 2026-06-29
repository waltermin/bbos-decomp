package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.apps.internal.api.crypto.certificate.CertificateConverter;

public class PKCS7CertificateConverter extends CertificateConverter {
   @Override
   public Object createCertificateAttachmentModel(Object initialData) {
      return new PKCS7CertificateAttachmentModel(initialData);
   }

   @Override
   public boolean isCertificateContentType(String contentType) {
      return PKCS7CertificateAttachmentModel.isCertificateContentType(contentType);
   }

   @Override
   public boolean isCertificateFileName(String fileName) {
      return PKCS7CertificateAttachmentModel.isCertificateFileName(fileName);
   }
}
