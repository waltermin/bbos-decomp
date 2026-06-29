package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.ui.MenuItem;

class LongTermHistoryScreen$1 extends MenuItem {
   private final LongTermHistoryScreen this$0;

   LongTermHistoryScreen$1(LongTermHistoryScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.displayHistory(0);
   }
}
