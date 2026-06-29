package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.MenuItem;

final class LocationsListScreen$4 extends MenuItem {
   private final LocationsListScreen this$0;

   LocationsListScreen$4(LocationsListScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.cleanup();
      this.this$0.makePhoneCall(this.this$0._location._phone);
   }
}
