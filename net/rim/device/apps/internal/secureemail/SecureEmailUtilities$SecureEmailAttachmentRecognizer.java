package net.rim.device.apps.internal.secureemail;

import net.rim.device.apps.api.framework.model.Recognizer;

class SecureEmailUtilities$SecureEmailAttachmentRecognizer implements Recognizer {
   public SecureEmailUtilities$SecureEmailAttachmentRecognizer() {
   }

   @Override
   public boolean recognize(Object o) {
      return o instanceof Object || o instanceof Object || o instanceof CertificateServersAttachmentModel;
   }
}
