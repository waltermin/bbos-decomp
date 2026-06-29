package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.MenuItem;

class DocViewTextDisplayField$DocViewPlayScreen$3 extends MenuItem {
   private final DocViewTextDisplayField$DocViewPlayScreen this$1;

   DocViewTextDisplayField$DocViewPlayScreen$3(DocViewTextDisplayField$DocViewPlayScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$1 = _1;
   }

   @Override
   public void run() {
      if (!this.this$1._gotCurrentSlide && this.this$1._currentIndex >= 0 && this.this$1._renderedDomIDs != null) {
         this.this$1.this$0.retrieveSlide(this.this$1._renderedDomIDs[this.this$1._currentIndex], false, true);
      }
   }
}
