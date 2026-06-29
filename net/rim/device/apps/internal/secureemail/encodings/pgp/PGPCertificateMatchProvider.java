package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.apps.api.framework.model.MatchProvider;

public final class PGPCertificateMatchProvider implements MatchProvider {
   byte[][] _keyIDs;

   public PGPCertificateMatchProvider(byte[][] keyIDs) {
      this._keyIDs = keyIDs;
   }

   @Override
   public final int match(Object searchCriteria) {
      if (searchCriteria instanceof PGPCertificate) {
         PGPCertificate cert = (PGPCertificate)searchCriteria;
         if (this._keyIDs != null) {
            int numKeyIDs = this._keyIDs.length;

            for (int i = 0; i < numKeyIDs; i++) {
               byte[] keyID = this._keyIDs[i];
               if (keyID != null && cert.getPublicKey(keyID) != null) {
                  return 1;
               }
            }
         }
      }

      return 0;
   }
}
