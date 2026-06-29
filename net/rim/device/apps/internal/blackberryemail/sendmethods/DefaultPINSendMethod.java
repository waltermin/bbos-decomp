package net.rim.device.apps.internal.blackberryemail.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class DefaultPINSendMethod implements SendMethod {
   @Override
   public final boolean send(RIMModel model, Object context) {
      if (!EmailMessageUtilities.canSendPIN()) {
         return false;
      }

      EmailMessageModel message = (EmailMessageModel)model;
      message = EmailSendUtility.sendMessage(message, null, context);
      message.setFlags(32);
      return true;
   }

   @Override
   public final String toString() {
      return EmailResources.getString(146);
   }

   @Override
   public final long getEncodingUID() {
      return 182808770805039415L;
   }

   @Override
   public final int getEncodingAction() {
      return 0;
   }

   @Override
   public final ServiceRecord getServiceRecord() {
      return null;
   }

   @Override
   public final boolean isConfigured(RIMModel model, Object context) {
      return true;
   }

   @Override
   public final boolean isConfigurable(RIMModel model, Object context) {
      return true;
   }

   @Override
   public final void setFlags(int flag) {
   }

   @Override
   public final void clearFlags(int flag) {
   }

   @Override
   public final int getFlags() {
      return 0;
   }
}
