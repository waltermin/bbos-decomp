package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.system.Application;

class HTMLForm$2 implements Runnable {
   private final HTMLForm this$0;

   HTMLForm$2(HTMLForm _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         int length = this.this$0._elements.getLength();

         for (int index = 0; index < length; index++) {
            ((HTMLInput)this.this$0._elements.item(index)).reset();
         }
      }
   }
}
