package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class EnableJavascriptRunnable implements Runnable {
   private int _result = -1;
   private BrowserContent _browserContent;

   public final boolean turnOnJavascript() {
      return this._result == 4;
   }

   @Override
   public final void run() {
      RenderingOptions renderingOptions = null;
      if (this._browserContent != null) {
         renderingOptions = this._browserContent.getRenderingOptions();
      }

      if (renderingOptions == null || renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 37, true)) {
         Dialog dialog = new Dialog(3, BrowserResources.getString(755), 0, null, 0);
         this._result = dialog.doModal();
      }
   }

   public EnableJavascriptRunnable(BrowserContent browserContent) {
      this._browserContent = browserContent;
   }
}
