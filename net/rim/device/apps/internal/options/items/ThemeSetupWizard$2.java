package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.theme.ThemeManager;

final class ThemeSetupWizard$2 implements Runnable {
   private final ThemeSetupWizard this$0;

   ThemeSetupWizard$2(ThemeSetupWizard _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      ThemeManager.setActiveTheme(this.this$0._newThemeName);
   }
}
