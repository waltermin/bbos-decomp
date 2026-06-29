package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.ui.Screen;

final class RibbonLauncherImpl$5 implements Runnable {
   private final RibbonLauncherImpl this$0;

   RibbonLauncherImpl$5(RibbonLauncherImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.initializeHomeScreen();
      this.this$0._hierarchyManager.onThemeChangedEvent();
      this.this$0.populateRibbon();
      Screen screen = this.this$0._externalMainScreen;
      if (screen == null) {
         screen = this.this$0._ribbonAppScreen;
      }

      if (screen.getUiEngine() == null) {
         try {
            this.this$0._app.pushScreen(screen);
         } finally {
            return;
         }
      }
   }
}
