package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;

public class SubjectKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = -1581141357654337861L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate != null) {
         DistinguishedName subject = certificate.getSubject();
         if (subject != null) {
            dataMap.add(subject.hashCode(), data);
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof DistinguishedName) {
         return target.hashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (target instanceof DistinguishedName) {
         DistinguishedName targetDN = (DistinguishedName)target;
         Certificate cert = data.getCertificate();
         if (cert != null) {
            return targetDN.equals(cert.getSubject());
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return -1581141357654337861L;
   }
}
