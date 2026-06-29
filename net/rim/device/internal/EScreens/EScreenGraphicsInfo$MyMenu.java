package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.ui.UiInternal;

class EScreenGraphicsInfo$MyMenu extends MenuItem {
   int _type;
   private final EScreenGraphicsInfo this$0;

   public EScreenGraphicsInfo$MyMenu(EScreenGraphicsInfo _1, String text, int type) {
      super(text, 0, 100);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public void run() {
      switch (this._type) {
         case 1:
            UiInternal.clearCacheStatistics();
         default:
            this.this$0.refreshData();
            this.this$0._list.invalidate();
      }
   }
}
