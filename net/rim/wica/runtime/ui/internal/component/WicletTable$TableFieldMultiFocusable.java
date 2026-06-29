package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Manager;
import net.rim.wica.runtime.ui.internal.MultiFocusable;
import net.rim.wica.runtime.ui.internal.WicaScreen;
import net.rim.wica.runtime.ui.internal.component.table.TableData;
import net.rim.wica.runtime.ui.internal.component.table.TableField;

class WicletTable$TableFieldMultiFocusable extends TableField implements MultiFocusable {
   public WicletTable$TableFieldMultiFocusable(TableData table) {
      super(table);
   }

   @Override
   public void moveFocus(int x, int y, int status, int time) {
      super.moveFocus(x, y, status, time);
   }

   @Override
   protected Manager getScrollingManager() {
      return ((WicaScreen)this.getScreen()).getScrollingManager();
   }
}
