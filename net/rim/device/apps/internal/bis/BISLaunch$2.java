package net.rim.device.apps.internal.bis;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.bis.launch.ui.UpdateScreen;

final class BISLaunch$2 implements Runnable {
   private final BISLaunch this$0;

   BISLaunch$2(BISLaunch _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (!this.this$0._updatesCheckSuccessful) {
         Dialog.alert(net.rim.device.apps.internal.bis.launch.resource.ApplicationResources.getString(17));
         this.this$0.exit();
      } else if (this.this$0._updatesAvailable) {
         this.this$0.pushScreen(new UpdateScreen(this.this$0._updatesRequired, this.this$0._urls, this.this$0._digests, this.this$0._size));
      } else {
         this.this$0.launchBISClient();
      }
   }
}
