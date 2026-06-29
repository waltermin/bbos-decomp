package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class MMSCWTLSClientTypeField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   MMSCWTLSClientTypeField() {
      super(BrowserResources.getString(491), BrowserResources.getStringArray(593), MMSTransportServiceBook.getWTLSClientType() + 1);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSTransportServiceBook.setWAPAccessMode(this.getSelectedIndex() - 1);
      }
   }
}
