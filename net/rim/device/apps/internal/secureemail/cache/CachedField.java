package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.internal.ui.component.VerticalSpacerField;

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
      if (field instanceof SeparatorField) {
         return true;
      }

      if (field instanceof Manager) {
         Manager manager = (Manager)field;
         int numFields = manager.getFieldCount();
         if (numFields == 0) {
            return true;
         }

         if (numFields > 0 && this.endsWithSeparator(manager.getField(numFields - 1))) {
            return true;
         }

         if (numFields > 1 && manager.getField(numFields - 1) instanceof VerticalSpacerField && this.endsWithSeparator(manager.getField(numFields - 2))) {
            return true;
         }
      }

      return false;
   }

   public CachedMessage getCachedMessage() {
      return this._manager == null ? null : this._manager.getCachedMessage();
   }
}
