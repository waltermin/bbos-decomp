package net.rim.device.api.ui.theme;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Status;

class ThemeManager$3 implements Runnable {
   private final int val$message;

   ThemeManager$3(int _1) {
      this.val$message = _1;
   }

   @Override
   public void run() {
      Status.show(ResourceBundle.getBundle(3711053710409943671L, "net.rim.device.internal.resource.UI").getString(this.val$message));
   }
}
