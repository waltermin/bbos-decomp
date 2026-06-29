package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class MMSCUsernameOptionField extends EditField implements MMSOptionsScreen$Saveable {
   MMSCUsernameOptionField() {
      super("Username: ", MMSTransportServiceBook.getMMSCUsername());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         String username = this.getText().trim();
         if (username.length() == 0) {
            username = null;
         }

         MMSTransportServiceBook.setMMSCUsername(username);
      }
   }
}
