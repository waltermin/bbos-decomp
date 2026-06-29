package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class DOWField$1 extends MenuItem {
   DOWField$1(ResourceBundle x0, int x1, int x2, int x3) {
   }

   @Override
   public void run() {
      DOWField dowf = (DOWField)this.getTarget();
      dowf.keyChar(' ', 0, 0);
   }

   @Override
   public int getPriority() {
      return this.getTarget().isMuddy() ? 100 + 1000 : 100 + 0;
   }
}
