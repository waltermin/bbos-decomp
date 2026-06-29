package net.rim.wica.runtime.ui.internal.component.table;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class TableField$FreezeColumnMenuItem extends MenuItem {
   public TableField$FreezeColumnMenuItem() {
      super("Freeze Column", 0, 0);
   }

   public TableField$FreezeColumnMenuItem(ResourceBundle bundle, int id) {
      super(bundle, id, 0, 0);
   }

   @Override
   public final void run() {
      ((TableField)this.getTarget()).setColFrozenState(true);
   }
}
