package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class IncompleteInputWarningRunnable implements Runnable {
   String _message;
   boolean _edit = true;

   final boolean editPage() {
      return this._edit;
   }

   @Override
   public final void run() {
      if (Dialog.ask(3, this._message, 4) != 4) {
         this._edit = false;
      }
   }

   IncompleteInputWarningRunnable() {
      this._message = BrowserResources.getString(579);
   }
}
