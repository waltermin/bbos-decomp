package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class AuthenticationHeaderOptionField extends EditField implements MMSOptionsScreen$Saveable {
   AuthenticationHeaderOptionField() {
      super("Authentication Header: ", MMSTransportServiceBook.getAuthenticationHeader());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         String header = this.getText().trim();
         if (header.length() == 0) {
            header = null;
         }

         MMSTransportServiceBook.setAuthenticationHeader(header);
      }
   }
}
