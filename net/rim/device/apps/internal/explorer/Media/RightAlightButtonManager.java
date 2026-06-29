package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;

final class RightAlightButtonManager extends Manager {
   private boolean _paintBackground;

   public RightAlightButtonManager(boolean paintBackground) {
      super(0);
      this._paintBackground = paintBackground;
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._paintBackground) {
         int graphicsColor = graphics.getColor();
         int graphicsAlpha = graphics.getGlobalAlpha();
         int drawColor = 14277081;
         graphics.setColor(drawColor);
         graphics.setGlobalAlpha(65);
         graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
         graphics.setColor(graphicsColor);
         graphics.setGlobalAlpha(graphicsAlpha);
      }

      super.paint(graphics);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int remainingWidth = 0;
      int myHeight = 0;
      int numFields = this.getFieldCount();
      Field field = null;
      if (numFields > 0) {
         field = this.getField(numFields - 1);
         this.layoutChild(field, width, height);
         remainingWidth = width - field.getWidth();
         myHeight = field.getHeight();

         for (int x = 0; x < numFields - 1; x++) {
            field = this.getField(x);
            this.layoutChild(field, remainingWidth, height);
            remainingWidth -= field.getWidth();
            myHeight = Math.max(myHeight, field.getHeight());
         }

         field = this.getField(numFields - 1);
         this.setPositionChild(field, width - field.getWidth(), myHeight - field.getHeight() >> 1);
         int x = 0;

         for (int i = 0; i < numFields - 1; i++) {
            field = this.getField(i);
            this.setPositionChild(field, x, myHeight - field.getHeight() >> 1);
            remainingWidth -= field.getWidth();
            x += field.getPreferredWidth();
         }

         this.setExtent(width, myHeight);
         this.setVirtualExtent(width, myHeight);
      }
   }
}
