package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.keystore.AssociatedData;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public class DefaultCertificateImporterFactory extends CertificateImporterFactory {
   @Override
   protected String getType() {
      return null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void importCertificateInternal(
      Certificate certificate,
      PrivateKey privateKey,
      String label,
      KeyStore keyStore,
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext
   ) {
      if (label == null) {
         Boolean tempBoolean = (Boolean)certificate.getInformation(-7341435958452683242L, null, null);
         if (tempBoolean != null && !tempBoolean) {
            label = certificate.getSubjectFriendlyName();
         }
      }

      if (keyStore.isMember(certificate)) {
         if (!keyStore.existsIndex(-2038609988711824737L)) {
            keyStore.addIndex(new CertificateKeyStoreIndex());
         }

         Enumeration keyStoreElements = keyStore.elements(-2038609988711824737L, certificate);
         if (keyStoreElements.hasMoreElements()) {
            KeyStoreData keyStoreData = (KeyStoreData)keyStoreElements.nextElement();
            label = keyStoreData.getLabel();
            if (privateKey == null) {
               this.trustCertificateInternal(certificate, Boolean.FALSE, label, certificateImporterContext);
               return;
            }

            if (keyStoreData.isPrivateKeySet()) {
               this.trustCertificateInternal(certificate, Boolean.TRUE, label, certificateImporterContext);
               return;
            }

            KeyStoreTicket keyStoreTicket = certificateImporterContext.obtainKeyStoreTicket(keyStore);
            keyStore.removeKey(keyStoreData, keyStoreTicket);
         }
      }

      KeyStoreTicket keyStoreTicket = certificateImporterContext.obtainKeyStoreTicket(keyStore);
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         if (certificateImporterContext._verifyRootsOnImport) {
            if (certificate.isRoot()) {
               certificate.verify();
               var10 = false;
            } else {
               var10 = false;
            }
         } else {
            var10 = false;
         }
      } finally {
         if (var10) {
            label87: {
               if (!SimpleChoiceDialog.askYesNoQuestionOnBackground(CertificateResources.getString(16))) {
                  return;
               }

               certificateImporterContext._verifyRootsOnImport = false;
               break label87;
            }
         }
      }

      AssociatedData[] emailAddresses = CertificateUtilities.getEmailAssociatedDataArray(certificate);
      KeyStoreData importedCertificateData = keyStore.set(emailAddresses, label, privateKey, null, 3, certificate, null, keyStoreTicket);
      if (label == null && importedCertificateData != null) {
         label = importedCertificateData.getLabel();
      }

      this.trustCertificateInternal(certificate, new Boolean(privateKey != null), label, emailAddresses, certificateImporterContext);
   }

   protected boolean trustCertificateInternal(
      Certificate certificate, Boolean privateKeyImported, String label, CertificateImporterFactory$CertificateImporterContext certificateImporterContext
   ) {
      AssociatedData[] emailAddresses = CertificateUtilities.getEmailAssociatedDataArray(certificate);
      return this.trustCertificateInternal(certificate, privateKeyImported, label, emailAddresses, certificateImporterContext);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected boolean trustCertificateInternal(
      Certificate certificate,
      Boolean privateKeyImported,
      String label,
      AssociatedData[] emailAddresses,
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext
   ) {
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         TrustedKeyStore e = (TrustedKeyStore)TrustedKeyStore.getInstance();
         if (!e.isMember(certificate)) {
            if (e.isAllowed(certificate)) {
               Boolean promptForTrust = (Boolean)certificate.getInformation(-1188891808812199856L, privateKeyImported, Boolean.FALSE);
               if (certificateImporterContext._trustCertificate != 1) {
                  if (certificateImporterContext._trustCertificate != 2) {
                     var10 = false;
                     return false;
                  }

                  if (promptForTrust == null) {
                     var10 = false;
                     return false;
                  }

                  if (!promptForTrust) {
                     var10 = false;
                     return false;
                  }

                  if (!SimpleChoiceDialog.askYesNoQuestionOnBackground(CertificateResources.getString(21), label)) {
                     var10 = false;
                     return false;
                  }
               }

               KeyStoreTicket trustedKeyStoreTicket = certificateImporterContext.obtainKeyStoreTicket(e);
               e.set(emailAddresses, label, null, null, 2, certificate, null, trustedKeyStoreTicket);
               return true;
            } else {
               var10 = false;
            }
         } else {
            var10 = false;
         }
      } finally {
         if (var10) {
            BackgroundDialog.showMessage(CertificateResources.getString(22));
            return false;
         }
      }

      return false;
   }
}
