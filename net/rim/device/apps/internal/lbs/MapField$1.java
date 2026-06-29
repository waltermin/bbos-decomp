package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapField$1 implements Runnable {
   private final MapField this$0;

   MapField$1(MapField this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.showHintLabel(LBSResources.getString(316));
   }
}
