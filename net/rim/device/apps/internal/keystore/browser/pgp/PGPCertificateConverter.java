package net.rim.device.apps.internal.keystore.browser.pgp;

import net.rim.device.apps.internal.api.crypto.certificate.CertificateConverter;

public class PGPCertificateConverter extends CertificateConverter {
   @Override
   public Object createCertificateAttachmentModel(Object initialData) {
      return new PGPKeyAttachmentModel(initialData);
   }

   @Override
   public boolean isCertificateContentType(String contentType) {
      return PGPKeyAttachmentModel.isCertificateContentType(contentType);
   }

   @Override
   public boolean isCertificateFileName(String fileName) {
      return PGPKeyAttachmentModel.isCertificateFileName(fileName);
   }
}
