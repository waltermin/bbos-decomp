package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class MMSCWTLSModeField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   MMSCWTLSModeField() {
      super(BrowserResources.getString(457), BrowserResources.getStringArray(528), MMSTransportServiceBook.getWTLSMode());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSTransportServiceBook.setWTLSMode(this.getSelectedIndex());
      }
   }
}
