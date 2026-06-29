package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.EncodingProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

final class EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel$EmailMessageServiceModel extends TransitoryTextModel {
   @Override
   protected final String getString(RIMModel model) {
      EmailMessageModel message = (EmailMessageModel)model;
      StringBuffer buffer = new StringBuffer();
      boolean isPINMessage = message.flagsSet(8192);
      if (isPINMessage) {
         buffer.append(EmailResources.getString(147));
      } else {
         ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(message);
         if (serviceRecord != null) {
            buffer.append(serviceRecord.getName());
         } else {
            buffer.append(CommonResources.getString(104));
         }
      }

      BodyModel bodyModel = message.getBodyModel();
      if (bodyModel instanceof EncodingProvider) {
         EncodingProvider encodingProvider = (EncodingProvider)bodyModel;
         if (encodingProvider.getEncodingUID() == 182808770805039415L) {
            boolean isCorporatePIN = isPINMessage && message.flagsSet(32768) && !message.flagsSet(1048576);
            if (message.flagsSet(16777216) || isCorporatePIN) {
               buffer.append(EmailResources.getString(149));
            }
         } else {
            buffer.append(' ');
            buffer.append('(');
            buffer.append(encodingProvider.getEncodingString());
            buffer.append(')');
         }
      }

      return buffer.toString();
   }
}
