package net.rim.device.api.crypto.certificate;

import java.util.Hashtable;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.system.ApplicationRegistry;

public class CertificateChainFactory extends CertificateProperties {
   private static final long HASHTABLE_ID = -8204259325785503721L;
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(-8204259325785503721L);
   private static CryptoSystemProperties _defaultCryptoSystemProperties = new CryptoSystemProperties();
   private static CertificateChainFactory _defaultCertificateChainFactory = new DefaultCertificateChainFactory();

   protected CertificateChainFactory() {
   }

   public static synchronized boolean register(CertificateChainFactory factory) {
      if (factory == null) {
         throw new IllegalArgumentException();
      }

      String type = factory.getType();
      if (type == null) {
         throw new IllegalArgumentException();
      }

      if (_hashtable.containsKey(type)) {
         return false;
      }

      _hashtable.put(type, factory);
      return true;
   }

   public static Certificate[][] createCertificateChains(Certificate certificate, Certificate[] certificatePool, KeyStore keyStore, String emailAddress) {
      if (certificate == null) {
         throw new IllegalArgumentException();
      }

      String certificateType = certificate.getType();
      CertificateChainFactory factory = (CertificateChainFactory)_hashtable.get(certificateType);
      if (factory == null) {
         factory = _defaultCertificateChainFactory;
      }

      return factory.createCertificateChainsInternal(certificate, certificatePool, keyStore, emailAddress);
   }

   public static long getCertificateChainProperties(
      Certificate[] certificateChain, KeyStore keyStore, KeyStore trustedKeyStore, long date, CryptoSystemProperties cryptoSystemProperties
   ) {
      if (certificateChain != null && certificateChain.length != 0) {
         if (cryptoSystemProperties == null) {
            cryptoSystemProperties = _defaultCryptoSystemProperties;
         }

         String certificateType = certificateChain[0].getType();
         CertificateChainFactory factory = (CertificateChainFactory)_hashtable.get(certificateType);
         if (factory == null) {
            factory = _defaultCertificateChainFactory;
         }

         return factory.getCertificateChainPropertiesInternal(certificateChain, keyStore, trustedKeyStore, date, cryptoSystemProperties);
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected Certificate[][] createCertificateChainsInternal(Certificate _1, Certificate[] _2, KeyStore _3, String _4) {
      throw null;
   }

   protected long getCertificateChainPropertiesInternal(Certificate[] _1, KeyStore _2, KeyStore _3, long _4, CryptoSystemProperties _6) {
      throw null;
   }

   protected String getType() {
      throw null;
   }

   protected static long getCommonCertificateProperties(Certificate certificate, long date, CryptoSystemProperties cryptoSystemProperties) {
      long properties = 0;
      if (!certificate.isValid(date)) {
         properties |= 256;
      }

      CertificateStatus currentStatus = certificate.getStatus();
      if (currentStatus == null || currentStatus.getStatus() == -1) {
         properties |= 512;
      } else if (currentStatus.getStatus() == 1) {
         properties |= 1024;
         properties |= (long)65536 << currentStatus.getRevocationReason();
      }

      if (currentStatus == null || currentStatus.isStale()) {
         properties |= 2048;
      }

      return properties;
   }
}
