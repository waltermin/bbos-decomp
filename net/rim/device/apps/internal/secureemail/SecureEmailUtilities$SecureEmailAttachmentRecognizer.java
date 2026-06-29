package net.rim.device.apps.internal.secureemail;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModel;

class SecureEmailUtilities$SecureEmailAttachmentRecognizer implements Recognizer {
   public SecureEmailUtilities$SecureEmailAttachmentRecognizer() {
   }

   @Override
   public boolean recognize(Object o) {
      return o instanceof AddressCardModel || o instanceof CertificateAttachmentModel || o instanceof CertificateServersAttachmentModel;
   }
}
