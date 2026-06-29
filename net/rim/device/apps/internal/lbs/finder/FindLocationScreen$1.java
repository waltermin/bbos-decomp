package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.MenuItem;

final class FindLocationScreen$1 extends MenuItem {
   private final FindLocationScreen this$0;

   FindLocationScreen$1(FindLocationScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.handleAction(this.this$0._listActions.getSelectedIndex(), false);
   }
}
