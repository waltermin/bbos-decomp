package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.UiApplication;

final class MapScreen$21 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$21(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushScreen(this.this$0._gpsSelectionScreen = new OptionsScreen(this.this$0));
   }
}
