package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.ui.MenuItem;

final class OptionsScreen$1 extends MenuItem {
   private final OptionsScreen this$0;

   OptionsScreen$1(OptionsScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.onSave();
   }
}
