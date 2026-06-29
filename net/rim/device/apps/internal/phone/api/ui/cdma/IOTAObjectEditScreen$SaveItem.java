package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;

final class IOTAObjectEditScreen$SaveItem extends MenuItem {
   private final IOTAObjectEditScreen this$0;

   IOTAObjectEditScreen$SaveItem(IOTAObjectEditScreen _1, String verb) {
      super(verb, 598272, 100);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.onSave();
      Menu.getTargetScreen().close();
   }
}
