package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class ZoomBitmapField$4 extends MenuItem {
   private final ZoomBitmapField this$0;

   ZoomBitmapField$4(ZoomBitmapField _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.zoomToFill();
   }
}
