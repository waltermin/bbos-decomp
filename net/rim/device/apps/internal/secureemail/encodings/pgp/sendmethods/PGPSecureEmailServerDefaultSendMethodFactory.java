package net.rim.device.apps.internal.secureemail.encodings.pgp.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethodFactory;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPResources;

public class PGPSecureEmailServerDefaultSendMethodFactory implements SendMethodFactory {
   public String getEncodingString() {
      return PGPResources.getString(8073);
   }

   @Override
   public long getEncodingUID() {
      return -742709496102783169L;
   }

   @Override
   public SendMethod[] create(RIMModel model, ServiceRecord serviceRecord, Object context) {
      if (model instanceof Object) {
         EmailMessageModel emailMessageModel = (EmailMessageModel)model;
         boolean isPIN = emailMessageModel.flagsSet(8192);
         if (isPIN && serviceRecord != null) {
            return null;
         }

         if (!isPIN && serviceRecord == null) {
            return null;
         }
      }

      return serviceRecord != null && SecureEmailUtilities.checkITAdminBoundUID(serviceRecord.getUid())
         ? new Object[]{new PGPSecureEmailServerDefaultSendMethod(serviceRecord, context)}
         : null;
   }

   @Override
   public int getPriority() {
      return 1;
   }
}
