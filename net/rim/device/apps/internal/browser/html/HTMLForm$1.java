package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;

class HTMLForm$1 implements Runnable {
   private final Field val$field;
   private final HTMLForm this$0;

   HTMLForm$1(HTMLForm _1, Field _2) {
      this.this$0 = _1;
      this.val$field = _2;
   }

   @Override
   public void run() {
      this.val$field.setFocus();
   }
}
