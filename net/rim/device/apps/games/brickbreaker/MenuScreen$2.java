package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.MenuItem;

final class MenuScreen$2 extends MenuItem {
   private final MenuScreen this$0;

   MenuScreen$2(MenuScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      MenuScreen._options.sendScore(false);
   }
}
