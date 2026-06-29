package net.rim.device.api.crypto.certificate;

import java.util.Hashtable;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.system.ApplicationRegistry;

public class CertificateImporterFactory {
   private static final long HASHTABLE_ID = -3725666709869900501L;
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(-3725666709869900501L);
   protected static CertificateImporterFactory _defaultCertificateImporterFactory = new DefaultCertificateImporterFactory();

   protected CertificateImporterFactory() {
   }

   public static synchronized boolean register(CertificateImporterFactory factory) {
      if (factory == null) {
         throw new Object();
      }

      String type = factory.getType();
      if (type == null) {
         throw new Object();
      }

      if (_hashtable.containsKey(type)) {
         return false;
      }

      _hashtable.put(type, factory);
      return true;
   }

   public static boolean importCertificate(Certificate certificate, PrivateKey privateKey, String label, KeyStore keyStore, KeyStoreTicket keyStoreTicket) {
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext = new CertificateImporterFactory$CertificateImporterContext();
      certificateImporterContext.addKeyStoreTicket(keyStore, keyStoreTicket);
      return importCertificateByType(certificate, privateKey, label, keyStore, certificateImporterContext);
   }

   public static boolean importAndTrustCertificate(
      Certificate certificate, PrivateKey privateKey, String label, KeyStore keyStore, KeyStoreTicket keyStoreTicket
   ) {
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext = new CertificateImporterFactory$CertificateImporterContext();
      certificateImporterContext.addKeyStoreTicket(keyStore, keyStoreTicket);
      certificateImporterContext._trustCertificate = 1;
      return importCertificateByType(certificate, privateKey, label, keyStore, certificateImporterContext);
   }

   public static boolean importCertificates(
      Certificate[] certificates, PrivateKey[] privateKeys, String[] labels, KeyStore keyStore, KeyStoreTicket keyStoreTicket
   ) {
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext = new CertificateImporterFactory$CertificateImporterContext();
      certificateImporterContext.addKeyStoreTicket(keyStore, keyStoreTicket);
      return importCertificatesInternal(certificates, privateKeys, labels, keyStore, keyStoreTicket, certificateImporterContext);
   }

   public static boolean importAndTrustCertificates(
      Certificate[] certificates, PrivateKey[] privateKeys, String[] labels, KeyStore keyStore, KeyStoreTicket keyStoreTicket
   ) {
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext = new CertificateImporterFactory$CertificateImporterContext();
      certificateImporterContext.addKeyStoreTicket(keyStore, keyStoreTicket);
      certificateImporterContext._trustCertificate = 1;
      return importCertificatesInternal(certificates, privateKeys, labels, keyStore, keyStoreTicket, certificateImporterContext);
   }

   protected static boolean importCertificatesInternal(
      Certificate[] certificates,
      PrivateKey[] privateKeys,
      String[] labels,
      KeyStore keyStore,
      KeyStoreTicket keyStoreTicket,
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext
   ) {
      if (certificates != null && keyStore != null) {
         int numCerts = certificates.length;
         if (privateKeys == null) {
            privateKeys = new PrivateKey[numCerts];

            for (int i = 0; i < numCerts; i++) {
               privateKeys[i] = null;
            }
         } else if (privateKeys.length != numCerts) {
            throw new Object();
         }

         if (labels == null) {
            labels = new Object[numCerts];

            for (int i = 0; i < numCerts; i++) {
               if (certificates[i] == null) {
                  throw new Object();
               }

               labels[i] = certificates[i].getSubjectFriendlyName();
            }
         } else if (labels.length != numCerts) {
            throw new Object();
         }

         for (int i = 0; i < numCerts; i++) {
            if (!importCertificateByType(certificates[i], privateKeys[i], labels[i], keyStore, certificateImporterContext)) {
               return false;
            }
         }

         return true;
      } else {
         throw new Object();
      }
   }

   protected static boolean importCertificateByType(
      Certificate certificate,
      PrivateKey privateKey,
      String label,
      KeyStore keyStore,
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext
   ) {
      if (certificate != null && keyStore != null) {
         String certificateType = certificate.getType();
         CertificateImporterFactory factory = (CertificateImporterFactory)_hashtable.get(certificateType);
         if (factory == null) {
            factory = _defaultCertificateImporterFactory;
         }

         try {
            factory.importCertificateInternal(certificate, privateKey, label, keyStore, certificateImporterContext);
            return true;
         } finally {
            ;
         }
      } else {
         throw new Object();
      }
   }

   protected static boolean importCertificateDefault(
      Certificate certificate,
      PrivateKey privateKey,
      String label,
      KeyStore keyStore,
      CertificateImporterFactory$CertificateImporterContext certificateImporterContext
   ) {
      if (certificate != null && keyStore != null) {
         try {
            _defaultCertificateImporterFactory.importCertificateInternal(certificate, privateKey, label, keyStore, certificateImporterContext);
            return true;
         } finally {
            ;
         }
      } else {
         throw new Object();
      }
   }

   protected void importCertificateInternal(Certificate _1, PrivateKey _2, String _3, KeyStore _4, CertificateImporterFactory$CertificateImporterContext _5) {
      throw null;
   }

   protected String getType() {
      throw null;
   }
}
