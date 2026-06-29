package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.MenuItem;

class EScreenUI$MyMenuItem extends MenuItem {
   private int _id;
   private Object _context;
   private final EScreenUI this$0;

   EScreenUI$MyMenuItem(EScreenUI _1, int id) {
      super(_1.getMenuString(id), _1.getMenuOrdinal(id), _1.getMenuPriority(id));
      this.this$0 = _1;
      this._id = id;
   }

   EScreenUI$MyMenuItem(EScreenUI _1, int id, Object context) {
      this(_1, id, _1.getMenuString(id), context);
   }

   EScreenUI$MyMenuItem(EScreenUI _1, int id, String text, Object context) {
      super(text, _1.getMenuOrdinal(id), _1.getMenuPriority(id));
      this.this$0 = _1;
      this._id = id;
      this._context = context;
   }

   @Override
   public void run() {
      switch (this._id) {
         case 1:
         default: {
            EScreenItemInfo itemInfo = (EScreenItemInfo)this._context;
            this.this$0._controller.pushScreen(itemInfo.id, itemInfo.idCookie);
            return;
         }
         case 2:
            this.this$0._controller.refresh(false);
            return;
         case 3:
            this.this$0.copyScreen();
            return;
         case 4: {
            EScreenItemInfo itemInfo = (EScreenItemInfo)this._context;
            this.this$0.doAction(itemInfo);
         }
         case 0:
      }
   }
}
