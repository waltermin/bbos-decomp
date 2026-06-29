package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.secureemail.KeyStoreLDAPCertificateHarvester;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;

public class EMSKeyStoreLDAPCertificateHarvester extends KeyStoreLDAPCertificateHarvester {
   private static final boolean DEBUG = true;

   public EMSKeyStoreLDAPCertificateHarvester(String emsEmailAddress, SecureEmailFactory secureEmailFactory, boolean isPINMessage) {
      super(secureEmailFactory, isPINMessage);
      RecipientData emsRecipientData = new RecipientData(null, 2, new String[]{emsEmailAddress}, null);
      this.queueRecipient(emsRecipientData);
   }

   @Override
   public void recipientAdded(EmailHeaderModel emailHeaderModel, Object context) {
      System.out.println("Ignoring added recipient because EMS is active");
   }

   @Override
   public void recipientAdded(RecipientData recipientData) {
      System.out.println("Ignoring added recipient because EMS is active");
   }
}
