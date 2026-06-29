package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Convert;

class ESHTMLSelectElement$1 implements Runnable {
   private final long val$value;
   private final ESHTMLSelectElement this$0;

   ESHTMLSelectElement$1(ESHTMLSelectElement _1, long _2) {
      this.this$0 = _1;
      this.val$value = _2;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         try {
            this.this$0.getSelectElement().setSelectedIndex(Convert.toInt32(this.val$value));
         } finally {
            return;
         }
      }
   }
}
