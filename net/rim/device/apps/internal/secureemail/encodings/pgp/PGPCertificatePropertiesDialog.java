package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificateInfoDialog;

final class PGPCertificatePropertiesDialog extends PGPCertificateInfoDialog {
   PGPCertificatePropertiesDialog(Certificate certificate, long certificateProperties) {
      super(certificate, null, null, null, false, null, null, 0);
      super._certificateChains = new Certificate[1][];
      super._certificateChains[0] = new Certificate[]{certificate};
      super._bestCertificateChain = super._certificateChains[0];
      super._bestCertificateChainProperties = certificateProperties;
   }

   @Override
   protected final void buildCertificateChains() {
   }
}
