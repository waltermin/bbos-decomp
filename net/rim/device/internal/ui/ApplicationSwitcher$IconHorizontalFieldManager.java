package net.rim.device.internal.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.HorizontalFieldManager;

class ApplicationSwitcher$IconHorizontalFieldManager extends HorizontalFieldManager {
   ApplicationSwitcher$IconHorizontalFieldManager(long style) {
      super(style);
   }

   @Override
   protected void subpaint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int left = clip.x;
      int right = clip.x + clip.width;
      int numFields = this.getFieldCount();
      int i = this.getFieldAtLocation(0, left);
      if (i != -1) {
         for (; i < numFields; i++) {
            Field field = this.getField(i);
            if (field.getLeft() >= right) {
               return;
            }

            boolean focusedField = i == this.getFieldWithFocusIndex();
            if (focusedField) {
               boolean oldDrawingFocus = graphics.isDrawingStyleSet(8);
               graphics.setDrawingStyle(8, field == this.getFieldWithFocus());
               this.paintChild(graphics, field);
               graphics.setDrawingStyle(8, oldDrawingFocus);
            } else {
               this.paintChild(graphics, field);
            }
         }
      }
   }
}
