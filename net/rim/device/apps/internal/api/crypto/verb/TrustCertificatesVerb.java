package net.rim.device.apps.internal.api.crypto.verb;

import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;

public final class TrustCertificatesVerb extends Verb {
   private String _description;
   private Certificate[] _certificateChain;
   private PrivateKey _privateKey;
   private KeyStore _keyStore;
   private boolean _requiresImport;

   public TrustCertificatesVerb(String description, Certificate[] certificateChain, KeyStore keyStore) {
      this(description, certificateChain, null, keyStore);
   }

   public TrustCertificatesVerb(String description, Certificate[] certificateChain, PrivateKey privateKey, KeyStore keyStore) {
      super(1200232);
      if (certificateChain != null && certificateChain.length != 0 && keyStore != null) {
         this._certificateChain = certificateChain;
         this._keyStore = keyStore;
         this._description = description;
         this._privateKey = privateKey;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String toString() {
      return this._description;
   }

   @Override
   public final Object invoke(Object context) {
      TrustedKeyStore trustedKeyStore = (TrustedKeyStore)TrustedKeyStore.getInstance();
      if (!trustedKeyStore.isAllowed(this._certificateChain[0])) {
         return null;
      }

      int chainLength = this._certificateChain.length;
      Certificate rootCert = this._certificateChain[chainLength - 1];
      Certificate certificateToImportAndTrust = this._certificateChain[0];
      KeyStoreTicket ticket = null;
      PrivateKey privateKey = this._privateKey;
      if (chainLength > 1 && rootCert.isRoot() && trustedKeyStore.isAllowed(rootCert)) {
         Certificate[] certificatesToImport = this.getCertificatesToImport();
         if (certificatesToImport != null && certificatesToImport.length > 0 && Dialog.ask(3, CryptoCommonResources.getString(28), 4) == 4) {
            certificateToImportAndTrust = rootCert;
            if (this._requiresImport) {
               try {
                  ticket = this._keyStore.getTicket();
               } finally {
                  ;
               }
            }

            PrivateKey[] privateKeys = new PrivateKey[certificatesToImport.length];
            privateKeys[0] = this._privateKey;
            CertificateImporterFactory.importCertificates(certificatesToImport, privateKeys, null, this._keyStore, ticket);
            privateKey = null;
         }
      }

      String label = certificateToImportAndTrust.getSubjectFriendlyName();
      CertificateImporterFactory.importAndTrustCertificate(certificateToImportAndTrust, privateKey, label, this._keyStore, ticket);
      KeyStoreTicket var12 = null;
      return null;
   }

   private final Certificate[] getCertificatesToImport() {
      int numCerts = this._certificateChain.length;
      if (numCerts < 2) {
         return null;
      }

      Certificate[] certificatesToImport = new Certificate[numCerts - 1];
      this._requiresImport = false;

      for (int i = this._certificateChain.length - 2; i >= 0; i--) {
         if (!this.isValidCertificateToImport(this._certificateChain[i])) {
            return null;
         }

         certificatesToImport[i] = this._certificateChain[i];
         if (!this._keyStore.isMember(this._certificateChain[i])) {
            this._requiresImport = true;
         }
      }

      return certificatesToImport;
   }

   private final boolean isValidCertificateToImport(Certificate cert) {
      return cert != null && !cert.isRoot();
   }
}
