package net.rim.device.api.ui.component;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;

class CheckboxField$ChangeOptionMenuItem extends MenuItem {
   CheckboxField$ChangeOptionMenuItem() {
      super(CommonResource.getBundle(), 1, 30270, 10);
   }

   CheckboxField$ChangeOptionMenuItem(String text) {
      super(text, 30270, 10);
   }

   @Override
   public void run() {
      CheckboxField cbf = (CheckboxField)this.getTarget();
      cbf.keyChar(' ', 0, 0);
   }

   @Override
   public int getPriority() {
      return this.getTarget().isMuddy() ? 100 + 1000 : 100 + 0;
   }
}
