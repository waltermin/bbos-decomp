package net.rim.device.apps.internal.secureemail.encodings.pgp;

import java.util.Hashtable;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateProperties;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificateChainFactory;
import net.rim.device.api.crypto.certificate.pgp.PGPSubKeyProperties;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.apps.internal.secureemail.KeyStoreLDAPCertificateHarvester;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;

public class PGPKeyStoreLDAPCertificateHarvester extends KeyStoreLDAPCertificateHarvester {
   private Hashtable _fetchedADKs = new Hashtable();
   private static final boolean DEBUG = false;

   public PGPKeyStoreLDAPCertificateHarvester(SecureEmailFactory secureEmailFactory, boolean isPINMessage) {
      super(secureEmailFactory, isPINMessage);
   }

   @Override
   protected void harvestCertificates(RecipientData recipientData) {
      super.harvestCertificates(recipientData);
      if (recipientData.getRecommendedCertificates() != null && recipientData.getRecommendedCertificates().length > 0) {
         this.harvestADKS(recipientData.getRecommendedCertificates());
      } else {
         if (recipientData.getAllowedCertificates() != null && recipientData.getAllowedCertificates().length > 0) {
            this.harvestADKS(recipientData.getAllowedCertificates());
         }
      }
   }

   private void harvestADKS(RecipientData$CertificateDetails[] certificateDetails) {
      for (int i = 0; i < certificateDetails.length; i++) {
         Certificate certificate = certificateDetails[i].getCertificate();
         if (certificate instanceof PGPCertificate) {
            byte[][] adkFingerprints = ((PGPCertificate)certificate).getADKFingerprints();
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
                  currADKData = new RecipientData(null, 3, currentKeyID, null);
                  this.harvestCertificates(currADKData, false);
                  this._fetchedADKs.put(currentADKFingerprint, currADKData);
               }

               certificateDetails[i].setAdditionalCertificates(currADKData.getAllCertificates());
            }
         }
      }
   }

   @Override
   public RecipientData$CertificateDetails buildCertificateDetails(String emailAddress, Certificate certificate, String label) {
      Certificate[] certificateChain = null;
      long certificateChainProperties = -1;
      if (certificate != null) {
         Certificate[][] allCertificateChains = CertificateUtilities.buildCertificateChains(certificate, super._preferredKeyStore, emailAddress);
         int numCertChains = allCertificateChains.length;
         long[] allCertificateChainProperties = new long[numCertChains];
         long date = System.currentTimeMillis();
         KeyStore trustedKeyStore = TrustedKeyStore.getInstance();

         for (int i = 0; i < numCertChains; i++) {
            allCertificateChainProperties[i] = PGPCertificateChainFactory.getChainAndParentKeyProperties(
               allCertificateChains[i], super._preferredKeyStore, trustedKeyStore, date, super._secureEmailFactory.getCryptoSystemProperties()
            );
         }

         int bestCertificateChainIndex = CertificateChainProperties.selectBestCertificateChain(allCertificateChainProperties);
         certificateChain = allCertificateChains[bestCertificateChainIndex];
         certificateChainProperties = allCertificateChainProperties[bestCertificateChainIndex];
         long[] subKeyProperties = PGPSubKeyProperties.getPGPEncryptionSubKeyProperties(
            (PGPCertificate)certificate, System.currentTimeMillis(), super._secureEmailFactory.getCryptoSystemProperties()
         );
         if (subKeyProperties != null && subKeyProperties.length != 0) {
            certificateChainProperties |= CertificateProperties.selectBestProperties(subKeyProperties);
         } else {
            certificateChainProperties |= 4;
         }
      }

      return new RecipientData$CertificateDetails(certificate, label, certificateChainProperties, certificateChain, null, emailAddress);
   }
}
