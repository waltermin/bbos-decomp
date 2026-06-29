package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.qm.peer.common.QmPopupStatus;

final class PeerApplication$4 implements Runnable {
   private final int val$type;
   private final String val$message;
   private final PeerApplication this$0;

   PeerApplication$4(PeerApplication _1, int _2, String _3) {
      this.this$0 = _1;
      this.val$type = _2;
      this.val$message = _3;
   }

   @Override
   public final void run() {
      if (this.val$type == 2) {
         QmPopupStatus.show(this.val$message, 2000);
      } else {
         Dialog.alert(this.val$message);
      }
   }
}
