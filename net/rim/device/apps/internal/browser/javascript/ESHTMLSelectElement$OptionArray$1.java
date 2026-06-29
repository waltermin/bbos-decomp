package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLSelectElement;

class ESHTMLSelectElement$OptionArray$1 implements Runnable {
   private final Node val$oldItem;
   private final HTMLSelectElement val$select;
   private final ESHTMLOptionElement val$newOption;
   private final ESHTMLSelectElement$OptionArray this$1;

   ESHTMLSelectElement$OptionArray$1(ESHTMLSelectElement$OptionArray _1, Node _2, HTMLSelectElement _3, ESHTMLOptionElement _4) {
      this.this$1 = _1;
      this.val$oldItem = _2;
      this.val$select = _3;
      this.val$newOption = _4;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         try {
            if (this.val$oldItem == null) {
               this.val$select.appendChild(this.val$newOption.getOptionElement());
            } else {
               this.val$select.replaceChild(this.val$newOption.getOptionElement(), this.val$oldItem);
            }
         } finally {
            return;
         }
      }
   }
}
