package net.rim.device.apps.internal.blackberryemail.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethodFactory;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class DefaultSendMethodFactory implements SendMethodFactory {
   public String getEncodingString() {
      return EmailResources.getString(146);
   }

   @Override
   public long getEncodingUID() {
      return 182808770805039415L;
   }

   @Override
   public int getPriority() {
      return 0;
   }

   @Override
   public SendMethod[] create(RIMModel model, ServiceRecord serviceRecord, Object context) {
      if (model instanceof EmailMessageModel) {
         EmailMessageModel emailMessageModel = (EmailMessageModel)model;
         boolean isPIN = emailMessageModel.flagsSet(8192);
         if (isPIN && serviceRecord == null) {
            return new SendMethod[]{new DefaultPINSendMethod()};
         }

         if (!isPIN && serviceRecord != null) {
            return new SendMethod[]{new DefaultEmailSendMethod(serviceRecord)};
         }
      }

      return null;
   }
}
