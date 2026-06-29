package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class MMSCPasswordOptionField extends EditField implements MMSOptionsScreen$Saveable {
   MMSCPasswordOptionField() {
      super("Password: ", MMSTransportServiceBook.getMMSCPassword());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         String password = this.getText().trim();
         if (password.length() == 0) {
            password = null;
         }

         MMSTransportServiceBook.setMMSCPassword(password);
      }
   }
}
