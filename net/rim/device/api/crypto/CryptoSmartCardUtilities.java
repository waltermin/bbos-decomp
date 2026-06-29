package net.rim.device.api.crypto;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.AssociatedData;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.SmartCardFactory;
import net.rim.device.api.smartcard.SmartCardReaderFactory;
import net.rim.device.internal.ui.component.PleaseWaitDialog;
import net.rim.vm.Array;

public final class CryptoSmartCardUtilities {
   private static final boolean DEBUG = false;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");

   private CryptoSmartCardUtilities() {
   }

   public static final boolean isImportCertificatesAvailable() {
      return SmartCardFactory.getNumSmartCards() > 0 && SmartCardReaderFactory.getNumSmartCardReaders() > 0;
   }

   public static final boolean importCertificates(KeyStore importKeyStore, KeyStore trustedKeyStore, String pleaseWaitMessage) {
      CryptoSmartCardUtilities$DoImportWork importWorker = new CryptoSmartCardUtilities$DoImportWork(importKeyStore, trustedKeyStore, null);
      PleaseWaitDialog pleaseWait = new PleaseWaitDialog(pleaseWaitMessage, importWorker);
      pleaseWait.display();
      return importWorker.wereCertificatesAdded();
   }

   public static final AssociatedData[] getSmartCardAssociatedData(Certificate cert) {
      AssociatedData[] associatedData = CertificateUtilities.getEmailAssociatedDataArray(cert);
      if (associatedData == null) {
         associatedData = new AssociatedData[1];
      } else {
         Array.resize(associatedData, associatedData.length + 1);
      }

      associatedData[associatedData.length - 1] = new AssociatedData(-4699629744920546763L, new byte[0]);
      return associatedData;
   }
}
