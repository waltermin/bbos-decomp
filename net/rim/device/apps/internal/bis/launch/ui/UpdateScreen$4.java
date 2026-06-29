package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.ui.MenuItem;

final class UpdateScreen$4 extends MenuItem {
   private final UpdateScreen this$0;

   UpdateScreen$4(UpdateScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.update();
   }
}
