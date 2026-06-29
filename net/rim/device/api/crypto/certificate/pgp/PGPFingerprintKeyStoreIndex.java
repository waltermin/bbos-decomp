package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.util.Arrays;

public class PGPFingerprintKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = 3692091765934112220L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate instanceof PGPCertificate) {
         PGPCertificate pgpCertificate = (PGPCertificate)certificate;
         byte[] fingerprint = pgpCertificate.getFingerprint();
         if (fingerprint != null) {
            dataMap.add(HashCodeCalculator.getCRC32(fingerprint), data);
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (!(target instanceof byte[])) {
         throw new Object();
      } else {
         return HashCodeCalculator.getCRC32((byte[])target);
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (target instanceof byte[]) {
         byte[] targetBytes = (byte[])target;
         Certificate certificate = data.getCertificate();
         if (certificate instanceof PGPCertificate) {
            PGPCertificate pgpCertificate = (PGPCertificate)certificate;
            byte[] fingerprint = pgpCertificate.getFingerprint();
            if (fingerprint != null && Arrays.equals(fingerprint, targetBytes)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return 3692091765934112220L;
   }
}
