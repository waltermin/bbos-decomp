package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.ui.Screen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class GoToVerb extends BrowserVerb {
   private Screen _screenToClose;
   private static final int DESCRIPTION = 101;

   public GoToVerb() {
      this(null);
   }

   public GoToVerb(Screen screenToClose) {
      super(1180240);
      this._screenToClose = screenToClose;
      BrowserHotkeys.registerBrowserHotKey(333, this);
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(101);
   }

   @Override
   public final Object invoke(Object context) {
      if (this._screenToClose != null) {
         this._screenToClose.close();
      }

      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      browser.showStartupPage(false);
      return null;
   }

   @Override
   public final boolean isEnabled() {
      BrowserSession session = BrowserSession.getCurrentSession();
      return session != null ? session.getConfig().getPropertyAsInt(7) == 0 : true;
   }

   @Override
   public final void cleanup() {
      BrowserHotkeys.deregisterBrowserHotKey(333);
   }
}
