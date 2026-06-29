package net.rim.device.apps.internal.browser.cod;

class OTAStatusReportSender$2 extends Thread {
   private final String val$moduleGroupName;
   private final OTAStatusReportSender$Report val$report;
   private final OTAStatusReportSender this$0;

   OTAStatusReportSender$2(OTAStatusReportSender _1, String _2, OTAStatusReportSender$Report _3) {
      this.this$0 = _1;
      this.val$moduleGroupName = _2;
      this.val$report = _3;
   }

   @Override
   public void run() {
      this.this$0.doSend(this.val$moduleGroupName, this.val$report);
   }
}
