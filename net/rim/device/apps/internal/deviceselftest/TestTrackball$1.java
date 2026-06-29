package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class TestTrackball$1 implements Runnable {
   private final TestTrackball this$0;

   TestTrackball$1(TestTrackball _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (!this.this$0.isDialogOn) {
         this.this$0.isDialogOn = true;
         int response = Dialog.ask(3, DeviceSelfTestResources.getString(122), 4);
         if (4 == response) {
            DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
            app.removeKeyListener(app.currentTest);
            response = Dialog.ask(3, DeviceSelfTestResources.getString(123), 4);
            if (4 == response) {
               app.currentReport.trackball = 1;
            }

            if (-1 == response) {
               app.currentReport.trackball = 0;
            }

            app.saveReports();
            app.popScreen(app.getActiveScreen());
            app.getNextTest();
         }

         this.this$0.isDialogOn = false;
      }
   }
}
