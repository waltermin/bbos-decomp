package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class TestHeadsetDetectScreen$1 implements Runnable {
   private final TestHeadsetDetectScreen this$0;

   TestHeadsetDetectScreen$1(TestHeadsetDetectScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(123), 4);
      DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
      if (4 == response) {
         app.currentReport.headsetDetectSwitch = 1;
      }

      if (-1 == response) {
         app.currentReport.headsetDetectSwitch = 0;
      }

      app.saveReports();
      this.this$0.cleanup();
      app.getNextTest();
   }
}
