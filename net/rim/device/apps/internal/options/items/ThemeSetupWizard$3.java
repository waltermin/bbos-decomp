package net.rim.device.apps.internal.options.items;

final class ThemeSetupWizard$3 implements Runnable {
   private final ThemeSetupWizard this$0;

   ThemeSetupWizard$3(ThemeSetupWizard _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.confirmNewTheme();
   }
}
