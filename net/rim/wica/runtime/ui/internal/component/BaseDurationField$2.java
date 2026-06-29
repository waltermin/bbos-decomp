package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class BaseDurationField$2 extends MenuItem {
   BaseDurationField$2(ResourceBundle x0, int x1, int x2, int x3) {
   }

   @Override
   public void run() {
      ((BaseDurationField)this.getTarget()).changeOptionDialog();
   }

   @Override
   public int getPriority() {
      return this.getTarget().isMuddy() ? 100 + 1000 : 100 + 0;
   }
}
