package net.rim.device.api.lbs;

import net.rim.device.api.ui.MenuItem;

class LBSLoggingConfigScreen$3 extends MenuItem {
   private final LBSLoggingConfigScreen this$0;

   LBSLoggingConfigScreen$3(LBSLoggingConfigScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      this.this$0.setStateForAllCheckboxes(false);
   }
}
