package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.MenuItem;

final class EngScreenSecurity$MyMenuItem extends MenuItem {
   private int _id;
   private final EngScreenSecurity this$0;

   EngScreenSecurity$MyMenuItem(EngScreenSecurity _1, int id) {
      super(_1._rb, _1.getMenuId(id), 0, 0);
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public final void run() {
      switch (this._id) {
         case 1:
         default:
            this.this$0.showEScreens(EScreenAccess.getAccessLevel());
            return;
         case 2:
            this.this$0.showEScreens(2);
         case 0:
      }
   }
}
