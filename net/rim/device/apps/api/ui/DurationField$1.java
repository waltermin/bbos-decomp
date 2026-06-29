package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class DurationField$1 extends MenuItem {
   DurationField$1(ResourceBundle x0, int x1, int x2, int x3) {
   }

   @Override
   public void run() {
      ((DurationField)this.getTarget()).changeOptionDialog();
   }

   @Override
   public int getPriority() {
      return this.getTarget().isMuddy() ? 100 + 1000 : 100 + 0;
   }
}
