package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

final class SystemOnOffManager$CradleMismatchDialogClosedListener implements DialogClosedListener {
   private final SystemOnOffManager this$0;

   SystemOnOffManager$CradleMismatchDialogClosedListener(SystemOnOffManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (dialog == this.this$0._cradleMismatchDialog) {
         this.this$0._cradleMismatchDialog = null;
      }
   }
}
