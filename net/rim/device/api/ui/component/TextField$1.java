package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;

class TextField$1 extends MenuItem {
   TextField$1(ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
   }

   @Override
   public void run() {
      TextField target = (TextField)this.getTarget();
      ResourceBundle bundle = CommonResource.getBundle();
      String[] choices = new String[]{bundle.getString(10043), bundle.getString(10044)};
      if (target.isStyle(2147483648L)) {
         target.clear(0);
      } else {
         int result = Dialog.ask(bundle.getString(10042), choices, 0);
         switch (result) {
            case 0:
               target.clear(0);
         }
      }
   }
}
