package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public class MMSPresentationField$MoveFieldMenuItem extends MenuItem {
   private Field _field;
   private final MMSPresentationField this$0;

   public MMSPresentationField$MoveFieldMenuItem(MMSPresentationField _1, Field field) {
      super(MMSResources.getResourceBundle(), 119, 16879615, Integer.MAX_VALUE);
      this.this$0 = _1;
      this._field = field;
   }

   @Override
   public void run() {
      this.this$0.beginMoveMode(this._field);
   }
}
