package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.MenuItem;

final class MoveableListField$MyMenu extends MenuItem {
   private int _type;
   private final MoveableListField this$0;
   public static final int TYPE_START_MOVE = 0;

   MoveableListField$MyMenu(MoveableListField _1, String text, int type) {
      super(text, 0, 0);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final void run() {
      switch (this._type) {
         case 0:
            this.this$0.startMove();
      }
   }
}
