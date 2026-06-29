package net.rim.device.api.crypto.certificate.x509;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.util.Arrays;

public class SubjectKeyIdentifierKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = 1612863905495138626L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate instanceof X509Certificate) {
         byte[] subjectKeyId = ((X509Certificate)certificate).getSubjectKeyIdentifier();
         if (subjectKeyId != null) {
            dataMap.add(HashCodeCalculator.getCRC32(subjectKeyId), data);
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (!(target instanceof byte[])) {
         throw new IllegalArgumentException();
      } else {
         return HashCodeCalculator.getCRC32((byte[])target);
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (target instanceof byte[]) {
         byte[] subjectKeyId = (byte[])target;
         Certificate cert = data.getCertificate();
         if (cert instanceof X509Certificate) {
            byte[] subjectKeyId2 = ((X509Certificate)cert).getSubjectKeyIdentifier();
            if (subjectKeyId2 != null) {
               return Arrays.equals(subjectKeyId, subjectKeyId2);
            }
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return 1612863905495138626L;
   }
}
