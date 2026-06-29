package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class PeerApplication$5 implements Runnable {
   private final PeerApplication this$0;

   PeerApplication$5(PeerApplication _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Dialog.alert(QmResources.getString(83));
      System.exit(0);
   }
}
