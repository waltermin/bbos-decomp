package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;

class LiveCallFactoryRegistry$1 implements Runnable {
   private final LiveCallFactory val$factory;
   private final PhoneCallInitialData val$localData;
   private final Object val$localContext;
   private final LiveCallFactoryRegistry this$0;

   LiveCallFactoryRegistry$1(LiveCallFactoryRegistry _1, LiveCallFactory _2, PhoneCallInitialData _3, Object _4) {
      this.this$0 = _1;
      this.val$factory = _2;
      this.val$localData = _3;
      this.val$localContext = _4;
   }

   @Override
   public void run() {
      this.this$0._createdCall = this.val$factory.createInstance(this.val$localData, this.val$localContext);
      synchronized (this.this$0) {
         this.this$0.notify();
      }
   }
}
