package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.util.Arrays;

public class PGPKeyIDKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = -2737350786039236692L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (!(certificate instanceof PGPCertificate)) {
         byte[][][] associatedData = (byte[][][])data.getAssociatedData(3622586747345475248L);
         if (associatedData != null) {
            int numAssociatedData = associatedData.length;

            for (int i = 0; i < numAssociatedData; i++) {
               dataMap.add(this.getHash(associatedData[i]), data);
            }
         }
      } else {
         PGPCertificate pgpCertificate = (PGPCertificate)certificate;
         byte[] keyID = pgpCertificate.getKeyID();
         if (keyID != null) {
            dataMap.add(this.getHash(keyID), data);
         }

         byte[][][] subKeyIDs = pgpCertificate.getSubKeyIDs();
         if (subKeyIDs != null) {
            int numSubKeyIDs = subKeyIDs.length;

            for (int i = 0; i < numSubKeyIDs; i++) {
               dataMap.add(this.getHash(subKeyIDs[i]), data);
            }
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
         if (!(certificate instanceof PGPCertificate)) {
            byte[][][] associatedData = (byte[][][])data.getAssociatedData(3622586747345475248L);
            if (associatedData != null) {
               int numAssociatedData = associatedData.length;

               for (int i = 0; i < numAssociatedData; i++) {
                  if (Arrays.equals((byte[])associatedData[i], targetBytes)) {
                     return true;
                  }
               }
            }
         } else {
            PGPCertificate pgpCertificate = (PGPCertificate)certificate;
            byte[] keyID = pgpCertificate.getKeyID();
            if (Arrays.equals(keyID, targetBytes)) {
               return true;
            }

            byte[][][] subKeyIDs = pgpCertificate.getSubKeyIDs();
            if (subKeyIDs != null) {
               int numSubKeyIDs = subKeyIDs.length;

               for (int i = 0; i < numSubKeyIDs; i++) {
                  if (Arrays.equals((byte[])subKeyIDs[i], targetBytes)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return -2737350786039236692L;
   }
}
