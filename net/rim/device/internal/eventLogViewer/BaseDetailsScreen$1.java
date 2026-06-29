package net.rim.device.internal.eventLogViewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class BaseDetailsScreen$1 extends MenuItem {
   private final BaseDetailsScreen this$0;

   BaseDetailsScreen$1(BaseDetailsScreen _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.copyEventData2Clipboard();
   }
}
