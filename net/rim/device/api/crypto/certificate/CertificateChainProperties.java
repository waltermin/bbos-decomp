package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.keystore.KeyStore;

public class CertificateChainProperties extends CertificateProperties {
   public static long selectBestCertificateChainProperties(long[] properties) {
      return CertificateProperties.selectBestProperties(properties);
   }

   public static int selectBestCertificateChain(long[] properties) {
      return CertificateProperties.selectBest(properties);
   }

   public static int[] selectCertificateChain(long[] properties, long[] propertyMasks) {
      return CertificateProperties.select(properties, propertyMasks);
   }

   public static long[] getCertificateChainProperties(Certificate[][] chains, KeyStore trustedKeyStore, long date) {
      return getCertificateChainProperties(chains, null, trustedKeyStore, date);
   }

   public static long[] getCertificateChainProperties(Certificate[][] chains, KeyStore keyStore, KeyStore trustedKeyStore, long date) {
      return getCertificateChainProperties(chains, keyStore, trustedKeyStore, date, null);
   }

   public static long[] getCertificateChainProperties(
      Certificate[][] chains, KeyStore trustedKeyStore, long date, CryptoSystemProperties cryptoSystemProperties
   ) {
      return getCertificateChainProperties(chains, null, trustedKeyStore, date, cryptoSystemProperties);
   }

   public static long[] getCertificateChainProperties(
      Certificate[][] chains, KeyStore keyStore, KeyStore trustedKeyStore, long date, CryptoSystemProperties cryptoSystemProperties
   ) {
      if (chains != null && chains.length != 0) {
         long[] properties = new long[chains.length];

         for (int i = 0; i < chains.length; i++) {
            properties[i] = getCertificateChainProperties(chains[i], keyStore, trustedKeyStore, date, cryptoSystemProperties);
         }

         return properties;
      } else {
         throw new Object();
      }
   }

   public static long getCertificateChainProperties(Certificate[] chain, KeyStore trustedKeyStore, long date) {
      return getCertificateChainProperties(chain, null, trustedKeyStore, date);
   }

   public static long getCertificateChainProperties(Certificate[] chain, KeyStore keyStore, KeyStore trustedKeyStore, long date) {
      return getCertificateChainProperties(chain, keyStore, trustedKeyStore, date, null);
   }

   public static long getCertificateChainProperties(Certificate[] chain, KeyStore trustedKeyStore, long date, CryptoSystemProperties cryptoSystemProperties) {
      return getCertificateChainProperties(chain, null, trustedKeyStore, date, cryptoSystemProperties);
   }

   public static long getCertificateChainProperties(
      Certificate[] chain, KeyStore keyStore, KeyStore trustedKeyStore, long date, CryptoSystemProperties cryptoSystemProperties
   ) {
      return CertificateChainFactory.getCertificateChainProperties(chain, keyStore, trustedKeyStore, date, cryptoSystemProperties);
   }
}
