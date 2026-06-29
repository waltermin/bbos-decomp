package net.rim.device.cldc.io.srp;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;

class SrpConnectionManager$2 implements Runnable {
   private final int val$linkType;
   private final int val$connectionType;
   private final SrpConnectionManager this$0;

   SrpConnectionManager$2(SrpConnectionManager _1, int _2, int _3) {
      this.this$0 = _1;
      this.val$linkType = _2;
      this.val$connectionType = _3;
   }

   @Override
   public void run() {
      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByType(0);

      for (int i = srs.length - 1; i >= 0; i--) {
         ServiceRecord sr = srs[i];
         this.this$0.getRoutingInfoRouter(this.val$linkType, this.val$connectionType, sr, null, (byte)1, false);
      }
   }
}
