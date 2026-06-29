package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;

final class BranchTitleField$BranchMenuItem extends MenuItem {
   private final BranchTitleField this$0;

   BranchTitleField$BranchMenuItem(BranchTitleField _1, int id) {
      super(CommonResource.getBundle(), id, 463104, 10);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.toggleExpansion();
   }
}
