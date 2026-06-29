package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.MenuItem;

class EScreenSession$MyMenuItem extends MenuItem {
   private int _id;
   private final EScreenSession this$0;

   EScreenSession$MyMenuItem(EScreenSession _1, int id) {
      super(_1.getMenuString(id), 0, _1.getMenuPriority(id));
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public void run() {
      switch (this._id) {
         case 0:
         case 2:
            break;
         case 1:
         default:
            this.this$0._listField.invalidate();
            return;
         case 3:
            this.this$0.resetStats();
            if (this.this$0.verifyParameters()) {
               this.this$0.setState(1);
               this.this$0.start();
               return;
            }
            break;
         case 4:
            this.this$0.stop();
      }
   }
}
