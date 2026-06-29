package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapFieldImplementation$6 implements Runnable {
   private final MapFieldImplementation this$0;

   MapFieldImplementation$6(MapFieldImplementation this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      Dialog.alert(LBSResources.getString(154));
   }
}
