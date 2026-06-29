package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class WMLScriptBrowserContent$ConfirmExecuteRunnable implements Runnable {
   private String _name;
   private boolean _execute = true;
   private RenderingOptions _renderingOptions;

   final boolean executeScript() {
      return this._execute;
   }

   @Override
   public final void run() {
      if (this._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 9, false)) {
         String[] name = new Object[]{this._name == null ? "WML Script" : this._name};
         String message = MessageFormat.format(BrowserResources.getString(286), name);
         String[] choices = BrowserResources.getStringArray(287);
         if (Dialog.ask(message, choices, 0) != 0) {
            this._execute = false;
         }
      }
   }

   WMLScriptBrowserContent$ConfirmExecuteRunnable(String name, RenderingOptions renderingOptions) {
      this._name = name;
      this._renderingOptions = renderingOptions;
   }
}
