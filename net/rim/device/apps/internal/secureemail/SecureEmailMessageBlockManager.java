package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Field;
import net.rim.vm.Array;

public class SecureEmailMessageBlockManager extends CursorProviderVerticalIndentFieldManager implements StatusProviderField {
   public SecureEmailMessageBlockManager(long style) {
      super(style);
   }

   @Override
   public StatusField[] getStatusFields() {
      StatusField[] statusFields = new StatusField[0];
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field currentField = this.getField(i);
         if (currentField instanceof StatusProviderField) {
            StatusProviderField currentStatusProviderField = (StatusProviderField)currentField;
            StatusField[] currentStatusFields = currentStatusProviderField.getStatusFields();
            int numStatusFields = statusFields.length;
            Array.resize(statusFields, numStatusFields + currentStatusFields.length);
            System.arraycopy(currentStatusFields, 0, statusFields, numStatusFields, currentStatusFields.length);
         }
      }

      return statusFields;
   }

   @Override
   public void showShortForm(boolean showShortForm) {
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field currentField = this.getField(i);
         if (currentField instanceof StatusProviderField) {
            ((StatusProviderField)currentField).showShortForm(showShortForm);
         }
      }
   }

   @Override
   public void setSecureEmailMessageManager(SecureEmailMessageManager secureEmailMessageManager) {
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field currentField = this.getField(i);
         if (currentField instanceof StatusProviderField) {
            ((StatusProviderField)currentField).setSecureEmailMessageManager(secureEmailMessageManager);
         }
      }
   }
}
