package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class DocViewSlideshowField$5 extends MenuItem {
   private final DocViewSlideshowField this$0;

   DocViewSlideshowField$5(DocViewSlideshowField _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.more();
   }
}
