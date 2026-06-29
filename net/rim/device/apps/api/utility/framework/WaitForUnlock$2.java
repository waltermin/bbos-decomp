package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.PopupScreen;

class WaitForUnlock$2 implements Runnable {
   private final PopupScreen val$pleaseWaitScreen;
   private final WaitForUnlock this$0;

   WaitForUnlock$2(WaitForUnlock _1, PopupScreen _2) {
      this.this$0 = _1;
      this.val$pleaseWaitScreen = _2;
   }

   @Override
   public void run() {
      Ui.getUiEngine().popScreen(this.val$pleaseWaitScreen);
   }
}
