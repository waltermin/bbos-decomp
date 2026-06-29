package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Convert;
import org.w3c.dom.html2.HTMLInputElement;

class ESHTMLInputElement$1 implements Runnable {
   private final String val$name;
   private final long val$value;
   private final ESHTMLInputElement this$0;

   ESHTMLInputElement$1(ESHTMLInputElement _1, String _2, long _3) {
      this.this$0 = _1;
      this.val$name = _2;
      this.val$value = _3;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         try {
            HTMLInputElement item = this.this$0.getInputElement();
            if (this.val$name == Names.defaultValue) {
               item.setDefaultValue(Convert.toString(this.val$value));
            } else if (this.val$name == Names.value) {
               item.setValue(Convert.toString(this.val$value));
            } else if (this.val$name == Names.checked) {
               item.setChecked(Convert.toBoolean(this.val$value));
            } else if (this.val$name == Names.defaultChecked) {
               item.setDefaultChecked(Convert.toBoolean(this.val$value));
            }
         } finally {
            return;
         }
      }
   }
}
