package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;

public class IssuerKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = -2041900799605303178L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate != null) {
         DistinguishedName issuer = certificate.getIssuer();
         if (issuer != null) {
            dataMap.add(issuer.hashCode(), data);
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof DistinguishedName) {
         return target.hashCode();
      } else {
         throw new Object();
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (target instanceof DistinguishedName) {
         DistinguishedName targetDN = (DistinguishedName)target;
         Certificate cert = data.getCertificate();
         if (cert != null) {
            return targetDN.equals(cert.getIssuer());
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return -2041900799605303178L;
   }
}
