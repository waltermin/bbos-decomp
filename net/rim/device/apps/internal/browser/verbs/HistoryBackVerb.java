package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class HistoryBackVerb extends BrowserVerb {
   private static final int DESCRIPTION = 105;

   public HistoryBackVerb() {
      super(1180416);
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(105);
   }

   @Override
   public final Object invoke(Object context) {
      boolean invokedByEscape = false;
      if (context instanceof Object) {
         invokedByEscape = context;
      }

      BrowserDaemonRegistry.getInstance().goBack(invokedByEscape, false);
      return null;
   }

   @Override
   public final boolean isEnabled() {
      BrowserSession session = BrowserSession.getCurrentSession();
      return session != null ? session.getHistory().canGoBack() : false;
   }
}
