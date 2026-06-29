package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.MenuItem;

class DocViewSlideshowField$2 extends MenuItem {
   private final DocViewSlideshowField this$0;

   DocViewSlideshowField$2(DocViewSlideshowField _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.advance(true, 1);
   }
}
