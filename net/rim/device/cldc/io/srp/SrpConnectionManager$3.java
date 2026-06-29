package net.rim.device.cldc.io.srp;

class SrpConnectionManager$3 implements Runnable {
   private final int val$linkType;
   private final int val$connectionType;
   private final Object val$routingObject;
   private final Object val$routingObject1;
   private final byte val$eventCode;
   private final int val$setupCode;
   private final SrpConnectionManager this$0;

   SrpConnectionManager$3(SrpConnectionManager _1, int _2, int _3, Object _4, Object _5, byte _6, int _7) {
      this.this$0 = _1;
      this.val$linkType = _2;
      this.val$connectionType = _3;
      this.val$routingObject = _4;
      this.val$routingObject1 = _5;
      this.val$eventCode = _6;
      this.val$setupCode = _7;
   }

   @Override
   public void run() {
      this.this$0
         .getRoutingInfoLocalRelay(
            this.val$linkType, this.val$connectionType, this.val$routingObject, this.val$routingObject1, this.val$eventCode, this.val$setupCode
         );
   }
}
