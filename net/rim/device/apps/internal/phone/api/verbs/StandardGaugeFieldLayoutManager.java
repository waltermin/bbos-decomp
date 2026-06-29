package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;

final class StandardGaugeFieldLayoutManager extends Manager {
   public StandardGaugeFieldLayoutManager() {
      super(0);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int count = this.getFieldCount();
      int widthUsed = 0;

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         switch (i) {
            case 0:
               XYRect xyRect = this.getField(0).getExtent();
               int x = xyRect.x + xyRect.width;
               int y = 0;
               this.setPositionChild(field, x, y);
               this.layoutChild(field, width, height);
               widthUsed += field.getWidth();
               break;
         }
      }

      this.setExtent(widthUsed, this.getField(0).getHeight());
   }
}
