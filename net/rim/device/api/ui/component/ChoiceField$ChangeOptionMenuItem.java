package net.rim.device.api.ui.component;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;

class ChoiceField$ChangeOptionMenuItem extends MenuItem {
   ChoiceField$ChangeOptionMenuItem() {
      super(CommonResource.getBundle(), 1, 30270, 10);
   }

   ChoiceField$ChangeOptionMenuItem(String text) {
      super(text, 30270, 10);
   }

   @Override
   public void run() {
      ((ChoiceField)this.getTarget()).changeOptionDialog();
   }

   @Override
   public int getPriority() {
      return this.getTarget().isMuddy() ? 100 + 1000 : 100 + 0;
   }
}
