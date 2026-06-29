package net.rim.device.console;

final class Console$ApplicationSwitchEnd implements Runnable {
   private final Console this$0;

   Console$ApplicationSwitchEnd(Console _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._appSwitchScreen = null;
   }
}
