package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapField$3 implements Runnable {
   private final MapField this$0;

   MapField$3(MapField this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      Dialog.alert(LBSResources.getString(487));
   }
}
