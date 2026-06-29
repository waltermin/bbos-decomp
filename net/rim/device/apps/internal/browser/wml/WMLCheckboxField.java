package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.internal.browser.ui.BrowserCheckboxField;

final class WMLCheckboxField extends BrowserCheckboxField {
   WMLCheckboxField(String label, String title) {
      super(label, false);
      if (title != null) {
         this.setOptionsMenuText(title);
      }
   }
}
