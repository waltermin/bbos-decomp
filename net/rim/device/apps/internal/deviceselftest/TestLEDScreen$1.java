package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class TestLEDScreen$1 implements Runnable {
   private final TestLEDScreen this$0;

   TestLEDScreen$1(TestLEDScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(123), 4);
      DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
      if (4 == response) {
         app.currentReport.led = 1;
      }

      if (-1 == response) {
         app.currentReport.led = 0;
      }

      app.saveReports();
      this.this$0.cleanup();
      app.getNextTest();
   }
}
