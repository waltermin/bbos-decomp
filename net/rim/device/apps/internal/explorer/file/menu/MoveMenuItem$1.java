package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.ui.component.ProgressDialog;

final class MoveMenuItem$1 implements Runnable {
   private final MoveMenuItem this$0;

   MoveMenuItem$1(MoveMenuItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._progressDialog = new ProgressDialog(ExplorerResources.getString(this.this$0._copy ? 120 : 121));
      this.this$0._progressDialog.show();
   }
}
