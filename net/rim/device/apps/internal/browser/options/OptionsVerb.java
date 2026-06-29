package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.internal.system.InternalServices;

public final class OptionsVerb extends BrowserVerb {
   boolean _releaseLockOnExit;

   public OptionsVerb(boolean releaseLockOnExit) {
      super(16987173, -6812884907508133143L, "net.rim.device.internal.resource.Common", 20);
      this._releaseLockOnExit = releaseLockOnExit;
      if (!InternalServices.isReducedFormFactor()) {
         BrowserHotkeys.registerBrowserHotKey(338, this);
      }
   }

   @Override
   public final Object invoke(Object context) {
      BrowserDaemonRegistry.getInstance().setReleaseLock(false);
      synchronized (Application.getEventLock()) {
         UiApplication.getUiApplication().pushScreen(new BrowserOptionsScreen(this._releaseLockOnExit));
         return null;
      }
   }

   @Override
   public final boolean isModal() {
      return true;
   }

   @Override
   public final void cleanup() {
      if (!InternalServices.isReducedFormFactor()) {
         BrowserHotkeys.deregisterBrowserHotKey(338);
      }
   }
}
