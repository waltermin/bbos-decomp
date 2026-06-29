package net.rim.device.api.crypto.certificate;

import java.util.Hashtable;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.system.ApplicationRegistry;

public class CertificateSummaryDataSyncModelFactory {
   private static final long HASHTABLE_ID = -7199809812455420773L;
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(-7199809812455420773L);

   public static boolean register(CertificateSummaryDataSyncModelFactory factory) {
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

   public static CertificateSummaryDataSyncModel createCertificateSummaryDataSyncModel(KeyStoreData keyStoreData) {
      if (keyStoreData == null) {
         throw new Object();
      }

      Certificate certificate = keyStoreData.getCertificate();
      if (certificate == null) {
         return null;
      }

      String certificateType = certificate.getType();
      if (certificateType == null) {
         throw new Object();
      }

      CertificateSummaryDataSyncModelFactory factory = (CertificateSummaryDataSyncModelFactory)_hashtable.get(certificateType);
      return factory == null ? null : factory.createCertificateSummaryDataSyncModelInternal(certificate, keyStoreData.isPrivateKeySet());
   }

   protected CertificateSummaryDataSyncModel createCertificateSummaryDataSyncModelInternal(Certificate _1, boolean _2) {
      throw null;
   }

   protected String getType() {
      throw null;
   }
}
