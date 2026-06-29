package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.MenuItem;

class DocViewTextDisplayField$DocViewPlayScreen$4 extends MenuItem {
   private final DocViewTextDisplayField$DocViewPlayScreen this$1;

   DocViewTextDisplayField$DocViewPlayScreen$4(DocViewTextDisplayField$DocViewPlayScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.setDisplayModeImpl((byte)1);
   }
}
