package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.UiApplication;

final class HintPopup$1 implements Runnable {
   private final HintPopup this$0;

   HintPopup$1(HintPopup _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (HintPopup.access$000(this.this$0) != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(HintPopup.access$000(this.this$0));
         HintPopup.access$002(this.this$0, -1);
      }

      HintPopup.access$100(this.this$0);
   }
}
