package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.messaging.ui.FolderList;

class FolderSelectionField$1 extends MenuItem {
   FolderSelectionField$1(ResourceBundle x0, int x1, int x2, int x3) {
   }

   @Override
   public void run() {
      FolderSelectionField fsf = (FolderSelectionField)this.getTarget();
      FolderList fl = FolderSelectionField.getSelectionList(fsf._folder, new LocalSelectFolderVerb(fsf));
      fl.run();
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
