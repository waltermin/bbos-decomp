package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

class Page$2 implements Runnable {
   private final Page this$0;

   Page$2(Page _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Status.show(BrowserResources.getString(580));
   }
}
