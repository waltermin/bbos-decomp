package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class ZoomImageCropper$2 extends MenuItem {
   private final ZoomImageCropper this$0;

   ZoomImageCropper$2(ZoomImageCropper _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.save();
   }
}
