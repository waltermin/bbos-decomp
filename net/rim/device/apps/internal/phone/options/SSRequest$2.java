package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;

class SSRequest$2 implements Runnable {
   private final Screen val$screen;
   private final SSRequest this$0;

   SSRequest$2(SSRequest _1, Screen _2) {
      this.this$0 = _1;
      this.val$screen = _2;
   }

   @Override
   public void run() {
      Ui.getUiEngine().popScreen(this.val$screen);
      PhoneOptionsScreen.setSSRequestInProgress(false);
   }
}
