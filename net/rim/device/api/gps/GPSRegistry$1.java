package net.rim.device.api.gps;

class GPSRegistry$1 implements Runnable {
   private final int val$processId;
   private final GPSRegistry this$0;

   GPSRegistry$1(GPSRegistry _1, int _2) {
      this.this$0 = _1;
      this.val$processId = _2;
   }

   @Override
   public void run() {
      this.this$0._pdeTable.remove(this.val$processId);
      if (GPSRegistry.isVerizon()) {
         GPS$AppCredential cred = new GPS$AppCredential(0, "");
         GPS$GPSPDEInfo pdeInfo = new GPS$GPSPDEInfo(0, 0, cred);
         GPS.setPDEInfo(pdeInfo);
      }
   }
}
