package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.ui.component.Dialog;

final class TestScreenBase$4 implements Runnable {
   private final TestScreenBase this$0;

   TestScreenBase$4(TestScreenBase _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int response = Dialog.ask(3, "Do you want to skip current test?", 4);
      if (4 == response) {
         this.this$0.cleanup();
         this.this$0.app.getNextTest();
      }
   }
}
