package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.MenuItem;

final class MenuScreen$4 extends MenuItem {
   private final MenuScreen this$0;

   MenuScreen$4(MenuScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      MenuScreen.onOptions();
   }
}
