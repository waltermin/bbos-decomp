package net.rim.device.apps.internal.browser.page;

import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;

class Page$3 implements Runnable {
   private final Page this$0;

   Page$3(Page _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (browser != null) {
         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            if (session.getHistory().canGoBack()) {
               browser.goBack(false, true);
            } else {
               browser.closeBrowser(true);
            }
         }
      }
   }
}
