package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.component.Dialog;

final class MapScreen$3 implements Runnable {
   private final String val$msg;
   private final MapScreen this$0;

   MapScreen$3(MapScreen this$0, String val$msg) {
      this.this$0 = this$0;
      this.val$msg = val$msg;
   }

   @Override
   public final void run() {
      Dialog.alert(this.val$msg);
   }
}
