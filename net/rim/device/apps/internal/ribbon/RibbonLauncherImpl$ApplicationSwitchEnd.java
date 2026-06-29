package net.rim.device.apps.internal.ribbon;

final class RibbonLauncherImpl$ApplicationSwitchEnd implements Runnable {
   private final RibbonLauncherImpl this$0;

   RibbonLauncherImpl$ApplicationSwitchEnd(RibbonLauncherImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._appSwitchScreen = null;
      this.this$0._imSwitchScreen = null;
   }
}
