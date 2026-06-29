package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class MoveMenuItem$Helper$1 implements Runnable {
   private final MoveMenuItem$Helper this$1;

   MoveMenuItem$Helper$1(MoveMenuItem$Helper _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      this.this$1._overwrite = 4 == Dialog.ask(3, ExplorerResources.getString(47));
   }
}
