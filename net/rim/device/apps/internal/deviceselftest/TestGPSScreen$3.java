package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class TestGPSScreen$3 implements Runnable {
   private final TestGPSScreen this$0;

   TestGPSScreen$3(TestGPSScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(123), 4);
      DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
      if (4 == response) {
         app.currentReport.gpsAntenna = 1;
      }

      if (-1 == response) {
         app.currentReport.gpsAntenna = 0;
      }

      app.saveReports();
      this.this$0.cleanup();
      app.getNextTest();
   }
}
