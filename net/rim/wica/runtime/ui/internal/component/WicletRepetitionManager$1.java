package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.container.VerticalFieldManager;

class WicletRepetitionManager$1 extends VerticalFieldManager {
   private final WicletRepetitionManager$RowManager this$1;

   WicletRepetitionManager$1(WicletRepetitionManager$RowManager this$1) {
      this.this$1 = this$1;
   }

   @Override
   public int getFieldCount() {
      int fieldCount = super.getFieldCount();
      return this.this$1.this$0._isCollapsible && this.getManager().getFieldWithFocus() == null && fieldCount > 1 ? 1 : fieldCount;
   }

   @Override
   public void deleteAll() {
      this.deleteRange(0, super.getFieldCount());
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int deleted) {
      return added != deleted;
   }
}
