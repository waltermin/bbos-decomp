package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

public class CachedField {
   protected EmailMessageModel _emailMessageModel;
   protected ServiceRecord _serviceRecord;
   protected CachedManager _manager;

   public void setManager(CachedManager manager) {
      this._manager = manager;
   }

   public CachedManager getManager() {
      return this._manager;
   }

   public void fillManager(Manager manager, Object context) {
   }

   public void fillStringBuffer(StringBuffer stringBuffer, Object context) {
   }

   public void setEmailMessageModel(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord) {
      this._emailMessageModel = emailMessageModel;
      this._serviceRecord = serviceRecord;
   }

   public boolean isEmailMessageModelSet() {
      return this._emailMessageModel != null;
   }

   public boolean endsWithSeparator(Field field) {
      if (field instanceof Object) {
         return true;
      }

      if (field instanceof Object) {
         Manager manager = (Manager)field;
         int numFields = manager.getFieldCount();
         if (numFields == 0) {
            return true;
         }

         if (numFields > 0 && this.endsWithSeparator(manager.getField(numFields - 1))) {
            return true;
         }

         if (numFields > 1 && manager.getField(numFields - 1) instanceof Object && this.endsWithSeparator(manager.getField(numFields - 2))) {
            return true;
         }
      }

      return false;
   }

   public CachedMessage getCachedMessage() {
      return this._manager == null ? null : this._manager.getCachedMessage();
   }
}
