package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.MenuItem;

final class PeerData$OptionsScreen$1 extends MenuItem {
   private final PeerData$OptionsScreen this$0;

   PeerData$OptionsScreen$1(PeerData$OptionsScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      label17:
      try {
         this.this$0.save();
      } finally {
         break label17;
      }

      this.this$0.close();
   }
}
