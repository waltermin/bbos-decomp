package net.rim.wica.runtime.ui.internal.component.table;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class TableField$UnfreezeColumnMenuItem extends MenuItem {
   public TableField$UnfreezeColumnMenuItem() {
      super("Unfreeze Column", 0, 0);
   }

   public TableField$UnfreezeColumnMenuItem(ResourceBundle bundle, int id) {
      super(bundle, id, 0, 0);
   }

   @Override
   public final void run() {
      ((TableField)this.getTarget()).setColFrozenState(false);
   }
}
