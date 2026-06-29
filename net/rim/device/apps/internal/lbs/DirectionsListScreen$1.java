package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;

final class DirectionsListScreen$1 extends MenuItem {
   private final DirectionsListScreen this$0;

   DirectionsListScreen$1(DirectionsListScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().popScreen(this.this$0);
      ((MapScreen)((MapField)this.this$0._field)._screen).getDirections();
   }
}
