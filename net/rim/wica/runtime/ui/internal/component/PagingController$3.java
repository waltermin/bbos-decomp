package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.MenuItem;

class PagingController$3 extends MenuItem {
   private final PagingController this$0;

   PagingController$3(PagingController this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      this.this$0._oldCount = this.this$0.getDisplayItemsCount();
      this.this$0._currentPage = 0;
      if (this.this$0._view instanceof WicletSingleSelectListField) {
         this.this$0.reconstructOrUpdate(this.this$0._oldCount, true, 0);
      } else {
         this.this$0.reconstructOrUpdate(this.this$0._oldCount, true, -1);
      }
   }
}
