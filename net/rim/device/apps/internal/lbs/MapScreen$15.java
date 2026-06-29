package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.UiApplication;

final class MapScreen$15 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$15(MapScreen this$0, int x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   final boolean isVisible() {
      return this.this$0._mapField._locationsListScreen != null;
   }

   @Override
   public final void run() {
      if (this.this$0._mapField._currentPOIs.length > 0) {
         UiApplication.getUiApplication().pushScreen(this.this$0._mapField._locationsListScreen);
      }
   }
}
