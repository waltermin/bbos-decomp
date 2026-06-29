package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.component.ButtonField;

final class BrowserConfigButtonField extends ButtonField {
   public BrowserConfigButtonField(String label, long style) {
      super(label, style);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return (status & 1) == 0 ? super.trackwheelClick(status, time) : false;
   }
}
