package net.rim.wica.runtime.activation.internal;

import net.rim.device.api.ui.component.Dialog;
import net.rim.wica.runtime.resources.RuntimeResources;

class AGMainScreen$3 implements Runnable {
   private final AGMainScreen this$0;

   AGMainScreen$3(AGMainScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      this.this$0.completeProgress();
      Dialog.inform(RuntimeResources.getString(27));
   }
}
