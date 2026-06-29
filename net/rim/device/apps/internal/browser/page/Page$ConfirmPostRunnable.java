package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class Page$ConfirmPostRunnable implements Runnable {
   private boolean _isPostDesired;

   public final boolean isPostDesired() {
      return this._isPostDesired;
   }

   @Override
   public final void run() {
      if (Dialog.ask(3, BrowserResources.getString(756), 4) == 4) {
         this._isPostDesired = true;
      }
   }
}
