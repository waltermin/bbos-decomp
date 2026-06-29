package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.MenuItem;

final class FindAddressDialog$1 extends MenuItem {
   private final FindAddressDialog this$0;

   FindAddressDialog$1(FindAddressDialog this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.searchSelected();
   }
}
