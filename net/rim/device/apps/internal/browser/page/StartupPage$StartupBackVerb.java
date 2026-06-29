package net.rim.device.apps.internal.browser.page;

import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.apps.internal.browser.verbs.HistoryBackVerb;

class StartupPage$StartupBackVerb extends BrowserVerb {
   public StartupPage$StartupBackVerb() {
      super(1180416);
   }

   @Override
   public String toString() {
      return BrowserResources.getString(105);
   }

   @Override
   public Object invoke(Object context) {
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session == null) {
         return null;
      }

      session.getHistory().forceNextNode();
      return new HistoryBackVerb().invoke(context);
   }
}
