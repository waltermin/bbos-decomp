package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class ApnOptionField extends EditField implements MMSOptionsScreen$Saveable {
   ApnOptionField(String apn) {
      super("APN: ", apn);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         String apn = this.getText().trim();
         if (apn.length() > 0) {
            MMSTransportServiceBook.setAPN(apn);
         }
      }
   }
}
