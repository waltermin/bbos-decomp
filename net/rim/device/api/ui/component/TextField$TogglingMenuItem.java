package net.rim.device.api.ui.component;

import net.rim.device.api.ui.MenuItem;
import net.rim.tid.im.SLControlObject;

final class TextField$TogglingMenuItem extends MenuItem {
   int _inputMode;

   TextField$TogglingMenuItem() {
      super("", 50680656, Integer.MAX_VALUE);
   }

   final void setInputMode(int inputMode) {
      this._inputMode = inputMode;
   }

   @Override
   public final void run() {
      TextField target = (TextField)this.getTarget();
      SLControlObject cObj = (SLControlObject)target.getInputContext().getInputMethodControlObject();
      cObj.actionPerformed(106, new Integer(this._inputMode));
   }
}
