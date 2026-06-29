package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory$CertificateImporterContext;
import net.rim.device.api.crypto.certificate.CertificateResources;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.pgp.PGPPrivateKey;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public class PGPCertificateImporterFactory extends CertificateImporterFactory {
   @Override
   protected String getType() {
      return "PGP";
   }

   @Override
   protected void importCertificateInternal(
      Certificate certificate,
      PrivateKey privateKey,
      String label,
      KeyStore keyStore,
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext
   ) {
      if (certificate instanceof PGPCertificate && (privateKey == null || privateKey instanceof PGPPrivateKey)) {
         PGPCertificate pgpCert = (PGPCertificate)certificate;
         PGPPrivateKey pgpPrivateKey = (PGPPrivateKey)privateKey;
         CertificateImporterFactory.importCertificateDefault(certificate, privateKey, label, keyStore, certificateImporterContext);
         if (certificateImporterContext._importEmbeddedCertificates != 0 && pgpCert.containsEmbeddedX509Certificates()) {
            if (certificateImporterContext._importEmbeddedCertificates == 2) {
               if (!SimpleChoiceDialog.askYesNoQuestionOnBackground(CertificateResources.getString(226))) {
                  certificateImporterContext._importEmbeddedCertificates = 0;
                  return;
               }

               certificateImporterContext._importEmbeddedCertificates = 1;
            }

            KeyStore x509KeyStore = DeviceKeyStore.getInstance();
            Certificate[] certsInsertedWithPrivateData = new Certificate[0];
            if (privateKey != null) {
               Certificate[] parentKeyX509Certs = pgpCert.getEmbeddedX509Certificates(pgpPrivateKey.getKeyID());
               if (parentKeyX509Certs != null) {
                  for (int i = 0; i < parentKeyX509Certs.length; i++) {
                     CertificateImporterFactory.importCertificateByType(
                        parentKeyX509Certs[i],
                        pgpPrivateKey.getParentKey(),
                        parentKeyX509Certs[i].getSubjectFriendlyName(),
                        x509KeyStore,
                        certificateImporterContext
                     );
                     Arrays.add(certsInsertedWithPrivateData, parentKeyX509Certs[i]);
                  }
               }

               PrivateKey[] subKeys = pgpPrivateKey.getSubKeys();

               for (int i = 0; i < subKeys.length; i++) {
                  Certificate[] subKeyX509Certs = pgpCert.getEmbeddedX509Certificates(pgpPrivateKey.getSubKeyIDs()[i]);
                  if (subKeyX509Certs != null) {
                     for (int j = 0; j < subKeyX509Certs.length; j++) {
                        CertificateImporterFactory.importCertificateByType(
                           subKeyX509Certs[j], subKeys[i], subKeyX509Certs[j].getSubjectFriendlyName(), x509KeyStore, certificateImporterContext
                        );
                        Arrays.add(certsInsertedWithPrivateData, subKeyX509Certs[j]);
                     }
                  }
               }
            }

            Certificate[] x509Certs = pgpCert.getEmbeddedX509Certificates();

            for (int i = 0; i < x509Certs.length; i++) {
               if (!Arrays.contains(certsInsertedWithPrivateData, x509Certs[i])) {
                  CertificateImporterFactory.importCertificateByType(
                     x509Certs[i], null, x509Certs[i].getSubjectFriendlyName(), x509KeyStore, certificateImporterContext
                  );
               }
            }

            if (pgpPrivateKey != null) {
               pgpPrivateKey.clearPasswordTicket();
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
