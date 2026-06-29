package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

final class TestScreenBase$5 implements Runnable {
   private final TestScreenBase this$0;

   TestScreenBase$5(TestScreenBase _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int response = Dialog.ask(3, "Do you want to skip all of the tests?", 4);
      if (4 == response) {
         this.this$0.cleanup();
         this.this$0.app.initialize();
         Status.show("The remaining tests have been skipped. The test report is available at the main screen!", 2000);
      }
   }
}
