package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.Dialog;

class SSRequest$1 implements Runnable {
   private final String[] val$errors;
   private final SSRequest this$0;

   SSRequest$1(SSRequest _1, String[] _2) {
      this.this$0 = _1;
      this.val$errors = _2;
   }

   @Override
   public void run() {
      for (int i = 0; i < this.val$errors.length; i++) {
         if (this.val$errors[i] != null && this.val$errors[i].length() > 0) {
            Dialog.alert(this.val$errors[i]);
         }
      }
   }
}
