package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.util.Arrays;

public class CertificateHashKeyStoreIndex implements KeyStoreIndex {
   public static final long ID;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate != null) {
         SHA1Digest digest = (SHA1Digest)(new Object());
         digest.update(certificate.getEncoding());
         dataMap.add(HashCodeCalculator.getDigest32(digest.getDigest()), data);
      }
   }

   @Override
   public int getHash(Object target) {
      if (!(target instanceof byte[])) {
         throw new Object();
      } else {
         return HashCodeCalculator.getDigest32((byte[])target);
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (target instanceof byte[]) {
         byte[] hash = (byte[])target;
         Certificate cert = data.getCertificate();
         if (cert != null) {
            SHA1Digest digest = (SHA1Digest)(new Object());
            digest.update(cert.getEncoding());
            return Arrays.equals(hash, digest.getDigest());
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return 4966172969402917741L;
   }
}
