package net.rim.device.apps.api.setupwizard;

class YesNoField$1 implements Runnable {
   private final YesNoField this$0;

   YesNoField$1(YesNoField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.activateVerb();
   }
}
