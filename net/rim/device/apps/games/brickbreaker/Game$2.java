package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.ImageBitmap;

final class Game$2 extends MenuItem {
   private final boolean val$_pausedtmp;
   private final Game this$0;

   Game$2(Game _1, String x0, int x1, int x2, boolean _5) {
      super(x0, x1, x2);
      this.this$0 = _1;
      this.val$_pausedtmp = _5;
   }

   @Override
   public final void run() {
      String msg = Game._resources.getString(64);
      Dialog d = new Dialog(msg, CommonResource.getStringArray(10012), null, 0, null, 0);
      d.setIcon(ImageBitmap.create(this.this$0.BMP_WIN));
      int response = d.doModal();
      if (response == 0) {
         this.this$0.init();
         this.this$0._board.init(1, true);
         this.this$0.invalidate();
         this.this$0.startTimer();
      } else {
         this.this$0.invalidate();
         if (!this.val$_pausedtmp) {
            this.this$0.startTimer();
         }
      }
   }
}
