package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModel;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;

class PGPCertificateSummaryDataSyncModel extends CertificateSummaryDataSyncModel implements Persistable {
   private byte[] _pgpKeyIDs;
   private static final int KEY_ID_LENGTH = 8;

   public PGPCertificateSummaryDataSyncModel(Certificate certificate, boolean isPrivateKeySet) {
      super(certificate, isPrivateKeySet);
      PGPCertificate pgpCertificate = (PGPCertificate)certificate;
      if (isPrivateKeySet) {
         byte[] keyID = pgpCertificate.getKeyID();
         byte[][][] subKeyIDs = pgpCertificate.getSubKeyIDs();
         int numSubKeyIDs = subKeyIDs.length;
         this._pgpKeyIDs = new byte[8 + 8 * numSubKeyIDs];
         System.arraycopy(keyID, 0, this._pgpKeyIDs, 0, 8);
         int currentOffset = 8;

         for (int i = 0; i < numSubKeyIDs; i++) {
            System.arraycopy(subKeyIDs[i], 0, this._pgpKeyIDs, currentOffset, 8);
            currentOffset += 8;
         }
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!super.convert(context, target)) {
         return false;
      }

      if (ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (this._pgpKeyIDs != null) {
            syncBuffer.addBytes(7, this._pgpKeyIDs);
         }

         return true;
      } else {
         return false;
      }
   }
}
