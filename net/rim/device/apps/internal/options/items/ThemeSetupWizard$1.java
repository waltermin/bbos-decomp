package net.rim.device.apps.internal.options.items;

final class ThemeSetupWizard$1 implements Runnable {
   private final ThemeSetupWizard this$0;

   ThemeSetupWizard$1(ThemeSetupWizard _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      new ThemeSetupWizard$ThumbnailLoaderThread(this.this$0).start();
   }
}
