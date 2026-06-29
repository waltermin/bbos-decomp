package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class MoveMenuItem$Helper$2 implements Runnable {
   private final MoveMenuItem$Helper this$1;

   MoveMenuItem$Helper$2(MoveMenuItem$Helper _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      this.this$1._moveconfirm = 4 == Dialog.ask(3, ExplorerResources.getString(125));
   }
}
