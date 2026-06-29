package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.ui.MenuItem;

final class TestMainScreen$4 extends MenuItem {
   private final TestMainScreen this$0;

   TestMainScreen$4(TestMainScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.doDelete();
   }
}
