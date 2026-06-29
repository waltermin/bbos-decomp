package net.rim.device.apps.internal.task;

import net.rim.device.api.ui.container.VerticalFieldManager;

final class EditTaskScreen$1 extends VerticalFieldManager {
   private final EditTaskScreen this$0;

   EditTaskScreen$1(EditTaskScreen _1) {
      this.this$0 = _1;
   }

   @Override
   protected final void sublayout(int width, int height) {
      super.sublayout(width, height);
      int minHeight = this.getFont().getHeight() << 1;
      if (this.getContentHeight() < minHeight) {
         this.setExtent(this.getContentWidth(), minHeight);
      }
   }
}
