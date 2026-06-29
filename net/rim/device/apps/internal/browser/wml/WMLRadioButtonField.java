package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserRadioButtonField;

final class WMLRadioButtonField extends BrowserRadioButtonField {
   WMLRadioButtonField(String label, String title) {
      super(label);
      if (title != null) {
         this.setOptionsMenuText(title);
      } else {
         this.setOptionsMenuText(BrowserResources.getString(552));
      }
   }
}
