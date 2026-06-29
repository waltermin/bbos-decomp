package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.component.Dialog;

final class MoveMenuItem$Helper$3 implements Runnable {
   private final String val$error;
   private final MoveMenuItem$Helper this$1;

   MoveMenuItem$Helper$3(MoveMenuItem$Helper _1, String _2) {
      this.this$1 = _1;
      this.val$error = _2;
   }

   @Override
   public final void run() {
      Dialog.alert(this.val$error);
   }
}
