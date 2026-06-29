package net.rim.device.apps.internal.secureemail.server;

import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.secureemail.CertificateHarvester;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.sendmethods.DoNotEncryptException;

public class SecureEmailServerCertificateHarvester extends CertificateHarvester {
   protected SecureEmailCertificateServer[] _secureEmailCertificateServers;
   protected long[] _encodingUIDs;
   protected int _encodingActions;
   protected boolean _pendingEnrollment;

   public SecureEmailServerCertificateHarvester(SecureEmailCertificateServer[] secureEmailCertificateServers, int encodingActions) {
      super(false);
      this._secureEmailCertificateServers = secureEmailCertificateServers;
      this._encodingUIDs = new long[0];
      int numSecureEmailCertificateServers = secureEmailCertificateServers != null ? secureEmailCertificateServers.length : 0;

      for (int i = 0; i < numSecureEmailCertificateServers; i++) {
         long[] currentEncodingUIDs = secureEmailCertificateServers[i].getEncodingUIDs();
         int numCurrentEncodingUIDs = currentEncodingUIDs != null ? currentEncodingUIDs.length : 0;

         for (int j = 0; j < numCurrentEncodingUIDs; j++) {
            long currentEncodingUID = currentEncodingUIDs[j];
            if (Arrays.getIndex(this._encodingUIDs, currentEncodingUID) < 0) {
               Arrays.add(this._encodingUIDs, currentEncodingUID);
            }
         }
      }

      this._encodingActions = encodingActions;
   }

   @Override
   public long[] getEncodingUIDs() {
      return this._encodingUIDs;
   }

   @Override
   public int getPriority() {
      return 1;
   }

   @Override
   public String getDisplayName() {
      return "Server";
   }

   @Override
   protected boolean harvestCertificatesForAction(int encodingAction) {
      return true;
   }

   @Override
   public Vector getProcessedRecipients() {
      if (this._pendingEnrollment) {
         this._pendingEnrollment = false;
         this.terminate();
         this.reactivate();
      }

      return super.getProcessedRecipients();
   }

   @Override
   protected void harvestCertificates(RecipientData recipientData) {
      this.harvestCertificates(recipientData, true);
   }

   protected void harvestCertificates(RecipientData recipientData, boolean updateGauge) {
      if (updateGauge) {
         super._completionDialog.stepGauge();
      }

      Certificate[] serverCertificates = new Certificate[0];
      String[] recipientAddresses = recipientData.getAddresses();
      int numRecipientAddresses = recipientAddresses != null ? recipientAddresses.length : 0;
      int numCertificateServers = this._secureEmailCertificateServers.length;

      for (int i = 0; i < numCertificateServers; i++) {
         SecureEmailCertificateServer currentCertificateServer = this._secureEmailCertificateServers[i];
         if (!currentCertificateServer.isInitialized()) {
            this._pendingEnrollment = true;
         } else {
            DoNotEncryptException ix;
            label128: {
               try {
                  for (int j = 0; j < numRecipientAddresses; j++) {
                     Certificate recipientCertificate = null;
                     if (updateGauge) {
                        recipientCertificate = currentCertificateServer.getCertificateByEmailAddress(
                           recipientAddresses[j], this._encodingActions, super._completionDialog
                        );
                     } else {
                        recipientCertificate = currentCertificateServer.getCertificateByEmailAddress(recipientAddresses[j], this._encodingActions, null);
                     }

                     if (recipientCertificate != null && !Arrays.contains(serverCertificates, recipientCertificate)) {
                        Arrays.add(serverCertificates, recipientCertificate);
                     }
                  }

                  Object certificateID = recipientData.getCertificateID();
                  if (certificateID != null) {
                     Certificate recipientCertificate = null;
                     if (updateGauge) {
                        recipientCertificate = currentCertificateServer.getCertificateByCertificateID(certificateID, super._completionDialog);
                     } else {
                        recipientCertificate = currentCertificateServer.getCertificateByCertificateID(certificateID, null);
                     }

                     if (recipientCertificate != null && !Arrays.contains(serverCertificates, recipientCertificate)) {
                        Arrays.add(serverCertificates, recipientCertificate);
                     }
                  }
                  continue;
               } catch (DoNotEncryptException var14) {
                  ix = var14;
                  break label128;
               } catch (SecureEmailServerEnrollmentException var15) {
               } finally {
                  continue;
               }

               this._pendingEnrollment = true;
               continue;
            }

            recipientData.setExcluded(ix.getNewEncodingAction());
         }
      }

      int numServerCertificates = serverCertificates.length;
      RecipientData$CertificateDetails[] certificateDetails = new RecipientData$CertificateDetails[numServerCertificates];

      for (int i = 0; i < numServerCertificates; i++) {
         Certificate currentServerCertificate = serverCertificates[i];
         certificateDetails[i] = new RecipientData$CertificateDetails(currentServerCertificate, currentServerCertificate.getSubjectFriendlyName(), 0);
      }

      recipientData.setRecommendedCertificates(certificateDetails);
   }
}
