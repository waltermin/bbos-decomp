package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.MenuItem;

final class Game$1 extends MenuItem {
   private final Game this$0;

   Game$1(Game _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.invalidate();
      this.this$0.startTimer();
   }
}
