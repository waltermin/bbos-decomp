package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.internal.ui.component.PopupDialog;

class DefaultCertificateInfoDialogFactory extends CertificateInfoDialogFactory {
   @Override
   protected String getType() {
      return null;
   }

   @Override
   protected PopupDialog createCertificateInfoDialogInternal(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      long style
   ) {
      return new CertificateInfoDialog(certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, style);
   }
}
