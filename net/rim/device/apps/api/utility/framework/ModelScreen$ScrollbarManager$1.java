package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;

class ModelScreen$ScrollbarManager$1 extends VerticalFieldManager {
   private final ModelScreen$ScrollbarManager this$0;

   ModelScreen$ScrollbarManager$1(ModelScreen$ScrollbarManager _1, long x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int removed) {
      if (super.incrementalLayout(index, added, removed)) {
         XYRect extent = this.this$0._scrollbar.getExtent();
         ModelScreen$ScrollbarManager.access$100(this.this$0, extent.x, extent.y, extent.width, extent.height);
         return true;
      } else {
         return false;
      }
   }
}
