package net.rim.device.apps.internal.browser.cod;

import java.util.Enumeration;

class OTAStatusReportSender$3 extends Thread {
   private final OTAStatusReportSender this$0;

   OTAStatusReportSender$3(OTAStatusReportSender _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Enumeration keys = OTAStatusReportSender._otaData._sendQueue.keys();

      while (keys.hasMoreElements()) {
         String moduleGroupName = (String)keys.nextElement();
         OTAStatusReportSender$Report report = (OTAStatusReportSender$Report)OTAStatusReportSender._otaData._sendQueue.get(moduleGroupName);
         if (report != null) {
            this.this$0.doSend(moduleGroupName, report);
         }
      }
   }
}
