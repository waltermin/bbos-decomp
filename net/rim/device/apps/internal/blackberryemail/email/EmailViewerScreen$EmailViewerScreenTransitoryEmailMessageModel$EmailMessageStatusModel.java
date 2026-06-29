package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel$EmailMessageStatusModel extends TransitoryTextModel {
   @Override
   protected final String getString(RIMModel model) {
      EmailMessageModelImpl message = (EmailMessageModelImpl)model;
      int status = message.getStatus();
      int stringResourceToUse = 730;
      String statusString = null;
      if (message.inbound() && !EmailMessageUtilities.moreRequestSent(message)) {
         stringResourceToUse = message.flagsSet(1) ? 750 : 740;
      } else {
         boolean retrieveTransmissionError = true;
         switch (status) {
            case 16383:
               stringResourceToUse = 760;
               break;
            case 32767:
               stringResourceToUse = 204;
               break;
            case 2097151:
               stringResourceToUse = 1122;
            case 2047:
            case 4095:
               if (EmailMessageUtilities.moreRequestSent(message)) {
                  stringResourceToUse = message.flagsSet(1) ? 750 : 740;
                  retrieveTransmissionError = false;
               }
               break;
            case 4194303:
               stringResourceToUse = 761;
               break;
            case 33554431:
               stringResourceToUse = 700;
               break;
            case 67108863:
               stringResourceToUse = 720;
               break;
            case 134217727:
            case 268435455:
            case 536870911:
            case 1073741823:
               stringResourceToUse = 710;
         }

         if (retrieveTransmissionError) {
            statusString = EmailMessageUtilities.getTransmissionErrorMessage(message);
         }
      }

      if (statusString == null) {
         statusString = EmailResources.getString(stringResourceToUse);
      }

      return statusString;
   }
}
