package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.MenuItem;

final class LocationsListScreen$3 extends MenuItem {
   private final LocationsListScreen this$0;

   LocationsListScreen$3(LocationsListScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.cleanup();
      if (this.this$0._activeField == this.this$0._listActions) {
         this.this$0.showOnMap(this.this$0._field._currentPOIs[0]);
      } else {
         if (this.this$0._location != null) {
            this.this$0.showOnMap(this.this$0._location);
         }
      }
   }
}
