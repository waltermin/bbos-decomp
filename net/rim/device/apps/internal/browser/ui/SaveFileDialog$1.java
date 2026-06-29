package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.component.Status;

class SaveFileDialog$1 implements Runnable {
   private final String val$message;

   SaveFileDialog$1(String _1) {
      this.val$message = _1;
   }

   @Override
   public void run() {
      Status.show(this.val$message);
   }
}
