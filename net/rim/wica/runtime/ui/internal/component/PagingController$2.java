package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

class PagingController$2 implements FieldChangeListener {
   private final PagingController$PagingNavigation this$1;

   PagingController$2(PagingController$PagingNavigation this$1) {
      this.this$1 = this$1;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         int oldCount = this.this$1.this$0.getDisplayItemsCount();
         this.this$1.this$0._currentPage++;
         this.this$1.this$0.reconstructOrUpdate(oldCount, true, -1);
      }
   }
}
