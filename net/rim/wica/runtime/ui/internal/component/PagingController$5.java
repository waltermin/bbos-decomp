package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.MenuItem;
import net.rim.wica.runtime.resources.RuntimeResources;

class PagingController$5 extends MenuItem {
   private final PagingController this$0;

   PagingController$5(PagingController this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      int numPages = this.this$0.getNumberOfPages();
      String dialogMsg = RuntimeResources.getString(28, String.valueOf(numPages));
      this.this$0._indexDialog = new PagingController$WicletPopupDialog(this.this$0, dialogMsg, RuntimeResources.getString(3), "*N");
      this.this$0._indexDialog.doModal();
   }
}
