package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.keystore.KeyStore;

public class CertificateIconProvider {
   public static int getCertificateIcon(Certificate certificate, KeyStore keyStore, KeyStore trustedKeyStore) {
      return getCertificateIcon(certificate, keyStore, trustedKeyStore, null);
   }

   public static int getCertificateIcon(Certificate certificate, KeyStore keyStore, KeyStore trustedKeyStore, CryptoSystemProperties cryptoSystemProperties) {
      Certificate[][][] certificateChains = CertificateUtilities.buildCertificateChains(certificate, keyStore);
      long[] properties = CertificateChainProperties.getCertificateChainProperties(
         certificateChains, keyStore, trustedKeyStore, System.currentTimeMillis(), cryptoSystemProperties
      );
      long bestProperties = CertificateProperties.selectBestProperties(properties);
      return getCertificateIcon(bestProperties);
   }

   public static int getCertificateIcon(long certificateProperties) {
      if ((certificateProperties & 1310) != 0) {
         return 3;
      } else {
         return (certificateProperties & 544) != 0 ? 2 : 1;
      }
   }
}
