package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.ui.MenuItem;

final class SearchPOIDialog$1 extends MenuItem {
   private final SearchPOIDialog this$0;

   SearchPOIDialog$1(SearchPOIDialog this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.close(true);
   }
}
