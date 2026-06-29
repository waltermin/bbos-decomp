package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import org.w3c.dom.Element;

class ESHTMLElement$StyleObject$2 implements Runnable {
   private final String val$visibilityMode;
   private final ESHTMLElement$StyleObject this$1;

   ESHTMLElement$StyleObject$2(ESHTMLElement$StyleObject _1, String _2) {
      this.this$1 = _1;
      this.val$visibilityMode = _2;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         try {
            ((Element)this.this$1.this$0.getHTMLElement()).setAttribute("style:visibility", this.val$visibilityMode);
         } finally {
            return;
         }
      }
   }
}
