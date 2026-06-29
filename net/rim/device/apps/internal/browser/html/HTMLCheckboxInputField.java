package net.rim.device.apps.internal.browser.html;

import net.rim.device.apps.internal.browser.ui.BrowserCheckboxField;

final class HTMLCheckboxInputField extends BrowserCheckboxField {
   private HTMLInput _controller;

   HTMLCheckboxInputField(HTMLInput controller, String label, boolean initiallySelected, long style) {
      super(label, initiallySelected, style);
      this._controller = controller;
   }

   public final void clickInternal() {
      this.trackwheelClick(0, 0);
   }

   @Override
   protected final void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      if ((context & -2147483648) == 0) {
         this._controller.eventOccurred(5);
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
