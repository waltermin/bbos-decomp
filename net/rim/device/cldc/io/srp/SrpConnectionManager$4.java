package net.rim.device.cldc.io.srp;

import net.rim.device.api.servicebook.ServiceRecord;

class SrpConnectionManager$4 implements Runnable {
   private final int val$linkType;
   private final int val$connectionType;
   private final ServiceRecord val$sr;
   private final Object val$sr1;
   private final byte val$eventCode;
   private final SrpConnectionManager this$0;

   SrpConnectionManager$4(SrpConnectionManager _1, int _2, int _3, ServiceRecord _4, Object _5, byte _6) {
      this.this$0 = _1;
      this.val$linkType = _2;
      this.val$connectionType = _3;
      this.val$sr = _4;
      this.val$sr1 = _5;
      this.val$eventCode = _6;
   }

   @Override
   public void run() {
      this.this$0.getRoutingInfoLocalRouter(this.val$linkType, this.val$connectionType, this.val$sr, this.val$sr1, this.val$eventCode);
   }
}
