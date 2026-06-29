package net.rim.device.api.crypto.certificate;

import java.util.Hashtable;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.ui.component.PopupDialog;

public class CertificateInfoDialogFactory {
   private static final long HASHTABLE_ID = 6643584644822215036L;
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(6643584644822215036L);
   private static CertificateInfoDialogFactory _defaultCertificateInfoDialogFactory = new DefaultCertificateInfoDialogFactory();

   protected CertificateInfoDialogFactory() {
   }

   public static boolean register(CertificateInfoDialogFactory factory) {
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

   public static PopupDialog createCertificateInfoDialog(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      long style
   ) {
      if (certificate == null) {
         throw new Object();
      }

      String certificateType = certificate.getType();
      CertificateInfoDialogFactory factory = (CertificateInfoDialogFactory)_hashtable.get(certificateType);
      if (factory == null) {
         factory = _defaultCertificateInfoDialogFactory;
      }

      return factory.createCertificateInfoDialogInternal(certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, style);
   }

   protected PopupDialog createCertificateInfoDialogInternal(
      Certificate _1, Certificate[] _2, KeyStore _3, CryptoSystemProperties _4, boolean _5, CertificateStatusManagerTicket _6, long _7
   ) {
      throw null;
   }

   protected String getType() {
      throw null;
   }
}
