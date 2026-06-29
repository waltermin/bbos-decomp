package net.rim.device.api.crypto.certificate.x509;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.util.Arrays;

public class X509PublicKeyHashKeyStoreIndex implements KeyStoreIndex {
   private Digest _digest;
   public static final long ID = 751537200683994917L;

   public X509PublicKeyHashKeyStoreIndex() {
      this._digest = new SHA1Digest();
   }

   public X509PublicKeyHashKeyStoreIndex(Digest digest) {
      if (digest == null) {
         throw new IllegalArgumentException();
      }

      this._digest = digest;
   }

   @Override
   public synchronized void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      if (data != null && dataMap != null) {
         Certificate certificate = data.getCertificate();
         if (certificate instanceof X509Certificate) {
            this._digest.update(((X509Certificate)certificate).getEncoding(1));
            dataMap.add(HashCodeCalculator.getCRC32(this._digest.getDigest()), data);
         }
      } else {
         throw new IllegalArgumentException();
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
   public synchronized boolean matches(KeyStoreData data, Object target) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      if (target instanceof byte[]) {
         byte[] hashValue = (byte[])target;
         Certificate cert = data.getCertificate();
         if (cert instanceof X509Certificate) {
            this._digest.update(((X509Certificate)cert).getEncoding(1));
            return Arrays.equals(hashValue, this._digest.getDigest());
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return 751537200683994917L;
   }
}
