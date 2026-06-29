package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateInfoDialogFactory;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.internal.ui.component.PopupDialog;

final class PGPCertificateInfoDialogFactory extends CertificateInfoDialogFactory {
   @Override
   protected final String getType() {
      return "PGP";
   }

   @Override
   protected final PopupDialog createCertificateInfoDialogInternal(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      long style
   ) {
      return new PGPCertificateInfoDialog(certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, style);
   }
}
