package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import java.util.Hashtable;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.server.SecureEmailCertificateServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerCertificateHarvester;

public class PGPUniversalServerCertificateHarvester extends SecureEmailServerCertificateHarvester {
   private Hashtable _fetchedADKs = (Hashtable)(new Object());

   public PGPUniversalServerCertificateHarvester(SecureEmailCertificateServer[] secureEmailCertificateServers, int encodingActions) {
      super(secureEmailCertificateServers, encodingActions);
   }

   @Override
   protected void harvestCertificates(RecipientData recipientData) {
      super.harvestCertificates(recipientData);
      this.harvestADKS(recipientData.getRecommendedCertificates());
   }

   private void harvestADKS(RecipientData$CertificateDetails[] certificateDetails) {
      for (int i = 0; i < certificateDetails.length; i++) {
         Certificate certificate = certificateDetails[i].getCertificate();
         if (certificate instanceof Object) {
            byte[][][] adkFingerprints = (byte[][][])((PGPCertificate)certificate).getADKFingerprints();
            if (adkFingerprints == null) {
               return;
            }

            RecipientData currADKData = null;

            for (byte[] currentADKFingerprint : adkFingerprints) {
               byte[] currentKeyID = new byte[8];
               System.arraycopy(currentADKFingerprint, currentADKFingerprint.length - 8, currentKeyID, 0, 8);
               if (this._fetchedADKs.containsKey(currentADKFingerprint)) {
                  currADKData = (RecipientData)this._fetchedADKs.get(currentADKFingerprint);
               } else {
                  currADKData = (RecipientData)(new Object(null, 3, currentKeyID, null));
                  this.harvestCertificates(currADKData, false);
                  this._fetchedADKs.put(currentADKFingerprint, currADKData);
               }

               certificateDetails[i].setAdditionalCertificates(currADKData.getRecommendedCertificates());
            }
         }
      }
   }
}
