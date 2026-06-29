package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class TestSpeakerScreen$1 implements Runnable {
   private final TestSpeakerScreen this$0;

   TestSpeakerScreen$1(TestSpeakerScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(123), 4);
      DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
      if (4 == response) {
         if (this.this$0.speakerType == 0) {
            app.currentReport.handsetSpeaker = 1;
         } else if (this.this$0.speakerType == 1) {
            app.currentReport.handsfreeSpeaker = 1;
         } else if (this.this$0.speakerType == 2) {
            app.currentReport.headsetSpeaker = 1;
         } else if (this.this$0.speakerType == 3) {
            app.currentReport.bluetoothSpeaker = 1;
         }
      }

      if (-1 == response) {
         if (this.this$0.speakerType == 0) {
            app.currentReport.handsetSpeaker = 0;
         } else if (this.this$0.speakerType == 1) {
            app.currentReport.handsfreeSpeaker = 0;
         } else if (this.this$0.speakerType == 2) {
            app.currentReport.headsetSpeaker = 0;
         } else if (this.this$0.speakerType == 3) {
            app.currentReport.bluetoothSpeaker = 0;
         }
      }

      app.saveReports();
      this.this$0.cleanup();
      app.getNextTest();
   }
}
