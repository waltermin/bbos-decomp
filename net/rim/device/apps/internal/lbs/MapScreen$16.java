package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.UiApplication;

final class MapScreen$16 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$16(MapScreen this$0, int x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   final boolean isVisible() {
      return this.this$0._mapField._directionsListScreen != null;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushScreen(this.this$0._mapField._directionsListScreen);
   }
}
