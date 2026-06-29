package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class TestBacklightScreen$1 implements Runnable {
   private final TestBacklightScreen this$0;

   TestBacklightScreen$1(TestBacklightScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(123), 4);
      if (4 == response) {
         app.currentReport.lcdBacklight = 1;
      }

      if (-1 == response) {
         app.currentReport.lcdBacklight = 0;
      }

      app.saveReports();
      this.this$0.cleanup();
      app.getNextTest();
   }
}
