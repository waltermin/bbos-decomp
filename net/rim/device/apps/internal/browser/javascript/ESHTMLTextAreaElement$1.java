package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Convert;

class ESHTMLTextAreaElement$1 implements Runnable {
   private final String val$name;
   private final long val$value;
   private final ESHTMLTextAreaElement this$0;

   ESHTMLTextAreaElement$1(ESHTMLTextAreaElement _1, String _2, long _3) {
      this.this$0 = _1;
      this.val$name = _2;
      this.val$value = _3;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         try {
            if (this.val$name == Names.defaultValue) {
               this.this$0.getTextArea().setDefaultValue(Convert.toString(this.val$value));
            } else if (this.val$name == Names.value) {
               this.this$0.getTextArea().setValue(Convert.toString(this.val$value));
            }
         } finally {
            return;
         }
      }
   }
}
