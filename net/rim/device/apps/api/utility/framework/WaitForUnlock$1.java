package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.PopupScreen;

class WaitForUnlock$1 implements Runnable {
   private final PopupScreen val$pleaseWaitScreen;
   private final WaitForUnlock this$0;

   WaitForUnlock$1(WaitForUnlock _1, PopupScreen _2) {
      this.this$0 = _1;
      this.val$pleaseWaitScreen = _2;
   }

   @Override
   public void run() {
      Ui.getUiEngine().pushGlobalScreen(this.val$pleaseWaitScreen, Integer.MAX_VALUE, 2);
   }
}
