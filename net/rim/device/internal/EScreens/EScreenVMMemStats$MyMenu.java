package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.MenuItem;
import net.rim.vm.Memory;

class EScreenVMMemStats$MyMenu extends MenuItem {
   int _type;
   private final EScreenVMMemStats this$0;

   public EScreenVMMemStats$MyMenu(EScreenVMMemStats _1, String text, int type) {
      super(text, 0, 100);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public void run() {
      switch (this._type) {
         case 0:
            break;
         case 1:
         default:
            Memory.quickGC();
            break;
         case 2:
            Memory.fullGC();
            break;
         case 3:
            Memory.thoroughGC();
      }

      this.this$0.refresh();
   }
}
