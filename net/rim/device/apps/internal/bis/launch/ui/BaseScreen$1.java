package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.ui.MenuItem;

final class BaseScreen$1 extends MenuItem {
   private final BaseScreen this$0;

   BaseScreen$1(BaseScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      System.exit(0);
   }
}
