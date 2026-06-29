package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class TestMicrophoneScreen$1 implements Runnable {
   private final TestMicrophoneScreen this$0;

   TestMicrophoneScreen$1(TestMicrophoneScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      label61:
      try {
         if (this.this$0.handler.p != null) {
            if (this.this$0.handler.p.getState() == 400) {
               this.this$0.handler.p.stop();
            }

            this.this$0.handler.p.close();
         }
      } finally {
         break label61;
      }

      int response = Dialog.ask(3, DeviceSelfTestResources.getString(123), 4);
      DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
      if (4 == response) {
         if (this.this$0.microphoneType == 0) {
            app.currentReport.handsetMicrophone = 1;
         } else if (this.this$0.microphoneType == 1) {
            app.currentReport.headsetMicrophone = 1;
         } else if (this.this$0.microphoneType == 2) {
            app.currentReport.bluetoothMicrophone = 1;
         }
      }

      if (-1 == response) {
         if (this.this$0.microphoneType == 0) {
            app.currentReport.handsetMicrophone = 0;
         } else if (this.this$0.microphoneType == 1) {
            app.currentReport.headsetMicrophone = 0;
         } else if (this.this$0.microphoneType == 2) {
            app.currentReport.bluetoothMicrophone = 0;
         }
      }

      app.saveReports();
      this.this$0.cleanup();
      app.getNextTest();
   }
}
