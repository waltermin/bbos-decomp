package net.rim.device.cldc.impl.hrt.editor;

import net.rim.device.api.ui.MenuItem;

final class HRTAppDacDialog$MyMenuItem extends MenuItem {
   private int _id;
   private final HRTAppDacDialog this$0;

   HRTAppDacDialog$MyMenuItem(HRTAppDacDialog _1, String name, int id) {
      super(name, 0, 0);
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public final void run() {
      switch (this._id) {
         case -1:
            break;
         case 0:
            this.this$0._retCode = 0;
            this.this$0.close();
            break;
         case 1:
         default:
            if (this.this$0.canClose()) {
               this.this$0._retCode = 1;
               this.this$0.close();
               return;
            }
      }
   }
}
