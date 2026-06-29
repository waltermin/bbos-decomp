package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;

public class KeyUsageDominoAddressKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = 7492162715265800726L;
   public static final int SIGNING = 0;
   public static final int ENCRYPTION = 1;
   public static final int SIGNING_AND_ENCRYPTION = 2;

   @Override
   public long getID() {
      return 7492162715265800726L;
   }

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate != null && certificate instanceof X509Certificate) {
         if (SecureEmailUtilities.isCertificateSupported(data, 2)) {
            int dominoAddressHashCode = DominoAddressUtilities.computeDominoAddressHashCode((X509DistinguishedName)certificate.getSubject());
            boolean sign = certificate.queryKeyUsage(1) != 0;
            boolean encrypt = certificate.queryKeyUsage(4) != 0 || certificate.queryKeyUsage(16) != 0;
            if (sign) {
               dataMap.add(0 ^ dominoAddressHashCode, data);
               if (encrypt) {
                  dataMap.add(2 ^ dominoAddressHashCode, data);
               }
            }

            if (encrypt) {
               dataMap.add(1 ^ dominoAddressHashCode, data);
            }
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (!(target instanceof KeyUsageDominoAddressKeyStoreIndex$Alias)) {
         throw new IllegalArgumentException();
      }

      KeyUsageDominoAddressKeyStoreIndex$Alias a = (KeyUsageDominoAddressKeyStoreIndex$Alias)target;
      return a.hashCode();
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (!(target instanceof KeyUsageDominoAddressKeyStoreIndex$Alias)) {
         throw new IllegalArgumentException();
      }

      KeyUsageDominoAddressKeyStoreIndex$Alias a = (KeyUsageDominoAddressKeyStoreIndex$Alias)target;
      if (a._dominoAddressDN == null) {
         return false;
      }

      Certificate certificate = data.getCertificate();
      if (certificate == null || !(certificate instanceof X509Certificate)) {
         return false;
      }

      if (!SecureEmailUtilities.isCertificateSupported(data, 2)) {
         return false;
      }

      boolean sign = certificate.queryKeyUsage(1) != 0;
      boolean encrypt = certificate.queryKeyUsage(4) != 0 || certificate.queryKeyUsage(16) != 0;
      switch (a._usage) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            if (!sign) {
               return false;
            }
            break;
         case 1:
            if (!encrypt) {
               return false;
            }
            break;
         case 2:
            if (!sign || !encrypt) {
               return false;
            }
      }

      return DominoAddressUtilities.dominoAddressDNMatchesX509DN(a._dominoAddressDN, (X509DistinguishedName)certificate.getSubject());
   }

   public static Object getAlias(int usage, String dominoAddress) {
      return new KeyUsageDominoAddressKeyStoreIndex$Alias(usage, dominoAddress);
   }
}
