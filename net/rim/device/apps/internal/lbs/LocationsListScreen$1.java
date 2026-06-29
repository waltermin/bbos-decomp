package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.MenuItem;

final class LocationsListScreen$1 extends MenuItem {
   private final LocationsListScreen$HTTPLink this$1;

   LocationsListScreen$1(LocationsListScreen$HTTPLink this$1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$1 = this$1;
   }

   @Override
   public final void run() {
      this.this$1.launchURL();
   }
}
