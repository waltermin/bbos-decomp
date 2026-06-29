package net.rim.device.apps.internal.ribbon;

final class RibbonLauncherImpl$6 implements Runnable {
   private final boolean val$moveFocusToFirst;
   private final RibbonLauncherImpl this$0;

   RibbonLauncherImpl$6(RibbonLauncherImpl _1, boolean _2) {
      this.this$0 = _1;
      this.val$moveFocusToFirst = _2;
   }

   @Override
   public final void run() {
      if (this.this$0._applicationIconArea.moveApplicationInProgress()) {
         this.this$0.completeMoveApplication(false);
      }

      RibbonLauncherImpl.activateFolder("");
      if (this.val$moveFocusToFirst) {
         this.this$0._applicationIconArea.resetFocusToTop();
      }

      if (!this.this$0._app.isForeground()) {
         this.this$0._app.requestForeground();
      }
   }
}
