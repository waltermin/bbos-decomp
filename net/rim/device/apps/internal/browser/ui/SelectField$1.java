package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class SelectField$1 extends MenuItem {
   SelectField$1(ResourceBundle x0, int x1, int x2, int x3) {
   }

   @Override
   public void run() {
      ((SelectField)this.getTarget()).changeDialog(false);
   }

   @Override
   public int getPriority() {
      return this.getTarget().isMuddy() ? 100 + 1000 : 100 + 0;
   }
}
