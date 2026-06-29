package net.rim.device.apps.internal.browser.cod;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.PopupScreen;

class JADBrowserField$1 extends PopupScreen {
   private final JADBrowserField this$0;

   JADBrowserField$1(JADBrowserField _1, Manager x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   public void onDisplay() {
      super.onDisplay();
      new Thread(this.this$0._downloadThread).start();
      this.this$0._statusButton.setFocus();
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      if (c != 27 && c != '\n') {
         return super.keyChar(c, status, time);
      }

      this.this$0.fieldChanged(this.this$0._statusButton, 0);
      return true;
   }
}
