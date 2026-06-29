package net.rim.device.apps.internal.browser.page;

import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;

class Page$4 implements Runnable {
   private final Page this$0;

   Page$4(Page _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (browser != null) {
         browser.getBrowserLock();
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            browser.abortCurrentRequest();
            var4 = false;
         } finally {
            if (var4) {
               browser.releaseBrowserLock();
            }
         }

         browser.releaseBrowserLock();
      }
   }
}
