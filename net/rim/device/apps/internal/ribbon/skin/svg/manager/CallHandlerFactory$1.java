package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;

class CallHandlerFactory$1 implements Runnable {
   private final CallHandlerFactory this$0;

   CallHandlerFactory$1(CallHandlerFactory _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      PhoneLogs.getInstance();
      PhoneLogs.addListener(this.this$0._helper);
      synchronized (this.this$0._helper) {
         this.this$0._initialized = true;
         if (this.this$0._handlerForUpdate != null) {
            this.this$0._handlerForUpdate.update(false);
            this.this$0._handlerForUpdate = null;
         }
      }
   }
}
