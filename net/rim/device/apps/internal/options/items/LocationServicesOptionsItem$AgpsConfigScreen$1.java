package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.MenuItem;

final class LocationServicesOptionsItem$AgpsConfigScreen$1 extends MenuItem {
   private final LocationServicesOptionsItem$AgpsConfigScreen this$1;

   LocationServicesOptionsItem$AgpsConfigScreen$1(LocationServicesOptionsItem$AgpsConfigScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      this.this$1.onSave();
   }
}
