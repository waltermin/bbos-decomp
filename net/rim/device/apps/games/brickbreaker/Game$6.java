package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.i18n.CommonResource;

final class Game$6 extends MenuItem {
   private final Game this$0;

   Game$6(Game _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      String msg = Game._resources.getString(2) + "\n" + Game._resources.getString(41) + "\n" + Game._resources.getString(52);
      Dialog d = new Dialog(msg, CommonResource.getStringArray(10004), null, 0, Bitmap.getBitmapResource("logo.png"), 0);
      d.doModal();
   }
}
