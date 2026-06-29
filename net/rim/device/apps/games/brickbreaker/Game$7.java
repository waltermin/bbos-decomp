package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;

final class Game$7 extends MenuItem {
   private final Game this$0;

   Game$7(Game _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().requestBackground();
   }
}
