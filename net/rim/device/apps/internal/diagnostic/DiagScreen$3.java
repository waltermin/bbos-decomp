package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.ui.MenuItem;

final class DiagScreen$3 extends MenuItem {
   private final DiagScreen this$0;

   DiagScreen$3(DiagScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.doDelete();
   }
}
