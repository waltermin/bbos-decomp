package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;

final class MenuScreen$1 extends MenuItem {
   private final MenuScreen this$0;

   MenuScreen$1(MenuScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._game != null) {
         UiApplication.getUiApplication().pushScreen(this.this$0._game);
         this.this$0._game.init();
         this.this$0._game.cycle();
      }
   }
}
