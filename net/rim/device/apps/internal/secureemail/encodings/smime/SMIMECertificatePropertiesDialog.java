package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateInfoDialog;

final class SMIMECertificatePropertiesDialog extends CertificateInfoDialog {
   SMIMECertificatePropertiesDialog(Certificate certificate, long certificateProperties) {
      super(certificate, null, null, null, false, null, null, 0);
      super._certificateChains = new Object[1][];
      super._certificateChains[0] = new Object[]{certificate};
      super._bestCertificateChain = super._certificateChains[0];
      super._bestCertificateChainProperties = certificateProperties;
   }

   @Override
   protected final void buildCertificateChains() {
   }
}
