package net.rim.device.api.crypto.certificate;

import java.util.Hashtable;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;

public class CertificateSummaryFieldFactory {
   private static final long HASHTABLE_ID = 4797364130951242112L;
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(4797364130951242112L);
   private static CertificateSummaryFieldFactory _defaultCertificateSummaryFieldFactory = new DefaultCertificateSummaryFieldFactory();

   public Field createCertificateSummaryPageField(Certificate _1) {
      throw null;
   }

   public static CertificateSummaryFieldFactory getCertificateSummaryFieldFactory(Certificate certificate) {
      if (certificate == null) {
         throw new IllegalArgumentException();
      }

      String certificateType = certificate.getType();
      CertificateSummaryFieldFactory factory = (CertificateSummaryFieldFactory)_hashtable.get(certificateType);
      if (factory == null) {
         factory = _defaultCertificateSummaryFieldFactory;
      }

      return factory;
   }

   public static boolean register(CertificateSummaryFieldFactory factory) {
      boolean _status = true;
      if (factory == null) {
         throw new IllegalArgumentException();
      }

      String type = factory.getType();
      if (type == null) {
         throw new IllegalArgumentException();
      }

      if (_hashtable.containsKey(type)) {
         _status = false;
      }

      _hashtable.put(type, factory);
      return _status;
   }

   protected String getType() {
      throw null;
   }

   public KeyStore getKeyStore() {
      throw null;
   }
}
