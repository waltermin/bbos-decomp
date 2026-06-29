package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;

public class KeyUsagePrivateKeysKeyStoreIndex implements KeyStoreIndex {
   public static final long ID;
   public static final int SIGNING;
   public static final int ENCRYPTION;
   public static final int SIGNING_AND_ENCRYPTION;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      if (data.isPrivateKeySet()) {
         Certificate certificate = data.getCertificate();
         if (certificate != null) {
            if (SecureEmailUtilities.isCertificateSupported(data, 4) && certificate.queryKeyUsage(4096) != 0) {
               boolean sign = certificate.queryKeyUsage(1) != 0;
               boolean encrypt = certificate.queryKeyUsage(4) != 0 || certificate.queryKeyUsage(16) != 0;
               if (sign) {
                  dataMap.add(0, data);
                  if (encrypt) {
                     dataMap.add(2, data);
                  }
               }

               if (encrypt) {
                  dataMap.add(1, data);
               }
            }
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof Object) {
         int value = target;
         switch (value) {
            case -1:
               break;
            case 0:
            case 1:
            case 2:
            default:
               return value;
         }
      }

      throw new Object();
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (data.isPrivateKeySet() && target instanceof Object) {
         Certificate certificate = data.getCertificate();
         if (certificate != null) {
            boolean sign = certificate.queryKeyUsage(1) != 0;
            boolean encrypt = certificate.queryKeyUsage(4) != 0 || certificate.queryKeyUsage(16) != 0;
            switch (target) {
               case -1:
                  break;
               case 0:
               default:
                  return sign;
               case 1:
                  return encrypt;
               case 2:
                  if (sign && encrypt) {
                     return true;
                  }

                  return false;
            }
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return -2733667523168089402L;
   }
}
