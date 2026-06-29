package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Convert;
import org.w3c.dom.html2.HTMLOptionElement;

class ESHTMLOptionElement$1 implements Runnable {
   private final String val$name;
   private final HTMLOptionElement val$item;
   private final long val$value;
   private final ESHTMLOptionElement this$0;

   ESHTMLOptionElement$1(ESHTMLOptionElement _1, String _2, HTMLOptionElement _3, long _4) {
      this.this$0 = _1;
      this.val$name = _2;
      this.val$item = _3;
      this.val$value = _4;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         try {
            if (this.val$name == Names.selected) {
               this.val$item.setSelected(Convert.toBoolean(this.val$value));
            } else if (this.val$name == Names.value) {
               this.val$item.setValue(Convert.toString(this.val$value));
            } else if (this.val$name == Names.defaultSelected) {
               this.val$item.setDefaultSelected(Convert.toBoolean(this.val$value));
            } else if (this.val$name == Names.text) {
               this.val$item.setAttribute(Names.text, Convert.toString(this.val$value));
            }
         } finally {
            return;
         }
      }
   }
}
