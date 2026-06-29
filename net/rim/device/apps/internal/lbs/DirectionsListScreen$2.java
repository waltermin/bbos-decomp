package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.MenuItem;

final class DirectionsListScreen$2 extends MenuItem {
   private final DirectionsListScreen this$0;

   DirectionsListScreen$2(DirectionsListScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.handleSelection();
   }
}
