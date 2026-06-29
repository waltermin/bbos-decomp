package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class TextField$2 extends MenuItem {
   TextField$2(ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
   }

   @Override
   public void run() {
      BasicEditField target = (BasicEditField)this.getTarget();
      if (target != null) {
         target.keyControl('\u0080', 0, 0);
      }
   }
}
