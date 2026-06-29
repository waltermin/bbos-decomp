package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.games.util.HighScoreAccessor;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.browser.BrowserServices;

final class MenuScreen$3 extends MenuItem {
   private final MenuScreen this$0;

   MenuScreen$3(MenuScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      BrowserServices.loadUrl(HighScoreAccessor.getHighScoreURL("BrickBreaker", "4.1"));
   }
}
