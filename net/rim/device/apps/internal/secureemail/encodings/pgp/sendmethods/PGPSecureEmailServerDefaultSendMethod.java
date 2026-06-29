package net.rim.device.apps.internal.secureemail.encodings.pgp.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.secureemail.CertificateHarvester;
import net.rim.device.apps.internal.secureemail.CertificateHarvesterManager;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPFactory;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPResources;
import net.rim.device.apps.internal.secureemail.encodings.pgp.server.PGPUniversalServerCertificateHarvester;
import net.rim.device.apps.internal.secureemail.sendmethods.SecureEmailAwareSendMethod;
import net.rim.device.apps.internal.secureemail.server.SecureEmailCertificateServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;

public class PGPSecureEmailServerDefaultSendMethod extends SecureEmailAwareSendMethod {
   PGPSecureEmailServerDefaultSendMethod(ServiceRecord serviceRecord, Object context) {
      super(serviceRecord, -742709496102783169L, 3, context);
      this.setFlags(24);
      SecureEmailCertificateServer[] secureEmailCertificateServers = SecureEmailServerManager.getInstance().getCertificateServers(serviceRecord);
      CertificateHarvester certificateHarvester = new PGPUniversalServerCertificateHarvester(secureEmailCertificateServers, 3);
      if (CertificateHarvesterManager.addCertificateHarvester(certificateHarvester, context) && serviceRecord != null) {
         String alwaysBCCEmailAddress = PGPFactory.getInstance().getUtilities().getAlwaysBCCEmailAddress(serviceRecord.getUid());
         if (alwaysBCCEmailAddress != null && alwaysBCCEmailAddress.length() > 0) {
            RecipientData recipientData = (RecipientData)(new Object(null, 1, new Object[]{alwaysBCCEmailAddress}, null));
            certificateHarvester.recipientAdded(recipientData);
         }
      }
   }

   @Override
   public boolean isConfigurable(RIMModel model, Object context) {
      return true;
   }

   @Override
   public boolean isConfigured(RIMModel model, Object context) {
      return true;
   }

   @Override
   public String toString() {
      return PGPResources.getString(8075);
   }
}
