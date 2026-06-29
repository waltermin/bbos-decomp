package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserCheckboxField;

final class ConfirmLeavePageRunnable implements Runnable {
   private boolean _continue = true;

   public final boolean getContinue() {
      return this._continue;
   }

   @Override
   public final void run() {
      if (GeneralProperty.getCurrentPropertyAsBoolean(24)) {
         String message = BrowserResources.getString(708);
         BrowserCheckboxField dontAskAgain = new BrowserCheckboxField(BrowserResources.getString(640), false);
         Dialog dialog = new Dialog(3, message, 0, null, 0);
         dialog.add(dontAskAgain);
         int result = dialog.doModal();
         if (dontAskAgain.getChecked()) {
            GeneralProperty.setCurrentProperty(24, false);
         }

         if (result != 4) {
            this._continue = false;
         }
      }
   }
}
