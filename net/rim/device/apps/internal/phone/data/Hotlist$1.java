package net.rim.device.apps.internal.phone.data;

class Hotlist$1 implements Runnable {
   private final CallerIDInfo val$cidInfo;
   private final int val$flgs;
   private final Object val$ctxt;
   private final Hotlist this$0;

   Hotlist$1(Hotlist _1, CallerIDInfo _2, int _3, Object _4) {
      this.this$0 = _1;
      this.val$cidInfo = _2;
      this.val$flgs = _3;
      this.val$ctxt = _4;
   }

   @Override
   public void run() {
      this.this$0.internalCallEvent(this.val$cidInfo, this.val$flgs, this.val$ctxt);
   }
}
