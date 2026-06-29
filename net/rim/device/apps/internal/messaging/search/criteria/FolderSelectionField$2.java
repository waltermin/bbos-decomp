package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class FolderSelectionField$2 extends MenuItem {
   private final FolderSelectionField this$0;

   FolderSelectionField$2(FolderSelectionField _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      FolderSelectionField fsf = (FolderSelectionField)this.getTarget();
      fsf.setSelectedFolder(null);
   }

   @Override
   public int getPriority() {
      int raise = 0;
      if (this.getTarget().isDirty()) {
         raise = 100;
      }

      return super.getPriority() + raise;
   }
}
