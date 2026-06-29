package net.rim.wica.runtime.activation.internal;

import net.rim.device.api.ui.component.GaugeField;

class AGMainScreen$4 implements Runnable {
   private final AGMainScreen this$0;

   AGMainScreen$4(AGMainScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      GaugeField statusBar = (GaugeField)this.this$0._statusManager.getField(1);
      statusBar.setValue(statusBar.getValue() + 1);
   }
}
