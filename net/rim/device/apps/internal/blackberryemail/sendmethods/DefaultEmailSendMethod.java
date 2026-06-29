package net.rim.device.apps.internal.blackberryemail.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailTransport;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class DefaultEmailSendMethod implements SendMethod {
   private ServiceRecord _serviceRecord;
   protected int _flags;

   public DefaultEmailSendMethod(ServiceRecord serviceRecord) {
      this._serviceRecord = serviceRecord;
   }

   @Override
   public boolean send(RIMModel model, Object context) {
      EmailTransport et = (EmailTransport)this._serviceRecord.getTransport();
      EmailMessageModel message = (EmailMessageModel)model;
      if (!et.canSendEmail(message)) {
         return false;
      }

      message = et.sendMessage(message, this._serviceRecord, context);
      message.setFlags(32);
      return true;
   }

   @Override
   public String toString() {
      return EmailResources.getString(146);
   }

   @Override
   public long getEncodingUID() {
      return 182808770805039415L;
   }

   @Override
   public int getEncodingAction() {
      return 0;
   }

   @Override
   public ServiceRecord getServiceRecord() {
      return this._serviceRecord;
   }

   @Override
   public boolean isConfigured(RIMModel model, Object context) {
      return true;
   }

   @Override
   public boolean isConfigurable(RIMModel model, Object context) {
      return true;
   }

   @Override
   public void setFlags(int flags) {
      this._flags |= flags;
   }

   @Override
   public void clearFlags(int flags) {
      this._flags &= ~flags;
   }

   @Override
   public int getFlags() {
      return this._flags;
   }
}
