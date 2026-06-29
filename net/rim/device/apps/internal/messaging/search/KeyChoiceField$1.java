package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class KeyChoiceField$1 extends MenuItem {
   KeyChoiceField$1(ResourceBundle x0, int x1, int x2, int x3) {
   }

   @Override
   public void run() {
      ((KeyChoiceField)this.getTarget()).setSelectedIndex(0);
   }
}
