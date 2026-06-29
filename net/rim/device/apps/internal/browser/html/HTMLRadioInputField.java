package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.browser.ui.BrowserRadioButtonField;

final class HTMLRadioInputField extends BrowserRadioButtonField {
   private HTMLInput _controller;

   HTMLRadioInputField(HTMLInput controller, String label, RadioButtonGroup group, boolean initiallySelected, long style) {
      super(label, group, initiallySelected, style);
      this._controller = controller;
   }

   public final void clickInternal() {
      this.trackwheelClick(0, 0);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         this._controller.eventOccurred(5);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      this._controller.eventOccurred(3);
   }

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      this._controller.eventOccurred(6);
   }
}
