package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import org.w3c.dom.html2.HTMLSelectElement;

class ESHTMLSelectElement$OptionArray$2 implements Runnable {
   private final HTMLSelectElement val$select;
   private final int val$newSize;
   private final ESHTMLSelectElement$OptionArray this$1;

   ESHTMLSelectElement$OptionArray$2(ESHTMLSelectElement$OptionArray _1, HTMLSelectElement _2, int _3) {
      this.this$1 = _1;
      this.val$select = _2;
      this.val$newSize = _3;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         this.val$select.setLength(this.val$newSize);
      }
   }
}
