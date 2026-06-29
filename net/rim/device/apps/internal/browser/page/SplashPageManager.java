package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.container.HorizontalFieldManager;

final class SplashPageManager extends HorizontalFieldManager {
   private int height = -1;

   protected SplashPageManager() {
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      if (this.height == -1) {
         this.height = maxHeight;
      }

      super.sublayout(maxWidth, this.height);
   }
}
