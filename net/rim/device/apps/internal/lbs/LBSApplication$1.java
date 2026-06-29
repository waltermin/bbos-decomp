package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LBSApplication$1 implements Runnable {
   private final LBSApplication this$0;

   LBSApplication$1(LBSApplication this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      String disableMessage = LBSResources.getString(194);
      if (!LBSOptions.getInstance().isDisabled() && MapsServices.getInstance().isMapsDisabled()) {
         String disableMessageSR = MapsServices.getInstance().getMapsDisabledMessage();
         if (disableMessageSR != null) {
            disableMessage = disableMessageSR;
         } else {
            disableMessage = LBSResources.getString(485);
         }
      }

      Dialog.alert(disableMessage);
      System.exit(0);
   }
}
