package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.internal.browser.page.BrowserContentImpl;

class LoadImageVerb$1 implements Runnable {
   private final LoadImageVerb this$0;

   LoadImageVerb$1(LoadImageVerb _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      ((BrowserContentImpl)this.this$0._browserContent).requestImage(this.this$0._urlToLoad);
   }
}
