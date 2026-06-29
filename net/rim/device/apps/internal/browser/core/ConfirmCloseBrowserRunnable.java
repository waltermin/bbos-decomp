package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class ConfirmCloseBrowserRunnable implements Runnable {
   private boolean _close = true;
   private boolean _confirm;

   public final boolean closeBrowser() {
      return this._close;
   }

   @Override
   public final void run() {
      if (this._confirm && GeneralProperty.getCurrentPropertyAsBoolean(0)) {
         Dialog dialog = (Dialog)(new Object(3, BrowserResources.getString(430), 0, Bitmap.getPredefinedBitmap(1), 0));
         if (dialog.doModal() != 4) {
            this._close = false;
         }
      }
   }

   public ConfirmCloseBrowserRunnable(boolean confirm) {
      this._confirm = confirm;
   }
}
