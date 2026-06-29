package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;

public class CertificateKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = -2038609988711824737L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate != null) {
         dataMap.add(certificate.hashCode(), data);
      }
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof Certificate) {
         return target.hashCode();
      } else {
         throw new Object();
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (!(target instanceof Certificate)) {
         return false;
      }

      Certificate targetCertificate = (Certificate)target;
      Certificate dataCertificate = data.getCertificate();
      return targetCertificate.equals(dataCertificate);
   }

   @Override
   public long getID() {
      return -2038609988711824737L;
   }
}
