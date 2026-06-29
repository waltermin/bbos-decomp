package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class PPGUrlOptionField extends EditField implements MMSOptionsScreen$Saveable {
   PPGUrlOptionField(String ppgUrl) {
      super("PPG URL: ", ppgUrl);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         String url = this.getText().trim();
         if (url.length() > 0) {
            MMSTransportServiceBook.setPPGAddress(url);
         }
      }
   }
}
