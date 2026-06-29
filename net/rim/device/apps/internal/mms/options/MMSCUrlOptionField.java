package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.internal.browser.stack.SBInjector;

final class MMSCUrlOptionField extends EditField implements MMSOptionsScreen$Saveable {
   MMSCUrlOptionField() {
      super("MMSC URL: ", MMSTransportServiceBook.getMMSCUrl());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         if (!MMSTransportServiceBook.hasMMSServiceRecord()) {
            SBInjector.injectMMS();
         }

         String url = this.getText().trim();
         if (url.length() > 0) {
            MMSTransportServiceBook.setMMSCUrl(url);
         }
      }
   }
}
